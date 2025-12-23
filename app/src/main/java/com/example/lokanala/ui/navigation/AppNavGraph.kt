package com.example.lokanala.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.example.lokanala.ui.screen.editumkm.EditUmkmScreen
import com.example.lokanala.ui.screen.category.CategoryScreen
import com.example.lokanala.ui.screen.category.CategoryViewModel
import com.example.lokanala.ui.screen.detail.DetailScreen
import com.example.lokanala.ui.screen.detail.UmkmDetailScreen
import com.example.lokanala.ui.screen.edit_merchant_product.EditProductScreen
import com.example.lokanala.ui.screen.edit_promotion_umkm.EditPromoViewModel
import com.example.lokanala.ui.screen.edit_promotion_umkm.EditPromotionScreen
import com.example.lokanala.ui.screen.home.HomeScreen
import com.example.lokanala.ui.screen.login.LoginScreen
import com.example.lokanala.ui.screen.merchant.MerchantScreen
import com.example.lokanala.ui.screen.my_merchant.MyMerchantScreen
import com.example.lokanala.ui.screen.my_merchant.MyMerchantViewModel
import com.example.lokanala.ui.screen.myumkm.MyUmkmScreen
import com.example.lokanala.ui.screen.myumkm.MyUmkmViewModel
import com.example.lokanala.ui.screen.notification.NotificationScreen
import com.example.lokanala.ui.screen.profile.ProfileScreen
import com.example.lokanala.ui.screen.profile.ProfileViewModel
import com.example.lokanala.ui.screen.promo.PromoScreen
import com.example.lokanala.ui.screen.promotion_umkm.MyUMKMPromotionScreen
import com.example.lokanala.ui.screen.promotion_umkm.PromotionViewModel
import com.example.lokanala.ui.screen.rating.RatingScreen
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import okhttp3.internal.platform.android.AndroidSocketAdapter.Companion.factory
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.lokanala.ui.screen.following.FollowingScreen
import com.example.lokanala.ui.screen.register.RegisterScreen

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppNavGraph(
    navController: NavHostController,
    userPreference: UserPreference
) {
    val context = LocalContext.current

    val promotionViewModel: PromotionViewModel = viewModel()
    val categoryViewModel: CategoryViewModel = viewModel()
    val myMerchantViewModel: MyMerchantViewModel = viewModel()
    val myUmkmViewModel: MyUmkmViewModel = viewModel(
        factory = ViewModelFactory.getInstance(context)
    )

    val isLoggedIn = userPreference.isLoggedIn().collectAsState(initial = false)
    val startDest = if (isLoggedIn.value) Screen.Home.route else Screen.Login.route

    AnimatedNavHost(
        navController = navController,
        startDestination = startDest,
        enterTransition = { fadeIn(animationSpec = tween(500)) },
        exitTransition = { fadeOut(animationSpec = tween(500)) },
        popEnterTransition = { fadeIn(animationSpec = tween(500)) },
        popExitTransition = { fadeOut(animationSpec = tween(500)) }
    ) {

        
        composable(Screen.Login.route) {
            LoginScreen(navController = navController)
        }

        composable(Screen.Register.route) {
            RegisterScreen(navController = navController)
        }

        
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }

        
        composable(
            route = Screen.Merchant.route,
            arguments = listOf(navArgument("umkmId") { type = NavType.LongType })
        ) {
            MerchantScreen(
                navController = navController,
                umkmId = it.arguments?.getLong("umkmId") ?: -1L
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

        composable(Screen.Following.route) {
            FollowingScreen(navController = navController)
        }

        
        composable(Screen.Notification.route) {
            NotificationScreen(
                navController = navController,
                onBack = { navController.popBackStack() }
            )
        }

        
        composable(Screen.MyUmkm.route) {
            MyUmkmScreen(
                navController = navController,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.AddUmkm.route) {
            AddUmkmScreen(
                navController = navController,
                onBack = { navController.popBackStack() }
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
                umkmId = umkmId
            )
        }

        
        composable(
            route = Screen.EditProduct.route,
            arguments = listOf(
                navArgument("umkmId") { type = NavType.IntType },
                navArgument("productId") { type = NavType.IntType }
            )
        ) {
            EditProductScreen(
                navController = navController,
                umkmId = it.arguments?.getInt("umkmId") ?: return@composable,
                productId = it.arguments?.getInt("productId") ?: return@composable,
                myMerchantViewModel = myMerchantViewModel
            )
        }

        
        composable(
            route = Screen.Promotion.route,
            arguments = listOf(navArgument("umkmId") { type = NavType.IntType })
        ) {
            MyUMKMPromotionScreen(
                navController = navController,
                viewModel = promotionViewModel,
                umkmId = it.arguments?.getInt("umkmId") ?: -1
            )
        }

        composable(
            route = Screen.AddPromotion.route,
            arguments = listOf(navArgument("umkmId") { type = NavType.IntType })
        ) {
            AddPromotionScreen(
                navController = navController,
                addPromoViewModel = remember { AddPromoViewModel() },
                umkmId = it.arguments?.getInt("umkmId") ?: -1
            )
        }

        composable(
            route = Screen.EditPromotion.route,
            arguments = listOf(
                navArgument("promoId") { type = NavType.IntType },
                navArgument("umkmId") { type = NavType.IntType }
            )
        ) {
            EditPromotionScreen(
                navController = navController,
                promotionId = it.arguments?.getInt("promoId") ?: -1,
                umkmId = it.arguments?.getInt("umkmId") ?: -1,
                promotionViewModel = promotionViewModel,
                editPromoViewModel = remember { EditPromoViewModel() }
            )
        }

        
        composable(
            route = Screen.Rating.route,
            arguments = listOf(navArgument("productId") { type = NavType.IntType })
        ) {
            RatingScreen(navController = navController)
        }

        
        composable(
            route = Screen.Promo.route,
            arguments = listOf(navArgument("umkmId") { type = NavType.LongType })
        ) {
            PromoScreen(
                navController = navController,
                umkmId = it.arguments?.getLong("umkmId") ?: -1L
            )
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
            route = Screen.ManageCategory.route, 
            arguments = listOf(navArgument("umkmId") { type = NavType.IntType })
        ) { backStackEntry ->
            val umkmId = backStackEntry.arguments?.getInt("umkmId") ?: return@composable
            CategoryScreen(
                navController = navController,
                umkmId = umkmId,
                viewModel = categoryViewModel,
                myMerchantViewModel = myMerchantViewModel
            )
        }

        composable(
            route = Screen.EditUmkm.route,
            arguments = listOf(navArgument("umkmId") { type = NavType.IntType })
        ) { backStackEntry ->
            val umkmId = backStackEntry.arguments?.getInt("umkmId") ?: return@composable

            EditUmkmScreen(
                idUmkm = umkmId, 
                onBack = { navController.popBackStack() },
                navController = navController
            )
        }

        composable(
            route = "umkm_detail/{umkmId}",
            arguments = listOf(
                navArgument("umkmId") {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("umkmId") ?: -1L
            UmkmDetailScreen(
                navController = navController,
                umkmId = id
            )
        }
    }
}