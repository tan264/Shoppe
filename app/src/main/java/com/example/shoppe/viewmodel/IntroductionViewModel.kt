package com.example.shoppe.viewmodel

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoppe.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IntroductionViewModel @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    firebaseAuth: FirebaseAuth,
) : ViewModel() {
    companion object {
        const val SHOPPING_ACTIVITY = 26
        const val ACCOUNT_OPTIONS_FRAGMENT = 2
    }

    private val _navigate = MutableStateFlow(0)
    val navigate = _navigate.asStateFlow()

    init {
        val isButtonClicked = sharedPreferences.getBoolean(Constants.INTRODUCTION_KEY, false)
        val user = firebaseAuth.currentUser

        if (user != null) {
            Log.d("tan264", user.uid)
            viewModelScope.launch {
                _navigate.emit(SHOPPING_ACTIVITY)
            }
        } else if (isButtonClicked) {
            viewModelScope.launch {
                _navigate.emit(ACCOUNT_OPTIONS_FRAGMENT)
            }
        }
    }

    fun startButtonClick() {
        sharedPreferences.edit().putBoolean(Constants.INTRODUCTION_KEY, true).apply()
    }

}