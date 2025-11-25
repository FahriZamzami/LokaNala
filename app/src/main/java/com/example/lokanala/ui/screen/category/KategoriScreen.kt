package com.example.lokanala.ui.screen.category

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.lokanala.ui.theme.LokanalaTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(
    navController: NavController,
    umkmId: Int,
    viewModel: CategoryViewModel, // Terima ViewModel
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // State untuk mengontrol dialog tambah/edit
    var showAddEditSheet by remember { mutableStateOf(false) }
    // State untuk menyimpan kategori yang sedang diedit
    var selectedCategory by remember { mutableStateOf<Category?>(null) }

    // State untuk dialog konfirmasi hapus
    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kategori", fontWeight = FontWeight.Bold, color = colors.onSurface) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = colors.primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = colors.background)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    selectedCategory = null // Pastikan null (mode Tambah)
                    showAddEditSheet = true
                },
                containerColor = colors.primary,
                contentColor = colors.onPrimary,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, "Tambah Kategori")
            }
        },
        containerColor = colors.background
    ) { innerPadding ->

        // Tampilkan daftar kategori
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(viewModel.categories, key = { it.id }) { category ->
                CategoryItemCard(
                    category = category,
                    onEditClick = {
                        selectedCategory = category // Set kategori (mode Edit)
                        showAddEditSheet = true
                    },
                    onDeleteClick = {
                        selectedCategory = category // Set kategori untuk dihapus
                        showDeleteDialog = true
                    }
                )
            }
        }

        // --- Bottom Sheet untuk Tambah/Edit Kategori ---
        if (showAddEditSheet) {
            ModalBottomSheet(
                onDismissRequest = { showAddEditSheet = false },
                sheetState = sheetState,
                containerColor = colors.surface,
                tonalElevation = 6.dp,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ) {
                AddEditCategorySheetContent(
                    existingCategory = selectedCategory,
                    onDismiss = {
                        coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) showAddEditSheet = false
                        }
                    },
                    onSubmit = { name, description ->
                        if (selectedCategory == null) {
                            // Mode Tambah
                            viewModel.addCategory(name, description)
                        } else {
                            // Mode Edit
                            viewModel.updateCategory(selectedCategory!!.id, name, description)
                        }

                        coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) showAddEditSheet = false
                        }
                    }
                )
            }
        }

        // --- Dialog Konfirmasi Hapus ---
        if (showDeleteDialog && selectedCategory != null) {
            DeleteCategoryDialog(
                category = selectedCategory!!,
                onDismiss = {
                    showDeleteDialog = false
                    selectedCategory = null
                },
                onConfirm = {
                    viewModel.deleteCategory(selectedCategory!!)
                    showDeleteDialog = false
                    selectedCategory = null
                }
            )
        }
    }
}

/**
 * Card untuk menampilkan satu item kategori dengan tombol Edit & Hapus.
 */
@Composable
fun CategoryItemCard(
    category: Category,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = category.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = colors.onSurface
                )
                if (category.description.isNotBlank()) {
                    Text(
                        text = category.description,
                        fontSize = 14.sp,
                        color = colors.onSurfaceVariant
                    )
                }
            }

            Row {
                IconButton(onClick = onEditClick) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = colors.primary)
                }
                IconButton(onClick = onDeleteClick) {
                    Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = colors.error)
                }
            }
        }
    }
}

/**
 * Konten untuk Bottom Sheet Tambah/Edit Kategori.
 */
@Composable
fun AddEditCategorySheetContent(
    existingCategory: Category?,
    onDismiss: () -> Unit,
    onSubmit: (name: String, description: String) -> Unit
) {
    var name by remember { mutableStateOf(existingCategory?.name ?: "") }
    var description by remember { mutableStateOf(existingCategory?.description ?: "") }
    val colors = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Handle (garis abu-abu di atas)
        Box(
            modifier = Modifier
                .width(40.dp)
                .height(4.dp)
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 16.dp)
                .background(colors.outlineVariant, RoundedCornerShape(2.dp))
        )

        Text(
            text = if (existingCategory == null) "Tambah Kategori Baru" else "Edit Kategori",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = colors.onSurface,
            modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp)
        )

        // Input Nama Kategori (sesuai gambar)
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("nama_kategori") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Input Deskripsi (sesuai gambar)
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("deskripsi") },
            modifier = Modifier.fillMaxWidth().heightIn(min = 100.dp),
            shape = RoundedCornerShape(10.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Tombol Aksi
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.primary)
            ) { Text("Batal") }

            Button(
                onClick = { onSubmit(name, description) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colors.primary, contentColor = colors.onPrimary),
                enabled = name.isNotBlank() // Tombol Simpan aktif jika nama tidak kosong
            ) { Text("Simpan") }
        }

        Spacer(modifier = Modifier.height(16.dp)) // Padding di bawah
    }
}

/**
 * Dialog konfirmasi sebelum menghapus.
 */
@Composable
fun DeleteCategoryDialog(
    category: Category,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Hapus Kategori") },
        text = { Text(text = "Apakah Anda yakin ingin menghapus kategori '${category.name}'?") },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Hapus")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun CategoryScreenPreview() {
    LokanalaTheme {
        CategoryScreen(
            navController = rememberNavController(),
            umkmId = 1,
            viewModel = viewModel() // Gunakan viewModel() untuk preview
        )
    }
}
