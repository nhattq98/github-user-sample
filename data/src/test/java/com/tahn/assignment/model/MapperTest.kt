package com.tahn.assignment.model

import com.tahn.assignment.local.database.entity.GithubUserEntity
import com.tahn.assignment.model.remote.GithubUserDetailResponse
import com.tahn.assignment.model.remote.GithubUserResponse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class MapperTest {
    @Test
    fun toEntity_returns_null_when_id_is_null() {
        val response =
            GithubUserResponse(
                id = null,
                login = "nhattran",
                avatarUrl = "avatar.com",
                url = "profile.com",
            )

        val result = response.toEntity()

        assertNull(result)
    }

    @Test
    fun toEntity_returns_valid_GithubUserEntity() {
        val response =
            GithubUserResponse(
                id = 123,
                login = "nhattran",
                avatarUrl = "avatar.com",
                url = "profile.com",
            )

        val result = response.toEntity()

        assertNotNull(result)
        assertEquals(123, result!!.id)
        assertEquals("nhattran", result.login)
        assertEquals("avatar.com", result.avatarUrl)
        assertEquals("profile.com", result.profileUrl)
    }

    @Test
    fun toDomain_from_GithubUserEntity_maps_correctly() {
        val entity =
            GithubUserEntity(
                id = 1,
                login = "user",
                avatarUrl = "avatar",
                profileUrl = "profile",
            )

        val result = entity.toDomain()

        assertEquals(1, result.id)
        assertEquals("user", result.username)
        assertEquals("avatar", result.avatarUrl)
        assertEquals("profile", result.profileUrl)
    }

    @Test
    fun `toDomain_from_GithubUserDetailResponse_with_null_id_returns_default_-1`() {
        val detail =
            GithubUserDetailResponse(
                id = null,
                login = "test",
                avatarUrl = "img",
                htmlUrl = "html",
                location = "VN",
                followers = 100,
                following = 200,
            )

        val result = detail.toDomain()

        assertEquals(-1, result.id)
        assertEquals("test", result.login)
        assertEquals("img", result.avatarUrl)
        assertEquals("html", result.htmlUrl)
        assertEquals("VN", result.location)
        assertEquals(100, result.followers)
        assertEquals(200, result.following)
    }
}
