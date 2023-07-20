package com.example.shoppe.di

import android.app.Application
import android.content.Context
import com.example.shoppe.firebase.FirebaseCommon
import com.example.shoppe.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth() = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseFirestoreDatabase() = Firebase.firestore

    @Provides
    fun provideIntroductionSP(
        application: Application
    ) = application.getSharedPreferences(Constants.INTRODUCTION_SP, Context.MODE_PRIVATE)

    @Provides
    @Singleton
    fun provideFirebaseCommon(
        firestore: FirebaseFirestore,
        auth: FirebaseAuth,
    ) = FirebaseCommon(firestore, auth)

    @Provides
    @Singleton
    fun provideStorage() = FirebaseStorage.getInstance().reference
}