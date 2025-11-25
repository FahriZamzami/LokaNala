package com.example.lokanala.ui.screen.category

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import java.util.UUID

// Data class untuk Kategori
data class Category(
    val id: String = UUID.randomUUID().toString(),
    var name: String,
    var description: String
)

class CategoryViewModel : ViewModel() {

    // Daftar Kategori (bisa diobservasi oleh Composable)
    var categories = mutableStateListOf<Category>()
        private set

    init {
        // Data dummy awal
        categories.addAll(listOf(
            Category(name = "Makanan", description = "Semua jenis makanan utama"),
            Category(name = "Minuman", description = "Kopi, teh, jus, dll."),
            Category(name = "Paket Hemat", description = "Paket bundling makanan dan minuman")
        ))
    }

    fun addCategory(name: String, description: String) {
        if (name.isBlank()) return // Validasi sederhana
        categories.add(Category(name = name, description = description))
    }

    fun updateCategory(id: String, newName: String, newDescription: String) {
        val index = categories.indexOfFirst { it.id == id }
        if (index != -1) {
            categories[index] = categories[index].copy(
                name = newName,
                description = newDescription
            )
        }
    }

    fun deleteCategory(category: Category) {
        categories.remove(category)
    }
}