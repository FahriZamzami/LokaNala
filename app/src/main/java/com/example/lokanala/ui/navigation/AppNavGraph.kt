package com.example.lokanala.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.lokanala.ui.screen.add_merchant_product.AddProductScreen
import com.example.lokanala.ui.screen.addumkm.AddUmkmScreen
import com.example.lokanala.ui.screen.add_promotion_umkm.AddPromotionScreen
import com.example.lokanala.ui.screen.category.CategoryScreen
import com.example.lokanala.ui.screen.category.CategoryViewModel
import com.example.lokanala.ui.screen.detail.DetailScreen
import com.example.lokanala.ui.screen.detail.UmkmDetailScreen
import com.example.lokanala.ui.screen.edit_promotion_umkm.EditPromotionScreen
import com.example.lokanala.ui.screen.home.HomeScreen
import com.example.lokanala.ui.screen.login.LoginScreen
import com.example.lokanala.ui.screen.merchant.MerchantScreen
import com.example.lokanala.ui.screen.my_merchant.MyMerchantScreen
import com.example.lokanala.ui.screen.my_merchant.MyMerchantViewModel
import com.example.lokanala.ui.screen.myumkm.MyUmkmScreen
import com.example.lokanala.ui.screen.notification.NotificationScreen
import com.example.lokanala.ui.screen.profile.ProfileScreen
import com.example.lokanala.ui.screen.promo.PromoScreen
import com.example.lokanala.ui.screen.promo_detail.PromoDetailScreen
import com.example.lokanala.ui.screen.promotion_umkm.MyUMKMPromotionScreen
import com.example.lokanala.ui.screen.promotion_umkm.PromotionViewModel
import com.example.lokanala.ui.screen.rating.RatingScreen
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppNavGraph(navController: NavHostController) {
    // ViewModel instances
    val promotionViewModel: PromotionViewModel = viewModel()
    val categoryViewModel: CategoryViewModel = viewModel()
    val myMerchantViewModel: MyMerchantViewModel = viewModel()

    AnimatedNavHost(
        navController = navController,
        startDestination = Screen.Login.route,
        enterTransition = { fadeIn(animationSpec = tween(500)) },
        exitTransition = { fadeOut(animationSpec = tween(500)) },
        popEnterTransition = { fadeIn(animationSpec = tween(500)) },
        popExitTransition = { fadeOut(animationSpec = tween(500)) }
    )  {

        composable(Screen.Login.route) {
            LoginScreen(navController = navController)
        }

        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }

        composable(
            route = Screen.Merchant.route,
            arguments = listOf(navArgument("umkmId") { type = NavType.LongType })
        ) { backStackEntry ->
            val umkmId = backStackEntry.arguments?.getLong("umkmId") ?: -1L
            MerchantScreen(
                navController = navController,
                umkmId = umkmId
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(navController = navController)
        }

        composable(Screen.Notification.route) {
            NotificationScreen(
                onBack = { navController.popBackStack() },
                navController = navController
            )
        }

        composable(Screen.MyUmkm.route) {
            MyUmkmScreen(
                onBack = { navController.popBackStack() },
                navController = navController
            )
        }

        composable(Screen.AddUmkm.route) {
            AddUmkmScreen(
                onBack = { navController.popBackStack() },
                navController = navController
            )
        }

        composable(
            route = Screen.MyMerchant.route,
            arguments = listOf(navArgument("umkmId") { type = NavType.IntType })
        ) { backStackEntry ->
            val umkmId = backStackEntry.arguments?.getInt("umkmId") ?: return@composable
            MyMerchantScreen(
                navController = navController,
                umkmId = umkmId,
                viewModel = myMerchantViewModel
            )
        }

        composable(
            route = Screen.AddProduct.route,
            arguments = listOf(navArgument("umkmId") { type = NavType.IntType })
        ) { backStackEntry ->
            val umkmId = backStackEntry.arguments?.getInt("umkmId") ?: return@composable
            AddProductScreen(
                navController = navController,
                umkmId = umkmId,
                categoryViewModel = categoryViewModel,
                myMerchantViewModel = myMerchantViewModel
            )
        }

        composable(Screen.Promotion.route) {
            MyUMKMPromotionScreen(
                navController = navController,
                viewModel = promotionViewModel
            )
        }

        composable(Screen.AddPromotion.route) {
            AddPromotionScreen(
                navController = navController,
                promotionViewModel = promotionViewModel
            )
        }

        composable(
            route = Screen.EditPromotion.route,
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: return@composable
            EditPromotionScreen(
                navController = navController,
                promotionId = id,
                promotionViewModel = promotionViewModel
            )
        }

        composable(
            route = Screen.Rating.route,
            arguments = listOf(navArgument("productId") { type = NavType.IntType })
        ) {
            RatingScreen(
                navController = navController
            )
        }

        composable(Screen.Promo.route) {
            PromoScreen(
                onBack = { navController.popBackStack() },
                onPromoClick = { promoId ->
                    navController.navigate(Screen.PromoDetail.createRoute(promoId))
                }
            )
        }

        composable(
            route = Screen.PromoDetail.route,
            arguments = listOf(navArgument("promoId") { type = NavType.IntType })
        ) {
            PromoDetailScreen(onBack = { navController.popBackStack() })
        }

        composable(
            route = Screen.Detail.route,
            arguments = listOf(navArgument("productId") { type = NavType.IntType })
        ) {
            DetailScreen(
                navController = navController,
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = "detailscreen/{umkmId}",
            arguments = listOf(navArgument("umkmId") { type = NavType.LongType })
        ) { backStackEntry ->
            val umkmId = backStackEntry.arguments?.getLong("umkmId") ?: -1L
            UmkmDetailScreen(
                umkmId = umkmId,
                navController = navController
            )
        }

        composable(
            route = Screen.Category.route,
            arguments = listOf(navArgument("umkmId") { type = NavType.IntType })
        ) { backStackEntry ->
            val umkmId = backStackEntry.arguments?.getInt("umkmId") ?: return@composable
            CategoryScreen(
                navController = navController,
                umkmId = umkmId,
                viewModel = categoryViewModel
            )
        }
    }
}
