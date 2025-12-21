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

    // ✅ HASIL MERGE DARI KODINGAN KEDUA
    object EditProduct : Screen("edit_product/{umkmId}/{productId}") {
        fun createRoute(umkmId: Int, productId: Int) =
            "edit_product/$umkmId/$productId"
    }

    object MyUmkm : Screen("my_umkm")
    object AddUmkm : Screen("add_umkm")

    object Promotion : Screen("promotion/{umkmId}") {
        fun createRoute(umkmId: Int) = "promotion/$umkmId"
    }

    object AddPromotion : Screen("add_promotion/{umkmId}") {
        fun createRoute(umkmId: Int) = "add_promotion/$umkmId"
    }

    object EditPromotion : Screen("edit_promotion/{promoId}/{umkmId}") {
        fun createRoute(promoId: Int, umkmId: Int) =
            "edit_promotion/$promoId/$umkmId"
    }

    object Rating : Screen("rating/{productId}") {
        fun createRoute(productId: Int) = "rating/$productId"
    }

    // ⭐ Route kamera review
    object AddPictureReview : Screen("add_picture_review")

    object Promo : Screen("promo")

    object PromoDetail : Screen("promo_detail/{promoId}") {
        fun createRoute(promoId: Int) = "promo_detail/$promoId"
    }

    object Detail : Screen("detail/{productId}") {
        fun createRoute(productId: Int) = "detail/$productId"
    }

    object Category : Screen("category/{umkmId}") {
        fun createRoute(umkmId: Int) = "category/$umkmId"
    }

    object ManageCategory : Screen("manage_category/{umkmId}") {
        fun createRoute(umkmId: Int) = "manage_category/$umkmId"
    }
}