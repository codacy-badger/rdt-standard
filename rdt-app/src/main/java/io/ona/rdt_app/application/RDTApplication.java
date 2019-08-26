package io.ona.rdt_app.application;

import android.content.Intent;
import android.support.annotation.NonNull;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.evernote.android.job.JobManager;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.commonregistry.CommonFtsObject;
import org.smartregister.domain.Setting;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.receiver.SyncStatusBroadcastReceiver;
import org.smartregister.repository.AllSettings;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.Repository;
import org.smartregister.view.activity.DrishtiApplication;
import org.smartregister.view.receiver.TimeChangedBroadcastReceiver;

import java.util.HashMap;
import java.util.Map;

import io.fabric.sdk.android.Fabric;
import io.ona.rdt_app.BuildConfig;
import io.ona.rdt_app.activity.LoginActivity;
import io.ona.rdt_app.job.RDTJobCreator;
import io.ona.rdt_app.repository.RDTRepository;
import io.ona.rdt_app.util.Constants;
import io.ona.rdt_app.util.RDTSyncConfiguration;
import io.ona.rdt_app.util.Utils;
import timber.log.Timber;

import static com.vijay.jsonwizard.constants.JsonFormConstants.VALUE;
import static io.ona.rdt_app.util.Constants.GLOBAL_CONFIGS;
import static io.ona.rdt_app.util.Constants.GlobalConfig.KILL_IMG_SYNC;
import static io.ona.rdt_app.util.Constants.GlobalConfig.SYNC_CONNECT_TIMEOUT;
import static io.ona.rdt_app.util.Constants.GlobalConfig.SYNC_READ_TIMEOUT;
import static io.ona.rdt_app.util.Constants.IS_IMG_SYNC_ENABLED;
import static io.ona.rdt_app.util.Constants.PATIENTS;
import static org.smartregister.AllConstants.SETTINGS;
import static org.smartregister.util.JsonFormUtils.KEY;
import static org.smartregister.util.Log.logError;
import static org.smartregister.util.Log.logInfo;

/**
 * Created by Vincent Karuri on 07/06/2019
 */
public class RDTApplication extends DrishtiApplication {

    private static CommonFtsObject commonFtsObject;
    private String password;
    private Map<String, String> globalConfigs;

    public static synchronized RDTApplication getInstance() {
        return (RDTApplication) mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
        globalConfigs = new HashMap<>();
        context = Context.getInstance();
        context.updateApplicationContext(getApplicationContext());
        context.updateCommonFtsObject(createCommonFtsObject());

        // Initialize Modules
        CoreLibrary.init(context, new RDTSyncConfiguration(), System.currentTimeMillis());

        LocationHelper.init(Utils.ALLOWED_LEVELS, Utils.DEFAULT_LOCATION_LEVEL);

        SyncStatusBroadcastReceiver.init(this);

        Fabric.with(this, new Crashlytics.Builder().core(new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build()).build());

        JobManager.create(this).addJobCreator(new RDTJobCreator());

        AllSharedPreferences sharedPreferences = getContext().allSharedPreferences();
        if (sharedPreferences.getPreference(IS_IMG_SYNC_ENABLED).isEmpty()) {
            Utils.setImageSyncEnabledStatus(true);
        }
    }

    @Override
    public void logoutCurrentUser() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        getApplicationContext().startActivity(intent);
        context.userService().logoutSession();
    }

    @Override
    public Repository getRepository() {
        try {
            if (repository == null) {
                repository = new RDTRepository(getInstance().getApplicationContext(), context);
            }
        } catch (UnsatisfiedLinkError e) {
            logError("Error on getRepository: " + e);
        }
        return repository;
    }

    @Override
    public String getPassword() {
        if (password == null) {
            String username = getContext().allSharedPreferences().fetchRegisteredANM();
            password = getContext().userService().getGroupId(username);
        }
        return password;
    }

    public Context getContext() {
        return context;
    }

    @Override
    public void onTerminate() {
        logInfo("Application is terminating. Stopping Sync scheduler and resetting isSyncInProgress setting.");
        TimeChangedBroadcastReceiver.destroy(this);
        SyncStatusBroadcastReceiver.destroy(this);
        super.onTerminate();
    }

    public static CommonFtsObject createCommonFtsObject() {
        if (commonFtsObject == null) {
            commonFtsObject = new CommonFtsObject(getFtsTables());
            commonFtsObject.updateSearchFields(PATIENTS, getFtsSearchFields());
            commonFtsObject.updateSortFields(PATIENTS, getFtsSortFields());
        }
        return commonFtsObject;
    }

    private static String[] getFtsTables() {
        return new String[]{PATIENTS};
    }

    private static String[] getFtsSearchFields() {
        return new String[]{Constants.DBConstants.NAME};
    }

    private static String[] getFtsSortFields() {
       return new String[]{Constants.DBConstants.NAME};
    }

    public AllSettings getSettingsRepository() {
        return getInstance().getContext().allSettings();
    }

    public void processGlobalConfigs() {
        Setting globalSettings = getSettingsRepository().getSetting(GLOBAL_CONFIGS);
        populateGlobalConfigs(globalSettings);
        processSyncConfigs();
    }

    private void populateGlobalConfigs(@NonNull Setting setting) {
        if (setting == null) {
            return;
        }
        try {
            JSONArray settingsArray = new JSONObject(setting.getValue()).getJSONArray(SETTINGS);
            for (int i = 0; i < settingsArray.length(); i++) {
                JSONObject jsonObject = settingsArray.getJSONObject(i);
                String value = jsonObject.optString(VALUE, null);
                String key = jsonObject.optString(KEY, null);
                if (value != null && key != null) {
                    globalConfigs.put(key, value);
                }
            }
        } catch (JSONException e) {
            Timber.e(e);
        }
    }

    private void processSyncConfigs() {
        RDTSyncConfiguration syncConfiguration = (RDTSyncConfiguration) CoreLibrary.getInstance().getSyncConfiguration();

        String connectTimeout = getGlobalConfigs().get(SYNC_CONNECT_TIMEOUT);
        if (!StringUtils.isBlank(connectTimeout)) {
            syncConfiguration.setConnectTimeout(Integer.valueOf(connectTimeout));
        }

        String readTimeout = getGlobalConfigs().get(SYNC_READ_TIMEOUT);
        if (!StringUtils.isBlank(readTimeout)) {
            syncConfiguration.setReadTimeout(Integer.valueOf(readTimeout));
        }

        String killImgSync = getGlobalConfigs().get(KILL_IMG_SYNC);
        if (!StringUtils.isBlank(killImgSync)) {
            Utils.setImageSyncEnabledStatus(Boolean.valueOf(killImgSync));
        }
    }

    public Map<String, String> getGlobalConfigs() {
        return globalConfigs;
    }
}
