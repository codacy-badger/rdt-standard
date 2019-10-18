package io.ona.rdt_app.activity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import java.util.HashMap;
import java.util.Map;

import edu.washington.cs.ubicomplab.rdt_reader.ImageProcessor;
import edu.washington.cs.ubicomplab.rdt_reader.activity.RDTCaptureActivity;
import io.ona.rdt_app.R;
import io.ona.rdt_app.application.RDTApplication;
import io.ona.rdt_app.contract.CustomRDTCaptureContract;
import io.ona.rdt_app.domain.ImageMetaData;
import io.ona.rdt_app.presenter.CustomRDTCapturePresenter;

import static com.vijay.jsonwizard.utils.Utils.hideProgressDialog;
import static com.vijay.jsonwizard.utils.Utils.showProgressDialog;
import static io.ona.rdt_app.util.Constants.SAVED_IMG_ID_AND_TIME_STAMP;
import static io.ona.rdt_app.util.Constants.Test.RDT_CAPTURE_DURATION;
import static io.ona.rdt_app.util.Constants.Test.TEST_CONTROL_RESULT;
import static io.ona.rdt_app.util.Constants.Test.TEST_PF_RESULT;
import static io.ona.rdt_app.util.Constants.Test.TEST_PV_RESULT;
import static io.ona.rdt_app.util.RDTJsonFormUtils.convertByteArrayToBitmap;
import static io.ona.rdt_app.widget.CustomRDTCaptureFactory.CAPTURE_TIMEOUT;
import static org.smartregister.util.JsonFormUtils.ENTITY_ID;

/**
 * Created by Vincent Karuri on 27/06/2019
 */
public class CustomRDTCaptureActivity extends RDTCaptureActivity implements CustomRDTCaptureContract.View {

    private static final String TAG = CustomRDTCaptureActivity.class.getName();

    private CustomRDTCapturePresenter presenter;
    private String baseEntityId;
    private String providerID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideProgressDialog();
        presenter = new CustomRDTCapturePresenter(this);
        providerID = RDTApplication.getInstance().getContext().allSharedPreferences().fetchRegisteredANM();
        baseEntityId = getIntent().getStringExtra(ENTITY_ID);
        showManualCaptureBtnDelayed(getIntent().getLongExtra(CAPTURE_TIMEOUT, 0));
    }

    @Override
    public void useCapturedImage(byte[] captureByteArray, byte[] windowByteArray, ImageProcessor.InterpretationResult interpretationResult, long timeTaken) {
        Log.i(TAG, "Processing captured image");

        ImageMetaData imageMetaData = new ImageMetaData();
        imageMetaData.withImage(convertByteArrayToBitmap(captureByteArray))
                .withBaseEntityId(baseEntityId)
                .withProviderId(providerID)
                .withInterpretationResult(interpretationResult)
                .withTimeTaken(timeTaken);

        showProgressDialog(R.string.please_wait, R.string.processing_image, this);
        presenter.saveImage(this, imageMetaData, this);
    }

    @Override
    public void onImageSaved(String imageMetaData) {
        hideProgressDialog();
        if (imageMetaData != null) {
            Map<String, String> keyVals = new HashMap();
            String[] vals = imageMetaData.split(",");
            keyVals.put(SAVED_IMG_ID_AND_TIME_STAMP, vals[0] + "," + vals[1]);
            keyVals.put(TEST_CONTROL_RESULT, vals[2]);
            keyVals.put(TEST_PV_RESULT, vals[3]);
            keyVals.put(TEST_PF_RESULT, vals[4]);
            keyVals.put(RDT_CAPTURE_DURATION, vals[5]);
            setResult(RESULT_OK, getResultIntent(keyVals));
        } else {
            Log.e(TAG, "Could not save null image path");
        }
        finish();
    }

    @Override
    public void onBackPressed() {
        // do nothing
    }

    private void showManualCaptureBtnDelayed(long milliseconds) {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
               modifyLayout();
            }
        }, milliseconds);
    }

    private void modifyLayout() {
        mImageQualityView.findViewById(R.id.bottom_manual_camera_controls).setVisibility(View.VISIBLE);
        mImageQualityView.findViewById(R.id.textInstruction).setVisibility(View.GONE);
        mImageQualityView.findViewById(R.id.img_quality_feedback_view).setVisibility(View.GONE);
        mImageQualityView.findViewById(R.id.manual_capture_instructions).setVisibility(View.VISIBLE);
    }
}
