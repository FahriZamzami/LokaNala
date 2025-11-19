package com.example.lokanala.ui.navigation

sealed class Screen(val route: String) {

    object Login : Screen("login")

    object Home : Screen("home")
    object Profile : Screen("profile")
    object Notification : Screen("notification")

    object Merchant : Screen("merchant/{umkmId}") {
        fun createRoute(umkmId: Long) = "merchant/$umkmId"
    }

    object MyMerchant : Screen("my_merchant/{umkmId}") {
        fun createRoute(umkmId: Int) = "my_merchant/$umkmId"
    }

    object MyUmkmCatalog : Screen("my_umkm_catalog/{umkmId}") {
        fun createRoute(umkmId: Int) = "my_umkm_catalog/$umkmId"
    }

    object AddProduct : Screen("add_product/{umkmId}") {
        fun createRoute(umkmId: Int) = "add_product/$umkmId"
    }

    object MyUmkm : Screen("my_umkm")
    object AddUmkm : Screen("add_umkm")

    object Promotion : Screen("promotion")
    object AddPromotion : Screen("add_promotion")
    object EditPromotion : Screen("edit_promotion/{id}") {
        fun createRoute(id: Int) = "edit_promotion/$id"
    }

    object Rating : Screen("rating/{productId}") {
        fun createRoute(productId: Int) = "rating/$productId"
    }

    // ⭐ TAMBAHKAN INI - Route untuk kamera review
    object AddPictureReview : Screen("add_picture_review")

    object Promo : Screen("promo")
    object PromoDetail : Screen("promo_detail/{promoId}") {
        fun createRoute(promoId: Int) = "promo_detail/$promoId"
    }

    object Detail : Screen("detail/{productId}") {
        fun createRoute(productId: Int) = "detail/$productId"
    }
}