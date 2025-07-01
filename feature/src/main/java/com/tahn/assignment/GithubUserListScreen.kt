package com.tahn.assignment

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tahn.assignment.theme.AppTheme

@Composable
fun GithubUserListScreen() {
    Scaffold { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) { }
    }
}

@Composable
fun GithubUserItem(modifier: Modifier) {
    Card(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(8.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewGithubUserItem() {
    AppTheme {

    }
}