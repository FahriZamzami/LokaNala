package com.example.lokanala.ui.screen.rating

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
import com.example.lokanala.ui.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RatingScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val viewModel: RatingViewModel = viewModel(
        factory = ViewModelFactory.getInstance(context)
    )

    val listState = rememberLazyListState()
    val reviews = viewModel.reviews
    val isLoading by viewModel.isLoading.collectAsState()
    val currentUserId by viewModel.currentUserId.collectAsState()

    // -----------------------------------------------------------------
    // 1. LOGIKA SORTING: USER SENDIRI PALING ATAS
    // -----------------------------------------------------------------
    val sortedReviews by remember {
        derivedStateOf {
            // Mengurutkan: Jika userId == currentUserId (True), taruh di atas
            reviews.sortedByDescending { it.userId == currentUserId }
        }
    }

    // -----------------------------------------------------------------
    // 2. LOGIKA TOMBOL TAMBAH (Menggunakan derivedStateOf agar reaktif)
    // -----------------------------------------------------------------
    val hasReviewed by remember {
        derivedStateOf {
            reviews.any { it.userId == currentUserId }
        }
    }

    var showBottomSheet by remember { mutableStateOf(false) }
    var isEditing by remember { mutableStateOf(false) }
    var reviewToEdit by remember { mutableStateOf<Review?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

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
            if (!isLoading && !hasReviewed && currentUserId != -1) {
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

                // 3. GUNAKAN 'sortedReviews' DI SINI, BUKAN 'reviews'
                items(items = sortedReviews, key = { it.id }) { review ->

                    val isMyReview = (review.userId == currentUserId)

                    ReviewCard(
                        review = review,
                        isUserReview = isMyReview,
                        onEdit = {
                            if (isMyReview) {
                                isEditing = true
                                reviewToEdit = review
                                showBottomSheet = true
                            }
                        },
                        onDelete = {
                            if (isMyReview) {
                                viewModel.deleteUserReview(review.id)
                            }
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
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            AddEditReviewSheetContent(
                context = context,
                existingReview = if (isEditing) reviewToEdit else null,
                onDismiss = { showBottomSheet = false },
                onSubmit = { rating, comment, photoUris ->
                    if (isEditing && reviewToEdit != null) {
                        viewModel.updateReview(context, reviewToEdit!!.id, rating, comment, photoUris)
                    } else {
                        viewModel.addReview(context, rating, comment, photoUris)
                    }
                    showBottomSheet = false
                }
            )
        }
    }
}