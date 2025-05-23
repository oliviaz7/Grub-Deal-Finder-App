package com.example.grub.ui.dealDetail

import DealImage
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.ThumbDownOffAlt
import androidx.compose.material.icons.filled.ThumbUpOffAlt
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.grub.model.ApplicableGroup
import com.example.grub.model.DayOfWeekAndTimeRestriction
import com.example.grub.model.Deal
import com.example.grub.model.VoteType
import com.example.grub.ui.shared.navigation.Destinations
import com.example.grub.ui.theme.defaultTextStyle
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DealDetailScreen(
    uiState: DealDetailUiState,
    navController: NavController,
    onSaveClicked: () -> Unit,
    onUpVoteClicked: () -> Unit,
    onDownVoteClicked: () -> Unit,
    onDeleteClicked: () -> Unit,
    setShowBottomSheet: (Boolean) -> Unit,
    clearSnackBarMsg: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
            clearSnackBarMsg()
        }
    }

    val postedByText = buildAnnotatedString {
        append("Posted By: ")
        withLink(
            LinkAnnotation.Clickable(
                tag = "USERNAME",
                styles = TextLinkStyles(style = SpanStyle(
                        fontWeight = FontWeight.Bold,
                        textDecoration = TextDecoration.Underline
                    )),
                linkInteractionListener = {
                    val userId = uiState.deal?.userId
                    if (userId != null) {
                        navController.navigate(
                            "${Destinations.PROFILE_ROUTE}?userId=$userId"
                        ) {
                            // Pop up to the start destination of the graph to
                            // avoid building up a large stack of destinations
                            // on the back stack as users select items
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = false
                            }
                            // Avoid multiple copies of the same destination when
                            // re-selecting the same item
                            launchSingleTop = true
                        }
                    }

                },
            )
        ) {
            append(uiState.deal!!.userName)
        }
    }

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
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        },
        snackbarHost = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                ) { data ->
                    Snackbar(
                        snackbarData = data,
                        containerColor = Color.White,
                        actionColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.primaryContainer,
                        shape = MaterialTheme.shapes.extraLarge,
                    )
                }
            }

        }
    ) { innerPadding ->
        val screenModifier = Modifier.padding(innerPadding)
        val deal = uiState.deal!!

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
                    .clickable {
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
                RestaurantDetails(uiState)
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
                    DealDetailHeader(
                        deal = deal,
                        onSaveClicked = onSaveClicked,
                        onUpVoteClicked = onUpVoteClicked,
                        onDownVoteClicked = onDownVoteClicked,
                        onDeleteClicked = {
                            onDeleteClicked()
                            navController.popBackStack()
                                          },
                        uiState = uiState,
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    DealDetailBox(deal)

                    Spacer(modifier = Modifier.height(8.dp))

                    if ((deal.applicableGroup != ApplicableGroup.NONE
                                && deal.applicableGroup != ApplicableGroup.ALL) ||
                        (deal.activeDayTime != DayOfWeekAndTimeRestriction.NoRestriction)
                    ) {
                        Spacer(modifier = Modifier.height(4.dp))
                        DealAvailability(deal, navController)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    Text(
                        text = postedByText,
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

        if (uiState.showBottomSheet) {
            LoginPrompt(setShowBottomSheet, navController)
        }
    }
}


@Composable
fun RestaurantDetails(
    uiState: DealDetailUiState,
) {
    val restaurantName = uiState.restaurantName!!
    val restaurantAddress = uiState.restaurantAddress!!

    Column(
        modifier = Modifier
            .padding(16.dp)
    ) {
        Text(
            text = restaurantName,
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.primaryContainer,
        )
        Row {
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


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DealDetailHeader(
    uiState: DealDetailUiState,
    deal: Deal,
    onSaveClicked: () -> Unit,
    onUpVoteClicked: () -> Unit,
    onDownVoteClicked: () -> Unit,
    onDeleteClicked: () -> Unit,
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        DeleteConfirmationDialog(
            onConfirm = {
                onDeleteClicked()
                showDeleteDialog = false
            },
            onDismiss = {
                showDeleteDialog = false
            }
        )
    }

    Row {
        Text(
            text = "${deal.type}",
            style = MaterialTheme.typography.displayMedium,
            color = Color.White,
            modifier = Modifier.weight(1f)
        )
        // delete
        if (deal.userId == uiState.currUser?.id) {
            Icon(
                imageVector = Icons.Outlined.Delete,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
                    .padding(top = 8.dp, end = 8.dp)
                    .size(40.dp)
                    .clickable { showDeleteDialog = true }
            )
        }
        // save deal
        Icon(
            imageVector = if (deal.userSaved && uiState.isLoggedIn) {
                Icons.Filled.Favorite
            } else {
                Icons.Outlined.FavoriteBorder
            },
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier
                .padding(top = 8.dp)
                .size(40.dp)
                .clickable { onSaveClicked() }
        )
    }

    if (deal.expiryDate != null) {
        Spacer(modifier = Modifier.height(8.dp))

        Row {
            Icon(
                Icons.Filled.CalendarMonth,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
                    .size(18.dp)
            )
            Spacer(modifier = Modifier.width(2.dp))
            Text(
                text = " Valid Until: " + deal.expiryDate.format(
                    DateTimeFormatter.ofPattern(
                        "MMMM dd, yyyy"
                    )
                ),
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White,
            )
        }
    }
    Spacer(modifier = Modifier.height(4.dp))
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
}

@Composable
fun DealDetailBox(
    deal: Deal,
) {
    Box(
        modifier = Modifier
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
            val dealTitle: String =
                if (deal.price != 0.0)
                    "$" + deal.price.toString() + " " + deal.item
                else
                    deal.item

            Text(
                text = dealTitle,
                style = defaultTextStyle.copy(
                    fontSize = 20.sp,
                    lineHeight = 20.sp,
                    letterSpacing = 0.15.sp,
                    fontWeight = FontWeight.SemiBold,
                    lineBreak = LineBreak.Heading
                ),
                color = MaterialTheme.colorScheme.primaryContainer,
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

        }
    }
}


@Composable
fun DealAvailability(
    deal: Deal,
    navController: NavController,
) {
    Box(
        modifier = Modifier
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
            if (deal.activeDayTime != DayOfWeekAndTimeRestriction.NoRestriction) {
                Text(
                    text = "Available on:",
                    style = defaultTextStyle.copy(
                        fontSize = 16.sp,
                        lineHeight = 24.sp,
                        letterSpacing = 0.15.sp,
                        fontWeight = FontWeight.Medium,
                        lineBreak = LineBreak.Heading,
                    ),
                    color = MaterialTheme.colorScheme.primaryContainer

                )

                Row {
                    Text(
                        text = deal.activeDayTime.toDisplayDay(),
                        style = defaultTextStyle.copy(
                            fontSize = 16.sp,
                            lineHeight = 24.sp,
                            letterSpacing = 0.15.sp,
                            fontWeight = FontWeight.Light,
                            lineBreak = LineBreak.Heading,
                        ),
                        color = MaterialTheme.colorScheme.primaryContainer
                    )
                    if(deal.activeDayTime.toDisplayTime() != "Available all day"){
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = deal.activeDayTime.toDisplayTime(),
                            style = defaultTextStyle.copy(
                                fontSize = 16.sp,
                                lineHeight = 24.sp,
                                letterSpacing = 0.15.sp,
                                fontWeight = FontWeight.Light,
                                lineBreak = LineBreak.Heading,
                            ),
                            color = MaterialTheme.colorScheme.primaryContainer
                        )
                    }

                }
            }

            if ((deal.applicableGroup != ApplicableGroup.NONE
                        && deal.applicableGroup != ApplicableGroup.ALL) &&
                (deal.activeDayTime != DayOfWeekAndTimeRestriction.NoRestriction)
            ) {
                Spacer(modifier = Modifier.height(16.dp))
            }
            if (
                deal.applicableGroup != ApplicableGroup.NONE
                && deal.applicableGroup != ApplicableGroup.ALL
            ) {


                Text(
                    text = "Available to:",
                    style = defaultTextStyle.copy(
                        fontSize = 16.sp,
                        lineHeight = 24.sp,
                        letterSpacing = 0.15.sp,
                        fontWeight = FontWeight.Medium,
                        lineBreak = LineBreak.Heading
                    ),
                    color = MaterialTheme.colorScheme.primaryContainer

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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginPrompt(
    setShowBottomSheet: (Boolean) -> Unit,
    navController: NavController,
) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()
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

            Button(
                onClick = {
                    navController.navigate(Destinations.LOGIN_ROUTE)
                    setShowBottomSheet(false)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Login",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
            }
            Button(
                onClick = {
                    navController.navigate(Destinations.SIGNUP_ROUTE)
                    setShowBottomSheet(false)
                },
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

@Composable
fun DeleteConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Delete Deal")
        },
        text = {
            Text(text = "Are you sure you want to delete this deal?")
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Yes")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("No")
            }
        }
    )
}
