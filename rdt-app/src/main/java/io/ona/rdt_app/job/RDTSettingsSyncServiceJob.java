package io.ona.rdt_app.job;

import android.content.Intent;
import android.support.annotation.NonNull;

import org.smartregister.AllConstants;
import org.smartregister.job.SyncSettingsServiceJob;

import io.ona.rdt_app.sync.RDTSettingsSyncIntentService;

/**
 * Created by Vincent Karuri on 26/08/2019
 */
public class RDTSettingsSyncServiceJob extends SyncSettingsServiceJob {

    public static final String TAG = "RDTSettingsSyncServiceJob";

    @NonNull
    @Override
    protected Result onRunJob(@NonNull Params params) {
        Intent intent = new Intent(getApplicationContext(), RDTSettingsSyncIntentService.class);
        getApplicationContext().startService(intent);
        return params != null && params.getExtras().getBoolean(AllConstants.INTENT_KEY.TO_RESCHEDULE, false) ? Result.RESCHEDULE : Result.SUCCESS;
    }
}
