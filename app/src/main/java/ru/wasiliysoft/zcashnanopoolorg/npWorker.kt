package ru.wasiliysoft.zcashnanopoolorg


// Created  WasiliySoft on 21.01.2019.

import android.content.Context
import android.util.Log
import androidx.work.*


class npWorker(ctx: Context, param: WorkerParameters) : Worker(ctx, param) {
    override fun doWork(): ListenableWorker.Result {
        Log.d(TAG, "loading: START")
        val minerId = inputData.getInt(INPUT_MINER_ID, 0)
        val miner = App.getMiners().read()[minerId]
        val ticker = miner.ticker
        val account = miner.account
        try {
            val generalData = App.getApi().getGeneral(ticker, account).execute()

            if (!generalData.isSuccessful || generalData.body().data == null) {
                Log.e(TAG, "loading: FAILURE")
                return ListenableWorker.Result.failure()
            }
            miner.gen = generalData.body().data
            val calcData = App.getApi().getCalcCoin(ticker, generalData.body().data.avgHashrate.h6unRound).execute()
            miner.calc = calcData.body().data
            App.getMiners().read()[minerId] = miner

            val data = Data.Builder().putInt(INPUT_MINER_ID, minerId).build()
            return ListenableWorker.Result.success(data)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        Log.e(TAG, "loading: FAILURE")
        return ListenableWorker.Result.failure()
    }


    companion object {
        const val TAG = "CrimeUploadWorker"
        const val INPUT_MINER_ID = "INPUT_MINER_ID"
        fun newInstance(id: Int): OneTimeWorkRequest {
            val myData = Data.Builder()
                    .putInt(INPUT_MINER_ID, id)
                    .build()

            return OneTimeWorkRequest
                    .Builder(npWorker::class.java)
                    .setInputData(myData)
                    .build()
        }
    }
}
