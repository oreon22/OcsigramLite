package ocsigram.lite.org.core.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ocsigram.lite.org.core.flavor.BetaFlavorFeatures
import ocsigram.lite.org.core.flavor.FlavorFeatures
import ocsigram.lite.org.core.tdlib.BetaTdLogConfigurator
import ocsigram.lite.org.core.tdlib.TdLogConfigurator
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FlavorModule {
    @Binds
    @Singleton
    abstract fun logConfigurator(value: BetaTdLogConfigurator): TdLogConfigurator

    @Binds
    @Singleton
    abstract fun flavorFeatures(value: BetaFlavorFeatures): FlavorFeatures
}
