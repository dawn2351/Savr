package com.zarnth.savr.presentation.home.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.activity.compose.BackHandler
import com.zarnth.savr.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditBookmarkSheet(
    showBottomSheet: Boolean,
    onDismissRequest: () -> Unit,
    titleValue: String,
    onTitleChange: (String) -> Unit,
    descriptionValue: String,
    onDescriptionChange: (String) -> Unit,
    onSaveClick: () -> Unit,
    url: String,
    imageUrl: String?
) {
    if (showBottomSheet) {
        var visible by remember { mutableStateOf(false) }
        val progress by animateFloatAsState(
            targetValue = if (visible) 1f else 0f,
            animationSpec = tween(320),
            label = "slide"
        )

        LaunchedEffect(Unit) {
            visible = true
        }

        BackHandler(onBack = onDismissRequest)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    translationY = (1 - progress) * 300
                    alpha = if (progress > 0f) 1f else 0f
                }
                .background(MaterialTheme.colorScheme.surfaceContainerLow)
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .width(36.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f))
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(Modifier.width(20.dp))
                Text(
                    text = "Edit Bookmark",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
                IconButton(onClick = onDismissRequest) {
                    Icon(
                        painter = painterResource(R.drawable.close_icon),
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .imePadding()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 32.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                BookmarkCard(
                    imageUrl = imageUrl,
                    title = titleValue.ifBlank { null },
                    description = descriptionValue.ifBlank { null },
                    photoClickUrl = {},
                    bodyClick = {},
                    onLongClick = {},
                    url = url,
                    modifier = Modifier.animateContentSize()
                )

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = titleValue,
                    onValueChange = onTitleChange,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    label = {
                        Text("Title")
                    },
                    trailingIcon = {
                        if (titleValue.isNotEmpty()) {
                            ClearTextTrailingIcon { onTitleChange("") }
                        }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = descriptionValue,
                    onValueChange = onDescriptionChange,
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 6,
                    label = {
                        Text("Description")
                    },
                    trailingIcon = {
                        if (descriptionValue.isNotEmpty()) {
                            ClearTextTrailingIcon { onDescriptionChange("") }
                        }
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        onSaveClick()
                        onDismissRequest()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        painter = painterResource(R.drawable.edit_icon),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Save Changes")
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(
                    onClick = onDismissRequest,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cancel")
                }
            }
        }
    }
}

@Composable
private fun ClearTextTrailingIcon(onClear: () -> Unit) {
    IconButton(onClick = onClear) {
        Icon(
            painter = painterResource(R.drawable.close_icon),
            contentDescription = "Clear",
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}