package ru.wasiliysoft.zcashnanopoolorg.Retrofit;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import ru.wasiliysoft.zcashnanopoolorg.BuildConfig;
import ru.wasiliysoft.zcashnanopoolorg.Model.NpCalc;
import ru.wasiliysoft.zcashnanopoolorg.Model.NpGeneral;

/**
 * Created by WasiliySoft on 25.11.2017.
 */

@SuppressWarnings("DefaultFileTemplate")
public interface NpAPI {
    //    @GET("https://api.nanopool.org/v1/zec/user/{address}")
    @GET("https://api.nanopool.org/v1/" + BuildConfig.COIN_TICKER + "/user/{address}")
    Call<NpGeneral.Resp> getGeneral(@Path("address") String address);

    //    @GET("https://api.nanopool.org/v1/zec/approximated_earnings/{hashrate}")
    @GET("https://api.nanopool.org/v1/" + BuildConfig.COIN_TICKER + "/approximated_earnings/{hashrate}")
    Call<NpCalc.Resp> getCalcCoin(@Path("hashrate") String hashrate);
}

