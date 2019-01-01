package ru.wasiliysoft.zcashnanopoolorg.Frafment;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

import retrofit2.Response;
import ru.wasiliysoft.zcashnanopoolorg.Activity.MainActivity;
import ru.wasiliysoft.zcashnanopoolorg.App;
import ru.wasiliysoft.zcashnanopoolorg.Model.NpCalc;
import ru.wasiliysoft.zcashnanopoolorg.Model.NpGeneral;
import ru.wasiliysoft.zcashnanopoolorg.NpLoader;
import ru.wasiliysoft.zcashnanopoolorg.R;

public class GeneralFragment extends Fragment implements LoaderManager.LoaderCallbacks<Response> {

    private static final int LOADER_ID_NPCALC = 1;
    private static final int LOADER_ID_NPGENERAL = 2;
    private TextView tvCurH;
    private SwipeRefreshLayout mySwipeRefreshLayout;
    private TextView tvBalance, tvUnconfirmedBalance;
    private LinearLayout llWorkers;
    private LayoutInflater ltInflater;

    private TableLayout tlCalc;
    private TableLayout tlAVGHashRate;
    private static NpGeneral.Data mNpGenData;
    private LoaderManager lm;
    private String mAddress;

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAddress = getArguments().getString(MainActivity.BUNDLE_MINER_ADDRESS);
        // Инициализация загрузчика
        lm = getLoaderManager();
        lm.initLoader(LOADER_ID_NPGENERAL, null, this);
//        getLoaderManager().initLoader(LOADER_ID_NPCALC, null, this);
    }

    void Refresh() {

        tlAVGHashRate.removeAllViews();
        tlCalc.removeAllViews();
        llWorkers.removeAllViews();
        lm.restartLoader(LOADER_ID_NPGENERAL, null, this).forceLoad();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_general, parent, false);

        tvCurH = v.findViewById(R.id.tvCurH);
        tvBalance = v.findViewById(R.id.tvBalance);
        tvUnconfirmedBalance = v.findViewById(R.id.tvUnconfirmedBalance);

        mySwipeRefreshLayout = v.findViewById(R.id.swiperefresh);
        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Refresh();
                    }
                }
        );

        ltInflater = getActivity().getLayoutInflater();
        tlCalc = v.findViewById(R.id.tlCalc);
        llWorkers = v.findViewById(R.id.llWorkers);
        tlAVGHashRate = v.findViewById(R.id.tlAvgHashrate);

        return v;
    }

    void settingBalances() {
        tvBalance.setText(mNpGenData.getBalance());
        tvUnconfirmedBalance.setText(mNpGenData.getUnconfirmedBalance());
    }

    void settingCurHashrate() {
        tvCurH.setText(mNpGenData.getHashrate());
    }

    void settingAVGHashRate() {
        NpGeneral.AvgHashrate h = mNpGenData.getAvgHashrate();
        tlAVGHashRate.removeAllViews();
        tlAVGHashRate.addView(getViewAVGHashRate(h.getH1(), "1 hour"));
        tlAVGHashRate.addView(getViewAVGHashRate(h.getH3(), "3 hour"));
        tlAVGHashRate.addView(getViewAVGHashRate(h.getH6(), "6 hour"));
        tlAVGHashRate.addView(getViewAVGHashRate(h.getH12(), "12 hour"));
        tlAVGHashRate.addView(getViewAVGHashRate(h.getH24(), "24 hour"));
    }

    View getViewAVGHashRate(String hashrate, String period) {
        View viewCalc = ltInflater.inflate(R.layout.avg_data, null, false);
        //                Period
        TextView c1 = viewCalc.findViewById(R.id.tvCol1);
        c1.setText(period);
        //                VGHashRate
        TextView c2 = viewCalc.findViewById(R.id.tvCol2);
        c2.setText(hashrate);
        return viewCalc;
    }

    void settingEarnings(NpCalc.Data c) {
        tlCalc.removeAllViews();
        tlCalc.addView(ltInflater.inflate(R.layout.calc_data_header, null, false));
        tlCalc.addView(getViewEarnings(c.getDay()));
        tlCalc.addView(getViewEarnings(c.getWeek()));
        tlCalc.addView(getViewEarnings(c.getMonth()));
    }

    View getViewEarnings(NpCalc.Earnings o) {

        View viewCalc = ltInflater.inflate(R.layout.calc_data, null, false);
        //                Period
        TextView c1 = viewCalc.findViewById(R.id.tvCol1);
        c1.setText(o.getPeriod());
        //                Coin
        TextView c2 = viewCalc.findViewById(R.id.tvCol2);
        c2.setText(o.getCoins());
        //                BTC
        TextView c3 = viewCalc.findViewById(R.id.tvCol3);
        c3.setText(o.getBitcoins());
        //                USD
        TextView c4 = viewCalc.findViewById(R.id.tvCol4);
        c4.setText(o.getDollars());
        //                RUR
        TextView c5 = viewCalc.findViewById(R.id.tvCol5);
        c5.setText(o.getRubles());
        return viewCalc;
    }

    void settingWorkers() {
        llWorkers.removeAllViews();
        List<NpGeneral.Worker> mListWorkers = mNpGenData.getWorkers();
        for (NpGeneral.Worker w : mListWorkers) {
            View view = ltInflater.inflate(R.layout.worker, null, false);

            ((TextView) view.findViewById(R.id.tvWorker)).setText(w.getId());

            ((TextView) view.findViewById(R.id.tvCurH))
                    .setText(new StringBuilder()
                            .append("[ Current Hashrate  ")
                            .append(w.getHashrate())
                            .append(" ]")
                    );
            ((TextView) view.findViewById(R.id.tvAVGH6))
                    .setText(new StringBuilder()
                            .append("[ AVG (6h) Hashrate ")
                            .append(w.getH6())
                            .append(" ]")
                    );

            long diffMinutes = (System.currentTimeMillis() / 1000 - w.getLastshare()) / 60;
            long hours = diffMinutes / 60;
            long minutes = diffMinutes % 60;
            ((TextView) view.findViewById(R.id.tvLastShare))
                    .setText(new StringBuilder()
                            .append("[ last share ")
                            .append(hours)
                            .append("h. ")
                            .append(minutes)
                            .append("min. ago ]")
                    );
            llWorkers.addView(view);
        }
    }

    @Override
    public void onLoadFinished(Loader<Response> loader, Response response) {
        mySwipeRefreshLayout.setRefreshing(false);
        Log.d("onLoadFinished id", loader.getId() + "");

        if (response == null || !response.isSuccessful()) {
            Toast.makeText(getActivity(), R.string.error_loading_data, Toast.LENGTH_SHORT).show();
            return;
        }
        switch (loader.getId()) {
            case LOADER_ID_NPGENERAL:
                NpGeneral.Resp mGeneralResp = (NpGeneral.Resp) response.body();
                if (!mGeneralResp.getStatus()) {
//                    TODO Обхекдинить класыы Response (выделить общее во внешний класс) для удаления дулирования кода
                    Toast.makeText(getActivity(), R.string.error_loading_data, Toast.LENGTH_SHORT).show();
                    return;
                }
                mNpGenData = mGeneralResp.getData();
                settingBalances();
                settingCurHashrate();
                settingWorkers();
                settingAVGHashRate();
//                Старт загрузки прибыльности
                if (!mNpGenData.getAvgHashrate().getH6unRound().equals("0.0")) {
                    lm.restartLoader(LOADER_ID_NPCALC, null, this).forceLoad();
                }
                break;
            case LOADER_ID_NPCALC:
                NpCalc.Resp mCalcResp = (NpCalc.Resp) response.body();
                if (!mCalcResp.getStatus()) {
                    Toast.makeText(getActivity(), R.string.error_loading_data, Toast.LENGTH_SHORT).show();
                    return;
                }
                NpCalc.Data mCalcData = mCalcResp.getData();
                settingEarnings(mCalcData);
                break;
        }

    }

    @Override
    public void onLoaderReset(Loader<Response> loader) {

    }

    @Override
    public Loader<Response> onCreateLoader(int i, Bundle bundle) {

        Log.d("onCreateLoader id", i + "");
        switch (i) {
            case LOADER_ID_NPGENERAL:
                return new GeneralLoader(getActivity(), mAddress);
            case LOADER_ID_NPCALC:
                if (mNpGenData != null) {
                    return new CalcLoader(getActivity(), mNpGenData.getAvgHashrate().getH6unRound());
                }
        }
        return null;
    }

    static class GeneralLoader extends NpLoader {
        String user;

        GeneralLoader(Context context, String user) {
            super(context);
            this.user = user;
        }

        @Override
        protected Response LoadData() throws IOException {
            Response mResp = App.getApi().getGeneral(user).execute();
            if (mResp.isSuccessful()) {
                return mResp;
            } else {
                Log.e("LoadData", " Error get General data, NP return status false");
                return null;
            }
        }
    }

    static class CalcLoader extends NpLoader {
        String hashrate;

        CalcLoader(Context context, String hashrate) {
            super(context);
            this.hashrate = hashrate;
        }

        @Override
        protected Response LoadData() throws IOException {
            Response mResp = App.getApi().getCalcCoin(hashrate).execute();
            if (mResp.isSuccessful()) {
                return mResp;
            } else {
                Log.e("LoadData", " Error get Calc data, NP return status false");
                return null;
            }
        }
    }
}
