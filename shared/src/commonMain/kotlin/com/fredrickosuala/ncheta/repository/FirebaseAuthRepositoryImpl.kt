package com.fredrickosuala.ncheta.repository

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.GoogleAuthProvider
import dev.gitlive.firebase.auth.OAuthProvider
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class FirebaseAuthRepositoryImpl : AuthRepository {

    private val firebaseAuth = Firebase.auth

    private val repositoryScope = CoroutineScope(Dispatchers.Default)

    override fun observeAuthState(): StateFlow<Boolean> {
        return firebaseAuth.authStateChanged
            .map { it != null }
            .stateIn(
                scope = repositoryScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = (firebaseAuth.currentUser != null)
            )
    }

    override fun getCurrentUser(): NchetaUser? {
        return firebaseAuth.currentUser?.let { firebaseUser ->
            NchetaUser(uid = firebaseUser.uid, email = firebaseUser.email)
        }
    }

    override suspend fun signUp(email: String, password: String): AuthResult {
        return try {
            firebaseAuth.createUserWithEmailAndPassword(email, password)
            AuthResult.Success
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "An unknown signup error occurred.")
        }
    }

    override suspend fun signIn(email: String, password: String): AuthResult {
        return try {
            firebaseAuth.signInWithEmailAndPassword(email, password)
            AuthResult.Success
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "An unknown sign-in error occurred.")
        }
    }

    override suspend fun signOut() {
        try {
            firebaseAuth.signOut()
        } catch (e: Exception) {
            println("Error signing out: ${e.message}")
        }
    }

    override suspend fun signInWithGoogle(idToken: String): AuthResult {
        return try {
            val credential = GoogleAuthProvider.credential(idToken = idToken, accessToken = null)
            firebaseAuth.signInWithCredential(credential)
            AuthResult.Success
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "An unknown Google Sign-In error occurred.")
        }
    }

    override suspend fun signInWithApple(idToken: String, nonce: String): AuthResult {
        return try {
            val credential = OAuthProvider.credential(
                providerId = "apple.com",
                idToken = idToken,
                rawNonce = nonce
            )
            firebaseAuth.signInWithCredential(credential)
            AuthResult.Success
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "An unknown Apple Sign-In error occurred.")
        }
    }
}