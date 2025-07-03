package com.tahn.assignment.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.tahn.assignment.model.GithubUser
import com.tahn.assignment.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GithubUserListScreen(viewModel: GithubUserListViewModel) {
    val lazyPagingItems = viewModel.userPagingSource.collectAsLazyPagingItems()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val pullToRefreshState = rememberPullToRefreshState()

    LaunchedEffect(lazyPagingItems.loadState) {
        val loadState = lazyPagingItems.loadState

        when (val refreshState = loadState.refresh) {
            is LoadState.Loading -> {
                if (!uiState.isRefreshing) {
                    viewModel.setRefreshing(true)
                }
            }

            is LoadState.NotLoading,
            is LoadState.Error,
            -> {
                viewModel.setRefreshing(false)
            }
        }

        val error =
            when {
                loadState.refresh is LoadState.Error -> loadState.refresh as LoadState.Error
                loadState.append is LoadState.Error -> loadState.append as LoadState.Error
                else -> null
            }

        error?.let {
            snackbarHostState.showSnackbar(
                message = it.error.localizedMessage ?: "Unknown error",
                actionLabel = "Retry",
            )
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopBar(title = "Github Users") {}
        },
    ) { innerPadding ->
        GithubUserContent(
            modifier = Modifier.padding(innerPadding),
            pullToRefreshState = pullToRefreshState,
            users = lazyPagingItems,
            uiState = uiState,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    title: String,
    onBackClick: () -> Unit,
) {
    TopAppBar(
        title = {
            Text(text = title)
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                )
            }
        },
        colors =
            TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = MaterialTheme.colorScheme.onPrimary,
                navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
            ),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GithubUserContent(
    modifier: Modifier = Modifier,
    pullToRefreshState: PullToRefreshState,
    users: LazyPagingItems<GithubUser>,
    uiState: GithubUserListUiState,
) {
    PullToRefreshBox(
        state = pullToRefreshState,
        isRefreshing = uiState.isRefreshing,
        onRefresh = {
            users.refresh()
        },
        modifier = modifier,
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(8.dp),
        ) {
            // TODO: safe check in here
            items(users.itemCount) { index ->
                val user = users[index]
                user?.let {
                    GithubUserItem(it)
                }
            }

            users.apply {
                when {
                    loadState.append is LoadState.Loading -> {
                        item {
                            PagingBottomLoadingIndicator(
                                modifier = Modifier.padding(8.dp),
                            )
                        }
                    }
                }
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

@Composable
fun PagingBottomLoadingIndicator(modifier: Modifier = Modifier) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .wrapContentHeight(),
    ) {
        LinearProgressIndicator(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )
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
