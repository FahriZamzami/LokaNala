package com.example.lokanala.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.lokanala.model.MyUmkm

@Composable
fun MyUmkmCard(
    umkm: MyUmkm,
    onEditClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
    onMerchantClick: () -> Unit = {}
) {

    val baseUrl = "https://mlszfdzz-3000.asse.devtunnels.ms/uploads/"
    val imageUrl = baseUrl + umkm.gambar

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp),
        shape = RoundedCornerShape(16.dp),
        onClick = onMerchantClick
    ) {

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {

            // Gambar
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = umkm.nama_umkm,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            Text(
                text = umkm.no_telepon,
                fontSize = 14.sp,
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            Spacer(Modifier.height(8.dp))

            // BUTTON EDIT + DELETE
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                // Edit
                TextButton(onClick = onEditClick) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text("Edit", fontSize = 14.sp)
                }

                // Delete
                TextButton(onClick = onDeleteClick) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text("Hapus", fontSize = 14.sp)
                }
            }
        }
    }
}
