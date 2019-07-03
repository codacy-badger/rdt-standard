package io.ona.rdt_app.presenter;

import android.widget.LinearLayout;

import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interactors.JsonFormInteractor;
import com.vijay.jsonwizard.presenters.JsonFormFragmentPresenter;

import io.ona.rdt_app.fragment.RDTJsonFormFragment;

/**
 * Created by Vincent Karuri on 19/06/2019
 */
public class RDTJsonFormFragmentPresenter extends JsonFormFragmentPresenter {

    public RDTJsonFormFragmentPresenter(JsonFormFragment formFragment, JsonFormInteractor jsonFormInteractor) {
        super(formFragment, jsonFormInteractor);
    }

    @Override
    public boolean onNextClick(LinearLayout mainView) {
        this.validateAndWriteValues();
        boolean validateOnSubmit = this.validateOnSubmit();
        if (validateOnSubmit || this.isFormValid()) {
            return this.moveToNextStep();
        } else {
            this.getView().showSnackBar(this.getView().getContext().getResources().getString(com.vijay.jsonwizard.R.string.json_form_on_next_error_msg));
            return false;
        }
    }

    private boolean moveToNextStep() {
        if (!mStepDetails.optString("next").isEmpty()) {
            JsonFormFragment next = RDTJsonFormFragment.getFormFragment(this.mStepDetails.optString("next"));
            this.getView().hideKeyBoard();
            this.getView().transactThis(next);
            return true;
        } else {
            return false;
        }
    }

    public void moveToNextStep(String stepName) {
        JsonFormFragment next = RDTJsonFormFragment.getFormFragment(stepName);
        this.getView().hideKeyBoard();
        this.getView().transactThis(next);
    }

    @Override
    public void setUpToolBar() {
        super.setUpToolBar();
        getView().updateVisibilityOfNextAndSave(false, false);
    }
}