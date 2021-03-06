package io.ona.rdt.widget;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.view.View;

import com.vijay.jsonwizard.domain.WidgetArgs;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.interfaces.JsonApi;
import com.vijay.jsonwizard.interfaces.OnActivityResultListener;
import com.vijay.jsonwizard.widgets.RDTCaptureFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import io.ona.rdt.activity.CustomRDTCaptureActivity;
import io.ona.rdt.activity.RDTJsonFormActivity;
import io.ona.rdt.domain.LineReadings;
import io.ona.rdt.domain.ParcelableImageMetadata;
import io.ona.rdt.fragment.RDTJsonFormFragment;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.vijay.jsonwizard.constants.JsonFormConstants.RDT_CAPTURE;
import static com.vijay.jsonwizard.constants.JsonFormConstants.RDT_CAPTURE_CODE;
import static com.vijay.jsonwizard.utils.Utils.hideProgressDialog;
import static com.vijay.jsonwizard.utils.Utils.showProgressDialog;
import static io.ona.rdt.util.Constants.FormFields.RDT_CAPTURE_TOP_LINE_RESULT;
import static io.ona.rdt.util.Constants.FormFields.RDT_CAPTURE_BOTTOM_LINE_RESULT;
import static io.ona.rdt.util.Constants.FormFields.RDT_CAPTURE_MIDDLE_LINE_RESULT;
import static io.ona.rdt.util.Constants.Form.RDT_TYPE;
import static io.ona.rdt.util.Constants.Test.CASSETTE_BOUNDARY;
import static io.ona.rdt.util.Constants.Test.CROPPED_IMG_ID;
import static io.ona.rdt.util.Constants.Test.CROPPED_IMG_MD5_HASH;
import static io.ona.rdt.util.Constants.Test.FLASH_ON;
import static io.ona.rdt.util.Constants.Test.FULL_IMG_MD5_HASH;
import static io.ona.rdt.util.Constants.Test.IS_MANUAL_CAPTURE;
import static io.ona.rdt.util.Constants.Test.PARCELABLE_IMAGE_METADATA;
import static io.ona.rdt.util.Constants.Test.RDT_CAPTURE_DURATION;
import static io.ona.rdt.util.Constants.Test.TIME_IMG_SAVED;
import static org.smartregister.util.JsonFormUtils.ENTITY_ID;

/**
 * Created by Vincent Karuri on 27/06/2019
 */
public class CustomRDTCaptureFactory extends RDTCaptureFactory implements OnActivityResultListener {

    private final String TAG = CustomRDTCaptureFactory.class.getName();
    private final String RDT_NAME = "rdt_name";

    private static final long CAPTURE_TIMEOUT_MS = 30000;

    public static final String CAPTURE_TIMEOUT = "capture_timeout";

    private String baseEntityId;
    private WidgetArgs widgetArgs;

    @Override
    public List<View> getViewsFromJson(String stepName, Context context, JsonFormFragment formFragment, JSONObject jsonObject, CommonListener listener, boolean popup) throws Exception {
        this.baseEntityId = ((JsonApi) context).getmJSONObject().optString(ENTITY_ID);
        this.widgetArgs = new WidgetArgs();
        widgetArgs.withFormFragment(formFragment)
                .withJsonObject(jsonObject)
                .withContext(context)
                .withStepName(stepName);

        List<View> views = super.getViewsFromJson(stepName, context, formFragment, jsonObject, listener, popup);
        return views;
    }

    @Override
    public List<View> getViewsFromJson(String stepName, Context context, JsonFormFragment formFragment, JSONObject jsonObject, CommonListener listener) throws Exception {
        return getViewsFromJson(stepName, context, formFragment, jsonObject, listener, false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        hideProgressDialog();

        RDTJsonFormFragment formFragment = (RDTJsonFormFragment) widgetArgs.getFormFragment();
        if (requestCode == RDT_CAPTURE_CODE && resultCode == RESULT_OK && data != null) {
            try {
                ParcelableImageMetadata parcelableImageMetadata = data.getParcelableExtra(PARCELABLE_IMAGE_METADATA);
                populateRelevantFields(parcelableImageMetadata);
                if (!formFragment.next()) {
                    formFragment.save(true);
                }
            } catch (JSONException e) {
                Log.e(TAG, e.getStackTrace().toString());
            }
        } else if (resultCode == RESULT_CANCELED) {
            formFragment.setMoveBackOneStep(true);
        } else if (data == null) {
            Log.i(TAG, "No result data for RDT capture!");
        }
    }

    private class LaunchRDTCameraTask extends AsyncTask<Intent, Void, Void> {

        private Context context = widgetArgs.getContext();

        @Override
        protected void onPreExecute() {
            showProgressDialog(com.vijay.jsonwizard.R.string.please_wait_title, com.vijay.jsonwizard.R.string.launching_rdt_capture_message, context);
        }

        @Override
        protected Void doInBackground(Intent... intents) {
            Activity activity = (Activity) context;
            activity.startActivityForResult(intents[0], RDT_CAPTURE_CODE);
            return null;
        }
    }

    @Override
    protected void launchRDTCaptureActivity() {
        Context context = widgetArgs.getContext();
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(context, CustomRDTCaptureActivity.class);
            intent.putExtra(ENTITY_ID, baseEntityId);
            intent.putExtra(RDT_NAME, ((RDTJsonFormActivity) context).getRdtType());
            intent.putExtra(CAPTURE_TIMEOUT, CAPTURE_TIMEOUT_MS);
            new LaunchRDTCameraTask().execute(intent);
        }
    }

    private void populateRelevantFields(ParcelableImageMetadata parcelableImageMetadata) throws JSONException {
        LineReadings lineReadings = parcelableImageMetadata.getLineReadings();
        JsonApi jsonApi = (JsonApi) widgetArgs.getContext();
        jsonApi.writeValue(widgetArgs.getStepName(), RDT_CAPTURE_TOP_LINE_RESULT, String.valueOf(lineReadings.isTopLine()), "", "", "", false);
        jsonApi.writeValue(widgetArgs.getStepName(), RDT_CAPTURE_MIDDLE_LINE_RESULT, String.valueOf(lineReadings.isMiddleLine()), "", "", "", false);
        jsonApi.writeValue(widgetArgs.getStepName(), RDT_CAPTURE_BOTTOM_LINE_RESULT, String.valueOf(lineReadings.isBottomLine()), "", "", "", false);
        jsonApi.writeValue(widgetArgs.getStepName(), RDT_CAPTURE_DURATION, String.valueOf(parcelableImageMetadata.getCaptureDuration()), "", "", "", false);
        jsonApi.writeValue(widgetArgs.getStepName(), RDT_TYPE, ((RDTJsonFormActivity) widgetArgs.getContext()).getRdtType(), "", "", "", false);
        jsonApi.writeValue(widgetArgs.getStepName(), CROPPED_IMG_ID, parcelableImageMetadata.getCroppedImageId(), "", "", "", false);
        jsonApi.writeValue(widgetArgs.getStepName(), TIME_IMG_SAVED, String.valueOf(parcelableImageMetadata.getImageTimeStamp()), "", "", "", false);
        jsonApi.writeValue(widgetArgs.getStepName(), RDT_CAPTURE, parcelableImageMetadata.getFullImageId(), "", "", "", false);
        jsonApi.writeValue(widgetArgs.getStepName(), FLASH_ON, String.valueOf(parcelableImageMetadata.isFlashOn()), "", "", "", false);
        jsonApi.writeValue(widgetArgs.getStepName(), CASSETTE_BOUNDARY, parcelableImageMetadata.getCassetteBoundary(), "", "", "", false);
        jsonApi.writeValue(widgetArgs.getStepName(), IS_MANUAL_CAPTURE, String.valueOf(parcelableImageMetadata.isManualCapture()), "", "", "", false);
        jsonApi.writeValue(widgetArgs.getStepName(), CROPPED_IMG_MD5_HASH, String.valueOf(parcelableImageMetadata.getCroppedImageMD5Hash()), "", "", "", false);
        jsonApi.writeValue(widgetArgs.getStepName(), FULL_IMG_MD5_HASH, String.valueOf(parcelableImageMetadata.getFullImageMD5Hash()), "", "", "", false);
    }

    @Override
    public void setUpRDTCaptureActivity() {
        Context context = widgetArgs.getContext();
        if (context instanceof JsonApi) {
            final JsonApi jsonApi = (JsonApi) context;
            jsonApi.addOnActivityResultListener(RDT_CAPTURE_CODE , this);
        }
    }
}
