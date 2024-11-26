package com.valoy.overplay.presentation.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.valoy.overplay.R
import com.valoy.overplay.presentation.theme.OverplayTheme

@Composable
fun MainScreen(viewModel: MainViewModel) {
    // Collect the UI state by subscribing to the GyroscopeSensorListener
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Collect the UI state by listening to the GyroscopeSensorFlow
    val gyroscopeSensorFlow by viewModel.gyroscopeStateFlow.collectAsStateWithLifecycle()

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Session(
            session = uiState.sessionCount,
            size = uiState.letterSize,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        )
    }
}

@Composable
private fun Session(session: Int, size: Int, modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Text(
            text = stringResource(id = R.string.session_count, session),
            fontSize = size.sp,
            modifier = Modifier.wrapContentSize()
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SessionPreview() {
    OverplayTheme {
        Session(1, 12)
    }
}
