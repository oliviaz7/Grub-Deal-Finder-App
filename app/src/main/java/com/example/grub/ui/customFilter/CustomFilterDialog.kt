import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


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
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CustomFilterDialog(
    selectedCustomFilter: CustomFilter,
    onSelectCustomFilter: (String, String) -> Unit,
    onSubmitCustomFilter: () -> Unit,
    onShowFilterDialog: (Boolean) -> Unit,
) {
    AlertDialog(
        onDismissRequest = {
            onShowFilterDialog(false)
        },
        title = { Text(text = "Custom Filter") },
        text = {
            Column {
                Text(text = "Deal Type")
                Row {
                    ElevatedFilterChip(
                        selected = selectedCustomFilter.type.contains("FREE"),
                        onClick = { onSelectCustomFilter("type", "Free") },
                        label = { Text(text = "Free") },
                        modifier = Modifier,
                    )
                    ElevatedFilterChip(
                        selected = selectedCustomFilter.type.contains("DISCOUNT"),
                        onClick = { onSelectCustomFilter("type", "Discount") },
                        label = { Text(text = "Discount") },
                        modifier = Modifier,
                    )
                    ElevatedFilterChip(
                        selected = selectedCustomFilter.type.contains("BOGO"),
                        onClick = { onSelectCustomFilter("type", "BOGO") },
                        label = { Text(text = "BOGO") },
                        modifier = Modifier,
                    )
                }

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
            TextButton(onClick = { onShowFilterDialog(false) }) { Text("Dismiss") }
        }
    )

}
