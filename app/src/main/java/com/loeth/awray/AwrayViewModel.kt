package com.loeth.awray

import android.icu.util.Calendar
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.toObject
import com.google.firebase.storage.FirebaseStorage
import com.loeth.awray.data.COLLECTION_CHAT
import com.loeth.awray.data.COLLECTION_MESSAGES
import com.loeth.awray.data.COLLECTION_USER
import com.loeth.awray.data.ChatData
import com.loeth.awray.data.ChatUser
import com.loeth.awray.data.Event
import com.loeth.awray.data.Message
import com.loeth.awray.data.UserData
import com.loeth.awray.ui.Gender
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AwrayViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore,
    private val storage: FirebaseStorage
) : ViewModel() {

    val inProgress = mutableStateOf(false)
    val popUpNotification = mutableStateOf<Event<String>?>(null)
    val signedIn = mutableStateOf(false)
    val userData = mutableStateOf<UserData?>(null)

    val matchProfiles = mutableStateOf<List<UserData>>(listOf())
    val inProgressProfiles = mutableStateOf(false)

    val chats = mutableStateOf<List<ChatData>>(listOf())
    val inProgressChats = mutableStateOf(false)

    val inProgressChatMessages = mutableStateOf(false)
    val chatMessages = mutableStateOf<List<Message>>(listOf())
    var currentChatMessageListener: ListenerRegistration? = null

    init {
        //auth.signOut()
        val currentUser = auth.currentUser
        signedIn.value = currentUser != null
        currentUser?.uid?.let { uid ->
            getUserData(uid)
        }
    }

    fun onSignUp(username: String, email: String, password: String) {
        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            handleException(customMessage = "Please fill in all fields")
            return
        }
        inProgress.value = true
        db.collection(COLLECTION_USER).whereEqualTo("username", username)
            .get()
            .addOnSuccessListener {
                if (it.isEmpty)
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                signedIn.value = true
                                createOrUpdateProfile(username = username)
                            } else
                                handleException(task.exception, "Sign Up failed")
                        }
                else
                    handleException(customMessage = "Username already exists")

                inProgress.value = false
            }
            .addOnFailureListener {
                handleException(it)
            }
    }

    fun onLogin(email: String, password: String) {
        if (email.isEmpty() or password.isEmpty()) {
            handleException(customMessage = "Please fill in all fields")
            return
        }
        inProgress.value = true
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    signedIn.value = true
                    inProgress.value = false
                    auth.currentUser?.uid?.let { uid ->
                        getUserData(uid)
                    }
                } else
                    handleException(task.exception, "Login failed")
            }
            .addOnFailureListener {
                handleException(it, "Login failed")

            }
    }

    private fun createOrUpdateProfile(
        name: String? = null,
        username: String? = null,
        bio: String? = null,
        imageUrl: String? = null,
        gender: Gender? = null,
        genderPreference: Gender? = null
    ) {
        var uid = auth.currentUser?.uid
        var userData = UserData(
            userId = uid,
            name = name ?: userData.value?.name,
            username = username ?: userData.value?.username,
            imageUrl = imageUrl ?: userData.value?.imageUrl,
            bio = bio ?: userData.value?.bio,
            gender = gender?.toString() ?: userData.value?.gender,
            genderPreference = genderPreference?.toString() ?: userData.value?.genderPreference
        )
        uid?.let { uid ->
            inProgress.value = true
            db.collection(COLLECTION_USER).document(uid).get()
                .addOnSuccessListener {
                    if (it.exists()) {
                        it.reference.update(userData.toMap())
                            .addOnSuccessListener {
                                this.userData.value = userData
                                inProgress.value = false
                                populateCards()
                            }
                            .addOnFailureListener {
                                handleException(it, "Cannot update profile")
                            }
                    } else {
                        db.collection(COLLECTION_USER).document(uid).set(userData)
                        inProgress.value = false
                        getUserData(uid)

                    }
                }
                .addOnFailureListener {
                    handleException(it, "Cannot create User")
                }

        }

    }

    private fun getUserData(uid: String) {
        inProgress.value = true
        db.collection(COLLECTION_USER).document(uid)
            .addSnapshotListener { value, error ->
                if (error != null)
                    handleException(error, "Cannot fetch user data")
                if (value != null) {
                    val user = value.toObject<UserData>()
                    userData.value = user
                    inProgress.value = false
                    populateCards()
                    populateChats()
                }
            }
    }

    fun onLogout() {
        auth.signOut()
        signedIn.value = false
        userData.value = null
        popUpNotification.value = Event("Logged out")
    }

    fun updateProfileData(
        name: String,
        username: String,
        bio: String,
        gender: Gender,
        genderPreference: Gender
    ) {
        createOrUpdateProfile(
            name = name,
            username = username,
            bio = bio,
            gender = gender,
            genderPreference = genderPreference
        )
    }

    fun uploadImage(uri: Uri, onSuccess: (Uri) -> Unit) {
        inProgress.value = true

        val storageRef = storage.reference
        val uuid = UUID.randomUUID()
        val imageRef = storageRef.child("images/$uuid")
        val uploadTask = imageRef.putFile(uri)

        uploadTask
            .addOnSuccessListener {
                val result = it.metadata?.reference?.downloadUrl
                result?.addOnSuccessListener(onSuccess)
            }
            .addOnFailureListener {
                handleException(it)
                inProgress.value = false

            }
    }

    fun uploadProfileImage(uri: Uri) {
        uploadImage(uri) {
            val imageUrl = it.toString()
            createOrUpdateProfile(imageUrl = imageUrl)
        }
    }

    private fun handleException(exception: Exception? = null, customMessage: String = "") {
        Log.e("Awray", "Awray Exception", exception)
        exception?.printStackTrace()
        val errorMsg = exception?.localizedMessage ?: ""
        val message = if (customMessage.isEmpty()) errorMsg else "$customMessage $errorMsg"
        popUpNotification.value = Event(message)
        inProgress.value = false
    }

    private fun populateCards() {
        inProgressProfiles.value = true
        Log.d("populateCards", "Started populating cards")

        val g = if (userData.value?.gender.isNullOrEmpty()) "ANY"
        else userData.value!!.gender!!.uppercase()
        val gPref =
            if (userData.value?.genderPreference.isNullOrEmpty()) "ANY"
            else userData.value!!.genderPreference!!.uppercase()

        Log.d("populateCards", "User gender: $g, Gender preference: $gPref")

        val userGender = Gender.valueOf(g)

        // Fetch base query without applying '!=' filters
        val baseQuery = db.collection(COLLECTION_USER)

        Log.d("populateCards", "Base query prepared")

        val cardsQuery = when (Gender.valueOf(gPref)) {
            Gender.MALE -> baseQuery.whereEqualTo("gender", Gender.MALE)
            Gender.FEMALE -> baseQuery.whereEqualTo("gender", Gender.FEMALE)
            Gender.ANY -> baseQuery
        }

        Log.d("populateCards", "Cards query created for gender: $gPref")

        cardsQuery.get()
            .addOnSuccessListener { documents ->
                val potentials = mutableListOf<UserData>()

                documents.forEach { document ->
                    Log.d("populateCards", "Documents fetched: ${documents.size()}")

                    document.toObject<UserData>().let { potential ->
                        Log.d("populateCards", "Mapped potential user: $potential")

                        // Convert string genderPreference to Gender enum
                        val genderPref = try {
                            Gender.valueOf(potential.genderPreference?.uppercase() ?: "ANY")
                        } catch (e: IllegalArgumentException) {
                            Gender.ANY
                        }

                        // Apply filters locally
                        if (potential.userId != userData.value?.userId) {
                            if ((genderPref == userGender || genderPref == Gender.ANY) &&
                                (potential.gender == gPref || gPref == "ANY")
                            ) {
                                potentials.add(potential)
                                Log.d("populateCards", "Added user: ${potential.userId}")
                            } else {
                                Log.d(
                                    "populateCards",
                                    "User skipped: ${potential.userId} - genderPref: $genderPref, userGender: $userGender, potentialGender: ${potential.gender}, userPreference: $gPref"
                                )
                            }
                        }

                    }
                }

                Log.d("populateCards", "Total potentials added: ${potentials.size}")

                matchProfiles.value = potentials
                inProgressProfiles.value = false
            }
            .addOnFailureListener { error ->
                Log.e("populateCards", "Error getting documents: ", error)
                inProgressProfiles.value = false
                handleException(error)
            }
    }

    fun onDislike(selectedUser: UserData) {
        //db.collection(COLLECTION_USER).document(userData.userId!!).update("swipedRight", true)
        db.collection(COLLECTION_USER).document(userData.value?.userId ?: "")
            .update("swipedRight", FieldValue.arrayUnion(selectedUser.userId))
    }

    fun onLike(selectedUser: UserData) {
        //val otherUser = matchProfiles.value.find { it.userId == selectedUser.userId }
        val reciprocalMatch = selectedUser.swipesRight.contains(userData.value?.userId)
        if (!reciprocalMatch) {
            db.collection(COLLECTION_USER).document(userData.value?.userId ?: "")
                .update("swipesRight", FieldValue.arrayUnion(selectedUser.userId))
        } else {
            //db.collection(COLLECTION_USER).document(userData.userId!!).update("swipedRight", true)
            popUpNotification.value = Event("You have a match!")
            //db.collection(COLLECTION_USER).document(selectedUser.userId!!).update("swipedRight", true)

            // Updating the swiped user's document
            db.collection(COLLECTION_USER).document(selectedUser.userId ?: "")
                .update(
                    "swipesRight",
                    FieldValue.arrayRemove(userData.value?.userId)
                ) // Remove current user's ID from 'swipesRight'
            db.collection(COLLECTION_USER).document(selectedUser.userId ?: "")
                .update(
                    "matches",
                    FieldValue.arrayUnion(userData.value?.userId)
                ) // Add current user's ID to 'matches'

// Updating the current user's document
            db.collection(COLLECTION_USER).document(userData.value?.userId ?: "")
                .update(
                    "matches",
                    FieldValue.arrayUnion(selectedUser.userId)
                ) // Add swiped user's ID to 'matches'


            val chatKey = db.collection(COLLECTION_CHAT).document().id
            val chat = ChatData(
                chatKey,
                ChatUser(
                    userData.value?.userId,
                    if (userData.value?.name.isNullOrEmpty()) userData.value?.username
                    else userData.value?.name,
                    userData.value?.imageUrl
                ),
                ChatUser(
                    selectedUser.userId,
                    if (selectedUser.name.isNullOrEmpty()) selectedUser.username
                    else selectedUser.name,
                    selectedUser.imageUrl
                )
            )
            db.collection(COLLECTION_CHAT).document(chatKey).set(chat)
        }

    }

    fun removeProfile(profile: UserData) {
        matchProfiles.value = matchProfiles.value.filterNot { it.userId == profile.userId }
    }

    private fun populateChats() {
        inProgressChats.value = true
        db.collection(COLLECTION_CHAT).where(
            Filter.or(
                Filter.equalTo("user1.userId", userData.value?.userId),
                Filter.equalTo("user2.userId", userData.value?.userId)
            )
        )
            .addSnapshotListener { value, error ->
                if (error != null) {
                    handleException(error)
                }
                if (value != null) {
                    chats.value = value.documents.mapNotNull { it.toObject<ChatData>() }

                    inProgressChats.value = false
                }
            }
    }

    fun onSendReply(chatId: String, message: String) {
        val time = Calendar.getInstance().time.toString()
        val message = Message(userData.value?.userId, message, time)
        db.collection(COLLECTION_CHAT).document(chatId).collection(COLLECTION_MESSAGES).document()
            .set(message)
    }

    fun populateChat(chatId: String) {
        inProgressChatMessages.value = true
        currentChatMessageListener = db.collection(COLLECTION_CHAT)
            .document(chatId)
            .collection(COLLECTION_MESSAGES)
            .addSnapshotListener { value, error ->
                if (error != null)
                    handleException(error)
                if (value != null)
                    chatMessages.value = value.documents
                        .mapNotNull { it.toObject<Message>() }
                        .sortedBy { it.timeStamp }
                inProgressChatMessages.value = false
            }
    }

    fun depopulateChat() {
        currentChatMessageListener = null
        chatMessages.value = listOf()
    }
}

