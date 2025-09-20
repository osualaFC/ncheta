package com.fredrickosuala.ncheta.repository

import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {
    /**
     * A flow that emits true if a user is logged in, and false otherwise.
     */
    fun observeAuthState(): StateFlow<Boolean>

    /**
     * Gets the current logged-in user, if any.
     */
    fun getCurrentUser(): NchetaUser?

    /**
     * Signs up a new user with email and password.
     */
    suspend fun signUp(email: String, password: String): AuthResult

    /**
     * Signs in an existing user with email and password.
     */
    suspend fun signIn(email: String, password: String): AuthResult

    /**
     * Signs out the current user.
     */
    suspend fun signOut()

    /**
     * Signs in a user using an ID token obtained from Google Sign-In.
     */
    suspend fun signInWithGoogle(idToken: String, accessToken: String? = null): AuthResult

    /**
     * Signs in a user using credentials obtained from Sign in with Apple.
     */
    suspend fun signInWithApple(idToken: String, nonce: String): AuthResult
}


data class NchetaUser(
    val uid: String,
    val email: String?
)


sealed class AuthResult {
    data object Success : AuthResult()
    data class Error(val message: String) : AuthResult()
}