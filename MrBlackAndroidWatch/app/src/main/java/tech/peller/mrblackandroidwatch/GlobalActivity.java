package tech.peller.mrblackandroidwatch;

import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;

import com.google.firebase.iid.FirebaseInstanceId;

import roboguice.activity.RoboActivity;
import tech.peller.mrblackandroidwatch.api.response.BaseResponse;
import tech.peller.mrblackandroidwatch.loader.users.AddTokenToUserLoader;
import tech.peller.mrblackandroidwatch.utils.ProgressDialogManager;
import tech.peller.mrblackandroidwatch.utils.ToastMrBlack;

public class GlobalActivity extends RoboActivity implements LoaderManager.LoaderCallbacks<BaseResponse> {
    private static final int LOADER_INDEX_REGISTER_TOKEN = 940117;

    public static GlobalActivity instance;

    public String gcmToken;
    private LoaderManager loaderManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activity_global);

        instance = this;

        loaderManager = getLoaderManager();

        startSession();
    }

    public void startSession() {
        gcmToken = FirebaseInstanceId.getInstance().getToken();
        ToastMrBlack.showToast(GlobalActivity.this, "Token is " + gcmToken);
        registrationToken();
    }

    public void registrationToken() {
        Loader<Object> loader = loaderManager.getLoader(LOADER_INDEX_REGISTER_TOKEN);
        if (loader != null && loader.isStarted()) {
            loaderManager.destroyLoader(loader.getId());
        }

        loaderManager.initLoader(LOADER_INDEX_REGISTER_TOKEN, null, GlobalActivity.this);
    }

    public void setToken(String token) {
        gcmToken = token;
    }

    @Override
    public Loader<BaseResponse> onCreateLoader(int i, Bundle bundle) {
        ProgressDialogManager.startProgress(this);

        switch (i) {
            case LOADER_INDEX_REGISTER_TOKEN:
                return new AddTokenToUserLoader(this, gcmToken, false);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<BaseResponse> loader, BaseResponse baseResponse) {
        ProgressDialogManager.stopProgress();

        int id = loader.getId();
        loaderManager.destroyLoader(id);

        switch (id) {
            case LOADER_INDEX_REGISTER_TOKEN:
                if(baseResponse.isSuccess()) {
                    ToastMrBlack.showToast(GlobalActivity.this, "Token is registered successfully");
                } else {
                    if(gcmToken == null) {
                        ToastMrBlack.showToast(GlobalActivity.this, "Token is null");
                        gcmToken = FirebaseInstanceId.getInstance().getToken();
                    }
                    loaderManager.restartLoader(LOADER_INDEX_REGISTER_TOKEN, null, this);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<BaseResponse> loader) {}

    @Override
    public void onBackPressed() {}

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
