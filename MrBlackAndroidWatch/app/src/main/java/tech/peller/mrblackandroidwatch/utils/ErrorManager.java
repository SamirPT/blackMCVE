package tech.peller.mrblackandroidwatch.utils;

import android.app.Activity;
import android.util.Log;

import tech.peller.mrblackandroidwatch.api.response.BaseResponse;
import tech.peller.mrblackandroidwatch.api.response.ErrorMessage;

/**
 * Created by Sam (samir@peller.tech) on 08.06.2017
 */

public class ErrorManager {
    public ErrorManager() {}

    public static void onError(BaseResponse data, String tag, Activity activity) {
        ErrorMessage errorMessage = data.asError().getObj();
        String statusName = data.getStatus().name();

        if (errorMessage != null) {
            String errorText = errorMessage.getError();
            String errorFields = errorMessage.getFields();
            Log.e(tag, statusName + ": " +
                    ((errorText != null && !errorText.isEmpty()) ? errorText: "No description")
                    + "\nFields: " +
                    ((errorFields != null && !errorFields.isEmpty()) ? errorFields : "No fields"));
            if(activity != null) {
                ToastMrBlack.showToast(activity, errorText);
            }
        } else {
            Log.e(tag, statusName);
            if(activity != null) {
                ToastMrBlack.showToast(activity, statusName);
            }
        }
    }
}
