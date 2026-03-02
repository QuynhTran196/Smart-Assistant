package com.smartassistant.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.smartassistant.app.ui.theme.BarlowFontFamily

/**
 * Add Action Dialog
 *
 * Modal dialog for adding files or starting a new chat.
 * Uses theme colors for consistent styling.
 */
@Composable
fun AddActionDialog(
    onUploadClick: () -> Unit,
    onNewChatClick: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(vertical = 8.dp)
        ) {

            DialogItem(
                text = "Upload file",
                onClick = {
                    onUploadClick()
                    onDismiss()
                }
            )

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 20.dp),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant
            )

            DialogItem(
                text = "New chat",
                onClick = {
                    onNewChatClick()
                    onDismiss()
                }
            )
        }
    }
}

@Composable
private fun DialogItem(
    text: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(
                horizontal = 20.dp,
                vertical = 16.dp
            )
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = BarlowFontFamily,
            color = MaterialTheme.colorScheme.primary
        )
    }
}


