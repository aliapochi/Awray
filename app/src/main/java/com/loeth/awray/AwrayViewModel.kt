package com.loeth.awray

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AwrayViewModel @Inject constructor(
    val auth: FirebaseAuth,
    val db: FirebaseStorage,
    val storage: FirebaseStorage
)
    : ViewModel() {
        val inProgress = mutableStateOf(true)
}