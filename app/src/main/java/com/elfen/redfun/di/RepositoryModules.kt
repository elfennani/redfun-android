package com.elfen.redfun.di

import com.elfen.redfun.data.FeedRepositoryImpl
import com.elfen.redfun.data.ProfileRepositoryImpl
import com.elfen.redfun.data.SessionRepositoryImpl
import com.elfen.redfun.data.SettingsRepositoryImpl
import com.elfen.redfun.domain.repository.FeedRepository
import com.elfen.redfun.domain.repository.ProfileRepository
import com.elfen.redfun.domain.repository.SessionRepository
import com.elfen.redfun.domain.repository.SettingsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModules {
    @Binds
    abstract fun bindSessionRepository(impl: SessionRepositoryImpl): SessionRepository

    @Binds
    abstract fun bindFeedRepository(impl: FeedRepositoryImpl): FeedRepository

    @Binds
    abstract fun bindSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository

    @Binds
    abstract fun bindProfileRepository(impl: ProfileRepositoryImpl): ProfileRepository
}