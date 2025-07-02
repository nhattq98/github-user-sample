package com.tahn.assignment.usecase

import androidx.paging.PagingData
import com.tahn.assignment.model.GithubUser
import com.tahn.assignment.repository.GithubUserRepository
import kotlinx.coroutines.flow.Flow

class GetGithubUsersPagingUseCase(
    private val githubUserRepository: GithubUserRepository,
) {
    operator fun invoke(): Flow<PagingData<GithubUser>> = githubUserRepository.getGithubUsersPaging()
}
