package com.example.lokanala.ui.screen.addumkm

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector // PERBAIKAN
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.lokanala.ui.theme.InputFieldBg
import com.example.lokanala.ui.theme.LokanalaTheme
import com.example.lokanala.ui.theme.PromoPinkBg
import com.example.lokanala.ui.theme.PromoPinkText
import com.example.lokanala.ui.theme.TextGreyLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddUmkmScreen(
    modifier: Modifier = Modifier,
    viewModel: AddUmkmViewModel = viewModel(),
    onBack: () -> Unit,
    navController: NavController
) {
    // State sementara untuk input (nanti pindahkan ke ViewModel)
    var umkmName by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var contact by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Tambah UMKM",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PromoPinkBg, // Latar pink
                    titleContentColor = Color.Black,
                    navigationIconContentColor = Color.Black
                )
            )
        },
        containerColor = PromoPinkBg // Latar belakang pink
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()) // Agar bisa discroll jika konten panjang
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Nama UMKM
                    SimpleTextField(
                        value = umkmName,
                        onValueChange = { umkmName = it },
                        label = "Nama UMKM:"
                    )

                    // Kategori (Dropdown Placeholder)
                    SimpleDropdownField(
                        label = "Kategori:",
                        options = listOf("Kuliner", "Fashion", "Jasa", "Lainnya"),
                        selectedOption = category,
                        onOptionSelected = { category = it }
                    )

                    // Deskripsi Singkat
                    SimpleTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = "Deskripsi Singkat:",
                        singleLine = false, // Multi-line
                        minLines = 3 // Minimal 3 baris
                    )

                    // Alamat
                    SimpleTextFieldWithIcon(
                        value = address,
                        onValueChange = { address = it },
                        label = "Alamat (otomatis dari GPS):",
                        icon = Icons.Default.LocationOn
                    )

                    // Upload Foto
                    SimpleTextFieldWithIcon(
                        value = "", // Ini hanya tampilan
                        onValueChange = {},
                        label = "Upload Foto / Logo UMKM:",
                        icon = Icons.Default.FolderOpen,
                        readOnly = true // Agar tidak bisa diketik
                    )

                    // Nomor Kontak
                    SimpleTextField(
                        value = contact,
                        onValueChange = { contact = it },
                        label = "Nomor Kontak:"
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Tombol Simpan
                    Button(
                        onClick = { /* TODO: Aksi Simpan */ },
                        modifier = Modifier.align(Alignment.End), // Ke kanan
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PromoPinkText // Warna pink
                        )
                    ) {
                        Text(
                            text = "Simpan",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
            }
        }
    }
}

// Composable helper untuk TextField standar
@Composable
private fun SimpleTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    singleLine: Boolean = true,
    minLines: Int = 1,
    readOnly: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(text = label, color = TextGreyLight, fontSize = 14.sp) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = InputFieldBg,
            unfocusedContainerColor = InputFieldBg,
            disabledContainerColor = InputFieldBg,
            focusedIndicatorColor = Color.Transparent, // Hilangkan border bawah
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black
        ),
        singleLine = singleLine,
        minLines = minLines,
        readOnly = readOnly
    )
}

// Composable helper untuk TextField dengan ikon di kanan
@Composable
private fun SimpleTextFieldWithIcon(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    readOnly: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(text = label, color = TextGreyLight, fontSize = 14.sp) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = InputFieldBg,
            unfocusedContainerColor = InputFieldBg,
            disabledContainerColor = InputFieldBg,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black
        ),
        trailingIcon = { Icon(icon, contentDescription = null, tint = TextGreyLight) },
        readOnly = readOnly
    )
}

// Placeholder untuk Dropdown Kategori (implementasi dropdown butuh state lebih kompleks)
@Composable
private fun SimpleDropdownField(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    // Implementasi Dropdown sesungguhnya bisa ditambahkan di sini
    // Untuk UI saja, kita buat tampilan mirip TextField
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(InputFieldBg, RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = if (selectedOption.isEmpty()) label else selectedOption,
                color = if (selectedOption.isEmpty()) TextGreyLight else Color.Black,
                fontSize = if (selectedOption.isEmpty()) 14.sp else 16.sp,
                modifier = Modifier.weight(1f)
            )
            Icon(Icons.Default.ArrowDropDown, contentDescription = "Pilih Kategori", tint = TextGreyLight)
        }
        // Tampilkan daftar opsi (sementara saja untuk visual)
        if (selectedOption.isEmpty()) { // Hanya tampilkan jika belum dipilih
            Column(modifier = Modifier.padding(top = 40.dp)) { // Beri jarak dari label
                options.forEach { option ->
                    Text(option, fontSize=14.sp, color = TextGreyLight, modifier=Modifier.padding(vertical=2.dp))
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun AddUmkmScreenPreview() {
    LokanalaTheme {
        AddUmkmScreen(onBack = {}, navController = rememberNavController())
    }
}
