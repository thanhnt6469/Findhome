package com.app.findhome.ui.pages.login

import android.app.Activity
import android.os.Build
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import com.app.findhome.ui.navigation.AppPage
import com.app.findhome.ui.pages.signup.SignupPage
import com.app.findhome.ui.pages.signup.SignupViewModel
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun GoogleLoginScreen(navController: NavController, googleAuthUiClient: GoogleAuthUiClient, activity: ComponentActivity) {
    val viewModel: LoginViewModel = hiltViewModel()
    val state by viewModel.googleState.collectAsState()
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
        onResult = { result ->
            if (result.resultCode != Activity.RESULT_OK) {
                Log.e("GoogleLogin", "Google Sign-In failed with result code: ${result.resultCode}")
                return@rememberLauncherForActivityResult
            }
            activity.lifecycleScope.launch {
                try {
                    val loginResult = googleAuthUiClient.loginWithIntent(result.data ?: return@launch)
                    viewModel.googleLogin(loginResult)
                } catch (e: Exception) {
                    Log.e("GoogleLogin", "Exception during Google Sign-In: ${e.message}", e)
                }
            }
        }
    )

    LoginPage(
        navController = navController,
        state = state,
        onSignupClick = { navController.navigate(AppPage.SignupPage.route) },
        onGoogleLogin = {
            activity.lifecycleScope.launch {
                viewModel.loginProgress.value = true
                try {
                    googleAuthUiClient.logIn()?.let {
                        launcher.launch(IntentSenderRequest.Builder(it).build())
                    } ?: Log.e("GoogleLogin", "SignInIntentSender is null")
                } catch (e: Exception) {
                    Log.e("GoogleLogin", "Exception launching Google Sign-In: ${e.message}", e)
                }
            }
        }
    )
}

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun GoogleSignupScreen(navController: NavController, googleAuthUiClient: GoogleAuthUiClient, activity: ComponentActivity) {
    val viewModel: LoginViewModel = hiltViewModel()
    val viewModel1: SignupViewModel = hiltViewModel()
    val state by viewModel.googleState.collectAsState()
    val role by viewModel1.selectedRole.collectAsState()
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
        onResult = { result ->
            if (result.resultCode != Activity.RESULT_OK) {
                Log.e("GoogleSignup", "Google Sign-Up failed with result code: ${result.resultCode}")
                return@rememberLauncherForActivityResult
            }
            activity.lifecycleScope.launch {
                try {
                    val signupResult = googleAuthUiClient.loginWithIntent(result.data ?: return@launch)
                    viewModel.googleLogin(signupResult)
                } catch (e: Exception) {
                    Log.e("GoogleSignup", "Exception during Google Sign-Up: ${e.message}", e)
                }
            }
        }
    )

    SignupPage(
        navController = navController,
        state = state,
        onLoginClick = { navController.navigate(AppPage.LoginPage.route) },
        onGoogleSignup = {
            activity.lifecycleScope.launch {
                viewModel.loginProgress.value = true
                try {
                    googleAuthUiClient.setRole(role)
                    googleAuthUiClient.logIn()?.let {
                        launcher.launch(IntentSenderRequest.Builder(it).build())
                    } ?: Log.e("GoogleSignup", "SignInIntentSender is null")
                } catch (e: Exception) {
                    Log.e("GoogleSignup", "Exception launching Google Sign-Up: ${e.message}", e)
                }
            }
        }
    )
}
