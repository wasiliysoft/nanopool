package ru.wasiliysoft.zcashnanopoolorg;

// Created by WasiliySoft on 09.02.2018.

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.yandex.metrica.YandexMetrica;
import com.yandex.metrica.push.YandexMetricaPush;

public class SilentPushReceiver extends BroadcastReceiver {
    String LOG_TAG = "SilentPushReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        //Извлечение данных из вашего push-уведомления
        String payload = intent.getStringExtra(YandexMetricaPush.EXTRA_PAYLOAD);
        StringBuilder sb = new StringBuilder("Silent push received.");
        if (!TextUtils.isEmpty(payload)) {
            sb.append("\nPayload: ").append(payload);
        }
        YandexMetrica.reportEvent("Silent push");
//        Toast.makeText(context, sb.toString(), Toast.LENGTH_SHORT).show();
    }
}