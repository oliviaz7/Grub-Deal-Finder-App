import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


/**
 * A composable dialog that allows users to select custom filters for deals. This dialog displays filter options
 * for different deal types (e.g., Free, Discount, BOGO) and provides options to confirm or dismiss the selection.
 *
 * @param selectedCustomFilter The current selected custom filter values, including the types of deals selected by the user.
 * @param onSelectCustomFilter A callback function that updates the selected filter when a filter chip is clicked.
 *                             It takes two `String` parameters: the filter type (e.g., "type") and the selected filter value (e.g., "Free").
 * @param onSubmitCustomFilter A callback function triggered when the user confirms their filter selection.
 *                             It is used to submit the selected filters.
 * @param onShowFilterDialog A callback function that controls the visibility of the filter dialog.
 *                           It accepts a `Boolean` to show or hide the dialog.
 *
 * This dialog displays filter chips for each deal type, allowing the user to select one or more deal types to filter by.
 * The dialog includes a confirm button to submit the selected filters and a dismiss button to close the dialog without changes.
 *
 *
 */
@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CustomFilterDialog(
    selectedCustomFilter: CustomFilter,
    onSelectCustomFilter: (String, String) -> Unit,
    onSelectPriceRange: (Int, Int) -> Unit,
    onSubmitCustomFilter: () -> Unit,
    onToggleAvailableNow: (Boolean) -> Unit,
    onShowFilterDialog: (Boolean) -> Unit,
    onClearOptions: () -> Unit,
) {
    val filterTypes = listOf("FREE", "DISCOUNT", "BOGO", "OTHER")
    val filterDays =
        listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
    val filterRestrictions =
        listOf("Under 18", "Senior", "Student", "Loyalty Member", "New User", "Birthday")
    AlertDialog(
        onDismissRequest = {
            onShowFilterDialog(false)
        },
        title = { Text(text = "Custom Filter") },
        text = {
            Column {
                Text(text = "Deal Type", style = MaterialTheme.typography.bodyLarge)
                LazyRow {
                    items(filterTypes) { filterType ->
                        Spacer(Modifier.width(2.dp))
                        ElevatedFilterChip(
                            selected = selectedCustomFilter.type.contains(filterType),
                            onClick = { onSelectCustomFilter("type", filterType) },
                            label = { Text(text = filterType.capitalize()) },
                            leadingIcon =
                            if (selectedCustomFilter.type.contains(filterType)) {
                                {
                                    Icon(
                                        imageVector = Icons.Filled.Done,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .padding(end = 4.dp)
                                            .size(16.dp)
                                    )
                                }
                            } else {
                                null
                            },
                            modifier = Modifier
                        )
                        Spacer(Modifier.width(6.dp))
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))


                Text(text = "Available to", style = MaterialTheme.typography.bodyLarge)
                LazyRow {
                    items(filterRestrictions) { filterRestriction ->
                        Spacer(Modifier.width(2.dp))
                        ElevatedFilterChip(
                            selected = selectedCustomFilter.restrictions.contains(filterRestriction),
                            onClick = { onSelectCustomFilter("restrictions", filterRestriction) },
                            label = { Text(filterRestriction) },
                            leadingIcon =
                                if (selectedCustomFilter.restrictions.contains(filterRestriction)) {
                                    {
                                        Icon(
                                            imageVector = Icons.Filled.Done,
                                            contentDescription = null,
                                            modifier = Modifier
                                                .padding(end = 4.dp)
                                                .size(16.dp)
                                        )
                                    }
                                } else {
                                    null
                                },
                            modifier = Modifier
                        )
                        Spacer(Modifier.width(6.dp))
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))

                Text(text = "Available on", style = MaterialTheme.typography.bodyLarge)
                LazyRow {
                    items(filterDays) { filterDay ->
                        Spacer(Modifier.width(2.dp))
                        ElevatedFilterChip(
                            selected = selectedCustomFilter.day.contains(filterDay),
                            onClick = { onSelectCustomFilter("day", filterDay) },
                            label = { Text(filterDay) },
                            leadingIcon =
                            if (selectedCustomFilter.day.contains(filterDay)) {
                                {
                                    Icon(
                                        imageVector = Icons.Filled.Done,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .padding(end = 4.dp)
                                            .size(16.dp)
                                    )
                                }
                            } else {
                                null
                            },
                            modifier = Modifier
                        )
                        Spacer(Modifier.width(6.dp))
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))


                Row (
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 2.dp) {
                        Checkbox(
                            checked = selectedCustomFilter.onlyIncludeAvailableNow,
                            onCheckedChange = { newValue -> onToggleAvailableNow(newValue) },
                            modifier = Modifier//.background(Color.Cyan)
                                .padding(end = 8.dp)
                        )
                    }
                    Text(
                        text = "Available now",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))


                val priceRangeInitial = selectedCustomFilter.minPrice..selectedCustomFilter.maxPrice
                var priceRange by remember(priceRangeInitial) {
                    mutableStateOf(priceRangeInitial)
                }
                Text(
                    text = "Price Range: $${priceRange.start.toInt()} - $${priceRange.endInclusive.toInt()}",
                    style = MaterialTheme.typography.bodyLarge
                )
                RangeSlider(
                    value = priceRange,
                    onValueChange = { newRange -> priceRange = newRange },
                    valueRange = 0f..100f,
                    modifier = Modifier.padding(vertical = 4.dp),
                    onValueChangeFinished = {
                        onSelectPriceRange(
                            priceRange.start.toInt(),
                            priceRange.endInclusive.toInt()
                        )
                    },
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSubmitCustomFilter()
                }
            )
            { Text("Confirm") }

        },
        dismissButton = {
            TextButton(onClick = { onClearOptions() }) { Text("Clear") }
        }
    )
}
