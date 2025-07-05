package com.tahn.assignment.local.database.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.tahn.assignment.local.database.AppDatabase
import com.tahn.assignment.local.database.entity.GithubUserRemoteKeyEntity
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class GithubUserRemoteKeyDaoTest {
    private lateinit var db: AppDatabase
    private lateinit var dao: GithubUserRemoteKeyDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db =
            Room
                .inMemoryDatabaseBuilder(context, AppDatabase::class.java)
                .allowMainThreadQueries()
                .build()
        dao = db.githubUserRemoteKeyDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun insertAndGetRemoteKey_shouldReturnCorrectData() =
        runTest {
            val expectedRemoteKey =
                GithubUserRemoteKeyEntity(userId = 1, prevKey = null, nextKey = 2)
            val remoteKey =
                listOf(
                    expectedRemoteKey,
                    GithubUserRemoteKeyEntity(userId = 2, prevKey = null, nextKey = 3),
                )

            dao.insertAll(remoteKey)
            val result = dao.getRemoteKeyByUserId(1)

            assertNotNull(result)
            assertEquals(expectedRemoteKey.userId, result?.userId)
            assertEquals(expectedRemoteKey.prevKey, result?.prevKey)
            assertEquals(expectedRemoteKey.nextKey, result?.nextKey)
        }

    @Test
    fun clearAll_shouldDeleteAllRemoteKeys() =
        runTest {
            val remoteKey = GithubUserRemoteKeyEntity(userId = 1, prevKey = null, nextKey = 2)
            dao.insertAll(listOf(remoteKey))

            dao.clearAll()
            val result = dao.getRemoteKeyByUserId(1)

            assertNull(result)
        }
}
