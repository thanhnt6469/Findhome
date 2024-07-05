package com.app.findhome.ui

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.app.findhome.ui.components.BottomBar
import com.app.findhome.ui.navigation.AppPage
import com.app.findhome.ui.pages.login.GoogleAuthUiClient
import com.app.findhome.ui.pages.login.GoogleLoginScreen
import com.app.findhome.ui.pages.login.GoogleSignupScreen
import com.app.findhome.ui.theme.AppTheme
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val googleAuthUiClient: GoogleAuthUiClient by lazy {
        GoogleAuthUiClient(context = applicationContext, oneTapClient = Identity.getSignInClient(applicationContext))
    }

    @RequiresApi(Build.VERSION_CODES.P)
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        val firebaseAppCheck = FirebaseAppCheck.getInstance()
        firebaseAppCheck.installAppCheckProviderFactory(
            PlayIntegrityAppCheckProviderFactory.getInstance()
        )
        setContent {
            AppTheme {
                val navController = rememberNavController()
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        val currentBackStackEntry = navController.currentBackStackEntryAsState()
                        val currentRoute = currentBackStackEntry.value?.destination?.route
                        if (currentRoute != null
                            && !currentRoute.startsWith(AppPage.DetailPage.route)
                            && !currentRoute.startsWith(AppPage.LoginPage.route)
                            && !currentRoute.startsWith(AppPage.SignupPage.route)
                            && !currentRoute.startsWith(AppPage.ArticlePage.route)
                            && !currentRoute.startsWith(AppPage.UserProfilePage.route)
                            && !currentRoute.startsWith(AppPage.EditArticlePage.route)
                            && !currentRoute.startsWith(AppPage.ChatPage.route)
                        ) {
                            BottomBar(modifier = Modifier.fillMaxWidth(), navController = navController)
                        }
                    }
                ) { paddingValues ->
                    NavHost(
                        navController = navController,
                        startDestination = AppPage.LoginPage.route,
                        modifier = Modifier.padding(paddingValues)
                    ) {
                        composable(route = AppPage.LoginPage.route) {
                            GoogleLoginScreen(navController = navController, googleAuthUiClient = googleAuthUiClient, activity = this@MainActivity)
                        }

                        composable(route = AppPage.SignupPage.route) {
                            GoogleSignupScreen(navController = navController, googleAuthUiClient = googleAuthUiClient, activity = this@MainActivity)
                        }
                    }
                }
            }
        }
    }
}