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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
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
    val showOverview by remember { derivedStateOf { listState.firstVisibleItemIndex == 0 } }

    var showBottomSheet by remember { mutableStateOf(false) }
    var isEditing by remember { mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val reviews = viewModel.reviews
    val userReview by viewModel.userReview
    val colorScheme = MaterialTheme.colorScheme

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Rating & Ulasan",
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Kembali",
                            tint = colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = colorScheme.background,
                    navigationIconContentColor = colorScheme.onSurface,
                    titleContentColor = colorScheme.onSurface
                )
            )
        },
        floatingActionButton = {
            if (userReview == null) {
                ExtendedFloatingActionButton(
                    text = {
                        Text(
                            "Tulis Ulasan",
                            fontWeight = FontWeight.SemiBold,
                            color = colorScheme.onPrimary
                        )
                    },
                    onClick = {
                        isEditing = false
                        showBottomSheet = true
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Tambah Ulasan",
                            tint = colorScheme.onPrimary
                        )
                    },
                    containerColor = colorScheme.primary,
                    shape = RoundedCornerShape(50)
                )
            }
        },
        containerColor = colorScheme.background
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            state = listState,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            item {
                RatingOverview(reviews)
            }

            item {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "${reviews.size} ulasan",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    color = colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
            }

            items(
                items = reviews,
                key = { review -> review.id }
            ) { review ->
                ReviewCard(
                    review = review,
                    isUserReview = (review == userReview),
                    onEdit = {
                        isEditing = true
                        showBottomSheet = true
                    },
                    onDelete = { viewModel.deleteUserReview() }
                )
            }
        }
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
            containerColor = colorScheme.surface,
            tonalElevation = 6.dp,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            dragHandle = null
        ) {
            AddEditReviewSheetContent(
                onDismiss = { showBottomSheet = false },
                onSubmit = { rating, comment, hasPhoto ->
                    if (isEditing) {
                        viewModel.editUserReview(rating, comment, hasPhoto)
                    } else {
                        viewModel.addReview(rating, comment, hasPhoto)
                    }
                    showBottomSheet = false
                },
                existingReview = if (isEditing) userReview else null
            )
        }
    }
}