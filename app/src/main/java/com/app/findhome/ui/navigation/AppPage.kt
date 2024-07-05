package com.app.findhome.ui.navigation

sealed class AppPage(val route:String){
    data object LoginPage : AppPage("login_page")
    data object SignupPage : AppPage("signup_page")
    data object HomePage : AppPage("home_page")
    data object DetailPage : AppPage("details_page")
    data object FavoritePage : AppPage("favorites_page")
    data object ArticlePage : AppPage("article_page")
    data object ChatListPage : AppPage("chat_list_page")
    data object ChatPage : AppPage("chat_page")
    data object ProfilePage : AppPage("profile_page")
    data object UserProfilePage : AppPage("user_profile_page")
    data object EditArticlePage : AppPage("edit_article_page")
}
