package tech.peller.mrblackandroidwatch.controller;

import android.content.AsyncTaskLoader;
import android.content.Context;

import roboguice.RoboGuice;

public abstract class RoboAsyncTaskLoader<D> extends AsyncTaskLoader<D> {

    public RoboAsyncTaskLoader(Context context) {
        super(context);
        RoboGuice.injectMembers(context, this);
    }

}