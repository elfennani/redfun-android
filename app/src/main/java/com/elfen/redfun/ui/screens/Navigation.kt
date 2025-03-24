package com.elfen.redfun.ui.screens

import android.content.Intent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import com.elfen.redfun.ANIM_DURATION_MILLIS
import com.elfen.redfun.data.local.dataStore
import com.elfen.redfun.ui.screens.auth.AuthRoute
import com.elfen.redfun.ui.screens.auth.AuthScreen
import com.elfen.redfun.ui.screens.home.HomeRoute
import com.elfen.redfun.ui.screens.home.HomeScreen
import com.elfen.redfun.ui.screens.login.LoginRoute
import com.elfen.redfun.ui.screens.login.LoginScreen
import com.elfen.redfun.ui.screens.post.PostRoute
import com.elfen.redfun.ui.screens.post.PostScreen
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import kotlin.math.roundToInt

@Composable
fun Navigation() {
    val navController = rememberNavController()
    val spec = remember { tween<Float>(ANIM_DURATION_MILLIS, easing = FastOutSlowInEasing) }
    val specInt = remember { tween<IntOffset>(ANIM_DURATION_MILLIS, easing = FastOutSlowInEasing) }
    val context = LocalContext.current
    val dataStore = context.dataStore
    val session =
        runBlocking { dataStore.data.map { it[stringPreferencesKey("session_id")] }.first() }


    Surface {
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
                fadeIn(animationSpec = spec) +
                        scaleIn(
                            initialScale = 0.9f,
                            animationSpec = spec
                        ) +
                        slideIntoContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Right,
                            initialOffset = { (it * 0.15f).roundToInt() },
                            animationSpec = specInt
                        )
            },
            popExitTransition = {
                fadeOut(animationSpec = spec) +
                        slideOutOfContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Right,
                            targetOffset = { (it * 0.15f).roundToInt() },
                            animationSpec = specInt
                        ) +
                        scaleOut(
                            targetScale = 0.85f,
                            animationSpec = spec
                        )
            },
        ) {
            composable<LoginRoute> {
                LoginScreen(navController)
            }
            composable<HomeRoute> {
                HomeScreen(navController)
            }
            composable<PostRoute> {
                PostScreen(navController)
            }
            composable<AuthRoute>(
                deepLinks = listOf(
                    navDeepLink {
                        uriPattern = "redfun://auth?code={code}"
                        action = Intent.ACTION_VIEW
                    }
                )
            ) {
                AuthScreen(navController)
            }
        }
    }
}