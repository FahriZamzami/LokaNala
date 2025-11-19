package com.example.lokanala.ui.screen.rating

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.lokanala.data.remote.response.rating.Review
import com.example.lokanala.ui.components.AddEditReviewSheetContent
import com.example.lokanala.ui.components.RatingOverview
import com.example.lokanala.ui.components.ReviewCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RatingScreen(
    navController: NavController,
    viewModel: RatingViewModel = viewModel()
) {
    val listState = rememberLazyListState()
    val context = LocalContext.current

    var showBottomSheet by remember { mutableStateOf(false) }
    // State untuk Edit Mode
    var isEditing by remember { mutableStateOf(false) }
    var reviewToEdit by remember { mutableStateOf<Review?>(null) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Ambil data dari ViewModel
    val reviews: List<Review> = viewModel.reviews
    val isLoading by viewModel.isLoading.collectAsState()

    // 1. Cek apakah user sudah review?
    // Pastikan Review.kt punya property 'isUserReview' (Boolean)
    val userReview = reviews.find { review -> review.isUserReview }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Rating & Ulasan", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Kembali")
                    }
                }
            )
        },
        floatingActionButton = {
            // 2. TOMBOL HANYA MUNCUL JIKA BELUM ADA REVIEW
            if (userReview == null && !isLoading) {
                ExtendedFloatingActionButton(
                    text = { Text("Tulis Ulasan", fontWeight = FontWeight.SemiBold) },
                    onClick = {
                        isEditing = false
                        reviewToEdit = null
                        showBottomSheet = true
                    },
                    icon = { Icon(Icons.Default.Star, "Tambah") },
                    containerColor = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(50)
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->

        if (isLoading && reviews.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                state = listState,
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                item { RatingOverview(reviews) }

                item {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "${reviews.size} ulasan",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                }

                items(items = reviews, key = { review -> review.id }) { review ->
                    ReviewCard(
                        review = review,
                        isUserReview = review.isUserReview,
                        onEdit = {
                            // 3. Event Edit
                            isEditing = true
                            reviewToEdit = review
                            showBottomSheet = true
                        },
                        onDelete = {
                            // 4. Event Delete
                            viewModel.deleteUserReview(review.id)
                        }
                    )
                }
            }
        }
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            AddEditReviewSheetContent(
                context = context,
                existingReview = if (isEditing) reviewToEdit else null,
                onDismiss = { showBottomSheet = false },
                onSubmit = { rating, comment, photoUris -> // <-- photoUris didapat dari sini

                    if (isEditing && reviewToEdit != null) {
                        // JIKA EDIT
                        viewModel.updateReview(
                            context = context,
                            reviewId = reviewToEdit!!.id,
                            rating = rating,
                            comment = comment,
                            photoUris = photoUris // <--- TAMBAHKAN BARIS INI
                        )
                    } else {
                        // JIKA BARU
                        viewModel.addReview(context, rating, comment, photoUris)
                    }

                    showBottomSheet = false
                }
            )
        }
    }
}