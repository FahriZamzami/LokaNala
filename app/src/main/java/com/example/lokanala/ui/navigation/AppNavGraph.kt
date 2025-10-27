package com.example.lokanala.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.lokanala.ui.screen.addumkm.AddUmkmScreen
import com.example.lokanala.ui.screen.add_promotion_umkm.AddPromotionScreen
import com.example.lokanala.ui.screen.detail.DetailScreen
import com.example.lokanala.ui.screen.edit_promotion_umkm.EditPromotionScreen
import com.example.lokanala.ui.screen.home.HomeScreen
import com.example.lokanala.ui.screen.merchant.MerchantScreen
import com.example.lokanala.ui.screen.myumkm.MyUmkmScreen
import com.example.lokanala.ui.screen.notification.NotificationScreen
import com.example.lokanala.ui.screen.profile.ProfileScreen
import com.example.lokanala.ui.screen.promo.PromoScreen
import com.example.lokanala.ui.screen.promo_detail.PromoDetailScreen
import com.example.lokanala.ui.screen.promotion_umkm.MyUMKMPromotionScreen
import com.example.lokanala.ui.screen.promotion_umkm.PromotionViewModel
import com.example.lokanala.ui.screen.rating.RatingScreen
import com.example.lokanala.ui.screen.rating.RatingViewModel

@Composable
fun AppNavGraph(navController: NavHostController) {
    // ViewModel global agar tidak reinit setiap navigasi
    val promotionViewModel: PromotionViewModel = viewModel()
    val ratingViewModel: RatingViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        /* ================================
           🏠 HOME & MENU UTAMA
        ================================ */
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }

        composable(Screen.Merchant.route) {
            MerchantScreen(navController = navController)
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

        /* ================================
           🏬 MY UMKM + ADD UMKM
        ================================ */
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

        /* ================================
           🎯 PROMOTION & RATING
        ================================ */
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

        composable(Screen.Rating.route) {
            RatingScreen(
                navController = navController,
                viewModel = ratingViewModel
            )
        }

        /* ================================
           🛍️ PROMO & DETAIL PRODUK
        ================================ */
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
                navController = navController, // ✅ kirim navController
                onBack = { navController.popBackStack() }
            )
        }
    }
}
