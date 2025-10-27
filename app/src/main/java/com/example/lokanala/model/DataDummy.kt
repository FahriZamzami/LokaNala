package com.example.lokanala.model

import com.example.lokanala.R
import com.example.lokanala.ui.navigation.Screen

val dummyProducts = listOf(
    Product(
        id = 1,
        name = "Seblak Level 5",
        description = "Seblak Khas Bandung isi kerupuk, sosis, dan seafood.",
        price = "Rp 15.000",
        rating = 4.7,
        reviewCount = 60,
        imageRes = R.drawable.img_seblak_level_5,
        imageResDetail = R.drawable.img_seblak_detail
    ),
    Product(
        id = 2,
        name = "Spesial Komplit",
        description = "Kombinasi kerupuk, sosis, bakso, ceker, dan telur dengan pilihan (level 1-5).",
        price = "Rp 17.000",
        rating = 4.6,
        reviewCount = 75,
        imageRes = R.drawable.img_spesial_komplit,
        imageResDetail = R.drawable.img_spesial_komplit // Placeholder
    ),
    Product(
        id = 3,
        name = "Seblak Ceker Pedas",
        description = "Kuah kental berpadu dengan ceker ayam empuk yang dimasak hingga meresap.",
        price = "Rp 20.000",
        rating = 4.5,
        reviewCount = 70,
        imageRes = R.drawable.img_seblak_ceker,
        imageResDetail = R.drawable.img_seblak_ceker // Placeholder
    )
)

val dummyReviews = listOf(
    Review(
        id = 1,
        name = "Ratna Solihin",
        rating = 5,
        date = "10 Oktober 2025",
        comment = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nulla sem lectus, mattis eu justo sed, maximus faucibus lectus. Phasellus suscipit risus quis diam fermentum, non varius velit tincidunt. Suspendisse potenti.",
        profilePicRes = R.drawable.img_ratna_solihin
    ),
    Review(
        id = 2,
        name = "Bambang Wijoyo",
        rating = 4,
        date = "9 Oktober 2025",
        comment = "Enak dan murah, tapi sayang tempatnya agak kecil. Tapi untuk rasa, juara!",
        profilePicRes = R.drawable.ic_launcher_background // Placeholder
    )
)

// PERBAIKAN: Menyesuaikan data dummy dengan model Promo yang lengkap
val dummyPromos = listOf(
    Promo(
        id = 1,
        title = "PAKET SEBLAK KOMPLIT + ES TEH",
        dateRange = "8 October 2025 - 15 October 2025",
        newPrice = "Rp 18.000",
        oldPrice = "Rp 22.000",
        imageResDetail = R.drawable.img_promo_seblak_detail,
        termsAndConditions = listOf(
            "promo paket makan hemat",
            "Hanya berlaku untuk makan di tempat",
            "1 orang hanya bisa membeli 1 paket dalam sekali pembelian",
            "promo berlaku selama waktu yan berlaku......."
        ),
        howToUse = listOf(
            "Masuk ke dalam halaman 'Promo'",
            "Pilih promo yang mau kamu pakai",
            "Tunjukan halaman promo di aplikasi aplikasi ke kasir terkait promo yang akan kamu order ...."
        )
    ),
    Promo(
        id = 2,
        title = "Discount 15% for All Item",
        dateRange = "8 September 2025 - 15 September 2025",
        imageResDetail = R.drawable.img_promo_seblak_detail, // Placeholder
        termsAndConditions = listOf("Syarat 1", "Syarat 2"),
        howToUse = listOf("Cara 1", "Cara 2")
    ),
    Promo(
        id = 3,
        title = "Discount 10% for All Item",
        dateRange = "1 September 2025 - 3 September 2025",
        imageResDetail = R.drawable.img_promo_seblak_detail, // Placeholder
        termsAndConditions = listOf("Syarat 1", "Syarat 2"),
        howToUse = listOf("Cara 1", "Cara 2")
    ),
    Promo(
        id = 4,
        title = "Discount 50% for All Item",
        dateRange = "15 August 2025 - 19 August 2025",
        hasMoreOptions = true,
        imageResDetail = R.drawable.img_promo_seblak_detail, // Placeholder
        termsAndConditions = listOf("Syarat 1", "Syarat 2"),
        howToUse = listOf("Cara 1", "Cara 2")
    ),
    Promo(
        id = 5,
        title = "Discount 5% for All Item",
        dateRange = "1 August 2025 - 7 August 2025",
        imageResDetail = R.drawable.img_promo_seblak_detail, // Placeholder
        termsAndConditions = listOf("Syarat 1", "Syarat 2"),
        howToUse = listOf("Cara 1", "Cara 2")
    ),
    Promo(
        id = 6,
        title = "Discount 10% for All Item",
        dateRange = "8 June 2025 - 15 June 2025",
        imageResDetail = R.drawable.img_promo_seblak_detail, // Placeholder
        termsAndConditions = listOf("Syarat 1", "Syarat 2"),
        howToUse = listOf("Cara 1", "Cara 2")
    ),
    Promo(
        id = 7,
        title = "Discount 10% for All Item",
        dateRange = "1 June 2025 - 7 June 2025",
        imageResDetail = R.drawable.img_promo_seblak_detail, // Placeholder
        termsAndConditions = listOf("Syarat 1", "Syarat 2"),
        howToUse = listOf("Cara 1", "Cara 2")
    )
)

val dummyUmkmList = listOf(
    Umkm(
        id = 1,
        name = "Embun Coffee Space",
        rating = 3.8,
        tag = "Katalog",
        imageRes = R.drawable.img_embun_coffee,
        navigationRoute = Screen.Merchant.route
    ),
    Umkm(
        id = 2,
        name = "Seblak sendik",
        rating = 4.5,
        tag = "Katalog",
        imageRes = R.drawable.img_seblak_sendik_card,
        navigationRoute = Screen.Merchant.route // Ini rute yang benar
    ),
    Umkm(
        id = 3,
        name = "Warteg Bahari",
        rating = 4.7,
        tag = "Katalog",
        imageRes = R.drawable.img_warteg_bahari,
        navigationRoute = Screen.Merchant.route
    ),
    Umkm(
        id = 4,
        name = "Sate Madura",
        rating = 4.5,
        tag = "Katalog",
        imageRes = R.drawable.img_sate_madura,
        navigationRoute = Screen.Merchant.route
    ),
    Umkm(
        id = 5,
        name = "sanjay nitta", // Ganti sesuai nama
        rating = 4.2,
        tag = "Katalog",
        imageRes = R.drawable.img_sanjay_nitta,
        navigationRoute = Screen.Merchant.route
    ),
    Umkm(
        id = 6,
        name = "minang wear", // Ganti sesuai nama
        rating = 4.5,
        tag = "Katalog",
        imageRes = R.drawable.img_minang_wear,
        navigationRoute = Screen.Merchant.route
    )
)
val dummyMyUmkmList = listOf(
    MyUmkm(
        id = 1,
        name = "Sanjay Nitta",
        rating = 3.8,
        imageRes = R.drawable.img_sanjay_nitta
    ),
    MyUmkm(
        id = 2,
        name = "Seblak sendik",
        rating = 3.8, // Rating di gambar berbeda dari sebelumnya, gunakan ini
        imageRes = R.drawable.img_seblak_sendik_card
    )
)
val dummyNotifications = listOf(
    NotificationItem(
        id = 1,
        title = "ULASAN BARU",
        description = "Ada ulasan baru untuk produk",
        productName = "Seblak Spesial Komplit"
    ),
    NotificationItem(
        id = 2,
        title = "ULASAN BARU",
        description = "Ada ulasan baru untuk produk",
        productName = "Seblak Ceker Pedas"
    ),
    NotificationItem(
        id = 3,
        title = "ULASAN BARU",
        description = "Ada ulasan baru untuk produk",
        productName = "Seblak Level 5"
    )
)
