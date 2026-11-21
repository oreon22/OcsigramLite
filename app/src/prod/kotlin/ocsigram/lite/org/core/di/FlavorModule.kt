package ocsigram.lite.org.core.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ocsigram.lite.org.core.flavor.FlavorFeatures
import ocsigram.lite.org.core.flavor.ProdFlavorFeatures
import ocsigram.lite.org.core.tdlib.ProdTdLogConfigurator
import ocsigram.lite.org.core.tdlib.TdLogConfigurator
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FlavorModule {
    @Binds
    @Singleton
    abstract fun logConfigurator(value: ProdTdLogConfigurator): TdLogConfigurator

    @Binds
    @Singleton
    abstract fun flavorFeatures(value: ProdFlavorFeatures): FlavorFeatures
}
