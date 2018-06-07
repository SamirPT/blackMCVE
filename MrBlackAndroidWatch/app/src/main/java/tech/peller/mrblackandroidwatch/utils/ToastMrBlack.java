package tech.peller.mrblackandroidwatch.utils;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import tech.peller.mrblackandroidwatch.R;

/**
 * Created by r2d2 on 20.01.2017.
 */

public class ToastMrBlack {

    public static void showToast(Context context, @Nullable CharSequence text, int duration) {
        if (context == null || text == null)
            return;
        Activity activity = ((Activity) context);
        View toast_layout = activity.getLayoutInflater().inflate(R.layout.custom_toast,
                (ViewGroup) activity.findViewById(R.id.custom_toast_container));
        TextView textView = (TextView) toast_layout.findViewById(R.id.text);
        textView.setText(customErrorMessageFixing(text.toString()));

        Toast toast = new Toast(context);
        toast.setGravity(Gravity.TOP | Gravity.FILL_HORIZONTAL, 0, 0);
        toast.setDuration(duration);
        toast.setView(toast_layout);
        toast.show();
    }

    @NonNull
    private static CharSequence customErrorMessageFixing(String errorMessage) {
        if (errorMessage.contains("Failed to connect")) {
            errorMessage = "The Internet connection appears to be offline.";
        }
        return errorMessage;
    }

    /**
     * Default toast with LENGTH_LONG
     *
     * @param context
     * @param text
     */
    public static void showToast(Context context, CharSequence text) {
        showToast(context, text, Toast.LENGTH_LONG);
    }
}
