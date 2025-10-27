package com.example.lokanala.ui.navigation

sealed class Screen(val route: String) {
    /* ================================
       🔐 AUTH
    ================================ */
    object Login : Screen("login")

    /* ================================
       🏠 MENU UTAMA
    ================================ */
    object Home : Screen("home")
    object Profile : Screen("profile")
    object Notification : Screen("notification")

    /* ================================
       🏬 MERCHANT (Customer & UMKM)
    ================================ */
    // Tampilan UMKM bagi customer
    object Merchant : Screen("merchant/{umkmId}") {
        fun createRoute(umkmId: Long) = "merchant/$umkmId"
    }

    // Tampilan katalog produk milik UMKM (pemilik)
    object MyMerchant : Screen("my_merchant/{umkmId}") {
        fun createRoute(umkmId: Int) = "my_merchant/$umkmId"
    }

    // Rute tambahan untuk katalog/produk UMKM
    object MyUmkmCatalog : Screen("my_umkm_catalog/{umkmId}") {
        fun createRoute(umkmId: Int) = "my_umkm_catalog/$umkmId"
    }

    // Tambah produk baru untuk UMKM
    object AddProduct : Screen("add_product/{umkmId}") {
        fun createRoute(umkmId: Int) = "add_product/$umkmId"
    }

    /* ================================
       🧾 UMKM MANAGEMENT
    ================================ */
    object MyUmkm : Screen("my_umkm")
    object AddUmkm : Screen("add_umkm")

    /* ================================
       🎯 PROMOTION & RATING
    ================================ */
    object Promotion : Screen("promotion")
    object AddPromotion : Screen("add_promotion")
    object EditPromotion : Screen("edit_promotion/{id}") {
        fun createRoute(id: Int) = "edit_promotion/$id"
    }

    object Rating : Screen("rating")

    /* ================================
       🛍️ PROMO & DETAIL PRODUK
    ================================ */
    object Promo : Screen("promo")
    object PromoDetail : Screen("promo_detail/{promoId}") {
        fun createRoute(promoId: Int) = "promo_detail/$promoId"
    }

    object Detail : Screen("detail/{productId}") {
        fun createRoute(productId: Int) = "detail/$productId"
    }
}
