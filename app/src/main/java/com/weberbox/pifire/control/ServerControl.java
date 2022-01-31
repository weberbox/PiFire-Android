package com.weberbox.pifire.control;

import android.content.Context;

import com.google.gson.Gson;
import com.weberbox.pifire.constants.Constants;
import com.weberbox.pifire.constants.ServerConstants;
import com.weberbox.pifire.interfaces.SocketCallback;
import com.weberbox.pifire.model.remote.ControlDataModel;
import com.weberbox.pifire.model.remote.ControlDataModel.Manual;
import com.weberbox.pifire.model.remote.ControlDataModel.NotifyData;
import com.weberbox.pifire.model.remote.ControlDataModel.NotifyReq;
import com.weberbox.pifire.model.remote.ControlDataModel.SetPoints;
import com.weberbox.pifire.model.remote.ControlDataModel.Timer;
import com.weberbox.pifire.model.remote.PelletDataModel.PelletProfileModel;
import com.weberbox.pifire.model.remote.PostDataModel;
import com.weberbox.pifire.model.remote.PostDataModel.OneSignalDevice;
import com.weberbox.pifire.model.remote.PostDataModel.PelletsAction;
import com.weberbox.pifire.model.remote.ServerResponseModel;
import com.weberbox.pifire.model.remote.SettingsDataModel;
import com.weberbox.pifire.model.remote.SettingsDataModel.CycleData;
import com.weberbox.pifire.model.remote.SettingsDataModel.Globals;
import com.weberbox.pifire.model.remote.SettingsDataModel.GrillProbeSettings;
import com.weberbox.pifire.model.remote.SettingsDataModel.HistoryPage;
import com.weberbox.pifire.model.remote.SettingsDataModel.Ifttt;
import com.weberbox.pifire.model.remote.SettingsDataModel.InfluxDB;
import com.weberbox.pifire.model.remote.SettingsDataModel.OneSignalDeviceInfo;
import com.weberbox.pifire.model.remote.SettingsDataModel.OneSignalPush;
import com.weberbox.pifire.model.remote.SettingsDataModel.PelletLevel;
import com.weberbox.pifire.model.remote.SettingsDataModel.ProbeSettings;
import com.weberbox.pifire.model.remote.SettingsDataModel.ProbeTypes;
import com.weberbox.pifire.model.remote.SettingsDataModel.PushBullet;
import com.weberbox.pifire.model.remote.SettingsDataModel.Pushover;
import com.weberbox.pifire.model.remote.SettingsDataModel.SmokePlus;
import com.weberbox.pifire.utils.SettingsUtils;
import com.weberbox.pifire.utils.TimeUtils;
import com.weberbox.pifire.utils.VersionUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.socket.client.Ack;
import io.socket.client.Socket;
import timber.log.Timber;

public class ServerControl {

    // Start Grill
    public static void modeStartGrill(Socket socket, SocketCallback callback) {
        if (VersionUtils.isSupported("1.2.6")) {
            String json = new Gson().toJson(new ControlDataModel()
                    .withMode(ServerConstants.G_MODE_START)
                    .withUpdated(true));
            controlPostEmit(socket, json, callback);
        } else {
            ServerControlDep.modeStartGrill(socket);
        }
    }

    // Monitor Grill
    public static void modeMonitorGrill(Socket socket, SocketCallback callback) {
        if (VersionUtils.isSupported("1.2.6")) {
            String json = new Gson().toJson(new ControlDataModel()
                    .withMode(ServerConstants.G_MODE_MONITOR)
                    .withUpdated(true));
            controlPostEmit(socket, json, callback);
        } else {
            ServerControlDep.modeMonitorGrill(socket);
        }
    }

    // Stop Grill
    public static void modeStopGrill(Socket socket, SocketCallback callback) {
        if (VersionUtils.isSupported("1.2.6")) {
            String json = new Gson().toJson(new ControlDataModel()
                    .withMode(ServerConstants.G_MODE_STOP)
                    .withUpdated(true));
            controlPostEmit(socket, json, callback);
        } else {
            ServerControlDep.modeStopGrill(socket);
        }
    }

    // Shutdown Grill
    public static void modeShutdownGrill(Socket socket, SocketCallback callback) {
        if (VersionUtils.isSupported("1.2.6")) {
            String json = new Gson().toJson(new ControlDataModel()
                    .withMode(ServerConstants.G_MODE_SHUTDOWN)
                    .withUpdated(true));
            controlPostEmit(socket, json, callback);
        } else {
            ServerControlDep.modeShutdownGrill(socket);
        }
    }

    // Mode Smoke
    public static void modeSmokeGrill(Socket socket, SocketCallback callback) {
        if (VersionUtils.isSupported("1.2.6")) {
            String json = new Gson().toJson(new ControlDataModel()
                    .withMode(ServerConstants.G_MODE_SMOKE)
                    .withUpdated(true));
            controlPostEmit(socket, json, callback);
        } else {
            ServerControlDep.modeSmokeGrill(socket);
        }
    }

    // Probe One Enable/Disable
    public static void probeOneToggle(Socket socket, List<Integer> probesEnabled,
                                      SocketCallback callback) {
        if (VersionUtils.isSupported("1.2.6")) {
            String json = new Gson().toJson(new SettingsDataModel()
                    .withProbeSettings(new ProbeSettings()
                            .withProbesEnabled(probesEnabled)));
            settingsPostEmit(socket, json, callback);
        } else {
            boolean enabled = probesEnabled.get(1) == 1;
            ServerControlDep.probeOneToggle(socket, enabled);
        }
    }

    // Probe Two Enable/Disable
    public static void probeTwoToggle(Socket socket, List<Integer> probesEnabled,
                                      SocketCallback callback) {
        if (VersionUtils.isSupported("1.2.6")) {
            String json = new Gson().toJson(new SettingsDataModel()
                    .withProbeSettings(new ProbeSettings()
                            .withProbesEnabled(probesEnabled)));
            settingsPostEmit(socket, json, callback);
        } else {
            boolean enabled = probesEnabled.get(2) == 1;
            ServerControlDep.probeTwoToggle(socket, enabled);
        }
    }

    // Smoke Plus Enable/Disable
    public static void setSmokePlus(Socket socket, boolean enabled, SocketCallback callback) {
        if (VersionUtils.isSupported("1.2.6")) {
            String json = new Gson().toJson(new ControlDataModel().withsPlus(enabled));
            controlPostEmit(socket, json, callback);
        } else {
            ServerControlDep.setSmokePlus(socket, enabled);
        }
    }

    // Set Grill Temp
    public static void setGrillTemp(Socket socket, String temp, SocketCallback callback) {
        if (VersionUtils.isSupported("1.2.6")) {
            String json = new Gson().toJson(new ControlDataModel()
                    .withSetPoints(new SetPoints().withGrill(Integer.valueOf(temp)))
                    .withUpdated(true)
                    .withMode(ServerConstants.G_MODE_HOLD));
            controlPostEmit(socket, json, callback);
        } else {
            ServerControlDep.setGrillTemp(socket, temp);
        }
    }

    // Set Temp Notify
    public static void setProbeNotify(Socket socket, int probe, String temp, boolean holdMode,
                                      boolean shutdown, SocketCallback callback) {
        if (VersionUtils.isSupported("1.2.6")) {
            String json = null;
            switch (probe) {
                case Constants.PICKER_TYPE_GRILL:
                    json = new Gson().toJson(new ControlDataModel()
                            .withSetPoints(new SetPoints().withGrill(Integer.valueOf(temp)))
                            .withNotifyReq(new NotifyReq().withGrill(true))
                            .withUpdated(holdMode));
                    break;
                case Constants.PICKER_TYPE_PROBE_ONE:
                    json = new Gson().toJson(new ControlDataModel()
                            .withSetPoints(new SetPoints().withProbe1(Integer.valueOf(temp)))
                            .withNotifyReq(new NotifyReq().withProbe1(true))
                            .withNotifyData(new NotifyData().withP1Shutdown(shutdown)));
                    break;
                case Constants.PICKER_TYPE_PROBE_TWO:
                    json = new Gson().toJson(new ControlDataModel()
                            .withSetPoints(new SetPoints().withProbe2(Integer.valueOf(temp)))
                            .withNotifyReq(new NotifyReq().withProbe2(true))
                            .withNotifyData(new NotifyData().withP2Shutdown(shutdown)));
                    break;
            }
            if (json != null) {
                controlPostEmit(socket, json, callback);
            }
        } else {
            ServerControlDep.setProbeNotify(socket, probe, temp, shutdown);
        }
    }

    // Clear Temp Notify
    public static void clearProbeNotify(Socket socket, int probe, SocketCallback callback) {
        if (VersionUtils.isSupported("1.2.6")) {
            String json = null;
            switch (probe) {
                case Constants.PICKER_TYPE_GRILL:
                    json = new Gson().toJson(new ControlDataModel()
                            .withNotifyReq(new NotifyReq().withGrill(false)));
                    break;
                case Constants.PICKER_TYPE_PROBE_ONE:
                    json = new Gson().toJson(new ControlDataModel()
                            .withSetPoints(new SetPoints().withProbe1(0))
                            .withNotifyReq(new NotifyReq().withProbe1(false))
                            .withNotifyData(new NotifyData().withP1Shutdown(false)));
                    break;
                case Constants.PICKER_TYPE_PROBE_TWO:
                    json = new Gson().toJson(new ControlDataModel()
                            .withSetPoints(new SetPoints().withProbe2(0))
                            .withNotifyReq(new NotifyReq().withProbe2(false))
                            .withNotifyData(new NotifyData().withP2Shutdown(false)));
                    break;
            }
            if (json != null) {
                controlPostEmit(socket, json, callback);
            }
        } else {
            ServerControlDep.clearProbeNotify(socket, probe);
        }
    }

//    // Timer Start/Stop
//    public static void sendTimerAction(Socket socket, int type) {
//        if (VersionUtils.isSupported("1.2.3")) {
//            String payload = null;
//            switch (type) {
//                case Constants.ACTION_TIMER_RESTART:
//                    payload = JSONUtils.encodeJSON(ServerConstants.TIMER_ACTION,
//                            ServerConstants.TIMER_START, true);
//                    break;
//                case Constants.ACTION_TIMER_PAUSE:
//                    payload = JSONUtils.encodeJSON(ServerConstants.TIMER_ACTION,
//                            ServerConstants.TIMER_PAUSE, true);
//                    break;
//                case Constants.ACTION_TIMER_STOP:
//                    payload = JSONUtils.encodeJSON(ServerConstants.TIMER_ACTION,
//                            ServerConstants.TIMER_STOP, true);
//                    break;
//            }
//            if (payload != null) {
//                socket.emit(ServerConstants.UPDATE_CONTROL_DATA, payload);
//            }
//        } else {
//            ServerControlDep.setTimerAction(socket, type);
//        }
//    }

    // Timer Start/Stop
    public static void sendTimerAction(Socket socket, int type, boolean paused, Float endTime,
                                       Float pauseTime, boolean started, SocketCallback callback) {
        if (VersionUtils.isSupported("1.2.6")) {
            String json = null;
            switch (type) {
                case Constants.ACTION_TIMER_RESTART:
                    json = new Gson().toJson(new ControlDataModel()
                            .withNotifyReq(new NotifyReq().withTimer(true))
                            .withTimer(new Timer().withEnd(TimeUtils.
                                    getRestartTime(endTime, pauseTime)).withPaused(0f)));
//                    if (paused) {
//                        json = new Gson().toJson(new ControlDataModel()
//                                .withNotifyReq(new NotifyReq().withTimer(true))
//                                .withTimer(new Timer().withEnd((float)
//                                        (endTime - pauseTime + TimeUnit.MILLISECONDS.toSeconds(
//                                                System.currentTimeMillis()))).withPaused(0f)));
//                    } else {
//                        json = new Gson().toJson(new ControlDataModel()
//                                .withNotifyReq(new NotifyReq().withTimer(true))
//                                .withTimer(new Timer()
//                                        .withStart((float) TimeUnit.MILLISECONDS.toSeconds(
//                                                        System.currentTimeMillis()))
//                                        .withEnd(endTime)));
//                    }
                    break;
                case Constants.ACTION_TIMER_PAUSE:
                    json = new Gson().toJson(new ControlDataModel()
                            .withNotifyReq(new NotifyReq().withTimer(false))
                            .withTimer(new Timer()
                                    .withPaused((float) TimeUnit.MILLISECONDS.toSeconds(
                                            System.currentTimeMillis()))));
//                    if (started) {
//                        json = new Gson().toJson(new ControlDataModel()
//                                .withNotifyReq(new NotifyReq().withTimer(false))
//                                .withTimer(new Timer()
//                                        .withPaused((float) TimeUnit.MILLISECONDS.toSeconds(
//                                        System.currentTimeMillis()))));
//                    } else {
//                        json = new Gson().toJson(new ControlDataModel()
//                                .withNotifyReq(new NotifyReq().withTimer(false))
//                                .withNotifyData(new NotifyData().withTimerShutdown(false))
//                                .withTimer(new Timer()
//                                        .withStart(0f)
//                                        .withPaused(0f)
//                                        .withEnd(0f)));
//                    }
                    break;
                case Constants.ACTION_TIMER_STOP:
                    json = new Gson().toJson(new ControlDataModel()
                            .withNotifyReq(new NotifyReq().withTimer(false))
                            .withNotifyData(new NotifyData().withTimerShutdown(false))
                            .withTimer(new Timer()
                                    .withStart(0f)
                                    .withPaused(0f)
                                    .withEnd(0f)));
                    break;
            }
            if (json != null) {
                controlPostEmit(socket, json, callback);
            }
        } else {
            ServerControlDep.setTimerAction(socket, type);
        }
    }

    // Timer Set Time
    public static void sendTimerTime(Socket socket, String hours, String minutes, boolean shutdown,
                                     SocketCallback callback) {
        if (VersionUtils.isSupported("1.2.6")) {
            String json = new Gson().toJson(new ControlDataModel()
                    .withNotifyReq(new NotifyReq().withTimer(true))
                    .withTimer(new Timer().withEnd(TimeUtils.getTimeInSeconds(hours, minutes))
                            .withStart((float)
                                    TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())))
                    .withNotifyData(new NotifyData().withTimerShutdown(shutdown)));
            controlPostEmit(socket, json, callback);
        } else {
            ServerControlDep.setTimerTime(socket, hours, minutes, shutdown);
        }
    }

    // History Refresh
    public static void sendHistoryDelete(Socket socket, SocketCallback callback) {
        if (VersionUtils.isSupported("1.2.6")) {
            adminPostEmit(socket, ServerConstants.PT_CLEAR_HISTORY, callback);
        } else {
            ServerControlDep.setHistoryDelete(socket);
        }
    }

    // Shutdown Timer
    public static void sendShutdownTime(Socket socket, String shutDownTime,
                                        SocketCallback callback) {
        if (VersionUtils.isSupported("1.2.6")) {
            String json = new Gson().toJson(new SettingsDataModel()
                    .withGlobals(new Globals().withShutdownTimer(Integer.parseInt(shutDownTime))));
            settingsPostEmit(socket, json, callback);
        } else {
            ServerControlDep.setShutdownTime(socket, shutDownTime);
        }
    }

    // Startup Timer
    public static void sendStartupTime(Socket socket, String startUpTime,
                                       SocketCallback callback) {
        String json = new Gson().toJson(new SettingsDataModel()
                .withGlobals(new Globals().withStartupTimer(Integer.parseInt(startUpTime))));
        settingsPostEmit(socket, json, callback);
    }

    // Grill Probe
    public static void setGrillProbe(Socket socket, List<Integer> probesEnabled, String grillProbe,
                                     SocketCallback callback) {
        if (VersionUtils.isSupported("1.2.6")) {
            String json = new Gson().toJson(new SettingsDataModel()
                    .withGrillProbeSettings(new GrillProbeSettings()
                            .withGrillProbe(grillProbe)
                            .withGrillProbeEnabled(probesEnabled)));
            settingsPostEmit(socket, json, callback);
        } else {
            ServerControlDep.setGrillProbe(socket, grillProbe);
        }
    }

    // Grill Probe 0 Type
    public static void setGrillProbeType(Socket socket, String grillProbeType,
                                         SocketCallback callback) {
        if (VersionUtils.isSupported("1.2.6")) {
            String json = new Gson().toJson(new SettingsDataModel()
                    .withProbeTypes(new ProbeTypes().withGrill0type(grillProbeType)));
            String json_c = new Gson().toJson(new ControlDataModel().withProbeProfileUpdate(true));
            settingsPostEmit(socket, json, callback);
            controlPostEmit(socket, json_c, callback);
        } else {
            ServerControlDep.setGrillProbeType(socket, grillProbeType);
        }
    }

    // Grill Probe 1 Type
    public static void setGrillProbe1Type(Socket socket, String grillProbe1Type,
                                          SocketCallback callback) {
        if (VersionUtils.isSupported("1.2.6")) {
            String json = new Gson().toJson(new SettingsDataModel()
                    .withProbeTypes(new ProbeTypes().withGrill1type(grillProbe1Type)));
            String json_c = new Gson().toJson(new ControlDataModel().withProbeProfileUpdate(true));
            settingsPostEmit(socket, json, callback);
            controlPostEmit(socket, json_c, callback);
        } else {
            ServerControlDep.setGrillProbe1Type(socket, grillProbe1Type);
        }
    }

    // Grill Probe 2 Type
    public static void setGrillProbe2Type(Socket socket, String grillProbe2Type,
                                          SocketCallback callback) {
        if (VersionUtils.isSupported("1.2.6")) {
            String json = new Gson().toJson(new SettingsDataModel()
                    .withProbeTypes(new ProbeTypes().withGrill2type(grillProbe2Type)));
            String json_c = new Gson().toJson(new ControlDataModel().withProbeProfileUpdate(true));
            settingsPostEmit(socket, json, callback);
            controlPostEmit(socket, json_c, callback);
        } else {
            ServerControlDep.setGrillProbe2Type(socket, grillProbe2Type);
        }
    }

    // Probe One Type
    public static void setProbe1Type(Socket socket, String probe1Type, SocketCallback callback) {
        if (VersionUtils.isSupported("1.2.6")) {
            String json = new Gson().toJson(new SettingsDataModel()
                    .withProbeTypes(new ProbeTypes().withProbe1type(probe1Type)));
            String json_c = new Gson().toJson(new ControlDataModel().withProbeProfileUpdate(true));
            settingsPostEmit(socket, json, callback);
            controlPostEmit(socket, json_c, callback);
        } else {
            ServerControlDep.setProbe1Type(socket, probe1Type);
        }
    }

    // Probe Two Type
    public static void setProbe2Type(Socket socket, String probe2Type, SocketCallback callback) {
        if (VersionUtils.isSupported("1.2.6")) {
            String json = new Gson().toJson(new SettingsDataModel()
                    .withProbeTypes(new ProbeTypes().withProbe2type(probe2Type)));
            String json_c = new Gson().toJson(new ControlDataModel().withProbeProfileUpdate(true));
            settingsPostEmit(socket, json, callback);
            controlPostEmit(socket, json_c, callback);
        } else {
            ServerControlDep.setProbe2Type(socket, probe2Type);
        }
    }

    // Enable IFTTT
    public static void setIFTTTEnabled(Socket socket, boolean enabled, SocketCallback callback) {
        if (VersionUtils.isSupported("1.2.6")) {
            String json = new Gson().toJson(new SettingsDataModel()
                    .withIfttt(new Ifttt().withEnabled(enabled)));
            settingsPostEmit(socket, json, callback);
        } else {
            ServerControlDep.setIFTTTEnabled(socket, enabled);
        }
    }

    // Set IFTTT APIKey
    public static void setIFTTTAPIKey(Socket socket, String apiKey, SocketCallback callback) {
        if (VersionUtils.isSupported("1.2.6")) {
            String json = new Gson().toJson(new SettingsDataModel()
                    .withIfttt(new Ifttt().withAPIKey(apiKey)));
            settingsPostEmit(socket, json, callback);
        } else {
            ServerControlDep.setIFTTTAPIKey(socket, apiKey);
        }
    }

    // Enable PushOver
    public static void setPushOverEnabled(Socket socket, boolean enabled, SocketCallback callback) {
        if (VersionUtils.isSupported("1.2.6")) {
            String json = new Gson().toJson(new SettingsDataModel()
                    .withPushover(new Pushover().withEnabled(enabled)));
            settingsPostEmit(socket, json, callback);
        } else {
            ServerControlDep.setPushOverEnabled(socket, enabled);
        }
    }

    // Set PushOver APIKey
    public static void setPushOverAPIKey(Socket socket, String apiKey, SocketCallback callback) {
        if (VersionUtils.isSupported("1.2.6")) {
            String json = new Gson().toJson(new SettingsDataModel()
                    .withPushover(new Pushover().withAPIKey(apiKey)));
            settingsPostEmit(socket, json, callback);
        } else {
            ServerControlDep.setPushOverAPIKey(socket, apiKey);
        }
    }

    // Set PushOver UserKeys
    public static void setPushOverUserKeys(Socket socket, String userKeys,
                                           SocketCallback callback) {
        if (VersionUtils.isSupported("1.2.6")) {
            String json = new Gson().toJson(new SettingsDataModel()
                    .withPushover(new Pushover().withUserKeys(userKeys)));
            settingsPostEmit(socket, json, callback);
        } else {
            ServerControlDep.setPushOverUserKeys(socket, userKeys);
        }
    }

    // Set PushOver PublicURL
    public static void setPushOverURL(Socket socket, String url, SocketCallback callback) {
        if (VersionUtils.isSupported("1.2.6")) {
            String json = new Gson().toJson(new SettingsDataModel()
                    .withPushover(new Pushover().withPublicURL(url)));
            settingsPostEmit(socket, json, callback);
        } else {
            ServerControlDep.setPushOverURL(socket, url);
        }
    }

    // Enable PushBullet
    public static void setPushBulletEnabled(Socket socket, boolean enabled,
                                            SocketCallback callback) {
        if (VersionUtils.isSupported("1.2.6")) {
            String json = new Gson().toJson(new SettingsDataModel()
                    .withPushBullet(new PushBullet().withEnabled(enabled)));
            settingsPostEmit(socket, json, callback);
        } else {
            ServerControlDep.setPushBulletEnabled(socket, enabled);
        }
    }

    // Set PushBullet APIKey
    public static void setPushBulletAPIKey(Socket socket, String apiKey, SocketCallback callback) {
        if (VersionUtils.isSupported("1.2.6")) {
            String json = new Gson().toJson(new SettingsDataModel()
                    .withPushBullet(new PushBullet().withAPIKey(apiKey)));
            settingsPostEmit(socket, json, callback);
        } else {
            ServerControlDep.setPushBulletAPIKey(socket, apiKey);
        }
    }

    // Set PushBullet URL
    public static void setPushBulletURL(Socket socket, String url, SocketCallback callback) {
        if (VersionUtils.isSupported("1.2.6")) {
            String json = new Gson().toJson(new SettingsDataModel()
                    .withPushBullet(new PushBullet().withPublicURL(url)));
            settingsPostEmit(socket, json, callback);
        } else {
            ServerControlDep.setPushBulletURL(socket, url);
        }
    }

    // Set OneSignal Enabled
    public static void setOneSignalEnabled(Socket socket, boolean enabled,
                                           SocketCallback callback) {
        String json = new Gson().toJson(new SettingsDataModel()
                .withOneSignal(new OneSignalPush().withEnabled(enabled)));
        settingsPostEmit(socket, json, callback);
    }

    // Set OneSignal App ID
    public static void setOneSignalAppID(Socket socket, String appID, SocketCallback callback) {
        String json = new Gson().toJson(new SettingsDataModel()
                .withOneSignal(new OneSignalPush().withAppId(appID)));
        settingsPostEmit(socket, json, callback);
    }

    // Register OneSignal Device
    public static void registerOneSignalDevice(Context context, Socket socket,
                                               Map<String, OneSignalDeviceInfo> device) {
        String json = new Gson().toJson(new SettingsDataModel()
                .withOneSignal(new SettingsDataModel.OneSignalPush().withOneSignalDevices(device)));
        socket.emit(ServerConstants.PE_POST_APP_DATA, ServerConstants.PA_UPDATE_ACTION,
                ServerConstants.PT_SETTINGS, json, (Ack) args -> {
                    if (args.length > 0 && args[0] != null) {
                        ServerResponseModel response =
                                ServerResponseModel.parseJSON(args[0].toString());
                        if (response.getResponse() != null &&
                                response.getResponse().getResult().equals("success")) {
                            new SettingsUtils(context, null).requestSettingsData(socket);
                        } else {
                            Timber.d("Failed to register with Pifire");
                        }
                    }
                });
    }

    // Remove OneSignal Device
    public static void removeOneSignalDevice(Socket socket, String playerId,
                                             SocketCallback callback) {
        String json = new Gson().toJson(new PostDataModel()
                .withOneSignalDevice(new OneSignalDevice().withOneSignalPlayerID(playerId)));
        removePostEmit(socket, ServerConstants.PT_ONESIGNAL_DEVICE, json, callback);
    }

    // Set InfluxDB Enabled
    public static void setInfluxDBEnabled(Socket socket, boolean enabled, SocketCallback callback) {
        String json = new Gson().toJson(new SettingsDataModel()
                .withInfluxdb(new InfluxDB().withEnabled(enabled)));
        settingsPostEmit(socket, json, callback);
    }

    // Set InfluxDB URL
    public static void setInfluxDBUrl(Socket socket, String url, SocketCallback callback) {
        String json = new Gson().toJson(new SettingsDataModel()
                .withInfluxdb(new InfluxDB().withUrl(url)));
        settingsPostEmit(socket, json, callback);
    }

    // Set InfluxDB Token
    public static void setInfluxDBToken(Socket socket, String token, SocketCallback callback) {
        String json = new Gson().toJson(new SettingsDataModel()
                .withInfluxdb(new InfluxDB().withToken(token)));
        settingsPostEmit(socket, json, callback);
    }

    // Set InfluxDB Org
    public static void setInfluxDBOrg(Socket socket, String org, SocketCallback callback) {
        String json = new Gson().toJson(new SettingsDataModel()
                .withInfluxdb(new InfluxDB().withOrg(org)));
        settingsPostEmit(socket, json, callback);
    }

    // Set InfluxDB Bucket
    public static void setInfluxDBBucket(Socket socket, String bucket, SocketCallback callback) {
        String json = new Gson().toJson(new SettingsDataModel()
                .withInfluxdb(new InfluxDB().withBucket(bucket)));
        settingsPostEmit(socket, json, callback);
    }

    // Set History Auto Refresh
    public static void setHistoryRefresh(Socket socket, boolean refresh, SocketCallback callback) {
        if (VersionUtils.isSupported("1.2.6")) {
            String json = new Gson().toJson(new SettingsDataModel()
                    .withHistoryPage(new HistoryPage().withAutoRefresh(refresh ? "on" : "off")));
            settingsPostEmit(socket, json, callback);
        } else {
            ServerControlDep.setHistoryRefresh(socket, refresh);
        }
    }

    // Set History Clear on Start
    public static void sendHistoryClear(Socket socket, boolean clear, SocketCallback callback) {
        if (VersionUtils.isSupported("1.2.6")) {
            String json = new Gson().toJson(new SettingsDataModel()
                    .withHistoryPage(new HistoryPage().withClearHistoryOnStart(clear)));
            settingsPostEmit(socket, json, callback);
        } else {
            ServerControlDep.setHistoryClear(socket, clear);
        }
    }

    // Set History to Display
    public static void setHistoryMins(Socket socket, String mins, SocketCallback callback) {
        if (VersionUtils.isSupported("1.2.6")) {
            String json = new Gson().toJson(new SettingsDataModel()
                    .withHistoryPage(new HistoryPage().withMinutes(Integer.parseInt(mins))));
            settingsPostEmit(socket, json, callback);
        } else {
            ServerControlDep.setHistoryMins(socket, mins);
        }
    }

    // Set History Points
    public static void setHistoryPoints(Socket socket, String points, SocketCallback callback) {
        if (VersionUtils.isSupported("1.2.6")) {
            String json = new Gson().toJson(new SettingsDataModel()
                    .withHistoryPage(new HistoryPage().withDataPoints(Integer.parseInt(points))));
            settingsPostEmit(socket, json, callback);
        } else {
            ServerControlDep.setHistoryPoints(socket, points);
        }
    }

    // Set Min Start Temp
    public static void setMinStartTemp(Socket socket, String temp, SocketCallback callback) {
        if (VersionUtils.isSupported("1.2.6")) {
            String json = new Gson().toJson(new SettingsDataModel()
                    .withSafety(new SettingsDataModel.Safety().
                            withMinStartupTemp(Integer.parseInt(temp))));
            settingsPostEmit(socket, json, callback);
        } else {
            ServerControlDep.setMinStartTemp(socket, temp);
        }
    }

    // Set Max Start Temp
    public static void setMaxStartTemp(Socket socket, String temp, SocketCallback callback) {
        if (VersionUtils.isSupported("1.2.6")) {
            String json = new Gson().toJson(new SettingsDataModel()
                    .withSafety(new SettingsDataModel.Safety().
                            withMaxStartupTemp(Integer.parseInt(temp))));
            settingsPostEmit(socket, json, callback);
        } else {
            ServerControlDep.setMaxStartTemp(socket, temp);
        }
    }

    // Set Reignite Retries
    public static void setReigniteRetries(Socket socket, String retries, SocketCallback callback) {
        if (VersionUtils.isSupported("1.2.6")) {
            String json = new Gson().toJson(new SettingsDataModel()
                    .withSafety(new SettingsDataModel.Safety().
                            withReigniteRetries(Integer.parseInt(retries))));
            settingsPostEmit(socket, json, callback);
        } else {
            ServerControlDep.setReigniteRetries(socket, retries);
        }
    }

    // Set Max Temp
    public static void setMaxGrillTemp(Socket socket, String temp, SocketCallback callback) {
        if (VersionUtils.isSupported("1.2.6")) {
            String json = new Gson().toJson(new SettingsDataModel()
                    .withSafety(new SettingsDataModel.Safety().
                            withMaxTemp(Integer.parseInt(temp))));
            settingsPostEmit(socket, json, callback);
        } else {
            ServerControlDep.setMaxGrillTemp(socket, temp);
        }
    }

    // Set Grill Name
    public static void setGrillName(Socket socket, String name, SocketCallback callback) {
        if (VersionUtils.isSupported("1.2.6")) {
            String json = new Gson().toJson(new SettingsDataModel()
                    .withGlobals(new Globals().withGrillName(name)));
            settingsPostEmit(socket, json, callback);
        } else {
            ServerControlDep.setGrillName(socket, name);
        }
    }

    // Set Auger Time
    public static void setAugerTime(Socket socket, String time, SocketCallback callback) {
        if (VersionUtils.isSupported("1.2.6")) {
            String json = new Gson().toJson(new SettingsDataModel()
                    .withCycleData(new CycleData().withSmokeCycleTime(Integer.parseInt(time))));
            settingsPostEmit(socket, json, callback);
        } else {
            ServerControlDep.setAugerTime(socket, time);
        }
    }

    // Set P-Mode
    public static void setPMode(Socket socket, String mode, SocketCallback callback) {
        if (VersionUtils.isSupported("1.2.6")) {
            String json = new Gson().toJson(new SettingsDataModel()
                    .withCycleData(new CycleData().withPMode(Integer.parseInt(mode))));
            settingsPostEmit(socket, json, callback);
        } else {
            ServerControlDep.setPMode(socket, mode);
        }
    }

    // Set Smoke Plus Default
    public static void setSmokePlusDefault(Socket socket, boolean enabled, SocketCallback callback) {
        if (VersionUtils.isSupported("1.2.6")) {
            String json = new Gson().toJson(new SettingsDataModel()
                    .withSmokePlus(new SmokePlus().withEnabled(enabled)));
            settingsPostEmit(socket, json, callback);
        } else {
            ServerControlDep.setSmokePlusDefault(socket, enabled);
        }
    }

    // Set Smoke Fan Cycle Time
    public static void setSmokeFan(Socket socket, String time, SocketCallback callback) {
        if (VersionUtils.isSupported("1.2.6")) {
            String json = new Gson().toJson(new SettingsDataModel()
                    .withSmokePlus(new SmokePlus().withCycle(Integer.parseInt(time))));
            settingsPostEmit(socket, json, callback);
        } else {
            ServerControlDep.setSmokeFan(socket, time);
        }
    }

    // Set Smoke Min Temp
    public static void setSmokeMinTemp(Socket socket, String temp, SocketCallback callback) {
        if (VersionUtils.isSupported("1.2.6")) {
            String json = new Gson().toJson(new SettingsDataModel()
                    .withSmokePlus(new SmokePlus().withMinTemp(Integer.parseInt(temp))));
            settingsPostEmit(socket, json, callback);
        } else {
            ServerControlDep.setSmokeMinTemp(socket, temp);
        }
    }

    // Set Smoke Max Temp
    public static void setSmokeMaxTemp(Socket socket, String temp, SocketCallback callback) {
        if (VersionUtils.isSupported("1.2.6")) {
            String json = new Gson().toJson(new SettingsDataModel()
                    .withSmokePlus(new SmokePlus().withMaxTemp(Integer.parseInt(temp))));
            settingsPostEmit(socket, json, callback);
        } else {
            ServerControlDep.setSmokeMaxTemp(socket, temp);
        }
    }

    // Set PID Cycle Time
    public static void setPIDTime(Socket socket, String time, SocketCallback callback) {
        if (VersionUtils.isSupported("1.2.6")) {
            String json = new Gson().toJson(new SettingsDataModel()
                    .withCycleData(new CycleData().withSmokeCycleTime(Integer.parseInt(time))));
            settingsPostEmit(socket, json, callback);
        } else {
            ServerControlDep.setPIDTime(socket, time);
        }
    }

    // Set PID PB
    public static void setPIDPB(Socket socket, String pb, SocketCallback callback) {
        if (VersionUtils.isSupported("1.2.6")) {
            String json = new Gson().toJson(new SettingsDataModel()
                    .withCycleData(new CycleData().withPb(Float.parseFloat(pb))));
            settingsPostEmit(socket, json, callback);
        } else {
            ServerControlDep.setPIDPB(socket, pb);
        }
    }

    // Set PID Ti
    public static void setPIDTi(Socket socket, String ti, SocketCallback callback) {
        if (VersionUtils.isSupported("1.2.6")) {
            String json = new Gson().toJson(new SettingsDataModel()
                    .withCycleData(new CycleData().withTi(Float.parseFloat(ti))));
            settingsPostEmit(socket, json, callback);
        } else {
            ServerControlDep.setPIDTi(socket, ti);
        }
    }

    // Set PID Td
    public static void setPIDTd(Socket socket, String td, SocketCallback callback) {
        if (VersionUtils.isSupported("1.2.6")) {
            String json = new Gson().toJson(new SettingsDataModel()
                    .withCycleData(new CycleData().withTd(Float.parseFloat(td))));
            settingsPostEmit(socket, json, callback);
        } else {
            ServerControlDep.setPIDTd(socket, td);
        }
    }

    // Set PID U Min
    public static void setPIDuMin(Socket socket, String uMin, SocketCallback callback) {
        if (VersionUtils.isSupported("1.2.6")) {
            String json = new Gson().toJson(new SettingsDataModel()
                    .withCycleData(new CycleData().withuMin(Float.parseFloat(uMin))));
            settingsPostEmit(socket, json, callback);
        } else {
            ServerControlDep.setPIDuMin(socket, uMin);
        }
    }

    // Set PID U Max
    public static void setPIDuMax(Socket socket, String uMax, SocketCallback callback) {
        if (VersionUtils.isSupported("1.2.6")) {
            String json = new Gson().toJson(new SettingsDataModel()
                    .withCycleData(new CycleData().withuMax(Float.parseFloat(uMax))));
            settingsPostEmit(socket, json, callback);
        } else {
            ServerControlDep.setPIDuMax(socket, uMax);
        }
    }

    // Set PID Center Ratio
    public static void setPIDCenter(Socket socket, String center, SocketCallback callback) {
        if (VersionUtils.isSupported("1.2.6")) {
            String json = new Gson().toJson(new SettingsDataModel()
                    .withCycleData(new CycleData().withCenter(Float.parseFloat(center))));
            settingsPostEmit(socket, json, callback);
        } else {
            ServerControlDep.setPIDCenter(socket, center);
        }
    }

    // Set Pellets Warning Enabled
    public static void setPelletWarningEnabled(Socket socket, boolean enabled,
                                               SocketCallback callback) {
        if (VersionUtils.isSupported("1.2.6")) {
            String json = new Gson().toJson(new SettingsDataModel()
                    .withPelletLevel(new PelletLevel().withWarningEnabled(enabled)));
            settingsPostEmit(socket, json, callback);
        } else {
            ServerControlDep.setPelletWarningEnabled(socket, enabled);
        }
    }

    // Set Pellets Warning Level
    public static void setPelletWarningLevel(Socket socket, String level, SocketCallback callback) {
        if (VersionUtils.isSupported("1.2.6")) {
            String json = new Gson().toJson(new SettingsDataModel()
                    .withPelletLevel(new PelletLevel().withWarningLevel(Integer.parseInt(level))));
            settingsPostEmit(socket, json, callback);
        } else {
            ServerControlDep.setPelletWarningLevel(socket, level);
        }
    }

    // Set Pellets Full
    public static void setPelletsFull(Socket socket, String full, SocketCallback callback) {
        if (VersionUtils.isSupported("1.2.6")) {
            String json = new Gson().toJson(new SettingsDataModel()
                    .withPelletLevel(new PelletLevel().withFull(Integer.parseInt(full))));
            settingsPostEmit(socket, json, callback);
        } else {
            ServerControlDep.setPelletsFull(socket, full);
        }
    }

    // Set Pellets Empty
    public static void setPelletsEmpty(Socket socket, String empty, SocketCallback callback) {
        if (VersionUtils.isSupported("1.2.6")) {
            String json = new Gson().toJson(new SettingsDataModel()
                    .withPelletLevel(new PelletLevel().withEmpty(Integer.parseInt(empty))));
            settingsPostEmit(socket, json, callback);
        } else {
            ServerControlDep.setPelletsEmpty(socket, empty);
        }
    }

    // Set Debug Mode
    public static void setDebugMode(Socket socket, boolean enabled, SocketCallback callback) {
        if (VersionUtils.isSupported("1.2.6")) {
            String json = new Gson().toJson(new SettingsDataModel()
                    .withGlobals(new Globals().withDebugMode(enabled)));
            settingsPostEmit(socket, json, callback);
        } else {
            ServerControlDep.setDebugMode(socket, enabled);
        }
    }

    // Delete History
    public static void sendDeleteHistory(Socket socket, SocketCallback callback) {
        if (VersionUtils.isSupported("1.2.6")) {
            adminPostEmit(socket, ServerConstants.PT_CLEAR_HISTORY, callback);
        } else {
            ServerControlDep.setDeleteHistory(socket);
        }
    }

    // Delete Events
    public static void sendDeleteEvents(Socket socket, SocketCallback callback) {
        if (VersionUtils.isSupported("1.2.6")) {
            adminPostEmit(socket, ServerConstants.PT_CLEAR_EVENTS, callback);
        } else {
            ServerControlDep.setDeleteEvents(socket);
        }
    }

    // Delete Pellets
    public static void sendDeletePellets(Socket socket, SocketCallback callback) {
        if (VersionUtils.isSupported("1.2.6")) {
            adminPostEmit(socket, ServerConstants.PT_CLEAR_PELLETS, callback);
        } else {
            ServerControlDep.setDeletePellets(socket);
        }
    }

    // Delete Pellets Log
    public static void sendDeletePelletsLog(Socket socket, SocketCallback callback) {
        if (VersionUtils.isSupported("1.2.6")) {
            adminPostEmit(socket, ServerConstants.PT_CLEAR_PELLETS_LOG, callback);
        } else {
            ServerControlDep.setDeletePelletsLog(socket);
        }
    }

    // Factory Reset
    public static void sendFactoryReset(Socket socket, SocketCallback callback) {
        if (VersionUtils.isSupported("1.2.6")) {
            adminPostEmit(socket, ServerConstants.PT_FACTORY_DEFAULTS, callback);
        } else {
            ServerControlDep.setFactoryReset(socket);
        }
    }

    // Reboot System
    public static void sendRebootSystem(Socket socket, SocketCallback callback) {
        if (VersionUtils.isSupported("1.2.3")) {
            adminPostEmit(socket, ServerConstants.PT_REBOOT, callback);
        } else {
            ServerControlDep.setRebootSystem(socket);
        }
    }

    // Shutdown System
    public static void sendShutdownSystem(Socket socket, SocketCallback callback) {
        if (VersionUtils.isSupported("1.2.6")) {
            adminPostEmit(socket, ServerConstants.PT_SHUTDOWN, callback);
        } else {
            ServerControlDep.setShutdownSystem(socket);
        }
    }

    // Delete Pellet Profile
    public static void sendDeletePelletProfile(Socket socket, String pelletId,
                                               SocketCallback callback) {
        if (VersionUtils.isSupported("1.2.6")) {
            String json = new Gson().toJson(new PostDataModel()
                    .withPelletsAction(new PelletsAction().withProfile(pelletId)));
            pelletsPostEmit(socket, ServerConstants.PT_DELETE_PROFILE, json, callback);
        } else {
            ServerControlDep.setDeletePelletProfile(socket, pelletId);
        }
    }

    // Add Pellet Profile
    public static void sendAddPelletProfile(Socket socket, PelletProfileModel profile,
                                            SocketCallback callback) {
        if (VersionUtils.isSupported("1.2.6")) {
            String json = new Gson().toJson(new PostDataModel()
                    .withPelletsAction(new PelletsAction()
                            .withProfile(profile.getId())
                            .withAddAndLoad(false)
                            .withBrandName(profile.getBrand())
                            .withWoodType(profile.getWood())
                            .withRating(profile.getRating())
                            .withComments(profile.getComments())));
            pelletsPostEmit(socket, ServerConstants.PT_ADD_PROFILE, json, callback);
        } else {
            ServerControlDep.setAddPelletProfile(socket, profile);
        }
    }

    // Add Pellet Profile Load
    public static void sendAddPelletProfileLoad(Socket socket, PelletProfileModel profile,
                                                SocketCallback callback) {
        if (VersionUtils.isSupported("1.2.6")) {
            String json = new Gson().toJson(new PostDataModel()
                    .withPelletsAction(new PelletsAction()
                            .withProfile(profile.getId())
                            .withAddAndLoad(true)
                            .withBrandName(profile.getBrand())
                            .withWoodType(profile.getWood())
                            .withRating(profile.getRating())
                            .withComments(profile.getComments())));
            pelletsPostEmit(socket, ServerConstants.PT_ADD_PROFILE, json, callback);
        } else {
            ServerControlDep.setAddPelletProfileLoad(socket, profile);
        }
    }

    // Edit Pellet Profile
    public static void sendEditPelletProfile(Socket socket, PelletProfileModel profile,
                                             SocketCallback callback) {
        if (VersionUtils.isSupported("1.2.6")) {
            String json = new Gson().toJson(new PostDataModel()
                    .withPelletsAction(new PelletsAction()
                            .withProfile(profile.getId())
                            .withBrandName(profile.getBrand())
                            .withWoodType(profile.getWood())
                            .withRating(profile.getRating())
                            .withComments(profile.getComments())));
            pelletsPostEmit(socket, ServerConstants.PT_EDIT_PROFILE, json, callback);
        } else {
            ServerControlDep.setEditPelletProfile(socket, profile);
        }
    }

    // Load Pellet Profile
    public static void sendLoadPelletProfile(Socket socket, String pelletId,
                                             SocketCallback callback) {
        if (VersionUtils.isSupported("1.2.6")) {
            String json = new Gson().toJson(new PostDataModel()
                    .withPelletsAction(new PelletsAction().withProfile(pelletId)));
            pelletsPostEmit(socket, ServerConstants.PT_LOAD_PROFILE, json, callback);
        } else {
            ServerControlDep.setLoadPelletProfile(socket, pelletId);
        }
    }

    // Delete Pellet Wood
    public static void sendDeletePelletWood(Socket socket, String wood, SocketCallback callback) {
        if (VersionUtils.isSupported("1.2.6")) {
            String json = new Gson().toJson(new PostDataModel()
                    .withPelletsAction(new PelletsAction().withDeleteWood(wood)));
            pelletsPostEmit(socket, ServerConstants.PT_EDIT_WOODS, json, callback);
        } else {
            ServerControlDep.setDeletePelletWood(socket, wood);
        }
    }

    // Add Pellet Wood
    public static void sendAddPelletWood(Socket socket, String wood, SocketCallback callback) {
        if (VersionUtils.isSupported("1.2.6")) {
            String json = new Gson().toJson(new PostDataModel()
                    .withPelletsAction(new PelletsAction().withNewWood(wood)));
            pelletsPostEmit(socket, ServerConstants.PT_EDIT_WOODS, json, callback);
        } else {
            ServerControlDep.setAddPelletWood(socket, wood);
        }
    }

    // Delete Pellet Brands
    public static void sendDeletePelletBrand(Socket socket, String brand, SocketCallback callback) {
        if (VersionUtils.isSupported("1.2.6")) {
            String json = new Gson().toJson(new PostDataModel()
                    .withPelletsAction(new PelletsAction().withDeleteBrand(brand)));
            pelletsPostEmit(socket, ServerConstants.PT_EDIT_BRANDS, json, callback);
        } else {
            ServerControlDep.setDeletePelletBrand(socket, brand);
        }
    }

    // Add Pellet Brands
    public static void sendAddPelletBrand(Socket socket, String brand, SocketCallback callback) {
        if (VersionUtils.isSupported("1.2.6")) {
            String json = new Gson().toJson(new PostDataModel()
                    .withPelletsAction(new PelletsAction().withNewBrand(brand)));
            pelletsPostEmit(socket, ServerConstants.PT_EDIT_BRANDS, json, callback);
        } else {
            ServerControlDep.setAddPelletBrand(socket, brand);
        }
    }

    // Delete Pellet Log
    public static void sendDeletePelletLog(Socket socket, String log, SocketCallback callback) {
        if (VersionUtils.isSupported("1.2.6")) {
            String json = new Gson().toJson(new PostDataModel()
                    .withPelletsAction(new PelletsAction().withLogItem(log)));
            pelletsPostEmit(socket, ServerConstants.PT_DELETE_LOG, json, callback);
        } else {
            ServerControlDep.setDeletePelletLog(socket, log);
        }
    }

    // Check Hopper Level
    public static void sendCheckHopperLevel(Socket socket, SocketCallback callback) {
        if (VersionUtils.isSupported("1.2.6")) {
            pelletsPostEmit(socket, ServerConstants.PT_HOPPER_CHECK, null, callback);
        } else {
            ServerControlDep.setCheckHopperLevel(socket);
        }
    }

    // Set Manual Mode
    public static void setManualMode(Socket socket, boolean enabled, SocketCallback callback) {
        if (VersionUtils.isSupported("1.2.6")) {
            String json = new Gson().toJson(new ControlDataModel()
                    .withMode(enabled ? ServerConstants.G_MODE_MANUAL :
                            ServerConstants.G_MODE_STOP)
                    .withUpdated(true));
            controlPostEmit(socket, json, callback);
        } else {
            ServerControlDep.setManualMode(socket, enabled);
        }
    }

    // Set Manual Fan Output
    public static void setManualFanOutput(Socket socket, boolean enabled,
                                          SocketCallback callback) {
        if (VersionUtils.isSupported("1.2.6")) {
            String json = new Gson().toJson(new ControlDataModel()
                    .withManual(new Manual().withChange(true).withFan(enabled)));
            controlPostEmit(socket, json, callback);
        } else {
            ServerControlDep.setManualFanOutput(socket, enabled);
        }
    }

    // Set Manual Auger Output
    public static void setManualAugerOutput(Socket socket, boolean enabled,
                                            SocketCallback callback) {
        if (VersionUtils.isSupported("1.2.6")) {
            String json = new Gson().toJson(new ControlDataModel()
                    .withManual(new Manual().withChange(true).withAuger(enabled)));
            controlPostEmit(socket, json, callback);
        } else {
            ServerControlDep.setManualAugerOutput(socket, enabled);
        }
    }

    // Set Manual Igniter Output
    public static void setManualIgniterOutput(Socket socket, boolean enabled,
                                              SocketCallback callback) {
        if (VersionUtils.isSupported("1.2.6")) {
            String json = new Gson().toJson(new ControlDataModel()
                    .withManual(new Manual().withChange(true).withIgniter(enabled)));
            controlPostEmit(socket, json, callback);
        } else {
            ServerControlDep.setManualIgniterOutput(socket, enabled);
        }
    }

    // Set Manual Power Output
    public static void setManualPowerOutput(Socket socket, boolean enabled,
                                            SocketCallback callback) {
        if (VersionUtils.isSupported("1.2.6")) {
            String json = new Gson().toJson(new ControlDataModel()
                    .withManual(new Manual().withChange(true).withPower(enabled)));
            controlPostEmit(socket, json, callback);
        } else {
            ServerControlDep.setManualPowerOutput(socket, enabled);
        }
    }

    // Grill Temp Units
    public static void setTempUnits(Socket socket, String units, SocketCallback callback) {
        if (VersionUtils.isSupported("1.2.6")) {
            socket.emit(ServerConstants.PE_POST_APP_DATA, ServerConstants.PA_UNITS_ACTION,
                    units.equals("F") ? ServerConstants.PT_UNITS_F : ServerConstants.PT_UNITS_C,
                    (Ack) args -> {
                        if (args.length > 0 && args[0] != null && callback != null) {
                            callback.onResponse(args[0].toString());
                        }
                    });
        } else {
            ServerControlDep.setTempUnits(socket, units);
        }
    }

    private static void controlPostEmit(Socket socket, String json, SocketCallback callback) {
        socket.emit(ServerConstants.PE_POST_APP_DATA, ServerConstants.PA_UPDATE_ACTION,
                ServerConstants.PT_CONTROL, json,
                (Ack) args -> {
                    if (args.length > 0 && args[0] != null && callback != null) {
                        callback.onResponse(args[0].toString());
                    }
                });
    }

    private static void settingsPostEmit(Socket socket, String json, SocketCallback callback) {
        socket.emit(ServerConstants.PE_POST_APP_DATA, ServerConstants.PA_UPDATE_ACTION,
                ServerConstants.PT_SETTINGS, json,
                (Ack) args -> {
                    if (args.length > 0 && args[0] != null && callback != null) {
                        callback.onResponse(args[0].toString());
                    }
                });
    }

    private static void pelletsPostEmit(Socket socket, String type, String json,
                                        SocketCallback callback) {
        socket.emit(ServerConstants.PE_POST_APP_DATA, ServerConstants.PA_PELLETS_ACTION, type,
                json, (Ack) args -> {
                    if (args.length > 0 && args[0] != null && callback != null) {
                        callback.onResponse(args[0].toString());
                    }
                });
    }

    @SuppressWarnings("SameParameterValue")
    private static void removePostEmit(Socket socket, String type, String json,
                                       SocketCallback callback) {
        socket.emit(ServerConstants.PE_POST_APP_DATA, ServerConstants.PA_REMOVE_ACTION, type,
                json, (Ack) args -> {
                    if (args.length > 0 && args[0] != null && callback != null) {
                        callback.onResponse(args[0].toString());
                    }
                });
    }

    private static void adminPostEmit(Socket socket, String type, SocketCallback callback) {
        socket.emit(ServerConstants.PE_POST_APP_DATA, ServerConstants.PA_ADMIN_ACTION, type,
                (Ack) args -> {
                    if (args.length > 0 && args[0] != null && callback != null) {
                        callback.onResponse(args[0].toString());
                    }
                });
    }

    public static void settingsGetEmit(Socket socket, SocketCallback callback) {
        if (VersionUtils.isSupported("1.2.6")) {
            socket.emit(ServerConstants.GE_GET_APP_DATA, ServerConstants.GA_SETTINGS_DATA,
                    (Ack) args -> {
                        if (args.length > 0 && args[0] != null && callback != null) {
                            callback.onResponse(args[0].toString());
                        }
                    });
        } else {
            socket.emit(ServerConstants.REQUEST_SETTINGS_DATA, (Ack) args -> {
                if (args.length > 0 && args[0] != null && callback != null) {
                    callback.onResponse(args[0].toString());
                }
            });
        }
    }

    public static void pelletsGetEmit(Socket socket, SocketCallback callback) {
        if (VersionUtils.isSupported("1.2.6")) {
            socket.emit(ServerConstants.GE_GET_APP_DATA, ServerConstants.GA_PELLETS_DATA,
                    (Ack) args -> {
                        if (args.length > 0 && args[0] != null && callback != null) {
                            callback.onResponse(args[0].toString());
                        }
                    });
        } else {
            socket.emit(ServerConstants.REQUEST_PELLET_DATA, (Ack) args -> {
                if (args.length > 0 && args[0] != null && callback != null) {
                    callback.onResponse(args[0].toString());
                }
            });
        }
    }

    public static void infoGetEmit(Socket socket, SocketCallback callback) {
        if (VersionUtils.isSupported("1.2.6")) {
            socket.emit(ServerConstants.GE_GET_APP_DATA, ServerConstants.GA_INFO_DATA,
                    (Ack) args -> {
                        if (args.length > 0 && args[0] != null && callback != null) {
                            callback.onResponse(args[0].toString());
                        }
                    });
        } else {
            socket.emit(ServerConstants.REQUEST_INFO_DATA, (Ack) args -> {
                if (args.length > 0 && args[0] != null && callback != null) {
                    callback.onResponse(args[0].toString());
                }
            });
        }
    }

    public static void eventsGetEmit(Socket socket, SocketCallback callback) {
        if (VersionUtils.isSupported("1.2.6")) {
            socket.emit(ServerConstants.GE_GET_APP_DATA, ServerConstants.GA_EVENTS_DATA,
                    (Ack) args -> {
                        if (args.length > 0 && args[0] != null && callback != null) {
                            callback.onResponse(args[0].toString());
                        }
                    });
        } else {
            socket.emit(ServerConstants.REQUEST_EVENT_DATA, (Ack) args -> {
                if (args.length > 0 && args[0] != null && callback != null) {
                    callback.onResponse(args[0].toString());
                }
            });
        }
    }

    public static void historyGetEmit(Socket socket, SocketCallback callback) {
        if (VersionUtils.isSupported("1.2.6")) {
            socket.emit(ServerConstants.GE_GET_APP_DATA, ServerConstants.GA_HISTORY_DATA,
                    (Ack) args -> {
                        if (args.length > 0 && args[0] != null && callback != null) {
                            callback.onResponse(args[0].toString());
                        }
                    });
        } else {
            socket.emit(ServerConstants.REQUEST_HISTORY_DATA, (Ack) args -> {
                if (args.length > 0 && args[0] != null && callback != null) {
                    callback.onResponse(args[0].toString());
                }
            });
        }
    }

    public static void manualGetEmit(Socket socket, SocketCallback callback) {
        if (VersionUtils.isSupported("1.2.6")) {
            socket.emit(ServerConstants.GE_GET_APP_DATA, ServerConstants.GA_MANUAL_DATA,
                    (Ack) args -> {
                        if (args.length > 0 && args[0] != null && callback != null) {
                            callback.onResponse(args[0].toString());
                        }
                    });
        } else {
            socket.emit(ServerConstants.REQUEST_MANUAL_DATA, (Ack) args -> {
                if (args.length > 0 && args[0] != null && callback != null) {
                    callback.onResponse(args[0].toString());
                }
            });
        }
    }

    public static void backupListGetEmit(Socket socket, String type, SocketCallback callback) {
        if (VersionUtils.isSupported("1.2.6")) {
            socket.emit(ServerConstants.GE_GET_APP_DATA, ServerConstants.GA_BACKUP_LIST,
                    type, (Ack) args -> {
                        if (args.length > 0 && args[0] != null && callback != null) {
                            callback.onResponse(args[0].toString());
                        }
                    });
        } else {
            socket.emit(ServerConstants.REQUEST_BACKUP_DATA, (Ack) args -> {
                if (args.length > 0 && args[0] != null && callback != null) {
                    callback.onResponse(args[0].toString());
                }
            });
        }
    }

    public static void backupDataGetEmit(Socket socket, String type, SocketCallback callback) {
        if (VersionUtils.isSupported("1.2.6")) {
            socket.emit(ServerConstants.GE_GET_APP_DATA, ServerConstants.GA_BACKUP_DATA,
                    type, (Ack) args -> {
                        if (args.length > 0 && args[0] != null && callback != null) {
                            callback.onResponse(args[0].toString());
                        }
                    });
        } else {
            socket.emit(ServerConstants.REQUEST_BACKUP_DATA, (Ack) args -> {
                if (args.length > 0 && args[0] != null && callback != null) {
                    callback.onResponse(args[0].toString());
                }
            });
        }
    }

    public static void backupRestoreRemoteEmit(Socket socket, String type, String fileName,
                                               SocketCallback callback) {
        if (VersionUtils.isSupported("1.2.6")) {
            socket.emit(ServerConstants.PE_POST_RESTORE_DATA, type, fileName, (Ack) args -> {
                if (args.length > 0 && args[0] != null && callback != null) {
                    callback.onResponse(args[0].toString());
                }
            });
        } else {
            socket.emit(ServerConstants.UPDATE_RESTORE_DATA, type, fileName, (Ack) args -> {
                if (args.length > 0 && args[0] != null && callback != null) {
                    callback.onResponse(args[0].toString());
                }
            });
        }
    }

    public static void backupRestoreLocalEmit(Socket socket, String type, String jsonData,
                                              SocketCallback callback) {
        if (VersionUtils.isSupported("1.2.6")) {
            socket.emit(ServerConstants.PE_POST_RESTORE_DATA, type, "none",
                    jsonData, (Ack) args -> {
                        if (args.length > 0 && args[0] != null && callback != null) {
                            callback.onResponse(args[0].toString());
                        }
                    });
        } else {
            socket.emit(ServerConstants.UPDATE_RESTORE_DATA, type, "none",
                    jsonData, (Ack) args -> {
                        if (args.length > 0 && args[0] != null && callback != null) {
                            callback.onResponse(args[0].toString());
                        }
                    });
        }
    }
}
