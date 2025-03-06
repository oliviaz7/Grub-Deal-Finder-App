package com.example.grub.ui.dealDetail

import DealImage
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbDownOffAlt
import androidx.compose.material.icons.filled.ThumbUpOffAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.grub.ui.theme.defaultTextStyle
import java.time.format.DateTimeFormatter


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DealDetailScreen(
    uiState: DealDetailUiState,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    Scaffold(
    ) { innerPadding ->
        val screenModifier = Modifier.padding(innerPadding)
        val deal = uiState.deal!!
        val restaurantName = uiState.restaurantName!!
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
                            contentDescription = "expiryIcon",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "address",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row() {
                        Icon(
                            Icons.Filled.Schedule,
                            contentDescription = "expiryIcon",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "Hours Open:",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

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
                        Icon(
                            Icons.Filled.Star,
                            contentDescription = "expiryIcon",
                            tint = Color.White,
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .size(40.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "${deal.type}",
                            style = MaterialTheme.typography.displayMedium,
                            color = Color.White,
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
                                contentDescription = "expiryIcon",
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
                    Spacer(modifier = Modifier.height(8.dp))
                    Row() {

                        Icon(
                            Icons.Filled.ThumbUpOffAlt,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier
                                .size(18.dp)
                                .scale(0.95f)
                                .padding(top = 4.dp)
                                .clickable { }, // Todo: -> handle click
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "7", //TODO: need to pull BE info
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White,
                        )
                        Spacer(modifier = Modifier.width(4.dp))

                        Icon(
                            Icons.Filled.ThumbDownOffAlt,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier
                                .size(18.dp)
                                .padding(top = 4.dp)
                                .scale(0.95f)
                                .clickable { }, // Todo -> handle click
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
                            if (deal.description != null) {
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
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Posted By: ${deal.userId}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White
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

                    Button(
                        onClick = { navController.popBackStack() },
                        colors = ButtonColors(
                            containerColor = MaterialTheme.colorScheme.background,
                            contentColor = MaterialTheme.colorScheme.primary,
                            disabledContainerColor = MaterialTheme.colorScheme.background,
                            disabledContentColor = MaterialTheme.colorScheme.primary,
                        ),
                        contentPadding = PaddingValues(12.dp, 0.dp),
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                    ) {
                        Text(
                            text = "Return to Deals",
                            style = defaultTextStyle.copy(
                                fontSize = 20.sp,
                                lineHeight = 20.sp,
                                letterSpacing = 0.15.sp,
                                fontWeight = FontWeight.SemiBold,
                                lineBreak = LineBreak.Heading
                            )
                        )
                    }
                }
            }
        }

    }
}