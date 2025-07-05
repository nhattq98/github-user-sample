package com.tahn.assignment.detail

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.tahn.assignment.R
import com.tahn.assignment.home.LinkAnnotationTest
import com.tahn.assignment.home.painterResourcePlaceholder
import com.tahn.assignment.model.GithubUserDetail
import com.tahn.assignment.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GithubUserDetailScreen(
    viewModel: GithubUserDetailViewModel,
    navigateBack: () -> Unit,
) {
    val uiState by viewModel.githubUserDetailUiState.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.snackbarMessage.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        topBar = {
            TopBar(title = "User Detail") {
                navigateBack.invoke()
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
        ) {
            when {
                uiState.isLoading ->
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                    )

                else -> {
                    GithubUserDetailContent(
                        userDetail = uiState.userDetail,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
        }
    }
}

@Composable
fun GithubUserDetailContent(
    modifier: Modifier,
    userDetail: GithubUserDetail?,
) {
    Column(modifier = modifier.padding(horizontal = 12.dp)) {
        userDetail?.let {
            UserProfileCard(
                username = userDetail.login,
                avatarUrl = userDetail.avatarUrl,
                location = userDetail.location.orEmpty(),
            )

            FollowerFollowingRow(
                follower = userDetail.followers?.toString(),
                following = userDetail.following?.toString(),
            )

            userDetail.htmlUrl?.let {
                UserDescription(url = it)
            }
        }
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

@Composable
fun UserProfileCard(
    modifier: Modifier = Modifier,
    username: String?,
    avatarUrl: String?,
    location: String?,
) {
    Card(
        modifier =
            modifier
                .height(100.dp)
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceContainerLowest)
                .shadow(2.dp, RoundedCornerShape(12.dp)),
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
                    model = avatarUrl,
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
                    username ?: stringResource(R.string.unknown),
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.height(4.dp))
                HorizontalDivider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                )
                Spacer(Modifier.height(4.dp))
                location?.let {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Place,
                            contentDescription = "Location",
                            tint = Color.Gray,
                            modifier = Modifier.size(16.dp),
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FollowerFollowingRow(
    follower: String?,
    following: String?,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        InfoItem(
            icon = Icons.Default.Person,
            count = follower ?: stringResource(R.string.unknown),
            label = stringResource(R.string.follower),
        )
        InfoItem(
            icon = Icons.Default.Person,
            count = following ?: stringResource(R.string.unknown),
            label = stringResource(R.string.following),
        )
    }
}

@Composable
fun InfoItem(
    icon: ImageVector,
    count: String,
    label: String,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier =
                Modifier
                    .size(48.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceContainerHighest,
                        shape = CircleShape,
                    ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = count,
            style = MaterialTheme.typography.titleMedium,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
        )
    }
}

@Composable
fun UserDescription(
    modifier: Modifier = Modifier,
    url: String,
) {
    Column(modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.blog),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(Modifier.height(8.dp))
        LinkAnnotationTest(url)
    }
}

@Preview
@Composable
fun PreviewUserProfileCard() {
    AppTheme {
        UserProfileCard(
            modifier = Modifier,
            username = "tahn",
            avatarUrl = "",
            location = "Vietnam",
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewFollowerFollowingRow() {
    AppTheme {
        FollowerFollowingRow("100", "200")
    }
}
