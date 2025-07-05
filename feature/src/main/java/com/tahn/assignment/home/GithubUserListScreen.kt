package com.tahn.assignment.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.tahn.assignment.R
import com.tahn.assignment.model.GithubUser
import com.tahn.assignment.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GithubUserListScreen(
    viewModel: GithubUserListViewModel,
    onNavigateToUserDetail: (username: String) -> Unit,
) {
    val lazyPagingItems = viewModel.userPagingSource.collectAsLazyPagingItems()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val pullToRefreshState = rememberPullToRefreshState()

    LaunchedEffect(lazyPagingItems.loadState) {
        val loadState = lazyPagingItems.loadState

        when (loadState.refresh) {
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
            )
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopBar(title = stringResource(R.string.github_users)) {}
        },
    ) { innerPadding ->
        GithubUserContent(
            modifier = Modifier.padding(innerPadding),
            pullToRefreshState = pullToRefreshState,
            users = lazyPagingItems,
            uiState = uiState,
            onNavigateToUserDetail = onNavigateToUserDetail,
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
                containerColor = MaterialTheme.colorScheme.surface,
                titleContentColor = MaterialTheme.colorScheme.onSurface,
                navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
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
    onNavigateToUserDetail: (String) -> Unit,
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
                    .padding(horizontal = 8.dp),
        ) {
            items(
                count = users.itemCount,
                key = users.itemKey { it.id },
            ) { index ->
                val user = users[index]
                user?.let {
                    GithubUserItem(it, onNavigateToUserDetail)
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
fun GithubUserItem(
    user: GithubUser,
    onNavigateToUserDetail: (String) -> Unit,
) {
    val context = LocalContext.current

    val imageRequest =
        remember(user.avatarUrl) {
            ImageRequest
                .Builder(context)
                .data(user.avatarUrl)
                .crossfade(true)
                .diskCachePolicy(CachePolicy.ENABLED)
                .memoryCachePolicy(CachePolicy.ENABLED)
                .build()
        }

    Card(
        modifier =
            Modifier
                .height(100.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerLowest)
                .shadow(2.dp)
                .clickable {
                    user.username?.let {
                        onNavigateToUserDetail.invoke(it)
                    }
                },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier =
                Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier =
                    Modifier
                        .background(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                        ).padding(8.dp),
                contentAlignment = Alignment.Center,
            ) {
                AsyncImage(
                    model = imageRequest,
                    contentDescription = null,
                    placeholder = painterResourcePlaceholder(),
                    error = painterResourcePlaceholder(),
                    modifier =
                        Modifier
                            .size(56.dp)
                            .clip(CircleShape),
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.fillMaxHeight(),
            ) {
                Text(
                    user.username ?: stringResource(R.string.unknown),
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.height(4.dp))
                HorizontalDivider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                )
                Spacer(Modifier.height(4.dp))
                user.profileUrl?.let {
                    LinkAnnotationTest(it)
                }
            }
        }
    }
}

@Composable
fun LinkAnnotationTest(url: String) {
    val annotatedLinkString: AnnotatedString =
        remember {
            buildAnnotatedString {
                val styleCenter =
                    SpanStyle(
                        fontSize = 14.sp,
                        textDecoration = TextDecoration.Underline,
                    )

                withLink(LinkAnnotation.Url(url = url)) {
                    withStyle(
                        style = styleCenter,
                    ) {
                        append(url)
                    }
                }
            }
        }

    Text(annotatedLinkString, color = MaterialTheme.colorScheme.primary)
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

@Composable
fun painterResourcePlaceholder(): Painter = rememberVectorPainter(Icons.Default.Person)

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
        GithubUserItem(mockUser, {})
    }
}
