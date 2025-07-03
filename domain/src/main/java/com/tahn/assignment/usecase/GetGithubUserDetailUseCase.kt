package com.tahn.assignment.usecase

import com.tahn.assignment.model.GithubUserDetail
import com.tahn.assignment.repository.GithubUserRepository
import kotlinx.coroutines.flow.Flow

class GetGithubUserDetailUseCase(
    private val githubUserRepository: GithubUserRepository,
) {
    operator fun invoke(username: String): Flow<GithubUserDetail> = githubUserRepository.getGithubUserDetail(username)
}
