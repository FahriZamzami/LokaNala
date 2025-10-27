package com.example.lokanala.ui.screen.rating

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
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

    // Gunakan remember agar background tidak redraw setiap scroll
    val backgroundModifier = remember(colorScheme) {
        Modifier.drawBehind {
            drawCircle(
                color = colorScheme.primaryContainer.copy(alpha = 0.15f),
                radius = 450f,
                center = Offset(size.width * 0.9f, size.height * 0.1f)
            )
            drawCircle(
                color = colorScheme.primaryContainer.copy(alpha = 0.1f),
                radius = 350f,
                center = Offset(size.width * 0.1f, size.height * 0.9f)
            )
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Rating & Reviews", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(colorScheme.primaryContainer.copy(alpha = 0.3f))
                            .clickable { navController.popBackStack() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = colorScheme.background
                )
            )
        },
        floatingActionButton = {
            if (userReview == null) {
                ExtendedFloatingActionButton(
                    text = { Text("Write a Review", fontWeight = FontWeight.Bold) },
                    onClick = {
                        isEditing = false
                        showBottomSheet = true
                    },
                    icon = { Icon(Icons.Default.Star, contentDescription = "Add Review") },
                    containerColor = colorScheme.primary,
                    contentColor = colorScheme.onPrimary,
                    shape = RoundedCornerShape(50)
                )
            }
        },
        containerColor = colorScheme.background
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(backgroundModifier)
        ) {
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
                    .fillMaxSize(),
                state = listState,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 🔹 Gunakan komponen baru
                item { RatingOverview(reviews) }

                item {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "${reviews.size} reviews",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = colorScheme.onBackground,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }

                itemsIndexed(reviews, key = { index, review -> index }) { _, review ->
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

                item { Spacer(modifier = Modifier.height(100.dp)) }
            }
        }
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
            containerColor = colorScheme.surface,
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
