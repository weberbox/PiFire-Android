package com.weberbox.pifire.utils;

import android.app.Application;
import android.content.Context;

import com.weberbox.pifire.BuildConfig;
import com.weberbox.pifire.R;

import io.sentry.Sentry;
import io.sentry.SentryLevel;
import io.sentry.android.core.SentryAndroid;
import io.sentry.android.fragment.FragmentLifecycleIntegration;
import io.sentry.android.timber.SentryTimberIntegration;
import io.sentry.protocol.User;

public class CrashUtils {

    public static void initCrashReporting(Context context, Boolean enabled) {
        if (enabled) {
            SentryAndroid.init(context, options -> {
                options.setEnableNdk(false);
                options.setEnableAutoSessionTracking(true);
                options.setDsn(context.getString(R.string.def_sentry_io_dsn));
                //options.setCacheDirPath(new File(context.getCacheDir(), "sentry").getAbsolutePath());
                options.setTracesSampleRate(1.0);
                options.addIntegration(
                        new SentryTimberIntegration(
                                SentryLevel.ERROR,
                                SentryLevel.DEBUG
                        )
                );
                options.addIntegration(
                        new FragmentLifecycleIntegration(
                                (Application) context,
                                true,
                                true)
                );
            });

            Sentry.configureScope(scope -> {
                scope.setTag("Application Name", context.getString(R.string.app_name));
                scope.setTag("Variant", BuildConfig.BUILD_TYPE);
                scope.setTag("Flavor Type", BuildConfig.FLAVOR_type);
                scope.setTag("Flavor Version", BuildConfig.FLAVOR_version);
                scope.setTag("Git Branch", BuildConfig.GIT_BRANCH);
                scope.setTag("Git Revision", BuildConfig.GIT_REV);
            });
        }
    }

    public static void setUserEmail(String email) {
        if (!email.isEmpty()) {
            User user = new User();
            user.setEmail(email);
            Sentry.setUser(user);
        }
    }
}
