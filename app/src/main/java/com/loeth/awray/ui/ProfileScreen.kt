package com.loeth.awray.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.loeth.awray.AwrayViewModel
import com.loeth.awray.CommonDivider
import com.loeth.awray.CommonImage
import com.loeth.awray.CommonProgressSpinner
import com.loeth.awray.DestinationScreen
import com.loeth.awray.navigateTo

enum class Gender {
    MALE,
    FEMALE,
    ANY
}

@Composable
fun ProfileScreen(navController: NavController, viewModel: AwrayViewModel) {
    val inProgress = viewModel.inProgress.value
    if (inProgress)
        CommonProgressSpinner()
    else {
        val userData = viewModel.userData.value
        var name by rememberSaveable { mutableStateOf(userData?.name ?: "") }
        var userName by rememberSaveable { mutableStateOf(userData?.username ?: "") }
        var bio by rememberSaveable { mutableStateOf(userData?.bio ?: "") }
        var gender by rememberSaveable {
            mutableStateOf(Gender.valueOf(userData?.gender?.uppercase() ?: "MALE"))
        }
        var genderPreference by rememberSaveable {
            mutableStateOf(Gender.valueOf(userData?.genderPreference?.uppercase() ?: "FEMALE"))
        }

        val scrollState = rememberScrollState()

        Column {
            ProfileContent(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
                    .padding(8.dp),
                viewModel = viewModel,
                name = name,
                username = userName,
                bio = bio,
                gender = gender,
                genderPreference = genderPreference,
                onNameChanged = { name = it },
                onUserNameChanged = { userName = it },
                onBioChanged = { bio = it },
                onGenderChanged = { gender = it },
                onGenderPreferenceChanged = { genderPreference = it },
                onSave = {
                    viewModel.updateProfileData(name, userName, bio, gender, genderPreference)
                },
                onBack = { navigateTo(navController, DestinationScreen.Swipe.route) },
                onLogout = {
                    viewModel.onLogout()
                    navigateTo(navController, DestinationScreen.Login.route)
                }

            )

            BottomNavigationMenu(
                selectedItem = BottomNavigationItem.PROFILE,
                navController = navController
            )
        }
    }

}

@Composable
fun ProfileContent(
    modifier: Modifier,
    viewModel: AwrayViewModel,
    name: String,
    username: String,
    bio: String,
    gender: Gender,
    genderPreference: Gender,
    onNameChanged: (String) -> Unit,
    onUserNameChanged: (String) -> Unit,
    onBioChanged: (String) -> Unit,
    onGenderChanged: (Gender) -> Unit,
    onGenderPreferenceChanged: (Gender) -> Unit,
    onSave: () -> Unit,
    onBack: () -> Unit,
    onLogout: () -> Unit
) {
    val imageUrl = viewModel.userData.value?.imageUrl

    Column() {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Back", modifier = Modifier.clickable { onBack.invoke() })
            Text(text = "Save", modifier = Modifier.clickable { onSave.invoke() })
        }

        CommonDivider()

        ProfileImage(imageUrl = imageUrl, viewModel = viewModel)

        CommonDivider()

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        )
        {
            Text(text = "Name", modifier = Modifier.width(100.dp))
            TextField(
                value = name,
                onValueChange = onNameChanged,
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                )
            )


        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        )
        {
            Text(text = "Username", modifier = Modifier.width(100.dp))
            TextField(
                value = username,
                onValueChange = onUserNameChanged,
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                )
            )

        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        )
        {
            Text(text = "Bio", modifier = Modifier.width(100.dp))
            TextField(
                value = bio,
                onValueChange = onBioChanged,
                modifier = Modifier
                    .height(150.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                ),
                singleLine = false

            )

        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            verticalAlignment = Alignment.Top
        )
        {
            Text(
                text = "I am interested in:", modifier = Modifier
                    .width(100.dp)
                    .padding(8.dp)
            )
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = gender == Gender.MALE,
                        onClick = { onGenderChanged(Gender.MALE) }
                    )
                    Text(
                        text = "Man",
                        modifier = Modifier
                            .padding(4.dp)
                            .clickable { onGenderChanged(Gender.MALE) })

                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = gender == Gender.FEMALE,
                        onClick = { onGenderChanged(Gender.FEMALE) }
                    )
                    Text(
                        text = "Woman",
                        modifier = Modifier
                            .padding(4.dp)
                            .clickable { onGenderChanged(Gender.FEMALE) })

                }
            }
        }
        CommonDivider()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            verticalAlignment = Alignment.Top
        )
        {
            Text(
                text = "I am interested in:", modifier = Modifier
                    .width(100.dp)
                    .padding(8.dp)
            )
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = genderPreference == Gender.MALE,
                        onClick = { onGenderPreferenceChanged(Gender.MALE) }
                    )
                    Text(
                        text = "Men",
                        modifier = Modifier
                            .padding(4.dp)
                            .clickable { onGenderPreferenceChanged(Gender.MALE) })

                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = genderPreference == Gender.FEMALE,
                        onClick = { onGenderPreferenceChanged(Gender.FEMALE) }
                    )
                    Text(
                        text = "Women",
                        modifier = Modifier
                            .padding(4.dp)
                            .clickable { onGenderPreferenceChanged(Gender.FEMALE) })

                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = genderPreference == Gender.ANY,
                        onClick = { onGenderPreferenceChanged(Gender.ANY) }
                    )
                    Text(
                        text = "Women",
                        modifier = Modifier
                            .padding(4.dp)
                            .clickable { onGenderPreferenceChanged(Gender.ANY) })

                }
            }
        }
        CommonDivider()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = "Logout", modifier = Modifier.clickable { onLogout.invoke() })
        }

    }
}


@Composable
fun ProfileImage(imageUrl: String? = null, viewModel: AwrayViewModel) {
    Box(modifier = Modifier.height(IntrinsicSize.Min)) {
        Column(modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable {
                //Select an Image
            },
            horizontalAlignment = Alignment.CenterHorizontally) {
            Card(shape = CircleShape, modifier = Modifier
                .padding(8.dp)
                .size(100.dp)) {
                CommonImage(data = imageUrl, modifier = Modifier.wrapContentSize())
            }
            Text(text = "Change profile picture")
        }

        val isLoading = viewModel.inProgress.value
        if(isLoading)
            CommonProgressSpinner()
    }
}