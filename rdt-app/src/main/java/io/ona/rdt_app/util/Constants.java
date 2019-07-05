package io.ona.rdt_app.util;

/**
 * Created by Vincent Karuri on 23/05/2019
 */
public interface Constants {
    int REQUEST_STORAGE_PERMISSION = 1000;
    int REQUEST_CODE_GET_JSON = 9388;

    String JSON_FORM_PARAM_JSON = "json";
    String METADATA = "metadata";
    String DETAILS = "details";
    String ENCOUNTER_TYPE = "encounter_type";
    String PATIENTS = "patients";
    String RDT_TESTS = "rdt_tests";
    String PATIENT_REGISTRATION = "patient_registration";
    String RDT_TEST = "rdt_test";
    String DOB = "dob";
    String PATIENT_AGE = "patient_age";
    String PROFILE_PIC = "profilepic";

    interface DBConstants {
        String NAME = "name";
        String AGE = "age";
        String SEX = "sex";
        String ID = "id";
        String BASE_ENTITY_ID = "base_entity_id";
        String _ID = "_id";
    }

    interface Form {
        String PATIENT_REGISTRATION_FORM = "json.form/patient-registration-form.json";
        String RDT_TEST_FORM = "json.form/rdt-capture-form.json";
        String LBL_RDT_ID = "lbl_rdt_id";
        String EXPIRATION_DATE_READER = "expiration_date_reader";
        String RDT_CAPTURE = "rdt_capture";
        String TIME_IMG_SAVED = "time_img_saved";
        String BASE_ENTITY_ID = "baseEntityId";
    }
}
