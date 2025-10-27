package com.example.lokanala.ui.screen.promotion_umkm

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel

data class Promotion(
    val id: Int,
    var title: String,
    var detail: String,
    var startDate: String,
    var endDate: String
)

class PromotionViewModel : ViewModel() {
    // daftar promo (mutable agar bisa diubah langsung)
    var promotions = mutableStateListOf(
        Promotion(1, "Discount 30% for All Item", "1. Berlaku untuk semua produk di toko offline.\n2. Tidak dapat digabung dengan promo lain.", "8 October 2025", "15 October 2025"),
        Promotion(2, "Discount 15% for Member", "1. Hanya untuk member aktif.\n2. Wajib menunjukkan kartu member.", "10 November 2025", "15 November 2025"),
        Promotion(3, "Buy 1 Get 1 Free", "1. Berlaku untuk produk makanan ringan.\n2. Tidak berlaku untuk pembelian online.", "1 December 2025", "7 December 2025"),
        Promotion(4, "Free Delivery for Orders Above 100k", "1. Berlaku hanya untuk wilayah Jabodetabek.\n2. Minimal transaksi Rp100.000.", "20 October 2025", "30 October 2025"),
        Promotion(5, "Cashback 20% via QRIS", "1. Maksimal cashback Rp20.000.\n2. Berlaku hanya untuk pembayaran menggunakan QRIS.", "5 November 2025", "12 November 2025"),
        Promotion(6, "Discount 25% for New Customers", "1. Hanya untuk pelanggan baru.\n2. Berlaku untuk transaksi pertama kali.", "1 October 2025", "31 October 2025"),
        Promotion(7, "Weekend Special: 40% Off", "1. Berlaku setiap Sabtu dan Minggu.\n2. Tidak berlaku untuk produk diskon lainnya.", "1 November 2025", "30 November 2025"),
        Promotion(8, "Free Tote Bag for Purchases Above 200k", "1. Persediaan terbatas.\n2. Berlaku selama stok masih ada.", "10 October 2025", "10 December 2025"),
        Promotion(9, "Discount 10% for Online Orders", "1. Berlaku di aplikasi Lokanala.\n2. Minimal transaksi Rp50.000.", "15 November 2025", "25 November 2025"),
        Promotion(10, "Flash Sale 50%", "1. Berlaku pukul 12.00 - 15.00 WIB.\n2. Hanya untuk produk terpilih.", "20 November 2025", "20 November 2025")
    )
        private set

    // update promo
    fun updatePromotion(updated: Promotion) {
        val index = promotions.indexOfFirst { it.id == updated.id }
        if (index != -1) promotions[index] = updated
    }

    // hapus promo
    fun deletePromotion(id: Int) {
        promotions.removeAll { it.id == id }
    }

    // ambil promo berdasarkan id
    fun getPromotionById(id: Int): Promotion? = promotions.find { it.id == id }
}

object MonthConverter {
    private val months = listOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )

    fun getMonthName(index: Int): String = months[index]

    fun getMonthIndex(name: String): Int =
        months.indexOfFirst { it.equals(name, ignoreCase = true) }.takeIf { it >= 0 } ?: 0
}