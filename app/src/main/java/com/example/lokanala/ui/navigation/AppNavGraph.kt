package com.example.lokanala.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.lokanala.data.pref.UserPreference
import com.example.lokanala.data.pref.dataStore
import com.example.lokanala.ui.ViewModelFactory
import com.example.lokanala.ui.screen.add_merchant_product.AddProductScreen
import com.example.lokanala.ui.screen.add_promotion_umkm.AddPromoViewModel
import com.example.lokanala.ui.screen.addumkm.AddUmkmScreen
import com.example.lokanala.ui.screen.add_promotion_umkm.AddPromotionScreen
import com.example.lokanala.ui.screen.category.CategoryScreen
import com.example.lokanala.ui.screen.category.CategoryViewModel
import com.example.lokanala.ui.screen.detail.DetailScreen
import com.example.lokanala.ui.screen.detail.UmkmDetailScreen
import com.example.lokanala.ui.screen.edit_promotion_umkm.EditPromoViewModel
import com.example.lokanala.ui.screen.edit_promotion_umkm.EditPromotionScreen
import com.example.lokanala.ui.screen.home.HomeScreen
import com.example.lokanala.ui.screen.login.LoginScreen
import com.example.lokanala.ui.screen.merchant.MerchantScreen
import com.example.lokanala.ui.screen.my_merchant.MyMerchantScreen
import com.example.lokanala.ui.screen.my_merchant.MyMerchantViewModel
import com.example.lokanala.ui.screen.myumkm.MyUmkmScreen
import com.example.lokanala.ui.screen.notification.NotificationScreen
import com.example.lokanala.ui.screen.profile.ProfileScreen
import com.example.lokanala.ui.screen.profile.ProfileViewModel
import com.example.lokanala.ui.screen.promo.PromoScreen
import com.example.lokanala.ui.screen.promo_detail.PromoDetailScreen
import com.example.lokanala.ui.screen.promotion_umkm.MyUMKMPromotionScreen
import com.example.lokanala.ui.screen.promotion_umkm.PromotionViewModel
import com.example.lokanala.ui.screen.rating.RatingScreen
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppNavGraph(navController: NavHostController, userPreference: UserPreference) {
    // PromotionViewModel dibiarkan di sini jika ingin dishare antar screen promosi
    val promotionViewModel: PromotionViewModel = viewModel()
    val categoryViewModel: CategoryViewModel = viewModel()
    val myMerchantViewModel: MyMerchantViewModel = viewModel()
    val isLoggedIn = userPreference.isLoggedIn().collectAsState(initial = false)
    val startDest = if (isLoggedIn.value) Screen.Home.route else Screen.Login.route

    // HAPUS: val ratingViewModel: RatingViewModel = viewModel()
    // Kita hapus agar RatingViewModel dibuat ulang setiap kali masuk halaman Rating
    // supaya bisa menangkap productId terbaru.

    AnimatedNavHost(
        navController = navController,
        startDestination = startDest,
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
            val context = LocalContext.current
            val factory = ViewModelFactory.getInstance(context)

            val profileViewModel: ProfileViewModel = viewModel(factory = factory)

            ProfileScreen(
                navController = navController,
                viewModel = profileViewModel
            )
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
                viewModel = myMerchantViewModel, // <-- Berikan ViewModel
                categoryViewModel = categoryViewModel
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
                categoryViewModel = categoryViewModel, // <-- Berikan CategoryViewModel
                myMerchantViewModel = myMerchantViewModel // <-- Berikan MyMerchantViewModel
            )
        }

        composable(
            route = Screen.Promotion.route,
            arguments = listOf(navArgument("umkmId") { type = NavType.IntType })
        ) { backStackEntry ->
            val umkmId = backStackEntry.arguments?.getInt("umkmId") ?: -1

            MyUMKMPromotionScreen(
                navController = navController,
                viewModel = promotionViewModel,
                umkmId = umkmId    // ⭐ KIRIM ID UMKM KE SCREEN ⭐
            )
        }

        composable(
            route = Screen.AddPromotion.route,
            arguments = listOf(navArgument("umkmId") { type = NavType.IntType })
        ) { backStackEntry ->
            val umkmId = backStackEntry.arguments?.getInt("umkmId") ?: -1
            val addPromoViewModel = remember { AddPromoViewModel() }

            AddPromotionScreen(
                navController = navController,
                addPromoViewModel = addPromoViewModel,
                umkmId = umkmId
            )
        }

        composable(
            route = Screen.EditPromotion.route,
            arguments = listOf(
                navArgument("promoId") { type = NavType.IntType },
                navArgument("umkmId") { type = NavType.IntType }
            )
        ) { backStackEntry ->

            val promoId = backStackEntry.arguments?.getInt("promoId") ?: -1
            val umkmId = backStackEntry.arguments?.getInt("umkmId") ?: -1

            val editPromoViewModel = remember { EditPromoViewModel() }

            EditPromotionScreen(
                navController = navController,
                promotionId = promoId,
                umkmId = umkmId,
                promotionViewModel = promotionViewModel,
                editPromoViewModel = editPromoViewModel
            )
        }

        // --- PERBAIKAN DI SINI ---
        composable(
            route = Screen.Rating.route, // "rating/{productId}"
            arguments = listOf(navArgument("productId") { type = NavType.IntType })
        ) {
            // Jangan oper viewModel dari luar.
            // Biarkan RatingScreen membuat ViewModel-nya sendiri agar mendapat SavedStateHandle yang benar.
            RatingScreen(
                navController = navController
                // viewModel akan diinisialisasi otomatis oleh RatingScreen
            )
        }
        // -------------------------

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