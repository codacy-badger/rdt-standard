package io.ona.rdt.presenter;

import android.os.Build;

import org.smartregister.commonregistry.CommonFtsObject;

import java.util.HashMap;
import java.util.Map;

import io.ona.rdt.BuildConfig;

import static io.ona.rdt.util.Constants.DBConstants.FIRST_NAME;
import static io.ona.rdt.util.Constants.DBConstants.LAST_NAME;
import static io.ona.rdt.util.Constants.DBConstants.PATIENT_ID;
import static io.ona.rdt.util.Constants.PhoneProperties.APP_VERSION;
import static io.ona.rdt.util.Constants.PhoneProperties.PHONE_MANUFACTURER;
import static io.ona.rdt.util.Constants.PhoneProperties.PHONE_MODEL;
import static io.ona.rdt.util.Constants.PhoneProperties.PHONE_OS_VERSION;
import static io.ona.rdt.util.Constants.Table.RDT_PATIENTS;

/**
 * Created by Vincent Karuri on 12/11/2019
 */
public class RDTApplicationPresenter {

    private Map<String, String> phoneProperties;
    private static CommonFtsObject commonFtsObject;

    public RDTApplicationPresenter() {
        phoneProperties = new HashMap<>();
    }

    public Map<String, String> getPhoneProperties() {
        if (phoneProperties.size() == 0) {
            phoneProperties.put(PHONE_MANUFACTURER, Build.MANUFACTURER);
            phoneProperties.put(PHONE_MODEL, Build.MODEL);
            phoneProperties.put(PHONE_OS_VERSION, Build.VERSION.RELEASE);
            phoneProperties.put(APP_VERSION, BuildConfig.VERSION_NAME);
        }
        return phoneProperties;
    }

    public CommonFtsObject createCommonFtsObject() {
        if (commonFtsObject == null) {
            commonFtsObject = new CommonFtsObject(getFtsTables());
            commonFtsObject.updateSearchFields(RDT_PATIENTS, getFtsSearchFields());
            commonFtsObject.updateSortFields(RDT_PATIENTS, getFtsSortFields());
        }
        return commonFtsObject;
    }

    private static String[] getFtsTables() {
        return new String[]{RDT_PATIENTS};
    }

    private static String[] getFtsSearchFields() {
        return new String[]{FIRST_NAME, LAST_NAME, PATIENT_ID};
    }

    private static String[] getFtsSortFields() {
        return new String[]{FIRST_NAME, LAST_NAME, PATIENT_ID};
    }
}
