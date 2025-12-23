package com.example.lokanala.ui.screen.addumkm

import android.Manifest
import android.R.attr.category
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.lokanala.ui.ViewModelFactory
import androidx.documentfile.provider.DocumentFile
import com.example.lokanala.ui.theme.LokanalaTheme
import java.io.File
import java.io.FileOutputStream
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import java.util.Objects

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddUmkmScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    navController: NavController,
    viewModel: AddUmkmViewModel = viewModel(factory = ViewModelFactory.getInstance(LocalContext.current))
) {
    val context = LocalContext.current
    val uiState by viewModel.state.collectAsState()
    var selectedKategoriId by remember { mutableStateOf<Int?>(null) }
    var selectedKategoriName by remember { mutableStateOf("") }

    
    var umkmName by remember { mutableStateOf("") }

    var description by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var contact by remember { mutableStateOf("") }
    var locationLink by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }

    
    val isFormValid = umkmName.isNotBlank() &&
            selectedKategoriName.isNotBlank() &&
            description.isNotBlank() &&
            address.isNotBlank() &&
            contact.isNotBlank() &&
            locationLink.isNotBlank() &&
            imageUri != null

    val fusedLocationClient = remember {
        com.google.android.gms.location.LocationServices.getFusedLocationProviderClient(context)
    }

    
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) imageUri = uri
    }

    
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) imageUri = tempCameraUri
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
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
        
        val timeStamp = System.currentTimeMillis()
        val tmpFile = File.createTempFile("CAM_${timeStamp}_", ".jpg", context.cacheDir).apply {
            createNewFile()
            
        }
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            tmpFile
        )
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        }
    }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            Toast.makeText(context, "UMKM Berhasil Ditambahkan", Toast.LENGTH_SHORT).show()
            onBack() 
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tambah UMKM", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(2.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    SimpleTextField(value = umkmName, onValueChange = { umkmName = it }, label = "Nama UMKM:")

                    SimpleDropdownField(
                        label = "Kategori:",
                        options = uiState.categories.map { it.namaKategori },
                        selectedOption = selectedKategoriName,
                        onOptionSelected = { name ->
                            selectedKategoriName = name
                            selectedKategoriId = uiState.categories.find { it.namaKategori == name }?.idKategoriUmkm
                        }
                    )

                    SimpleTextField(value = description, onValueChange = { description = it }, label = "Deskripsi:", minLines = 3, singleLine = false)

                    
                    SimpleTextFieldWithIcon(
                        value = locationLink,
                        onValueChange = { locationLink = it },
                        label = "Link Lokasi (Klik ikon GPS):",
                        icon = Icons.Default.LocationOn,
                        onIconClick = {
                            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                        }
                    )

                    
                    Text(
                        "Foto UMKM:",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (imageUri == null) {
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            
                            OutlinedButton(
                                onClick = {
                                    val uri = createTmpUri()
                                    tempCameraUri = uri
                                    cameraLauncher.launch(uri)
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Camera, contentDescription = null)
                                Spacer(Modifier.width(4.dp))
                                Text("Kamera")
                            }

                            OutlinedButton(
                                onClick = { galleryLauncher.launch("image/*") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.FolderOpen, contentDescription = null)
                                Spacer(Modifier.width(4.dp))
                                Text("Galeri")
                            }
                        }
                    } else {
                        
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            
                            coil.compose.AsyncImage(
                                model = imageUri,
                                contentDescription = "Selected Image",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = androidx.compose.ui.layout.ContentScale.Crop
                            )

                            
                            Surface(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(8.dp)
                                    .size(32.dp)
                                    .clickable {
                                        imageUri = null 
                                    },
                                shape = androidx.compose.foundation.shape.CircleShape,
                                color = Color.Black.copy(alpha = 0.6f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Hapus Gambar",
                                    tint = Color.White,
                                    modifier = Modifier.padding(6.dp)
                                )
                            }
                        }
                    }


                    if (imageUri != null) {
                        Text(
                            text = "File terpilih: ${DocumentFile.fromSingleUri(context, imageUri!!)?.name}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }

                    SimpleTextField(value = address, onValueChange = { address = it }, label = "Alamat Lengkap:")

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

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            if (isFormValid) {
                                val file = imageUri?.let { uriToFile(context, it) }
                                viewModel.saveUmkm(
                                    idKategori = selectedKategoriId, 
                                    nama = umkmName,
                                    alamat = address,
                                    noTelp = contact,
                                    deskripsi = description,
                                    linkLokasi = locationLink,
                                    imageFile = file
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        
                        enabled = !uiState.isLoading && isFormValid,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            disabledContainerColor = Color.Gray 
                        )
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                        } else {
                            Text("Simpan UMKM", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

fun uriToFile(context: Context, uri: Uri): File? {
    val contentResolver = context.contentResolver
    val myFile = File.createTempFile("upload_${System.currentTimeMillis()}_", ".jpg", context.cacheDir)

    return try {
        
        val inputStream = contentResolver.openInputStream(uri) ?: return null
        var bitmap = android.graphics.BitmapFactory.decodeStream(inputStream)
        inputStream.close()

        
        val exifInputStream = contentResolver.openInputStream(uri)
        if (exifInputStream != null) {
            val exif = ExifInterface(exifInputStream)
            val orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED
            )

            
            bitmap = when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(bitmap, 90f)
                ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(bitmap, 180f)
                ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(bitmap, 270f)
                else -> bitmap
            }
            exifInputStream.close()
        }

        
        val outputStream = FileOutputStream(myFile)
        bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 30, outputStream)
        outputStream.flush()
        outputStream.close()
        myFile
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}


private fun rotateImage(source: Bitmap, angle: Float): Bitmap {
    val matrix = Matrix()
    matrix.postRotate(angle)
    return Bitmap.createBitmap(
        source, 0, 0, source.width, source.height, matrix, true
    )
}

@Composable
private fun SimpleTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    singleLine: Boolean = true,
    minLines: Int = 1,
    readOnly: Boolean    = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
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
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        ),
        singleLine = singleLine,
        minLines = minLines,
        readOnly = readOnly,
        keyboardOptions = keyboardOptions
    )
}

@Composable
private fun SimpleTextFieldWithIcon(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    readOnly: Boolean = false,
    onIconClick: () -> Unit = {} 
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
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        ),
        
        trailingIcon = {
            IconButton(onClick = onIconClick) {
                Icon(icon, contentDescription = null, tint = colors.primary)
            }
        },
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
            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}