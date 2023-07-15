package com.example.shoppe.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoppe.models.User
import com.example.shoppe.utils.Constants
import com.example.shoppe.utils.RegisterFieldsState
import com.example.shoppe.utils.RegisterValidation
import com.example.shoppe.utils.Resource
import com.example.shoppe.utils.validateEmail
import com.example.shoppe.utils.validatePassword
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val db: FirebaseFirestore
) : ViewModel() {

    private val _register = MutableStateFlow<Resource<User>>(Resource.Unspecified())

    // val register: Flow<Resource<User>> = _register
    val register = _register.asStateFlow()


    private val _validation = Channel<RegisterFieldsState>()
    val validation = _validation.receiveAsFlow()

    fun createAccountWithEmailAndPassword(user: User, password: String) {
        val emailValidation = validateEmail(user.email)
        val passwordValidation = validatePassword(password)
        if (emailValidation is RegisterValidation.Success && passwordValidation is RegisterValidation.Success) {
            viewModelScope.launch {
                _register.emit(Resource.Loading())
            }
            firebaseAuth.createUserWithEmailAndPassword(user.email, password)
                .addOnSuccessListener { authResult ->
                    authResult.user?.let {
                        saveUserInfo(it.uid, user)
                        _register.value = Resource.Success(user)
                    }
                }.addOnFailureListener {
                    _register.value = Resource.Error(it.message.toString())
                }
        } else {
            val registerFieldsState = RegisterFieldsState(
                emailValidation,
                passwordValidation
            )
            viewModelScope.launch {
                _validation.send(registerFieldsState)
            }
        }

    }

    private fun saveUserInfo(userUid: String, user: User) {
        db.collection(Constants.USER_COLLECTION).document(userUid).set(user).addOnSuccessListener {
            _register.value = Resource.Success(user)
        }.addOnFailureListener {
            _register.value = Resource.Error(it.message.toString())
        }
    }
}