package com.weberbox.pifire.utils;

import android.app.Application;
import android.app.NotificationManager;
import com.weberbox.pifire.BuildConfig;
import com.weberbox.pifire.R;
import com.weberbox.pifire.config.AppConfig;

import org.acra.ReportField;
import org.acra.config.CoreConfigurationBuilder;
import org.acra.config.HttpSenderConfigurationBuilder;
import org.acra.config.LimiterConfigurationBuilder;
import org.acra.config.NotificationConfigurationBuilder;
import org.acra.data.StringFormat;
import org.acra.sender.HttpSender;

import java.util.concurrent.TimeUnit;

public class AcraUtils {

    private static final String[] EXCLUDE_PREFS = new String[]
            {"^.*(auth|serverkey|password|keys|url|username|api|address).*$"};

    public static CoreConfigurationBuilder buildAcra(Application app, String url, String login,
                                                     String auth) {

        CoreConfigurationBuilder builder = new CoreConfigurationBuilder(app);
        builder.withBuildConfigClass(BuildConfig.class)
                .withEnabled(true)
                .withReportFormat(StringFormat.JSON)
                .withExcludeMatchingSharedPreferencesKeys(EXCLUDE_PREFS)
                .withReportContent(
                        ReportField.TOTAL_MEM_SIZE,
                        ReportField.BUILD_CONFIG,
                        ReportField.REPORT_ID,
                        ReportField.THREAD_DETAILS,
                        ReportField.INSTALLATION_ID,
                        ReportField.SHARED_PREFERENCES,
                        ReportField.USER_COMMENT,
                        ReportField.USER_EMAIL,
                        ReportField.PACKAGE_NAME,
                        ReportField.APP_VERSION_NAME,
                        ReportField.APP_VERSION_CODE,
                        ReportField.ANDROID_VERSION,
                        ReportField.BRAND,
                        ReportField.PHONE_MODEL,
                        ReportField.PRODUCT,
                        ReportField.USER_APP_START_DATE,
                        ReportField.USER_CRASH_DATE,
                        ReportField.STACK_TRACE,
                        ReportField.LOGCAT);
        builder.getPluginConfigurationBuilder(LimiterConfigurationBuilder.class)
                .withEnabled(true)
                .withPeriodUnit(TimeUnit.DAYS)
                .withDeleteReportsOnAppUpdate(true);

        if (AppConfig.DEBUG) {
            builder.getPluginConfigurationBuilder(LimiterConfigurationBuilder.class)
                    .withPeriod(1)
                    .withOverallLimit(250)
                    .withFailedReportLimit(50)
                    .withExceptionClassLimit(100)
                    .withStacktraceLimit(50);
        } else {
            builder.getPluginConfigurationBuilder(LimiterConfigurationBuilder.class)
                    .withPeriod(7)
                    .withOverallLimit(25)
                    .withFailedReportLimit(20)
                    .withExceptionClassLimit(20)
                    .withStacktraceLimit(20);
        }

        builder.getPluginConfigurationBuilder(HttpSenderConfigurationBuilder.class)
                .withEnabled(true)
                .withUri(url)
                .withBasicAuthLogin(login)
                .withBasicAuthPassword(auth)
                .withHttpMethod(HttpSender.Method.POST)
                .withConnectionTimeout(5000)
                .withSocketTimeout(20000)
                .withCompress(true)
                .withDropReportsOnTimeout(false);
        builder.getPluginConfigurationBuilder(NotificationConfigurationBuilder.class)
                .withEnabled(true)
                .withResTitle(R.string.acra_notification_title)
                .withResText(R.string.acra_notification_message)
                .withResChannelName(R.string.notification_channel_crash)
                .withResChannelDescription(R.string.notification_desc_crash)
                .withResChannelImportance(NotificationManager.IMPORTANCE_HIGH)
                .withResSendWithCommentButtonText(R.string.acra_notification_comment)
                .withResCommentPrompt(R.string.acra_notification_comment_hint)
                .withSendOnClick(false);

        return builder;

    }
}
