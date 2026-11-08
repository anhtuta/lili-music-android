package com.anhtuta.lilimusic.feature.library

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.anhtuta.lilimusic.core.ui.button.LiliIconButton

internal val LibrarySearchHeight = 48.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun LibrarySearchField(
    query: String,
    onQueryChange: (String) -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val hint = stringResource(R.string.library_search_hint)
    val colors = OutlinedTextFieldDefaults.colors()
    BasicTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = LibraryChromeInsets.Horizontal)
                .height(LibrarySearchHeight)
                .testTag(LibraryTestTags.SEARCH),
        singleLine = true,
        textStyle =
            MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurface,
            ),
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        interactionSource = interactionSource,
        decorationBox = { innerTextField ->
            OutlinedTextFieldDefaults.DecorationBox(
                value = query,
                innerTextField = innerTextField,
                enabled = true,
                singleLine = true,
                visualTransformation = VisualTransformation.None,
                interactionSource = interactionSource,
                placeholder = { Text(text = hint) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = null,
                    )
                },
                trailingIcon =
                    if (query.isNotEmpty()) {
                        {
                            LiliIconButton(
                                icon = Icons.Filled.Close,
                                label = stringResource(R.string.library_clear_search),
                                onClick = { onQueryChange("") },
                                modifier = Modifier.testTag(LibraryTestTags.CLEAR_SEARCH),
                            )
                        }
                    } else {
                        null
                    },
                colors = colors,
                contentPadding =
                    OutlinedTextFieldDefaults.contentPadding(
                        start = 8.dp,
                        top = 8.dp,
                        end = 8.dp,
                        bottom = 8.dp,
                    ),
            )
        },
    )
}
