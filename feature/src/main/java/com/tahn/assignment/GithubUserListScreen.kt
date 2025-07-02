package com.tahn.assignment

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.tahn.assignment.model.GithubUser
import com.tahn.assignment.theme.AppTheme

@Composable
fun GithubUserListScreen(viewModel: GithubUserListViewModel) {
    val lazyPagingItems = viewModel.userPagingSource.collectAsLazyPagingItems()

    Scaffold { innerPadding ->
        GithubUserContent(modifier = Modifier.padding(innerPadding), users = lazyPagingItems)
    }
}

@Composable
fun GithubUserContent(
    modifier: Modifier = Modifier,
    users: LazyPagingItems<GithubUser>,
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.padding(8.dp),
    ) {
        items(users.itemCount) { index ->
            val user = users[index]
            user?.let {
                GithubUserItem(it)
            }
        }
    }
}

@Composable
fun GithubUserItem(user: GithubUser) {
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .shadow(2.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Row(
            modifier =
                Modifier
                    .padding(12.dp)
                    .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AsyncImage(
                model = user.avatarUrl,
                contentDescription = null,
                modifier =
                    Modifier
                        .size(56.dp)
                        .clip(CircleShape),
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(user.username ?: "Unknown", fontWeight = FontWeight.Bold)
                user.profileUrl?.let {
                    Text(
                        text = it,
                        color = Color.Blue,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewGithubUserItem() {
    val mockUser =
        GithubUser(
            id = 0,
            username = "tahn",
            avatarUrl = "",
            profileUrl = "https://profile.url",
        )
    AppTheme {
        GithubUserItem(mockUser)
    }
}
