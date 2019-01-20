package ru.wasiliysoft.zcashnanopoolorg;


import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.io.IOException;

import retrofit2.Response;

/**
 * Created by WasiliySoft on 28.11.2017.
 */

@SuppressWarnings("DefaultFileTemplate")
public abstract class NpLoader extends AsyncTaskLoader<Response> {
    private Response mResp;

    protected NpLoader(Context context) {
        super(context);
    }

    protected abstract Response LoadData() throws IOException;

    @Override
    public void deliverResult(Response response) {
        mResp = response;
        if (isStarted()) {
            super.deliverResult(response);
        }
    }

    @Override
    public Response loadInBackground() {
        try {
            return LoadData();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    protected void onStartLoading() {
        if (mResp != null) {
            deliverResult(mResp);
        }
        if (takeContentChanged() || mResp == null) {
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    @Override
    protected void onReset() {
        super.onReset();
        onStopLoading();
        mResp = null;
    }
}
