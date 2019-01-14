package com.thomaskioko.paybillmanager.mobile.injection.module

import com.nhaarman.mockitokotlin2.mock
import com.thomaskioko.paybillmanager.data.repository.jengatoken.JengaRemote
import com.thomaskioko.paybillmanager.remote.service.JengaService
import dagger.Module
import dagger.Provides

@Module
object TestRemoteModule {


    @Provides
    @JvmStatic
    fun provideJengaService(): JengaService {
        return mock()
    }

    @Provides
    @JvmStatic
    fun provideTokenRemote(): JengaRemote {
        return mock()
    }
}