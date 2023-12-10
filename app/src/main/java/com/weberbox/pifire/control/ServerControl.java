package com.weberbox.pifire.control;

import android.content.Context;

import com.google.gson.Gson;
import com.weberbox.pifire.constants.ServerConstants;
import com.weberbox.pifire.interfaces.SocketCallback;
import com.weberbox.pifire.model.local.DashProbeModel.DashProbe;
import com.weberbox.pifire.model.remote.ControlDataModel;
import com.weberbox.pifire.model.remote.ControlDataModel.Manual;
import com.weberbox.pifire.model.remote.DashDataModel.NotifyData;
import com.weberbox.pifire.model.remote.PelletDataModel.PelletProfileModel;
import com.weberbox.pifire.model.remote.PostDataModel;
import com.weberbox.pifire.model.remote.PostDataModel.OneSignalDevice;
import com.weberbox.pifire.model.remote.PostDataModel.PelletsAction;
import com.weberbox.pifire.model.remote.PostDataModel.TimerAction;
import com.weberbox.pifire.model.remote.ProbeDataModel.ProbeMap;
import com.weberbox.pifire.model.remote.ServerResponseModel;
import com.weberbox.pifire.model.remote.SettingsDataModel;
import com.weberbox.pifire.model.remote.SettingsDataModel.Apprise;
import com.weberbox.pifire.model.remote.SettingsDataModel.CycleData;
import com.weberbox.pifire.model.remote.SettingsDataModel.Globals;
import com.weberbox.pifire.model.remote.SettingsDataModel.Ifttt;
import com.weberbox.pifire.model.remote.SettingsDataModel.InfluxDB;
import com.weberbox.pifire.model.remote.SettingsDataModel.KeepWarm;
import com.weberbox.pifire.model.remote.SettingsDataModel.OneSignalDeviceInfo;
import com.weberbox.pifire.model.remote.SettingsDataModel.OneSignalPush;
import com.weberbox.pifire.model.remote.SettingsDataModel.PWM;
import com.weberbox.pifire.model.remote.SettingsDataModel.PWMProfile;
import com.weberbox.pifire.model.remote.SettingsDataModel.PelletLevel;
import com.weberbox.pifire.model.remote.SettingsDataModel.PushBullet;
import com.weberbox.pifire.model.remote.SettingsDataModel.Pushover;
import com.weberbox.pifire.model.remote.SettingsDataModel.SSProfile;
import com.weberbox.pifire.model.remote.SettingsDataModel.SmartStart;
import com.weberbox.pifire.model.remote.SettingsDataModel.SmokePlus;
import com.weberbox.pifire.model.remote.SettingsDataModel.StartToMode;
import com.weberbox.pifire.model.remote.SettingsDataModel.NotifyServices;
import com.weberbox.pifire.utils.AckTimeOut;
import com.weberbox.pifire.utils.SettingsUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.socket.client.Ack;
import io.socket.client.Socket;
import timber.log.Timber;

public class ServerControl {

    // Start Grill
    public static void modeStartGrill(Socket socket, SocketCallback callback) {
        String json = new Gson().toJson(new ControlDataModel()
                .withMode(ServerConstants.G_MODE_START)
                .withUpdated(true));
        controlPostEmit(socket, json, callback);
    }

    // Monitor Grill
    public static void modeMonitorGrill(Socket socket, SocketCallback callback) {
        String json = new Gson().toJson(new ControlDataModel()
                .withMode(ServerConstants.G_MODE_MONITOR)
                .withUpdated(true));
        controlPostEmit(socket, json, callback);
    }

    // Stop Grill
    public static void modeStopGrill(Socket socket, SocketCallback callback) {
        String json = new Gson().toJson(new ControlDataModel()
                .withMode(ServerConstants.G_MODE_STOP)
                .withUpdated(true));
        controlPostEmit(socket, json, callback);
    }

    // Shutdown Grill
    public static void modeShutdownGrill(Socket socket, SocketCallback callback) {
        String json = new Gson().toJson(new ControlDataModel()
                .withMode(ServerConstants.G_MODE_SHUTDOWN)
                .withUpdated(true));
        controlPostEmit(socket, json, callback);
    }

    // Prime Mode
    public static void modePrimeGrill(Socket socket, Integer primeAmount, String nextMode,
                                      SocketCallback callback) {
        String json = new Gson().toJson(new ControlDataModel()
                .withMode(ServerConstants.G_MODE_PRIME)
                .withPrimeAmount(primeAmount)
                .withNextMode(nextMode)
                .withUpdated(true));
        controlPostEmit(socket, json, callback);
    }

    // Mode Smoke
    public static void modeSmokeGrill(Socket socket, SocketCallback callback) {
        String json = new Gson().toJson(new ControlDataModel()
                .withMode(ServerConstants.G_MODE_SMOKE)
                .withUpdated(true));
        controlPostEmit(socket, json, callback);
    }

    // Smoke Plus Enable/Disable
    public static void setSmokePlus(Socket socket, boolean enabled, SocketCallback callback) {
        String json = new Gson().toJson(new ControlDataModel().withsPlus(enabled));
        controlPostEmit(socket, json, callback);
    }

    // Set Grill Temp
    public static void setGrillHoldTemp(Socket socket, String temp, SocketCallback callback) {
        String json = new Gson().toJson(new ControlDataModel()
                .withUpdated(true)
                .withMode(ServerConstants.G_MODE_HOLD)
                .withPrimarySetPoint(Integer.valueOf(temp)));
        controlPostEmit(socket, json, callback);
    }

    // Set Temp Notify
    public static void setProbeNotify(Socket socket, DashProbe probe,
                                      ArrayList<NotifyData> notifyData, String temp,
                                      boolean holdMode, SocketCallback callback) {
        for (NotifyData notify : notifyData) {
            if (notify.getLabel().equals(probe.getLabel())) {
                notify.setTarget(Integer.valueOf(temp));
                notify.setKeepWarm(probe.getKeepWarm());
                notify.setShutdown(probe.getShutdown());
                notify.setReq(true);
            }
        }
        String json = new Gson().toJson(new ControlDataModel()
                .withNotifyData(notifyData)
                .withUpdated(holdMode));
        controlPostEmit(socket, json, callback);
    }

    // Clear Temp Notify
    public static void clearProbeNotify(Socket socket, DashProbe probe,
                                        ArrayList<NotifyData> notifyData, SocketCallback callback) {
        for (NotifyData notify : notifyData) {
            if (notify.getLabel().equals(probe.getLabel())) {
                notify.setTarget(0);
                notify.setKeepWarm(false);
                notify.setShutdown(false);
                notify.setReq(false);
            }
        }
        String json = new Gson().toJson(new ControlDataModel()
                .withNotifyData(notifyData));
        controlPostEmit(socket, json, callback);
    }

    // Timer Start/Stop
    public static void sendTimerAction(Socket socket, String action, SocketCallback callback) {
        timerPostEmit(socket, action, null, callback);
    }

    // Timer Set Time
    public static void sendTimerTime(Socket socket, String hours, String minutes, boolean shutdown,
                                     boolean keepWarm, SocketCallback callback) {
        String json = new Gson().toJson(new PostDataModel()
                .withTimerAction(new TimerAction()
                        .withHours(Integer.parseInt(hours))
                        .withMinutes(Integer.parseInt(minutes))
                        .withShutdown(shutdown)
                        .withKeepWarm(keepWarm)));
        timerPostEmit(socket, ServerConstants.PT_TIMER_START, json, callback);
    }

    // Shutdown Timer
    public static void sendShutdownTime(Socket socket, String shutDownTime,
                                        SocketCallback callback) {
        String json = new Gson().toJson(new SettingsDataModel()
                .withGlobals(new Globals().withShutdownTimer(Integer.parseInt(shutDownTime))));
        settingsPostEmit(socket, json, callback);
        controlSettingsUpdateEmit(socket, callback);
    }

    // Startup Timer
    public static void sendStartupTime(Socket socket, String startUpTime,
                                       SocketCallback callback) {
        String json = new Gson().toJson(new SettingsDataModel()
                .withGlobals(new Globals().withStartupTimer(Integer.parseInt(startUpTime))));
        settingsPostEmit(socket, json, callback);
        controlSettingsUpdateEmit(socket, callback);
    }

    // Startup Exit Temp
    public static void setStartExitTemp(Socket socket, String exitTemp,
                                       SocketCallback callback) {
        String json = new Gson().toJson(new SettingsDataModel()
                .withGlobals(new Globals().withStartExitTemp(Integer.parseInt(exitTemp))));
        settingsPostEmit(socket, json, callback);
        controlSettingsUpdateEmit(socket, callback);
    }

    // Auto Power Off
    public static void sendAutoPowerOff(Socket socket, Boolean autoPowerOff,
                                        SocketCallback callback) {
        String json = new Gson().toJson(new SettingsDataModel()
                .withGlobals(new Globals().withAutoPowerOff(autoPowerOff)));
        settingsPostEmit(socket, json, callback);
        controlSettingsUpdateEmit(socket, callback);
    }

    // Smart Start
    public static void setSmartStartEnabled(Socket socket, Boolean enabled,
                                            SocketCallback callback) {
        String json = new Gson().toJson(new SettingsDataModel()
                .withSmartStart(new SmartStart().withEnabled(enabled)));
        settingsPostEmit(socket, json, callback);
    }

    // Smart Start Exit Temp
    public static void setSmartStartExitTemp(Socket socket, String temp,
                                            SocketCallback callback) {
        String json = new Gson().toJson(new SettingsDataModel()
                .withSmartStart(new SmartStart().withExitTemp(Integer.parseInt(temp))));
        settingsPostEmit(socket, json, callback);
    }

    // Set Smart Start Table
    public static void setSmartStartTable(Socket socket, List<Integer> tempRange,
                                          List<SSProfile> profiles, SocketCallback callback) {
        String json = new Gson().toJson(new SettingsDataModel()
                .withSmartStart(new SmartStart().withTempRangeList(tempRange)
                        .withProfiles(profiles)));
        settingsPostEmit(socket, json, callback);
    }

    // Set Updated Probe Map
    public static void sendUpdatedProbeMap(Socket socket, ProbeMap probeMap,
                                          SocketCallback callback) {
        String json = new Gson().toJson(new SettingsDataModel()
                .withProbeSettings(new SettingsDataModel.ProbeSettings().withProbeMap(probeMap)));
        settingsPostEmit(socket, json, callback);
    }

    // Enable IFTTT
    public static void setIFTTTEnabled(Socket socket, boolean enabled, SocketCallback callback) {
        String json = new Gson().toJson(new SettingsDataModel()
                .withNotifyServices(new NotifyServices()
                        .withIfttt(new Ifttt().withEnabled(enabled))));
        settingsPostEmit(socket, json, callback);
        controlSettingsUpdateEmit(socket, callback);
    }

    // Set IFTTT APIKey
    public static void setIFTTTAPIKey(Socket socket, String apiKey, SocketCallback callback) {
        String json = new Gson().toJson(new SettingsDataModel()
                .withNotifyServices(new NotifyServices()
                    .withIfttt(new Ifttt().withAPIKey(apiKey))));
        settingsPostEmit(socket, json, callback);
        controlSettingsUpdateEmit(socket, callback);
    }

    // Enable PushOver
    public static void setPushOverEnabled(Socket socket, boolean enabled, SocketCallback callback) {
        String json = new Gson().toJson(new SettingsDataModel()
                .withNotifyServices(new NotifyServices()
                    .withPushover(new Pushover().withEnabled(enabled))));
        settingsPostEmit(socket, json, callback);
        controlSettingsUpdateEmit(socket, callback);
    }

    // Set PushOver APIKey
    public static void setPushOverAPIKey(Socket socket, String apiKey, SocketCallback callback) {
        String json = new Gson().toJson(new SettingsDataModel()
                .withNotifyServices(new NotifyServices()
                    .withPushover(new Pushover().withAPIKey(apiKey))));
        settingsPostEmit(socket, json, callback);
        controlSettingsUpdateEmit(socket, callback);
    }

    // Set PushOver UserKeys
    public static void setPushOverUserKeys(Socket socket, String userKeys,
                                           SocketCallback callback) {
        String json = new Gson().toJson(new SettingsDataModel()
                .withNotifyServices(new NotifyServices()
                    .withPushover(new Pushover().withUserKeys(userKeys))));
        settingsPostEmit(socket, json, callback);
        controlSettingsUpdateEmit(socket, callback);
    }

    // Set PushOver PublicURL
    public static void setPushOverURL(Socket socket, String url, SocketCallback callback) {
        String json = new Gson().toJson(new SettingsDataModel()
                .withNotifyServices(new NotifyServices()
                    .withPushover(new Pushover().withPublicURL(url))));
        settingsPostEmit(socket, json, callback);
        controlSettingsUpdateEmit(socket, callback);
    }

    // Enable PushBullet
    public static void setPushBulletEnabled(Socket socket, boolean enabled,
                                            SocketCallback callback) {
        String json = new Gson().toJson(new SettingsDataModel()
                .withNotifyServices(new NotifyServices()
                        .withPushBullet(new PushBullet().withEnabled(enabled))));
        settingsPostEmit(socket, json, callback);
        controlSettingsUpdateEmit(socket, callback);
    }

    // Set PushBullet APIKey
    public static void setPushBulletAPIKey(Socket socket, String apiKey, SocketCallback callback) {
        String json = new Gson().toJson(new SettingsDataModel()
                .withNotifyServices(new NotifyServices()
                    .withPushBullet(new PushBullet().withAPIKey(apiKey))));
        settingsPostEmit(socket, json, callback);
        controlSettingsUpdateEmit(socket, callback);
    }

    // Set PushBullet URL
    public static void setPushBulletURL(Socket socket, String url, SocketCallback callback) {
        String json = new Gson().toJson(new SettingsDataModel()
                .withNotifyServices(new NotifyServices()
                    .withPushBullet(new PushBullet().withPublicURL(url))));
        settingsPostEmit(socket, json, callback);
        controlSettingsUpdateEmit(socket, callback);
    }

    // Set Apprise Enabled
    public static void setAppriseEnabled(Socket socket, boolean enabled,
                                         SocketCallback callback) {
        String json = new Gson().toJson(new SettingsDataModel()
                .withNotifyServices(new NotifyServices()
                    .withApprise(new Apprise().withEnabled(enabled))));
        settingsPostEmit(socket, json, callback);
        controlSettingsUpdateEmit(socket, callback);
    }

    // Set Apprise Locations
    public static void setAppriseLocations(Socket socket, List<String> locations,
                                           SocketCallback callback) {
        String json = new Gson().toJson(new SettingsDataModel()
                .withNotifyServices(new NotifyServices()
                    .withApprise(new Apprise().withLocations(locations))));
        settingsPostEmit(socket, json, callback);
        controlSettingsUpdateEmit(socket, callback);
    }

    // Set OneSignal Enabled
    public static void setOneSignalEnabled(Socket socket, boolean enabled,
                                           SocketCallback callback) {
        String json = new Gson().toJson(new SettingsDataModel()
                .withNotifyServices(new NotifyServices()
                        .withOneSignal(new OneSignalPush().withEnabled(enabled))));
        settingsPostEmit(socket, json, callback);
        controlSettingsUpdateEmit(socket, callback);
    }

    // Set OneSignal App ID
    public static void setOneSignalAppID(Socket socket, String appID, SocketCallback callback) {
        String json = new Gson().toJson(new SettingsDataModel()
                .withNotifyServices(new NotifyServices()
                    .withOneSignal(new OneSignalPush().withAppId(appID))));
        settingsPostEmit(socket, json, callback);
        controlSettingsUpdateEmit(socket, callback);
    }

    // Register OneSignal Device
    public static void registerOneSignalDevice(Context context, Socket socket,
                                               Map<String, OneSignalDeviceInfo> device) {
        String json = new Gson().toJson(new SettingsDataModel()
                .withNotifyServices(new NotifyServices()
                    .withOneSignal(new SettingsDataModel.OneSignalPush()
                            .withOneSignalDevices(device))));
        socket.emit(ServerConstants.PE_POST_APP_DATA, ServerConstants.PA_UPDATE_ACTION,
                ServerConstants.PT_SETTINGS, json, (Ack) args -> {
                    if (args.length > 0 && args[0] != null) {
                        ServerResponseModel response =
                                ServerResponseModel.parseJSON(args[0].toString());
                        if (response.getResult().equals("success")) {
                            new SettingsUtils(context, null).requestSettingsData(socket);
                        } else {
                            Timber.d("Failed to register with Pifire");
                        }
                    }
                });
    }

    @SuppressWarnings("unused")
    // Remove OneSignal Device
    public static void removeOneSignalDevice(Socket socket, String playerId,
                                             SocketCallback callback) {
        String json = new Gson().toJson(new PostDataModel()
                .withOneSignalDevice(new OneSignalDevice().withOneSignalPlayerID(playerId)));
        removePostEmit(socket, ServerConstants.PT_ONESIGNAL_DEVICE, json, callback);
        controlSettingsUpdateEmit(socket, callback);
    }

    // Set InfluxDB Enabled
    public static void setInfluxDBEnabled(Socket socket, boolean enabled, SocketCallback callback) {
        String json = new Gson().toJson(new SettingsDataModel()
                .withNotifyServices(new NotifyServices()
                    .withInfluxdb(new InfluxDB().withEnabled(enabled))));
        settingsPostEmit(socket, json, callback);
        controlSettingsUpdateEmit(socket, callback);
    }

    // Set InfluxDB URL
    public static void setInfluxDBUrl(Socket socket, String url, SocketCallback callback) {
        String json = new Gson().toJson(new SettingsDataModel()
                .withNotifyServices(new NotifyServices()
                    .withInfluxdb(new InfluxDB().withUrl(url))));
        settingsPostEmit(socket, json, callback);
        controlSettingsUpdateEmit(socket, callback);
    }

    // Set InfluxDB Token
    public static void setInfluxDBToken(Socket socket, String token, SocketCallback callback) {
        String json = new Gson().toJson(new SettingsDataModel()
                .withNotifyServices(new NotifyServices()
                        .withInfluxdb(new InfluxDB().withToken(token))));
        settingsPostEmit(socket, json, callback);
        controlSettingsUpdateEmit(socket, callback);
    }

    // Set InfluxDB Org
    public static void setInfluxDBOrg(Socket socket, String org, SocketCallback callback) {
        String json = new Gson().toJson(new SettingsDataModel()
                .withNotifyServices(new NotifyServices()
                    .withInfluxdb(new InfluxDB().withOrg(org))));
        settingsPostEmit(socket, json, callback);
        controlSettingsUpdateEmit(socket, callback);
    }

    // Set InfluxDB Bucket
    public static void setInfluxDBBucket(Socket socket, String bucket, SocketCallback callback) {
        String json = new Gson().toJson(new SettingsDataModel()
                .withNotifyServices(new NotifyServices()
                    .withInfluxdb(new InfluxDB().withBucket(bucket))));
        settingsPostEmit(socket, json, callback);
        controlSettingsUpdateEmit(socket, callback);
    }

    // Set Min Start Temp
    public static void setMinStartTemp(Socket socket, String temp, SocketCallback callback) {
        String json = new Gson().toJson(new SettingsDataModel()
                .withSafety(new SettingsDataModel.Safety().
                        withMinStartupTemp(Integer.parseInt(temp))));
        settingsPostEmit(socket, json, callback);
    }

    // Set Max Start Temp
    public static void setMaxStartTemp(Socket socket, String temp, SocketCallback callback) {
        String json = new Gson().toJson(new SettingsDataModel()
                .withSafety(new SettingsDataModel.Safety().
                        withMaxStartupTemp(Integer.parseInt(temp))));
        settingsPostEmit(socket, json, callback);
    }

    // Set Reignite Retries
    public static void setReigniteRetries(Socket socket, String retries, SocketCallback callback) {
        String json = new Gson().toJson(new SettingsDataModel()
                .withSafety(new SettingsDataModel.Safety().
                        withReigniteRetries(Integer.parseInt(retries))));
        settingsPostEmit(socket, json, callback);
    }

    // Set Max Temp
    public static void setMaxGrillTemp(Socket socket, String temp, SocketCallback callback) {
        String json = new Gson().toJson(new SettingsDataModel()
                .withSafety(new SettingsDataModel.Safety().
                        withMaxTemp(Integer.parseInt(temp))));
        settingsPostEmit(socket, json, callback);
    }

    // Set Grill Name
    public static void setGrillName(Socket socket, String name, SocketCallback callback) {
        String json = new Gson().toJson(new SettingsDataModel()
                .withGlobals(new Globals().withGrillName(name)));
        settingsPostEmit(socket, json, callback);
    }

    // Set Auger On Time
    public static void setAugerOnTime(Socket socket, String time, SocketCallback callback) {
        String json = new Gson().toJson(new SettingsDataModel()
                .withCycleData(new CycleData().withSmokeOnCycleTime(Integer.parseInt(time))));
        settingsPostEmit(socket, json, callback);
        controlSettingsUpdateEmit(socket, callback);
    }

    // Set Auger Off Time
    public static void setAugerOffTime(Socket socket, String time, SocketCallback callback) {
        String json = new Gson().toJson(new SettingsDataModel()
                .withCycleData(new CycleData().withSmokeOffCycleTime(Integer.parseInt(time))));
        settingsPostEmit(socket, json, callback);
        controlSettingsUpdateEmit(socket, callback);
    }

    // Set P-Mode
    public static void setPMode(Socket socket, String mode, SocketCallback callback) {
        String json = new Gson().toJson(new SettingsDataModel()
                .withCycleData(new CycleData().withPMode(Integer.parseInt(mode))));
        settingsPostEmit(socket, json, callback);
        controlSettingsUpdateEmit(socket, callback);
    }

    // Set Smoke Plus Default
    public static void setSmokePlusDefault(Socket socket, boolean enabled, SocketCallback callback) {
        String json = new Gson().toJson(new SettingsDataModel()
                .withSmokePlus(new SmokePlus().withEnabled(enabled)));
        settingsPostEmit(socket, json, callback);
        controlSettingsUpdateEmit(socket, callback);
    }

    // Set Smoke Fan Ramp
    public static void setSPlusFanRamp(Socket socket, boolean enabled, SocketCallback callback) {
        String json = new Gson().toJson(new SettingsDataModel()
                .withSmokePlus(new SmokePlus().withFanRamp(enabled)));
        settingsPostEmit(socket, json, callback);
        controlSettingsUpdateEmit(socket, callback);
    }

    // Set Smoke Fan Ramp Duty Cycle
    public static void setFanDutyCycle(Socket socket, String dutyCycle, SocketCallback callback) {
        String json = new Gson().toJson(new SettingsDataModel()
                .withSmokePlus(new SmokePlus().withDutyCycle(Integer.parseInt(dutyCycle))));
        settingsPostEmit(socket, json, callback);
        controlSettingsUpdateEmit(socket, callback);
    }

    // Set Smoke Fan On Time
    public static void setFanOnTime(Socket socket, String onTime, SocketCallback callback) {
        String json = new Gson().toJson(new SettingsDataModel()
                .withSmokePlus(new SmokePlus().withOnTime(Integer.parseInt(onTime))));
        settingsPostEmit(socket, json, callback);
        controlSettingsUpdateEmit(socket, callback);
    }

    // Set Smoke Fan Off Time
    public static void setFanOffTime(Socket socket, String offTime, SocketCallback callback) {
        String json = new Gson().toJson(new SettingsDataModel()
                .withSmokePlus(new SmokePlus().withOffTime(Integer.parseInt(offTime))));
        settingsPostEmit(socket, json, callback);
        controlSettingsUpdateEmit(socket, callback);
    }

    // Set Smoke Fan Cycle Time
    public static void setSmokeFan(Socket socket, String time, SocketCallback callback) {
        String json = new Gson().toJson(new SettingsDataModel()
                .withSmokePlus(new SmokePlus().withCycle(Integer.parseInt(time))));
        settingsPostEmit(socket, json, callback);
        controlSettingsUpdateEmit(socket, callback);
    }

    // Set Smoke Min Temp
    public static void setSmokeMinTemp(Socket socket, String temp, SocketCallback callback) {
        String json = new Gson().toJson(new SettingsDataModel()
                .withSmokePlus(new SmokePlus().withMinTemp(Integer.parseInt(temp))));
        settingsPostEmit(socket, json, callback);
        controlSettingsUpdateEmit(socket, callback);
    }

    // Set Smoke Max Temp
    public static void setSmokeMaxTemp(Socket socket, String temp, SocketCallback callback) {
        String json = new Gson().toJson(new SettingsDataModel()
                .withSmokePlus(new SmokePlus().withMaxTemp(Integer.parseInt(temp))));
        settingsPostEmit(socket, json, callback);
        controlSettingsUpdateEmit(socket, callback);
    }

    // Set Controller Cycle Time
    public static void setControllerTime(Socket socket, String time, SocketCallback callback) {
        String json = new Gson().toJson(new SettingsDataModel()
                .withCycleData(new CycleData().withHoldCycleTime(Integer.parseInt(time))));
        settingsPostEmit(socket, json, callback);
    }

    // Set Controller U Min
    public static void setControlleruMin(Socket socket, String uMin, SocketCallback callback) {
        String json = new Gson().toJson(new SettingsDataModel()
                .withCycleData(new CycleData().withuMin(Float.parseFloat(uMin))));
        settingsPostEmit(socket, json, callback);
    }

    // Set Controller U Max
    public static void setControlleruMax(Socket socket, String uMax, SocketCallback callback) {
        String json = new Gson().toJson(new SettingsDataModel()
                .withCycleData(new CycleData().withuMax(Float.parseFloat(uMax))));
        settingsPostEmit(socket, json, callback);
    }

    // Set Lid Open Detect
    public static void setLidOpenDetect(Socket socket, boolean enabled, SocketCallback callback) {
        String json = new Gson().toJson(new SettingsDataModel()
                .withCycleData(new CycleData().withLidOpenDetectEnabled(enabled)));
        settingsPostEmit(socket, json, callback);
        controlSettingsUpdateEmit(socket, callback);
    }

    // Set Lid Open Threshold
    public static void setLidOpenThresh(Socket socket, String threshold , SocketCallback callback) {
        String json = new Gson().toJson(new SettingsDataModel()
                .withCycleData(new CycleData().withLidOpenThreshold(Integer.parseInt(threshold))));
        settingsPostEmit(socket, json, callback);
        controlSettingsUpdateEmit(socket, callback);
    }

    // Set Lid Open Threshold
    public static void setLidOpenPause(Socket socket, String pauseTime , SocketCallback callback) {
        String json = new Gson().toJson(new SettingsDataModel()
                .withCycleData(new CycleData().withLidOpenPauseTime(Integer.parseInt(pauseTime))));
        settingsPostEmit(socket, json, callback);
        controlSettingsUpdateEmit(socket, callback);
    }

    // Set Keep Warm S Plus
    public static void setKeepWarmSPlus(Socket socket, boolean enabled, SocketCallback callback) {
        String json = new Gson().toJson(new SettingsDataModel()
                .withKeepWarm(new KeepWarm().withSPlus(enabled)));
        settingsPostEmit(socket, json, callback);
        controlSettingsUpdateEmit(socket, callback);
    }

    // Set Keep Warm Temp
    public static void setKeepWarmTemp(Socket socket, String temp, SocketCallback callback) {
        String json = new Gson().toJson(new SettingsDataModel()
                .withKeepWarm(new KeepWarm().withTemp(Integer.parseInt(temp))));
        settingsPostEmit(socket, json, callback);
        controlSettingsUpdateEmit(socket, callback);
    }

    // Set PWM Temp Control Enabled
    public static void setPWMControl(Socket socket, boolean enabled, SocketCallback callback) {
        String json = new Gson().toJson(new ControlDataModel().withPWMControl(enabled));
        controlPostEmit(socket, json, callback);
        controlSettingsUpdateEmit(socket, callback);
    }

    // Set PWM Temp Control Enabled
    public static void setPWMControlDefault(Socket socket, boolean enabled, SocketCallback callback) {
        String json = new Gson().toJson(new SettingsDataModel()
                .withPWM(new PWM().withPWMControl(enabled)));
        settingsPostEmit(socket, json, callback);
        controlSettingsUpdateEmit(socket, callback);
    }

    // Set PWM Temp Control Update Time
    public static void setPWMTempUpdateTime(Socket socket, String updateTime, SocketCallback callback) {
        String json = new Gson().toJson(new SettingsDataModel()
                .withPWM(new PWM().withUpdateTime(Integer.parseInt(updateTime))));
        settingsPostEmit(socket, json, callback);
        controlSettingsUpdateEmit(socket, callback);
    }

    // Set PWM Fan Frequency
    public static void setPWMFanFrequency(Socket socket, String frequency, SocketCallback callback) {
        String json = new Gson().toJson(new SettingsDataModel()
                .withPWM(new PWM().withFrequency(Integer.parseInt(frequency))));
        settingsPostEmit(socket, json, callback);
        controlSettingsUpdateEmit(socket, callback);
    }

    // Set PWM Min Duty Cycle
    public static void setPWMMinDutyCycle(Socket socket, String dutyCycle, SocketCallback callback) {
        String json = new Gson().toJson(new SettingsDataModel()
                .withPWM(new PWM().withMinDutyCycle(Integer.parseInt(dutyCycle))));
        settingsPostEmit(socket, json, callback);
        controlSettingsUpdateEmit(socket, callback);
    }

    // Set PWM Max Duty Cycle
    public static void setPWMMaxDutyCycle(Socket socket, String dutyCycle, SocketCallback callback) {
        String json = new Gson().toJson(new SettingsDataModel()
                .withPWM(new PWM().withMaxDutyCycle(Integer.parseInt(dutyCycle))));
        settingsPostEmit(socket, json, callback);
        controlSettingsUpdateEmit(socket, callback);
    }

    // Set PWM Control Items
    public static void setPWMControlTable(Socket socket, List<Integer> tempRange,
                                          List<PWMProfile> profiles, SocketCallback callback) {
        String json = new Gson().toJson(new SettingsDataModel()
                .withPWM(new PWM().withTempRangeList(tempRange).withProfiles(profiles)));
        settingsPostEmit(socket, json, callback);
        controlSettingsUpdateEmit(socket, callback);
    }

    // Set Start To Mode
    public static void setStartToMode(Socket socket, String mode, SocketCallback callback) {
        String json = new Gson().toJson(new SettingsDataModel()
                .withStartToMode(new StartToMode().withAfterStartUpMode(mode)));
        settingsPostEmit(socket, json, callback);
    }

    // Set Start To Mode Temp
    public static void setStartToModeTemp(Socket socket, String temp, SocketCallback callback) {
        String json = new Gson().toJson(new SettingsDataModel()
                .withStartToMode(new StartToMode().withPrimarySetPoint(Integer.parseInt(temp))));
        settingsPostEmit(socket, json, callback);
    }

    // Set Pellets Warning Enabled
    public static void setPelletWarningEnabled(Socket socket, boolean enabled,
                                               SocketCallback callback) {
        String json = new Gson().toJson(new SettingsDataModel()
                .withPelletLevel(new PelletLevel().withWarningEnabled(enabled)));
        settingsPostEmit(socket, json, callback);
        controlSettingsUpdateEmit(socket, callback);
    }

    // Set Pellets Warning Level
    public static void setPelletWarningLevel(Socket socket, String level, SocketCallback callback) {
        String json = new Gson().toJson(new SettingsDataModel()
                .withPelletLevel(new PelletLevel().withWarningLevel(Integer.parseInt(level))));
        settingsPostEmit(socket, json, callback);
        controlSettingsUpdateEmit(socket, callback);
    }

    // Set Pellets Warning Time
    public static void setPelletWarningTime(Socket socket, String time, SocketCallback callback) {
        String json = new Gson().toJson(new SettingsDataModel()
                .withPelletLevel(new PelletLevel().withWarningTime(Integer.parseInt(time))));
        settingsPostEmit(socket, json, callback);
        controlSettingsUpdateEmit(socket, callback);
    }

    // Set Pellets Full
    public static void setPelletsFull(Socket socket, String full, SocketCallback callback) {
        String json = new Gson().toJson(new SettingsDataModel()
                .withPelletLevel(new PelletLevel().withFull(Integer.parseInt(full))));
        settingsPostEmit(socket, json, callback);
        String json_c = new Gson().toJson(new ControlDataModel().withDistanceUpdate(true));
        controlPostEmit(socket, json_c, callback);
    }

    // Set Pellets Empty
    public static void setPelletsEmpty(Socket socket, String empty, SocketCallback callback) {
        String json = new Gson().toJson(new SettingsDataModel()
                .withPelletLevel(new PelletLevel().withEmpty(Integer.parseInt(empty))));
        settingsPostEmit(socket, json, callback);
        String json_c = new Gson().toJson(new ControlDataModel().withDistanceUpdate(true));
        controlPostEmit(socket, json_c, callback);
    }

    // Set Pellets Auger Rate
    public static void setPelletsAugerRate(Socket socket, String rate, SocketCallback callback) {
        String json = new Gson().toJson(new SettingsDataModel()
                .withGlobals(new Globals().withAugerRate(Float.parseFloat(rate))));
        settingsPostEmit(socket, json, callback);
        controlSettingsUpdateEmit(socket, callback);
    }

    // Set Pellets Prime Ignition
    public static void setPelletPrimeIgnition(Socket socket, boolean enabled,
                                               SocketCallback callback) {
        String json = new Gson().toJson(new SettingsDataModel()
                .withGlobals(new Globals().withPrimeIgnition(enabled)));
        settingsPostEmit(socket, json, callback);
        controlSettingsUpdateEmit(socket, callback);
    }

    // Set Debug Mode
    public static void setDebugMode(Socket socket, boolean enabled, SocketCallback callback) {
        String json = new Gson().toJson(new SettingsDataModel()
                .withGlobals(new Globals().withDebugMode(enabled)));
        settingsPostEmit(socket, json, callback);
        controlSettingsUpdateEmit(socket, callback);
    }

    // Set Boot To Monitor
    public static void setBootToMonitor(Socket socket, boolean enabled, SocketCallback callback) {
        String json = new Gson().toJson(new SettingsDataModel()
                .withGlobals(new Globals().withBootToMonitor(enabled)));
        settingsPostEmit(socket, json, callback);
        controlSettingsUpdateEmit(socket, callback);
    }

    // Delete History
    public static void sendDeleteHistory(Socket socket, SocketCallback callback) {
        adminPostEmit(socket, ServerConstants.PT_CLEAR_HISTORY, callback);
    }

    // Delete Events
    public static void sendDeleteEvents(Socket socket, SocketCallback callback) {
        adminPostEmit(socket, ServerConstants.PT_CLEAR_EVENTS, callback);
    }

    // Delete Pellets
    public static void sendDeletePellets(Socket socket, SocketCallback callback) {
        adminPostEmit(socket, ServerConstants.PT_CLEAR_PELLETS, callback);
    }

    // Delete Pellets Log
    public static void sendDeletePelletsLog(Socket socket, SocketCallback callback) {
        adminPostEmit(socket, ServerConstants.PT_CLEAR_PELLETS_LOG, callback);
    }

    // Factory Reset
    public static void sendFactoryReset(Socket socket, SocketCallback callback) {
        adminPostEmit(socket, ServerConstants.PT_FACTORY_DEFAULTS, callback);
    }

    // Restart System
    public static void sendRestartSystem(Socket socket, SocketCallback callback) {
        adminPostEmit(socket, ServerConstants.PT_RESTART, callback);
    }

    // Reboot System
    public static void sendRebootSystem(Socket socket, SocketCallback callback) {
        adminPostEmit(socket, ServerConstants.PT_REBOOT, callback);
    }

    // Shutdown System
    public static void sendShutdownSystem(Socket socket, SocketCallback callback) {
        adminPostEmit(socket, ServerConstants.PT_SHUTDOWN, callback);
    }

    // Delete Pellet Profile
    public static void sendDeletePelletProfile(Socket socket, String pelletId,
                                               SocketCallback callback) {
        String json = new Gson().toJson(new PostDataModel()
                .withPelletsAction(new PelletsAction().withProfile(pelletId)));
        pelletsPostEmit(socket, ServerConstants.PT_DELETE_PROFILE, json, callback);
    }

    // Add Pellet Profile
    public static void sendAddPelletProfile(Socket socket, PelletProfileModel profile,
                                            SocketCallback callback) {
        String json = new Gson().toJson(new PostDataModel()
                .withPelletsAction(new PelletsAction()
                        .withProfile(profile.getId())
                        .withAddAndLoad(false)
                        .withBrandName(profile.getBrand())
                        .withWoodType(profile.getWood())
                        .withRating(profile.getRating())
                        .withComments(profile.getComments())));
        pelletsPostEmit(socket, ServerConstants.PT_ADD_PROFILE, json, callback);
    }

    // Add Pellet Profile Load
    public static void sendAddPelletProfileLoad(Socket socket, PelletProfileModel profile,
                                                SocketCallback callback) {
        String json = new Gson().toJson(new PostDataModel()
                .withPelletsAction(new PelletsAction()
                        .withProfile(profile.getId())
                        .withAddAndLoad(true)
                        .withBrandName(profile.getBrand())
                        .withWoodType(profile.getWood())
                        .withRating(profile.getRating())
                        .withComments(profile.getComments())));
        pelletsPostEmit(socket, ServerConstants.PT_ADD_PROFILE, json, callback);
    }

    // Edit Pellet Profile
    public static void sendEditPelletProfile(Socket socket, PelletProfileModel profile,
                                             SocketCallback callback) {
        String json = new Gson().toJson(new PostDataModel()
                .withPelletsAction(new PelletsAction()
                        .withProfile(profile.getId())
                        .withBrandName(profile.getBrand())
                        .withWoodType(profile.getWood())
                        .withRating(profile.getRating())
                        .withComments(profile.getComments())));
        pelletsPostEmit(socket, ServerConstants.PT_EDIT_PROFILE, json, callback);
    }

    // Load Pellet Profile
    public static void sendLoadPelletProfile(Socket socket, String pelletId,
                                             SocketCallback callback) {
        String json = new Gson().toJson(new PostDataModel()
                .withPelletsAction(new PelletsAction().withProfile(pelletId)));
        pelletsPostEmit(socket, ServerConstants.PT_LOAD_PROFILE, json, callback);
    }

    // Delete Pellet Wood
    public static void sendDeletePelletWood(Socket socket, String wood, SocketCallback callback) {
        String json = new Gson().toJson(new PostDataModel()
                .withPelletsAction(new PelletsAction().withDeleteWood(wood)));
        pelletsPostEmit(socket, ServerConstants.PT_EDIT_WOODS, json, callback);
    }

    // Add Pellet Wood
    public static void sendAddPelletWood(Socket socket, String wood, SocketCallback callback) {
        String json = new Gson().toJson(new PostDataModel()
                .withPelletsAction(new PelletsAction().withNewWood(wood)));
        pelletsPostEmit(socket, ServerConstants.PT_EDIT_WOODS, json, callback);
    }

    // Delete Pellet Brands
    public static void sendDeletePelletBrand(Socket socket, String brand, SocketCallback callback) {
        String json = new Gson().toJson(new PostDataModel()
                .withPelletsAction(new PelletsAction().withDeleteBrand(brand)));
        pelletsPostEmit(socket, ServerConstants.PT_EDIT_BRANDS, json, callback);
    }

    // Add Pellet Brands
    public static void sendAddPelletBrand(Socket socket, String brand, SocketCallback callback) {
        String json = new Gson().toJson(new PostDataModel()
                .withPelletsAction(new PelletsAction().withNewBrand(brand)));
        pelletsPostEmit(socket, ServerConstants.PT_EDIT_BRANDS, json, callback);
    }

    // Delete Pellet Log
    public static void sendDeletePelletLog(Socket socket, String log, SocketCallback callback) {
        String json = new Gson().toJson(new PostDataModel()
                .withPelletsAction(new PelletsAction().withLogItem(log)));
        pelletsPostEmit(socket, ServerConstants.PT_DELETE_LOG, json, callback);
    }

    // Check Hopper Level
    public static void sendCheckHopperLevel(Socket socket, SocketCallback callback) {
        pelletsPostEmit(socket, ServerConstants.PT_HOPPER_CHECK, null, callback);
    }

    // Set Manual Mode
    public static void setManualMode(Socket socket, boolean enabled, SocketCallback callback) {
        String json = new Gson().toJson(new ControlDataModel()
                .withMode(enabled ? ServerConstants.G_MODE_MANUAL :
                        ServerConstants.G_MODE_STOP)
                .withUpdated(true));
        controlPostEmit(socket, json, callback);
    }

    // Set Manual Fan Output
    public static void setManualFanOutput(Socket socket, boolean enabled,
                                          SocketCallback callback) {
        String json = new Gson().toJson(new ControlDataModel()
                .withManual(new Manual().withChange(true).withFan(enabled)));
        controlPostEmit(socket, json, callback);
    }

    // Set Manual Auger Output
    public static void setManualAugerOutput(Socket socket, boolean enabled,
                                            SocketCallback callback) {
        String json = new Gson().toJson(new ControlDataModel()
                .withManual(new Manual().withChange(true).withAuger(enabled)));
        controlPostEmit(socket, json, callback);
    }

    // Set Manual Igniter Output
    public static void setManualIgniterOutput(Socket socket, boolean enabled,
                                              SocketCallback callback) {
        String json = new Gson().toJson(new ControlDataModel()
                .withManual(new Manual().withChange(true).withIgniter(enabled)));
        controlPostEmit(socket, json, callback);
    }

    // Set Manual Power Output
    public static void setManualPowerOutput(Socket socket, boolean enabled,
                                            SocketCallback callback) {
        String json = new Gson().toJson(new ControlDataModel()
                .withManual(new Manual().withChange(true).withPower(enabled)));
        controlPostEmit(socket, json, callback);
    }

    // Set Manual PWM Output
    public static void setManualPWMOutput(Socket socket, int dutyCycle,
                                          SocketCallback callback) {
        String json = new Gson().toJson(new ControlDataModel()
                .withManual(new Manual().withChange(true).withPWM(dutyCycle)));
        controlPostEmit(socket, json, callback);
    }

    // Grill Temp Units
    public static void setTempUnits(Socket socket, String units, SocketCallback callback) {
        socket.emit(ServerConstants.PE_POST_APP_DATA, ServerConstants.PA_UNITS_ACTION,
                units.equals("F") ? ServerConstants.PT_UNITS_F : ServerConstants.PT_UNITS_C,
                (Ack) args -> {
                    if (args.length > 0 && args[0] != null && callback != null) {
                        callback.onResponse(args[0].toString());
                    }
                });
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

    private static void timerPostEmit(Socket socket, String type, String json,
                                      SocketCallback callback) {
        socket.emit(ServerConstants.PE_POST_APP_DATA, ServerConstants.PA_TIMER_ACTION, type,
                json, (Ack) args -> {
                    if (args.length > 0 && args[0] != null && callback != null) {
                        callback.onResponse(args[0].toString());
                    }
                });
    }

    public static void pelletsGetEmit(Socket socket, SocketCallback callback) {
        socket.emit(ServerConstants.GE_GET_APP_DATA, ServerConstants.GA_PELLETS_DATA,
                (Ack) args -> {
                    if (args.length > 0 && args[0] != null && callback != null) {
                        callback.onResponse(args[0].toString());
                    }
                });
    }

    public static void infoGetEmit(Socket socket, SocketCallback callback) {
        socket.emit(ServerConstants.GE_GET_APP_DATA, ServerConstants.GA_INFO_DATA,
                (Ack) args -> {
                    if (args.length > 0 && args[0] != null && callback != null) {
                        callback.onResponse(args[0].toString());
                    }
                });
    }

    public static void eventsGetEmit(Socket socket, SocketCallback callback) {
        socket.emit(ServerConstants.GE_GET_APP_DATA, ServerConstants.GA_EVENTS_DATA,
                (Ack) args -> {
                    if (args.length > 0 && args[0] != null && callback != null) {
                        callback.onResponse(args[0].toString());
                    }
                });
    }

    public static void manualGetEmit(Socket socket, SocketCallback callback) {
        socket.emit(ServerConstants.GE_GET_APP_DATA, ServerConstants.GA_MANUAL_DATA,
                (Ack) args -> {
                    if (args.length > 0 && args[0] != null && callback != null) {
                        callback.onResponse(args[0].toString());
                    }
                });
    }

    public static void settingsGetEmit(Socket socket, SocketCallback callback) {
        settingsEmit(socket, callback);
    }

    public static void controlSettingsUpdateEmit(Socket socket, SocketCallback callback) {
        String json_c = new Gson().toJson(new ControlDataModel().withSettingsUpdate(true));
        controlPostEmit(socket, json_c, callback);
    }

    private static void settingsEmit(Socket socket, SocketCallback callback) {
        socket.emit(ServerConstants.GE_GET_APP_DATA, ServerConstants.GA_SETTINGS_DATA,
                new AckTimeOut(4000) {
                    @Override
                    public void call(Object... args) {
                        if (args.length > 0 && args[0] != null && callback != null) {
                            cancelTimer();
                            callback.onResponse(args[0].toString());
                        }
                    }
                });
    }
}
