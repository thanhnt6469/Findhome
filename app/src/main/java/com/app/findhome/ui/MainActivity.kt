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
import androidx.navigation.compose.navArgument
import androidx.navigation.compose.rememberNavController
import com.app.findhome.data.model.User
import com.app.findhome.ui.components.BottomBar
import com.app.findhome.ui.navigation.AppPage
import com.app.findhome.ui.pages.article.ArticlePage
import com.app.findhome.ui.pages.article.EditArticlePage
import com.app.findhome.ui.pages.details.DetailPage
import com.app.findhome.ui.pages.favorite.FavoritePage
import com.app.findhome.ui.pages.favorite.FavoriteViewModel
import com.app.findhome.ui.pages.home.HomePage
import com.app.findhome.ui.pages.login.GoogleAuthUiClient
import com.app.findhome.ui.pages.login.GoogleLoginScreen
import com.app.findhome.ui.pages.login.GoogleSignupScreen
import com.app.findhome.ui.pages.message.ChatListPage
import com.app.findhome.ui.pages.message.ChatPage
import com.app.findhome.ui.pages.message.ChatViewModel
import com.app.findhome.ui.pages.profile.ProfilePage
import com.app.findhome.ui.pages.profile.ProfileViewModel
import com.app.findhome.ui.pages.profile.UserProfilePage
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
                val favoriteViewModel: FavoriteViewModel = hiltViewModel()
                val profileViewModel: ProfileViewModel = hiltViewModel()
                val chatViewModel: ChatViewModel = hiltViewModel()
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

                        composable(route = AppPage.HomePage.route) {
                            HomePage(navController = navController, favoriteViewModel = favoriteViewModel)
                        }

                        composable(route = AppPage.FavoritePage.route) {
                            FavoritePage(navController = navController, favoriteViewModel = favoriteViewModel)
                        }

                        composable(
                            route = "${AppPage.DetailPage.route}/{propertyId}",
                            arguments = listOf(navArgument("propertyId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val propertyId = backStackEntry.arguments?.getString("propertyId") ?: ""
                            DetailPage(navController = navController, favoriteViewModel = favoriteViewModel, propertyId = propertyId)
                        }

                        composable(route = AppPage.ArticlePage.route) {
                            ArticlePage(navController = navController, userData = googleAuthUiClient.getLoggedInUser())
                        }

                        composable(
                            route = "${AppPage.ChatPage.route}/{userId}/{nameGuest}",
                            arguments = listOf(
                                navArgument("userId") { type = NavType.StringType },
                                navArgument("nameGuest") { type = NavType.StringType }
                            )
                        ) {backStackEntry ->
                            val userIdGuest = backStackEntry.arguments?.getString("userId") ?: ""
                            val nameGuest = backStackEntry.arguments?.getString("nameGuest") ?: ""
                            val userIdCurrent = FirebaseAuth.getInstance().currentUser?.uid
                            val nameCurrent = googleAuthUiClient.getLoggedInUser()?.fullName
                            if (userIdCurrent != null && nameCurrent != null) {
                                ChatPage(
                                    currentUser = User(userId = userIdCurrent, fullName = nameCurrent),
                                    otherUser = User(userId = userIdGuest, fullName = nameGuest),
                                    navController = navController,
                                )
                            }
                        }

                        composable(route = AppPage.ChatListPage.route) {
                            val userIdCurrent = FirebaseAuth.getInstance().currentUser?.uid
                            val nameCurrent = googleAuthUiClient.getLoggedInUser()?.fullName
                            val profilePictureUrl = googleAuthUiClient.getLoggedInUser()?.profilePictureUrl
                            val state by chatViewModel.state.collectAsState()
                            if (userIdCurrent != null && profilePictureUrl != null && nameCurrent != null) {
                                ChatListPage(
                                    currentUser = User(userId = userIdCurrent, fullName = nameCurrent, profilePictureUrl = profilePictureUrl),
                                    state,
                                    navController = navController,
                                    chatViewModel = chatViewModel
                                )
                            }
                        }

                        composable(route = AppPage.ProfilePage.route) {
                            ProfilePage(navController = navController, userData = googleAuthUiClient.getLoggedInUser(), favoriteViewModel, profileViewModel)
                        }

                        composable(
                            route = "${AppPage.UserProfilePage.route}/{userId}",
                            arguments = listOf(navArgument("userId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val userId = backStackEntry.arguments?.getString("userId") ?: ""
                            UserProfilePage(userId = userId,navController = navController, favoriteViewModel = favoriteViewModel)
                        }

                        composable(
                            route = "${AppPage.EditArticlePage.route}/{propertyId}",
                            arguments = listOf(navArgument("propertyId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val propertyId = backStackEntry.arguments?.getString("propertyId") ?: ""
                            EditArticlePage(navController = navController, propertyId = propertyId)
                        }
                    }
                }
            }
        }
    }
}