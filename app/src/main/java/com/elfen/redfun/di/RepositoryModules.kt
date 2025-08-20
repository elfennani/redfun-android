package com.elfen.redfun.di

import com.elfen.redfun.data.SessionRepositoryImpl
import com.elfen.redfun.domain.repository.SessionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@Module
@InstallIn(ActivityComponent::class)
abstract class RepositoryModules {
    @Binds
    abstract fun bindSessionRepository(impl: SessionRepositoryImpl): SessionRepository
}