package com.elfen.redfun.ui.screens

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.annotation.IntegerRes
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import com.elfen.redfun.ANIM_DURATION_MILLIS
import com.elfen.redfun.R
import com.elfen.redfun.data.local.dataStore
import com.elfen.redfun.ui.screens.auth.AuthRoute
import com.elfen.redfun.ui.screens.auth.AuthScreen
import com.elfen.redfun.ui.screens.home.HomeRoute
import com.elfen.redfun.ui.screens.home.HomeScreen
import com.elfen.redfun.ui.screens.home.HomeViewModel
import com.elfen.redfun.ui.screens.login.LoginRoute
import com.elfen.redfun.ui.screens.login.LoginScreen
import com.elfen.redfun.ui.screens.post.PostRoute
import com.elfen.redfun.ui.screens.post.PostScreen
import com.elfen.redfun.ui.screens.profile.ProfileRoute
import com.elfen.redfun.ui.screens.profile.ProfileScreen
import com.elfen.redfun.ui.screens.saved.SavedRoute
import com.elfen.redfun.ui.screens.saved.SavedScreen
import com.elfen.redfun.ui.screens.search.SearchRoute
import com.elfen.redfun.ui.screens.sessions.SessionRoute
import com.elfen.redfun.ui.screens.sessions.SessionScreen
import com.elfen.redfun.ui.screens.subreddit.SubredditRoute
import com.elfen.redfun.ui.screens.subreddit.SubredditScreen
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import kotlin.math.log
import kotlin.math.roundToInt

enum class Destination(val label: String, val route: Any, @DrawableRes val icon: Int) {
    HOME("Home", HomeRoute, R.drawable.outline_home_24),
    SEARCH("Search", SearchRoute, R.drawable.outline_search_24),
    SAVED("Saved", SavedRoute, R.drawable.outline_bookmarks_24),
    PROFILE("Profile", ProfileRoute, R.drawable.outline_person_24)
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Navigation() {
    val navController = rememberNavController()
    val spec = remember { tween<Float>(ANIM_DURATION_MILLIS, easing = FastOutSlowInEasing) }
    val specInt = remember { tween<IntOffset>(ANIM_DURATION_MILLIS, easing = FastOutSlowInEasing) }
    val context = LocalContext.current
    val dataStore = context.dataStore
    val session =
        runBlocking { dataStore.data.map { it[stringPreferencesKey("session_id")] }.first() }


    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                Destination.entries.forEach { destination ->
                    NavigationBarItem(
                        alwaysShowLabel = false,
                        label = {
                            Text(
                                text = destination.label,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        icon = {
                            Icon(painterResource(destination.icon), destination.label)
                        },
                        selected = navController.currentDestination?.hasRoute(destination.route::class) == true,
                        onClick = {
                            if (currentDestination?.route != destination.route) {
                                navController.navigate(destination.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = if (session == null) LoginRoute else HomeRoute,
            enterTransition = {
                fadeIn() + slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(ANIM_DURATION_MILLIS),
                ) + scaleIn(initialScale = 0.9f)
            },
            exitTransition = {
                fadeOut(animationSpec = tween(ANIM_DURATION_MILLIS)) + scaleOut(
                    targetScale = 0.75f
                )
            },
            popEnterTransition = {
                fadeIn(animationSpec = spec) + scaleIn(
                    initialScale = 0.9f, animationSpec = spec
                ) + slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    initialOffset = { (it * 0.15f).roundToInt() },
                    animationSpec = specInt
                )
            },
            popExitTransition = {
                fadeOut(animationSpec = spec) + slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    targetOffset = { (it * 0.15f).roundToInt() },
                    animationSpec = specInt
                ) + scaleOut(
                    targetScale = 0.85f, animationSpec = spec
                )
            },
        ) {
            composable<LoginRoute> {
                LoginScreen(navController)
            }
            composable<HomeRoute> {
                val viewModel = hiltViewModel<HomeViewModel>()

                HomeScreen(navController, viewModel)
            }
            composable<SearchRoute> {
                Text("Search Screen", modifier = Modifier.padding(16.dp))
            }
            composable<ProfileRoute> {
                ProfileScreen(
                    onNavigate = navController::navigate
                )
            }
            composable<SessionRoute> {
                SessionScreen(navController)
            }
            composable<PostRoute> {
                PostScreen(navController)
            }
            composable<SavedRoute> {
                SavedScreen(navController)
            }
            composable<SubredditRoute> {
                SubredditScreen(navController = navController)
            }
            composable<AuthRoute>(
                deepLinks = listOf(
                    navDeepLink {
                        uriPattern = "redfun://auth?code={code}"
                        action = Intent.ACTION_VIEW
                    })
            ) {
                AuthScreen(navController)
            }
        }
    }
}