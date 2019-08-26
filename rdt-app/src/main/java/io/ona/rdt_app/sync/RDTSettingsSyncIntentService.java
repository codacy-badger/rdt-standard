package io.ona.rdt_app.sync;

import android.content.Intent;
import android.os.Bundle;

import org.smartregister.AllConstants;
import org.smartregister.sync.intent.SettingsSyncIntentService;

import io.ona.rdt_app.application.RDTApplication;
import io.ona.rdt_app.job.ImageUploadSyncServiceJob;

import static io.ona.rdt_app.util.Utils.isImageSyncEnabled;

/**
 * Created by Vincent Karuri on 26/08/2019
 */
public class RDTSettingsSyncIntentService extends SettingsSyncIntentService {

    @Override
    protected void onHandleIntent(Intent intent) {
        super.onHandleIntent(intent);
        Bundle data = intent.getExtras();
        if (data != null && data.getInt(AllConstants.INTENT_KEY.SYNC_TOTAL_RECORDS, 0) > 0) {
            RDTApplication.getInstance().processGlobalConfigs();
        }
        if (isImageSyncEnabled()) {
            ImageUploadSyncServiceJob.scheduleJobImmediately(ImageUploadSyncServiceJob.TAG);
        }
    }
}
