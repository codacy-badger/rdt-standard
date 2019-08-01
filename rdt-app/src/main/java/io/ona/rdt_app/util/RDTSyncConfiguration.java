package io.ona.rdt_app.util;

import org.smartregister.SyncConfiguration;
import org.smartregister.SyncFilter;
import org.smartregister.repository.AllSharedPreferences;

import io.ona.rdt_app.BuildConfig;
import io.ona.rdt_app.application.RDTApplication;

/**
 * Created by Vincent Karuri on 14/06/2019
 */
public class RDTSyncConfiguration extends SyncConfiguration {
    @Override
    public int getSyncMaxRetries() {
        return BuildConfig.MAX_SYNC_RETRIES;
    }

    @Override
    public SyncFilter getSyncFilterParam() {
        return SyncFilter.PROVIDER;
    }

    @Override
    public String getSyncFilterValue() {
        AllSharedPreferences sharedPreferences = RDTApplication.getInstance().getContext().userService().getAllSharedPreferences();
        return sharedPreferences.fetchRegisteredANM();
    }

    @Override
    public int getUniqueIdSource() {
        return BuildConfig.OPENMRS_UNIQUE_ID_SOURCE;
    }

    @Override
    public int getUniqueIdBatchSize() {
        return BuildConfig.OPENMRS_UNIQUE_ID_BATCH_SIZE;
    }

    @Override
    public int getUniqueIdInitialBatchSize() {
        return BuildConfig.OPENMRS_UNIQUE_ID_INITIAL_BATCH_SIZE;
    }

    @Override
    public SyncFilter getEncryptionParam() {
        return SyncFilter.TEAM_ID;
    }

    @Override
    public boolean updateClientDetailsTable() {
        return false;
    }
}
