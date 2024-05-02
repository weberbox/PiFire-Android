package com.weberbox.pifire.utils;

import android.app.Application;
import android.content.Context;

import androidx.annotation.StringRes;

import com.pixplicity.easyprefs.library.Prefs;
import com.weberbox.pifire.BuildConfig;
import com.weberbox.pifire.R;
import com.weberbox.pifire.model.remote.SettingsDataModel.Modules;

import java.util.ArrayList;

import io.sentry.Sentry;
import io.sentry.SentryLevel;
import io.sentry.android.core.SentryAndroid;
import io.sentry.android.fragment.FragmentLifecycleIntegration;
import io.sentry.android.timber.SentryTimberIntegration;
import io.sentry.protocol.User;

public class CrashUtils {

    public static void initCrashReporting(Context context) {
        if (sentryEnabled(context) && sentryDSNSet(context)) {
            SentryAndroid.init(context, options -> {
                options.setEnableNdk(false);
                options.setAttachScreenshot(true);
                options.setEnableAutoSessionTracking(true);
                options.setEnableDeduplication(true);
                options.setDsn(context.getString(R.string.def_sentry_io_dsn));
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
                options.setBeforeSend((event, hint) -> {
                    event.setExtra("PiFire Version",
                            getPrefString(context, R.string.prefs_server_version));
                    event.setExtra("PiFire Build",
                            getPrefString(context, R.string.prefs_server_build));
                    event.setExtra("Temp Units",
                            getPrefString(context, R.string.prefs_grill_units));
                    event.setExtra("Modules", getServerModules(context));

                    if (event.isCrashed() && event.getEventId() != null) {
                        storeCrashEvent(context, event.getEventId().toString());
                    }
                    return event;
                });
            });

            Sentry.configureScope(scope -> {
                scope.setTag("Application Name", context.getString(R.string.app_name));
                scope.setTag("Variant", BuildConfig.BUILD_TYPE);
                scope.setTag("Flavor Type", BuildConfig.FLAVOR);
                scope.setTag("Git Branch", BuildConfig.GIT_BRANCH);
                scope.setTag("Git Revision", BuildConfig.GIT_REV);
            });

            setUserEmail(getPrefString(context, R.string.prefs_crash_user_email));
        }
    }

    private static void setUserEmail(String email) {
        if (!email.isEmpty()) {
            User user = new User();
            user.setEmail(email);
            Sentry.setUser(user);
        }
    }

    private static ArrayList<Modules> getServerModules(Context ctx) {
        ArrayList<Modules> arrayList = new ArrayList<>();
        arrayList.add(new Modules()
                .withPlatform(getPrefString(ctx, R.string.prefs_modules_platform))
                .withDisplay(getPrefString(ctx, R.string.prefs_modules_display))
                .withDistance(getPrefString(ctx, R.string.prefs_modules_distance)));
        return arrayList;
    }

    private static Boolean sentryDSNSet(Context context) {
        return !context.getString(R.string.def_sentry_io_dsn).isEmpty();
    }

    public static Boolean sentryEnabled(Context context) {
        return getPrefBoolean(context, R.string.prefs_crash_enable);
    }

    private static String getPrefString(Context ctx, @StringRes int preference) {
        return Prefs.getString(ctx.getString(preference), "Unknown");
    }

    @SuppressWarnings("SameParameterValue")
    private static Boolean getPrefBoolean(Context ctx, @StringRes int preference) {
        return Prefs.getBoolean(ctx.getString(preference), false);
    }

    private static void storeCrashEvent(Context context, String sentryId) {
        Prefs.putString(context.getString(R.string.prefs_crash_event), sentryId);
    }
}
