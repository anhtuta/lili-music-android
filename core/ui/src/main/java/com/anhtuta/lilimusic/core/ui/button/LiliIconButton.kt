package com.anhtuta.lilimusic.core.ui.button

import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiliIconButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    tint: Color = Color.Unspecified,
    iconSize: Dp = 24.dp,
    containerSize: Dp? = null,
) {
    LiliIconButton(
        label = label,
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        containerSize = containerSize,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier =
                Modifier
                    .size(iconSize)
                    .testTag(LiliIconButtonTags.GLYPH),
            tint =
                if (tint == Color.Unspecified) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    tint
                },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiliIconButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    containerSize: Dp? = null,
    content: @Composable () -> Unit,
) {
    TooltipBox(
        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
        tooltip = { PlainTooltip { Text(text = label) } },
        state = rememberTooltipState(),
        modifier = modifier,
    ) {
        IconButton(
            onClick = onClick,
            enabled = enabled,
            modifier =
                Modifier
                    .then(
                        if (containerSize != null) {
                            Modifier
                                .minimumInteractiveComponentSize()
                                .size(containerSize)
                        } else {
                            Modifier
                        },
                    ).semantics { contentDescription = label },
        ) {
            content()
        }
    }
}
