package com.anhtuta.lilimusic.feature.library

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.anhtuta.lilimusic.core.model.SongSort
import com.anhtuta.lilimusic.core.model.SortOrder
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun LibrarySortPill(
    selected: SongSort,
    sortOrder: SortOrder,
    onSortFieldChosen: (SongSort) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val fieldLabel = stringResource(selected.labelRes())
    val orderLabel =
        stringResource(
            if (sortOrder == SortOrder.ASC) {
                R.string.library_sort_order_asc
            } else {
                R.string.library_sort_order_desc
            },
        )
    val tooltip = stringResource(R.string.library_sort_tooltip, fieldLabel, orderLabel.lowercase(Locale.US))
    val arrow =
        if (sortOrder == SortOrder.ASC) {
            Icons.Filled.ArrowUpward
        } else {
            Icons.Filled.ArrowDownward
        }
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box {
            TooltipBox(
                positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                tooltip = { PlainTooltip { Text(text = tooltip) } },
                state = rememberTooltipState(),
            ) {
                SortPillButton(
                    fieldLabel = fieldLabel,
                    orderLabel = orderLabel,
                    tooltip = tooltip,
                    arrow = arrow,
                    onClick = { expanded = true },
                )
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.testTag(LibraryTestTags.SORT_MENU),
            ) {
                LibrarySortMenuItems(
                    selected = selected,
                    orderLabel = orderLabel,
                    arrow = arrow,
                    onSortFieldChosen = { sort ->
                        onSortFieldChosen(sort)
                        expanded = false
                    },
                )
            }
        }
    }
}

@Composable
private fun LibrarySortMenuItems(
    selected: SongSort,
    orderLabel: String,
    arrow: ImageVector,
    onSortFieldChosen: (SongSort) -> Unit,
) {
    SongSort.entries.forEach { sort ->
        val label = stringResource(sort.labelRes())
        DropdownMenuItem(
            text = { Text(text = label) },
            onClick = { onSortFieldChosen(sort) },
            leadingIcon =
                if (sort == selected) {
                    {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = stringResource(R.string.library_sort_current),
                        )
                    }
                } else {
                    null
                },
            trailingIcon =
                if (sort == selected) {
                    {
                        Icon(
                            imageVector = arrow,
                            contentDescription = orderLabel,
                        )
                    }
                } else {
                    null
                },
            modifier = Modifier.testTag(LibraryTestTags.sort(sort)),
        )
    }
}

@Composable
private fun SortPillButton(
    fieldLabel: String,
    orderLabel: String,
    tooltip: String,
    arrow: ImageVector,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(50),
        color = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier =
            Modifier
                .testTag(LibraryTestTags.SORT_PILL)
                .semantics { contentDescription = tooltip },
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = fieldLabel.uppercase(Locale.US),
                style = MaterialTheme.typography.labelLarge,
            )
            Icon(
                imageVector = arrow,
                contentDescription = orderLabel,
                modifier =
                    Modifier
                        .padding(start = 8.dp)
                        .size(16.dp)
                        .testTag(LibraryTestTags.SORT_ORDER),
            )
        }
    }
}

internal fun SongSort.labelRes(): Int =
    when (this) {
        SongSort.TITLE -> R.string.library_sort_title
        SongSort.LISTENS -> R.string.library_sort_listens
        SongSort.SONG_OF_THE_YEAR -> R.string.library_sort_song_of_the_year
        SongSort.TYPE -> R.string.library_sort_type
        SongSort.CREATED_DATE -> R.string.library_sort_created_date
    }
