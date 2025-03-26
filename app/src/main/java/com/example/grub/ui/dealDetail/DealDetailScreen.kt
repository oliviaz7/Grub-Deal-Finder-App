package com.example.grub.ui.dealDetail

import DealImage
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.ThumbDownOffAlt
import androidx.compose.material.icons.filled.ThumbUpOffAlt
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.grub.model.ApplicableGroup
import com.example.grub.model.DayOfWeekAndTimeRestriction
import com.example.grub.model.VoteType
import com.example.grub.ui.navigation.Destinations
import com.example.grub.ui.theme.defaultTextStyle
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter


@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DealDetailScreen(
    uiState: DealDetailUiState,
    navController: NavController,
    onSaveClicked: () -> Unit,
    onUpVoteClicked: () -> Unit,
    onDownVoteClicked: () -> Unit,
    setShowBottomSheet: (Boolean) -> Unit,
    onLogin: (Context) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                SmallFloatingActionButton(
                    onClick = { navController.popBackStack() },
                    containerColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    ) { innerPadding ->
        val screenModifier = Modifier.padding(innerPadding)
        val deal = uiState.deal!!
        val restaurantName = uiState.restaurantName!!
        val restaurantAddress = uiState.restaurantAddress!!
        Log.d("deal detail screen", deal.toString())

        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .background(MaterialTheme.colorScheme.primaryContainer)
        ) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .aspectRatio(8f / 5f)
                    .background(MaterialTheme.colorScheme.background)
                    .clickable{
                        navController.currentBackStackEntry?.savedStateHandle?.set(
                            key = "dealImageURL",
                            value = deal.imageUrl
                        )
                        navController.navigate(Destinations.IMAGE_ROUTE)
                    }
            ) {
                DealImage(
                    deal.imageUrl,
                    Modifier
                        .fillMaxSize()
                )
            }

            Box(
                Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(bottom = 20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                ) {
                    Text(
                        text = restaurantName,
                        style = MaterialTheme.typography.displaySmall
                    )
                    Row() {
                        Icon(
                            Icons.Filled.LocationOn,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = restaurantAddress,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            Box(
                Modifier
                    .offset(y = (-20).dp)
                    .background(
                        MaterialTheme.colorScheme.primaryContainer,
                        shape = MaterialTheme.shapes.large
                    )
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    Row() {
                        Text(
                            text = "${deal.type}",
                            style = MaterialTheme.typography.displayMedium,
                            color = Color.White,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = if (deal.userSaved && uiState.isLoggedIn) {
                                Icons.Filled.Bookmark
                            } else {
                                Icons.Outlined.BookmarkBorder
                            },
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .size(40.dp)
                                .clickable { onSaveClicked() }
                        )
                    }

                    Text(
                        text = "Deal Details",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary,
                    )

                    if (deal.expiryDate != null) {
                        Spacer(modifier = Modifier.height(8.dp))

                        Row() {
                            Icon(
                                Icons.Filled.CalendarMonth,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier
                                    .size(18.dp)
                                    .scale(0.95f)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "Valid Until: " + deal.expiryDate.format(
                                    DateTimeFormatter.ofPattern(
                                        "MMMM dd, yyyy"
                                    )
                                ),
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.White,
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row {
                        Icon(
                            Icons.Filled.ThumbUpOffAlt,
                            contentDescription = null,
                            tint =
                                if (deal.userVote == VoteType.UPVOTE)
                                    MaterialTheme.colorScheme.primary
                                else
                                    Color.White,
                            modifier = Modifier
                                .size(26.dp)
                                .clickable { onUpVoteClicked() },
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = " ${deal.numUpVotes - deal.numDownVotes} ",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                        )
                        Spacer(modifier = Modifier.width(8.dp))

                        Icon(
                            Icons.Filled.ThumbDownOffAlt,
                            contentDescription = null,
                            tint =
                                if (deal.userVote == VoteType.DOWNVOTE)
                                    MaterialTheme.colorScheme.primary
                                else
                                    Color.White,
                            modifier = Modifier
                                .size(26.dp)
                                .clickable { onDownVoteClicked() },
                        )

                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    Box(
                        modifier = modifier
                            .fillMaxWidth()
                            .drawBehind {
                                val shadowSize = 2.dp.toPx()
                                drawRoundRect(
                                    color = Color.Black.copy(alpha = 0.4f),
                                    size = size.copy(
                                        height = size.height + shadowSize,
                                        width = size.width + shadowSize,
                                    ),
                                    topLeft = Offset(8f, 9f),
                                    cornerRadius = CornerRadius(x = 20f, y = 20f)
                                )
                            }
                            .background(
                                MaterialTheme.colorScheme.background.copy(alpha = 1f),
                                MaterialTheme.shapes.large
                            )

                    ) {
                        Column(
                            Modifier
                                .padding(16.dp)
                        ) {
                            Text(
                                text = deal.item,
                                style = defaultTextStyle.copy(
                                    fontSize = 20.sp,
                                    lineHeight = 20.sp,
                                    letterSpacing = 0.15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    lineBreak = LineBreak.Heading
                                ),
                                color = Color.Black
                            )
                            if (deal.description != null && deal.description != "") {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = deal.description,
                                    style = defaultTextStyle.copy(
                                        fontSize = 16.sp,
                                        lineHeight = 24.sp,
                                        letterSpacing = 0.15.sp,
                                        fontWeight = FontWeight.Light,
                                        lineBreak = LineBreak.Heading
                                    ),
                                    color = MaterialTheme.colorScheme.primaryContainer

                                )
                            }

                            if (deal.activeDayTime != DayOfWeekAndTimeRestriction.NoRestriction) {
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Available on:",
                                    style = defaultTextStyle.copy(
                                        fontSize = 16.sp,
                                        lineHeight = 24.sp,
                                        letterSpacing = 0.15.sp,
                                        fontWeight = FontWeight.Medium,
                                        lineBreak = LineBreak.Heading
                                    ),
                                    color = Color.Black

                                )
                                Text(
                                    text = deal.activeDayTime.toDisplayString(),
                                    style = defaultTextStyle.copy(
                                        fontSize = 16.sp,
                                        lineHeight = 24.sp,
                                        letterSpacing = 0.15.sp,
                                        fontWeight = FontWeight.Light,
                                        lineBreak = LineBreak.Heading
                                    ),
                                    color = MaterialTheme.colorScheme.primaryContainer

                                )
                            }

                            if (
                                deal.applicableGroup != ApplicableGroup.NONE
                                && deal.applicableGroup != ApplicableGroup.ALL
                            ) {
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Available to:",
                                    style = defaultTextStyle.copy(
                                        fontSize = 16.sp,
                                        lineHeight = 24.sp,
                                        letterSpacing = 0.15.sp,
                                        fontWeight = FontWeight.Medium,
                                        lineBreak = LineBreak.Heading
                                    ),
                                    color = Color.Black

                                )
                                Text(
                                    text = deal.applicableGroup.toString(),
                                    style = defaultTextStyle.copy(
                                        fontSize = 16.sp,
                                        lineHeight = 24.sp,
                                        letterSpacing = 0.15.sp,
                                        fontWeight = FontWeight.Light,
                                        lineBreak = LineBreak.Heading
                                    ),
                                    color = MaterialTheme.colorScheme.primaryContainer

                                )
                            }

                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Posted By: ${deal.userName}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = "Posted On: ${
                            deal.datePosted.format(
                                DateTimeFormatter.ofPattern(
                                    "MMMM dd, yyyy"
                                )
                            )
                        }",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White

                    )
                    Spacer(modifier = Modifier.height(12.dp))

                }
            }
        }
        val scope = rememberCoroutineScope()
        val sheetState = rememberModalBottomSheetState()

        if (uiState.showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        setShowBottomSheet(false)
                    }
                },
                sheetState = sheetState,
                dragHandle = null,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp, start = 24.dp, end = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Sign in to interact with a deal",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    val context = LocalContext.current

                    Button(
                        onClick = { navController.navigate(Destinations.LOGIN_ROUTE) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "Login",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )
                    }
                    Button(
                        onClick = { navController.navigate(Destinations.SIGNUP_ROUTE) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "Sign up",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}