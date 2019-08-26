package io.ona.rdt_app.util;

/**
 * Created by Vincent Karuri on 23/05/2019
 */
public interface Constants {
    int REQUEST_RDT_PERMISSIONS = 1000;
    int REQUEST_CODE_GET_JSON = 9388;

    String JSON_FORM_PARAM_JSON = "json";
    String ENTITY_ID = "entity_id";
    String METADATA = "metadata";
    String DETAILS = "details";
    String ENCOUNTER_TYPE = "encounter_type";
    String PATIENTS = "patients";
    String RDT_TESTS = "rdt_tests";
    String PATIENT_REGISTRATION = "patient_registration";
    String DOB = "dob";
    String PATIENT_NAME = "patient_name";
    String SEX = "sex";
    String PATIENT_AGE = "patient_age";
    String CONDITIONAL_SAVE = "conditional_save";
    String MULTI_VERSION = "multi_version";
    String EXPIRATION_DATE_RESULT = "expiration_date_result";
    String EXPIRATION_DATE = "expiration_date";
    String ONA_RDT = "experimental";
    String CARESTART_RDT = "carestart";
    String LBL_CARE_START = "lbl_care_start";
    String BULLET_DOT = " \u00B7 ";
    String IS_IMG_SYNC_ENABLED = "is_img_sync_enabled";
    String EXPIRED_PAGE_ADDRESS = "expired_page_address";
    String SAVED_IMG_ID_AND_TIME_STAMP = "saved_img_id_and_time_stamp";

    String GLOBAL_CONFIGS = "global_configs";

    interface Tags {
        String COUNTRY = "Country";
        String PROVINCE = "Province";
        String DISTRICT = "District";
        String HEALTH_CENTER = "Rural Health Centre";
        String VILLAGE = "Village";
    }

    interface DBConstants {
        String NAME = "name";
        String AGE = "age";
        String SEX = "sex";
    }

    interface Form {
        String PATIENT_REGISTRATION_FORM = "json.form/patient-registration-form.json";
        String RDT_TEST_FORM = "json.form/rdt-capture-form.json";
        String LBL_RDT_ID = "lbl_rdt_id";
        String RDT_ID = "rdt_id";
        String LBL_PATIENT_NAME = "lbl_patient_name";
        String LBL_PATIENT_GENDER_AND_ID = "lbl_patient_gender_and_id";
        String RDT_CAPTURE_CONTROL_RESULT = "rdt_capture_control_result";
        String RDT_CAPTURE_PV_RESULT = "rdt_capture_pv_result";
        String RDT_CAPTURE_PF_RESULT = "rdt_capture_pf_result";
    }

    interface Test {
        String TEST_CONTROL_RESULT = "test_control_result";
        String TEST_PV_RESULT = "test_pv_result";
        String TEST_PF_RESULT = "test_pf_result";
    }

    interface GlobalConfig {
        String SYNC_CONNECT_TIMEOUT = "sync_connect_timeout";
        String SYNC_READ_TIMEOUT = "sync_read_timeout";
        String KILL_IMG_SYNC = "kill_img_sync";
    }
}
