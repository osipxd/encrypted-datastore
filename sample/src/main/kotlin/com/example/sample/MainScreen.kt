@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.sample

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.sample.ui.theme.Motion.standardEnter
import com.example.sample.ui.theme.Motion.standardExit
import com.example.sample.ui.theme.SampleAppTheme

@Composable
fun MainScreen(viewModel: MainViewModel) {
    val state by viewModel.state.collectAsState()

    MainScreenContent(
        state = state,
        onThemeChange = viewModel::onThemeChange,
        onLikeClick = viewModel::onLikeClick,
        onResetLikesClick = viewModel::onResetLikesClick,
        onSelectStorage = viewModel::onSelectStorage,
        onClearClick = viewModel::onClearClick,
    )
}

@Composable
private fun MainScreenContent(
    state: MainScreenState,
    onThemeChange: (ThemeMode) -> Unit,
    onLikeClick: () -> Unit,
    onResetLikesClick: () -> Unit,
    onSelectStorage: (Boolean) -> Unit,
    onClearClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                actions = { ThemeButton(state.theme, onChange = onThemeChange) },
            )
        },
        floatingActionButton = {
            LikeButton(state.likes, onLikeClick, onResetLikesClick)
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Selected theme and likes count are stored in encrypted typed DataStore. Try to change theme or press the like button and then restart the app.")
            Text("We can use both typed DataStore and preferences DataStore for this task, so you can switch between storages.")

            Storage(
                title = "Typed DataStore",
                description = "Use kotlinx-serialization to serialize app settings into JSON.",
                content = state.typedStorage.dataDump,
                selected = state.useTypedStorage,
                onClick = { onSelectStorage(true) },
                onClearClick = onClearClick,
            )
            Storage(
                title = "Preferences DataStore",
                description = "Use default key-value SharedPreferences-like storage powered by ProtoBuf.",
                content = state.preferencesStorage.dataDump,
                selected = !state.useTypedStorage,
                onClick = { onSelectStorage(false) },
                onClearClick = onClearClick,
            )
        }
    }
}

@Composable
private fun ThemeButton(theme: ThemeMode, onChange: (ThemeMode) -> Unit) {
    var menuExpanded by remember { mutableStateOf(false) }

    IconButton(onClick = { menuExpanded = !menuExpanded }) {
        val icon = when (theme) {
            ThemeMode.Light -> Icons.Filled.LightMode
            ThemeMode.Dark -> Icons.Filled.DarkMode
            ThemeMode.Auto -> Icons.Filled.Contrast
        }

        Icon(icon, contentDescription = "Change App Theme")
    }

    DropdownMenu(menuExpanded, onDismissRequest = { menuExpanded = false }) {
        for (mode in ThemeMode.entries) {
            DropdownMenuItem(
                text = { Text("$mode Mode") },
                onClick = {
                    onChange(mode)
                    menuExpanded = false
                },
            )
        }
    }
}

private val TransformOrigin.Companion.BottomEnd
    get() = TransformOrigin(1f, 1f)

private val ClearEnterAnimation =
    scaleIn(standardEnter(), transformOrigin = TransformOrigin.BottomEnd) + fadeIn(standardEnter())
private val ClearExitAnimation =
    scaleOut(standardExit(), transformOrigin = TransformOrigin.BottomEnd) + fadeOut(standardExit())

@Composable
private fun LikeButton(
    likes: Int,
    onClick: () -> Unit,
    onResetClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        AnimatedVisibility(
            visible = likes > 0,
            enter = ClearEnterAnimation,
            exit = ClearExitAnimation,
        ) {
            SmallFloatingActionButton(
                onClick = onResetClick,
                containerColor = MaterialTheme.colorScheme.errorContainer,
            ) {
                Icon(Icons.Filled.Delete, contentDescription = "Clear")
            }
        }
        BadgedBox(badge = { LikesBadge(likes) }) {
            FloatingActionButton(onClick) {
                Icon(Icons.Filled.ThumbUp, contentDescription = "Like")
            }
        }
    }
}

@Composable
private fun LikesBadge(likes: Int) {
    AnimatedVisibility(likes > 0, enter = fadeIn(standardEnter()), exit = fadeOut(standardExit())) {
        Badge { Text("$likes", style = MaterialTheme.typography.bodyMedium) }
    }
}

private val SelectedBorder
    @Composable
    get() = BorderStroke(4.dp, MaterialTheme.colorScheme.primary)

@Composable
private fun Storage(
    title: String,
    description: String,
    content: String,
    selected: Boolean,
    onClick: () -> Unit,
    onClearClick: () -> Unit,
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
        ),
        border = SelectedBorder.takeIf { selected },
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(title, style = MaterialTheme.typography.headlineSmall)
            Text(description)
            StorageContent(content, onClearClick)
        }
    }
}

@Composable
private fun StorageContent(content: String, onClearClick: () -> Unit) {
    Surface(
        Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceContainerHighest,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(content, Modifier.padding(16.dp), fontFamily = FontFamily.Monospace)
            IconButton(onClearClick) {
                Icon(
                    Icons.Filled.Delete,
                    contentDescription = "Clear",
                    tint = MaterialTheme.colorScheme.error,
                )
            }
        }
    }
}

@Composable
@Preview
private fun Preview() {
    val state = MainScreenState()

    SampleAppTheme {
        MainScreenContent(
            state,
            onThemeChange = {},
            onLikeClick = {},
            onResetLikesClick = {},
            onSelectStorage = {},
            onClearClick = {},
        )
    }
}
