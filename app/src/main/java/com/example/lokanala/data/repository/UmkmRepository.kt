package com.example.lokanala.data.repository

import android.content.Context
import android.net.Uri
import com.example.lokanala.MyApp
import com.example.lokanala.data.remote.api.RetrofitClient
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class UmkmRepository {

    private val api = RetrofitClient.umkmApi

    suspend fun addUMKM(
        idUser: Int,
        idKategori: Int,
        nama: String,
        alamat: String,
        noTelp: String,
        deskripsi: String,
        linkLokasi: String,
        imageUri: Uri?
    ): String {

        val context = MyApp.appContext

        // Convert URI → File
        val imageFile = imageUri?.let { uriToFile(context, it) }

        // FORM DATA (multipart/form-data)
        val parts = mutableMapOf<String, RequestBody>()

        val text = "text/plain".toMediaType()

        parts["id_user"] = idUser.toString().toRequestBody(text)
        parts["id_kategori_umkm"] = idKategori.toString().toRequestBody(text)
        parts["nama_umkm"] = nama.toRequestBody(text)
        parts["alamat"] = alamat.toRequestBody(text)
        parts["no_telepon"] = noTelp.toRequestBody(text)
        parts["deskripsi"] = deskripsi.toRequestBody(text)
        parts["link_lokasi"] = linkLokasi.toRequestBody(text)

        // IMAGE PART
        val imagePart = imageFile?.let {
            val req = it.asRequestBody("image/*".toMediaType())
            MultipartBody.Part.createFormData("gambar", it.name, req)
        }

        // SEND
        val response = api.addUMKM(parts, imagePart)

        if (!response.isSuccessful) {
            throw Exception("Gagal menambahkan UMKM : ${response.errorBody()?.string()}")
        }

        return "UMKM berhasil ditambahkan"
    }

    private fun uriToFile(context: Context, uri: Uri): File {
        val inputStream = context.contentResolver.openInputStream(uri)
        val tempFile = File(context.cacheDir, "upload_${System.currentTimeMillis()}.jpg")

        inputStream?.use { input ->
            tempFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }

        return tempFile
    }


    suspend fun getMyUmkm(idUser: Int) = api.getMyUmkm(idUser)

    suspend fun deleteUmkm(idUmkm: Int): Boolean {
        val response = api.deleteUmkm(idUmkm)
        if (!response.isSuccessful) {
            val err = response.errorBody()?.string()
            throw Exception("Gagal menghapus UMKM: $err")
        }
        return true
    }
}