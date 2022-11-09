package com.weberbox.pifire.utils;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import com.pixplicity.easyprefs.library.Prefs;
import com.weberbox.pifire.BuildConfig;
import com.weberbox.pifire.R;
import com.weberbox.pifire.constants.Constants;
import com.weberbox.pifire.model.remote.SettingsDataModel.Modules;
import com.weberbox.pifire.ui.dialogs.MaterialDialogText;
import com.weberbox.pifire.ui.fragments.FeedbackFragment;

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
                    event.setExtra("Four Probes",
                            getPrefBoolean(context, R.string.prefs_four_probe));
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
                scope.setTag("Flavor Type", BuildConfig.FLAVOR_type);
                scope.setTag("Flavor Version", BuildConfig.FLAVOR_version);
                scope.setTag("Flavor Update", BuildConfig.FLAVOR_update);
                scope.setTag("Git Branch", BuildConfig.GIT_BRANCH);
                scope.setTag("Git Revision", BuildConfig.GIT_REV);
                scope.setTag("Beta Build", String.valueOf(BuildConfig.IS_BETA));
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
                .withAdc(getPrefString(ctx, R.string.prefs_modules_adc))
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

    private static Boolean getPrefBoolean(Context ctx, @StringRes int preference) {
        return Prefs.getBoolean(ctx.getString(preference), false);
    }

    private static void storeCrashEvent(Context context, String sentryId) {
        Prefs.putString(context.getString(R.string.prefs_crash_event), sentryId);
    }

    public static void checkIfCrashed(AppCompatActivity activity) {
        boolean showDialog = Prefs.getBoolean(activity.getString(R.string.prefs_crash_dialog),
                activity.getResources().getBoolean(R.bool.def_crash_dialog));
        if (showDialog && sentryEnabled(activity) && sentryDSNSet(activity)) {
            String crashEvent = Prefs.getString(activity.getString(R.string.prefs_crash_event));
            if (!crashEvent.isEmpty()) {
                MaterialDialogText dialog = new MaterialDialogText.Builder(activity)
                        .setTitle(activity.getString(R.string.dialog_crash_title))
                        .setMessage(activity.getString(R.string.dialog_crash_message))
                        .setNegativeButton(activity.getString(R.string.no_thanks),
                                (dialogInterface, which) -> dialogInterface.dismiss())
                        .setPositiveButton(activity.getString(R.string.sure),
                                (dialogInterface, which) -> {
                                    launchFeedbackFragment(activity, crashEvent);
                                    dialogInterface.dismiss();
                                })
                        .build();
                dialog.setOnDismissListener(dialogInterface ->
                        Prefs.putString(activity.getString(R.string.prefs_crash_event), ""));
                dialog.show();
            }
        }
    }

    private static void launchFeedbackFragment(AppCompatActivity activity, String crashId) {
        FeedbackFragment fragment = new FeedbackFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.INTENT_CRASHED_ID, crashId);
        fragment.setArguments(bundle);
        activity.getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.animator.fragment_fade_enter, R.animator.fragment_fade_exit)
                .replace(android.R.id.content, fragment)
                .addToBackStack(null)
                .commit();
    }
}
