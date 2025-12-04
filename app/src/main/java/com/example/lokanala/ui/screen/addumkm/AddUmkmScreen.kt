package com.example.lokanala.ui.screen.addumkm

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.lokanala.ui.theme.LokanalaTheme
import kotlinx.coroutines.launch
import com.example.lokanala.ui.navigation.Screen
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxWidth

import coil.compose.rememberAsyncImagePainter
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddUmkmScreen(
    modifier: Modifier = Modifier,
    viewModel: AddUmkmViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel(),
    onBack: () -> Unit,
    navController: NavController
) {
    val currentUserState by authViewModel.currentUser.collectAsState()
    val currentUser = currentUserState

    // Form inputs
    var umkmName by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var contact by remember { mutableStateOf("") }
    var locationLink by remember { mutableStateOf("") }

    var isLoading by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // 🟦 Tambahkan state untuk gambar
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    // 🟦 Launcher untuk memilih gambar dari galeri
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        imageUri = uri
    }

    // Kategori
    val kategoriList by viewModel.kategoriList.collectAsState()
    val kategoriNames = kategoriList.map { it.nama_kategori }
    val kategoriMap = kategoriList.associate { it.nama_kategori to it.id_kategori_umkm }

    LaunchedEffect(Unit) {
        viewModel.loadKategori()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Tambah UMKM", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    SimpleTextField(umkmName, { umkmName = it }, "Nama UMKM:")

                    SimpleDropdownField(
                        label = "Kategori:",
                        options = kategoriNames,
                        selectedOption = category,
                        onOptionSelected = { category = it }
                    )

                    SimpleTextField(description, { description = it }, "Deskripsi:")
                    SimpleTextField(address, { address = it }, "Alamat:")
                    SimpleTextField(locationLink, { locationLink = it }, "Link Lokasi Map:")

                    // 🟦 TextField upload foto → tombol pilih foto
                    OutlinedTextField(
                        value = "",
                        onValueChange = {},
                        label = { Text("Upload Foto UMKM") },
                        trailingIcon = {
                            IconButton(onClick = { imagePickerLauncher.launch("image/*") }) {
                                Icon(Icons.Default.FolderOpen, contentDescription = "Pilih Foto")
                            }
                        },
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // 🟦 Preview Gambar
                    imageUri?.let {
                        Spacer(Modifier.height(12.dp))
                        Image(
                            painter = rememberAsyncImagePainter(model = it),
                            contentDescription = "Preview Image",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }

                    SimpleTextField(contact, { contact = it }, "Nomor Kontak:")

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (currentUser == null) {
                                scope.launch { snackbarHostState.showSnackbar("User belum login") }
                                navController.navigate(Screen.Login.route)
                                return@Button
                            }

                            val idKategori = kategoriMap[category]
                            if (idKategori == null) {
                                scope.launch { snackbarHostState.showSnackbar("Kategori belum dipilih") }
                                return@Button
                            }

                            isLoading = true

                            viewModel.addUMKM(
                                idUser = currentUser.id_user,
                                idKategori = idKategori,
                                nama = umkmName,
                                alamat = address,
                                noTelp = contact,
                                deskripsi = description,
                                linkLokasi = locationLink,
                                imageUri = imageUri   // 🟦 Kirim gambar ke ViewModel
                            ) { success, msg ->
                                isLoading = false
                                scope.launch { snackbarHostState.showSnackbar(msg) }

                                if (success) navController.popBackStack()
                            }
                        },
                        enabled = !isLoading,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = if (isLoading) "Menyimpan..." else "Simpan")
                    }
                }
            }
        }
    }
}

@Composable
private fun SimpleTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    singleLine: Boolean = true,
    minLines: Int = 1,
    readOnly: Boolean = false
) {
    val colors = MaterialTheme.colorScheme
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = colors.onSurfaceVariant, fontSize = 14.sp) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = colors.surfaceVariant,
            unfocusedContainerColor = colors.surfaceVariant,
            disabledContainerColor = colors.surfaceVariant,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            focusedTextColor = colors.onSurface,
            unfocusedTextColor = colors.onSurface
        ),
        singleLine = singleLine,
        minLines = minLines,
        readOnly = readOnly
    )
}

@Composable
private fun SimpleTextFieldWithIcon(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    readOnly: Boolean = false
) {
    val colors = MaterialTheme.colorScheme
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = colors.onSurfaceVariant, fontSize = 14.sp) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = colors.surfaceVariant,
            unfocusedContainerColor = colors.surfaceVariant,
            disabledContainerColor = colors.surfaceVariant,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            focusedTextColor = colors.onSurface,
            unfocusedTextColor = colors.onSurface
        ),
        trailingIcon = { Icon(icon, contentDescription = null, tint = colors.onSurfaceVariant) },
        readOnly = readOnly
    )
}

@Composable
private fun SimpleDropdownField(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val colors = MaterialTheme.colorScheme

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.surfaceVariant, RoundedCornerShape(12.dp))
            .clickable { expanded = !expanded }
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = if (selectedOption.isEmpty()) label else selectedOption,
                color = if (selectedOption.isEmpty()) colors.onSurfaceVariant else colors.onSurface,
                fontSize = 14.sp,
                modifier = Modifier.weight(1f)
            )
            Icon(
                Icons.Default.ArrowDropDown,
                contentDescription = "Dropdown",
                tint = colors.onSurfaceVariant
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .fillMaxWidth()
                .background(colors.surface)
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option, fontSize = 14.sp, color = colors.onSurface) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
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