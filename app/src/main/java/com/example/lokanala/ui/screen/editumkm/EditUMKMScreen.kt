package com.example.lokanala.ui.screen.editumkm

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.lokanala.data.remote.response_and_request.UmkmResponse
import com.example.lokanala.ui.ViewModelFactory
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditUmkmScreen(
    idUmkm: Int,
    onBack: () -> Unit,
    navController: NavController,
    viewModel: EditUmkmViewModel = viewModel(factory = ViewModelFactory.getInstance(LocalContext.current))
) {
    val context = LocalContext.current
    val uiState by viewModel.state.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val umkmDetail by viewModel.umkmDetail.collectAsState()

    
    LaunchedEffect(idUmkm) {
        viewModel.fetchUmkmDetail(idUmkm)
    }

    
    var umkmName by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var contact by remember { mutableStateOf("") }
    var locationLink by remember { mutableStateOf("") }
    var selectedKategoriId by remember { mutableStateOf<Int?>(null) }
    var selectedKategoriName by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    
    LaunchedEffect(umkmDetail) {
        umkmDetail?.let {
            umkmName = it.nama ?: ""
            description = it.deskripsi ?: ""
            address = it.alamat ?: ""
            contact = it.noTelepon ?: ""
            locationLink = it.linkLokasi ?: ""
            selectedKategoriId = it.idKategoriUmkm
        }
    }

    
    LaunchedEffect(categories, selectedKategoriId) {
        selectedKategoriName = categories.find { it.idKategoriUmkm == selectedKategoriId }?.namaKategori ?: ""
    }

    val isFormValid = umkmName.isNotBlank() && address.isNotBlank() && contact.isNotBlank() && selectedKategoriName.isNotBlank()

    val fusedLocationClient = remember {
        com.google.android.gms.location.LocationServices.getFusedLocationProviderClient(context)
    }

    
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) imageUri = uri
    }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) imageUri = tempCameraUri
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            try {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    location?.let {
                        locationLink = "https://www.google.com/maps/search/?api=1&query=${it.latitude},${it.longitude}"
                    }
                }
            } catch (e: SecurityException) {
                Toast.makeText(context, "Izin lokasi diperlukan", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun createTmpUri(): Uri {
        val tmpFile = File.createTempFile("edit_camera_", ".jpg", context.cacheDir)
        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", tmpFile).also {
            tempCameraUri = it
        }
    }

    
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            Toast.makeText(context, "Perubahan Berhasil Disimpan", Toast.LENGTH_SHORT).show()
            viewModel.resetState()
            onBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Profil UMKM", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(0.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {

                    SimpleTextField(umkmName, { umkmName = it }, "Nama UMKM:")

                    SimpleDropdownField(
                        label = "Kategori:",
                        options = categories.map { it.namaKategori },
                        selectedOption = selectedKategoriName,
                        onOptionSelected = { name ->
                            selectedKategoriName = name
                            selectedKategoriId = categories.find { it.namaKategori == name }?.idKategoriUmkm
                        }
                    )

                    SimpleTextField(description, { description = it }, "Deskripsi:", minLines = 3, singleLine = false)

                    SimpleTextFieldWithIcon(
                        value = locationLink,
                        onValueChange = { locationLink = it },
                        label = "Link Lokasi (GPS):",
                        icon = Icons.Default.LocationOn,
                        onIconClick = { locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION) }
                    )

                    Text(
                        "Foto UMKM:",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        if (imageUri != null) {
                            
                            coil.compose.AsyncImage(
                                model = imageUri,
                                contentDescription = "New Selected Image",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = androidx.compose.ui.layout.ContentScale.Crop
                            )
                        } else if (!umkmDetail?.gambarUrl.isNullOrEmpty()) {
                            
                            coil.compose.AsyncImage(
                                model = umkmDetail?.gambarUrl,
                                contentDescription = "Current UMKM Image",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = androidx.compose.ui.layout.ContentScale.Crop
                            )
                        } else {
                            Icon(Icons.Default.Camera, null, modifier = Modifier.size(48.dp), tint = Color.Gray)
                        }

                        
                        if (imageUri != null) {
                            Surface(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(8.dp)
                                    .size(32.dp)
                                    .clickable { imageUri = null },
                                shape = androidx.compose.foundation.shape.CircleShape,
                                color = Color.Black.copy(alpha = 0.6f)
                            ) {
                                Icon(Icons.Default.Clear, "Hapus", tint = Color.White, modifier = Modifier.padding(6.dp))
                            }
                        }
                    }


                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { cameraLauncher.launch(createTmpUri()) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Camera, null)
                            Spacer(Modifier.width(4.dp))
                            Text("Kamera")
                        }

                        OutlinedButton(
                            onClick = { galleryLauncher.launch("image/*") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.FolderOpen, null)
                            Spacer(Modifier.width(4.dp))
                            Text("Galeri")
                        }
                    }

                    if (imageUri != null) {
                        Text(
                            text = "File baru terpilih: ${DocumentFile.fromSingleUri(context, imageUri!!)?.name}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    SimpleTextField(address, { address = it }, "Alamat Lengkap:")

                    SimpleTextField(
                        value = contact,
                        onValueChange = { input ->
                            if (input.all { it.isDigit() } || input.isEmpty()) {
                                contact = input
                            }
                        },
                        label = "Nomor Kontak:",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            val file = imageUri?.let { uri: Uri ->
                                uriToFileEdit(context, uri) }
                            viewModel.updateUmkm(
                                idUmkm = idUmkm,
                                idKategori = selectedKategoriId,
                                nama = umkmName,
                                alamat = address,
                                noTelp = contact,
                                deskripsi = description,
                                linkLokasi = locationLink,
                                imageFile = file
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !uiState.isLoading && isFormValid,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (uiState.isLoading) CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                        else Text("Simpan Perubahan", fontWeight = FontWeight.Bold)
                    }

                    TextButton(
                        onClick = { showDeleteDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !uiState.isLoading
                    ) {
                        Text("Hapus UMKM", color = Color.Red, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Hapus UMKM") },
            text = { Text("Apakah Anda yakin ingin menghapus '${umkmDetail?.nama ?: "UMKM ini"}'?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteUmkm(idUmkm)
                    showDeleteDialog = false
                }) { Text("Hapus", color = Color.Red) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Batal") }
            }
        )
    }
}



@Composable
private fun SimpleTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    minLines: Int = 1,
    singleLine: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    val colors = MaterialTheme.colorScheme
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontSize = 14.sp) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = colors.surfaceVariant,
            unfocusedContainerColor = colors.surfaceVariant,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        ),
        singleLine = singleLine,
        minLines = minLines,
        keyboardOptions = keyboardOptions
    )
}

@Composable
private fun SimpleTextFieldWithIcon(value: String, onValueChange: (String) -> Unit, label: String, icon: ImageVector, onIconClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontSize = 14.sp) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = colors.surfaceVariant,
            unfocusedContainerColor = colors.surfaceVariant,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        ),
        trailingIcon = {
            IconButton(onClick = onIconClick) {
                Icon(icon, contentDescription = null, tint = colors.primary)
            }
        }
    )
}

@Composable
private fun SimpleDropdownField(label: String, options: List<String>, selectedOption: String, onOptionSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val colors = MaterialTheme.colorScheme

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = {},
            label = { Text(label, fontSize = 14.sp) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            readOnly = true,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = colors.surfaceVariant,
                unfocusedContainerColor = colors.surfaceVariant,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            ),
            trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) }
        )
        Box(modifier = Modifier.matchParentSize().clickable { expanded = !expanded })
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { selection ->
                DropdownMenuItem(text = { Text(selection) }, onClick = {
                    onOptionSelected(selection)
                    expanded = false
                })
            }
        }
    }
}

fun uriToFileEdit(context: Context, uri: Uri): File? {
    val contentResolver = context.contentResolver
    val myFile = File.createTempFile("edit_umkm_fixed_", ".jpg", context.cacheDir)

    return try {
        val inputStreamForExif: InputStream? = contentResolver.openInputStream(uri)
        val orientation = inputStreamForExif?.use { input ->
            val exif = ExifInterface(input)
            exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED
            )
        } ?: ExifInterface.ORIENTATION_UNDEFINED

        val inputStreamForBitmap: InputStream? = contentResolver.openInputStream(uri)
        val originalBitmap = inputStreamForBitmap?.use { input ->
            BitmapFactory.decodeStream(input)
        } ?: return null

        val rotatedBitmap = when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(originalBitmap, 90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(originalBitmap, 180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(originalBitmap, 270f)
            else -> originalBitmap
        }

        val fos = FileOutputStream(myFile)
        rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 30, fos)
        fos.flush()
        fos.close()

        myFile
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

private fun rotateImage(source: Bitmap, angle: Float): Bitmap {
    val matrix = Matrix()
    matrix.postRotate(angle)
    return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
}