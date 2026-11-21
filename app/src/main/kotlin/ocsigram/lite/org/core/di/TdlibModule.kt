package ocsigram.lite.org.core.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ocsigram.lite.org.core.tdlib.TdClient
import ocsigram.lite.org.core.tdlib.TdGateway
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TdlibModule {
    @Binds
    @Singleton
    abstract fun gateway(client: TdClient): TdGateway
}
