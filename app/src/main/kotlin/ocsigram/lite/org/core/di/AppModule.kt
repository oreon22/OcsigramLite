package ocsigram.lite.org.core.di

import com.google.firebase.FirebaseApp
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ocsigram.lite.org.core.analytics.AnalyticsTracker
import ocsigram.lite.org.core.analytics.CrashReporter
import ocsigram.lite.org.core.analytics.FirebaseAnalyticsTracker
import ocsigram.lite.org.core.analytics.FirebaseCrashReporter
import ocsigram.lite.org.core.analytics.NoOpAnalyticsTracker
import ocsigram.lite.org.core.analytics.NoOpCrashReporter
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun analytics(@ApplicationContext context: Context): AnalyticsTracker =
        FirebaseApp.getApps(context).firstOrNull()?.let {
            FirebaseAnalyticsTracker(FirebaseAnalytics.getInstance(context))
        } ?: NoOpAnalyticsTracker()

    @Provides
    @Singleton
    fun crashReporter(@ApplicationContext context: Context): CrashReporter =
        FirebaseApp.getApps(context).firstOrNull()?.let {
            FirebaseCrashReporter(FirebaseCrashlytics.getInstance())
        } ?: NoOpCrashReporter()
}
