package ru.wasiliysoft.zcashnanopoolorg;

import android.app.Application;
//import android.content.Context;
//import android.support.multidex.MultiDex;

import com.yandex.metrica.YandexMetrica;
import com.yandex.metrica.push.YandexMetricaPush;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.wasiliysoft.zcashnanopoolorg.Model.Miners;
import ru.wasiliysoft.zcashnanopoolorg.Retrofit.NpAPI;

/**
 * Created by WasiliySoft on 25.11.2017.
 */

@SuppressWarnings("DefaultFileTemplate")
public class App extends Application {
    private static NpAPI sNpAPI;
    private static Miners sMiners;

    @Override
    public void onCreate() {
        super.onCreate();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.nanopool.org/v1/zec/") //Базовая часть адреса
                .addConverterFactory(GsonConverterFactory.create()) //Конвертер, необходимый для преобразования JSON'а в объекты
                .build();
        sNpAPI = retrofit.create(NpAPI.class); //Создаем объект, при помощи которого будем выполнять запросы
        sMiners = new Miners(getApplicationContext());

        super.onCreate();
        // Инициализация AppMetrica SDK
        YandexMetrica.activate(getApplicationContext(), BuildConfig.YANDEX_KEY);
        // Отслеживание активности пользователей
        YandexMetrica.enableActivityAutoTracking(this);
        YandexMetricaPush.init(this);
    }

    public static Miners getMiners() {
        return sMiners;
    }

    public static NpAPI getApi() {
        return sNpAPI;
    }

//    @Override
//    protected void attachBaseContext(Context context) {
//        super.attachBaseContext(context);
//        MultiDex.install(this);
//    }
}
