package com.example.lokanala.ui.navigation

sealed class Screen(val route: String) {
    // Menu utama
    object Home : Screen("home")
    object Merchant : Screen("merchant")
    object Profile : Screen("profile")
    object Notification : Screen("notification")

    // UMKM
    object MyUmkm : Screen("my_umkm")
    object AddUmkm : Screen("add_umkm")

    // Promotion
    object Promotion : Screen("promotion")
    object AddPromotion : Screen("add_promotion")
    object EditPromotion : Screen("edit_promotion/{id}") {
        fun createRoute(id: Int) = "edit_promotion/$id"
    }

    // Rating
    object Rating : Screen("rating")

    // Promo
    object Promo : Screen("promo")
    object PromoDetail : Screen("promo_detail/{promoId}") {
        fun createRoute(promoId: Int) = "promo_detail/$promoId"
    }

    // Produk detail
    object Detail : Screen("detail/{productId}") {
        fun createRoute(productId: Int) = "detail/$productId"
    }
}
