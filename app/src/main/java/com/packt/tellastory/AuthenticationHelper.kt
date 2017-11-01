package com.packt.tellastory

import com.google.firebase.auth.FirebaseUser

object AuthenticationHelper {

    var user : FirebaseUser? = null

    val isAuthenticated: Boolean
        get() = user != null

    val userName: String?
        get() {
            if (user?.phoneNumber != null) {
                return user?.phoneNumber.toString()
            } else {
                return user?.displayName
            }
        }
}