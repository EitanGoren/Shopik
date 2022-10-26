package com.eitan.shopik.system;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class UploadEngagementDataToDB extends Worker {

    Context context;

    /**
     * @param appContext   The application {@link Context}
     * @param workerParams Parameters to setup the internal state of this worker
     */
    public UploadEngagementDataToDB(@NonNull Context appContext, @NonNull WorkerParameters workerParams) {
        super(appContext, workerParams);
        this.context = appContext;
    }

    @NonNull
    @Override
    public Result doWork() {
        return Result.success();
    }
}
