package ru.wasiliysoft.zcashnanopoolorg.Frafment


import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.work.WorkInfo

import androidx.work.WorkManager

import kotlinx.android.synthetic.main.fragment_general.*
import ru.wasiliysoft.zcashnanopoolorg.App
import ru.wasiliysoft.zcashnanopoolorg.Model.Miner
import ru.wasiliysoft.zcashnanopoolorg.Model.NpCalc
import ru.wasiliysoft.zcashnanopoolorg.Model.NpGeneral
import ru.wasiliysoft.zcashnanopoolorg.R
import ru.wasiliysoft.zcashnanopoolorg.npWorker

class GeneralFragment : Fragment() {
    private var mMiner: Miner? = null
    private var minerId: Int = 0
    private lateinit var ltInflater: LayoutInflater

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        minerId = arguments!!.getInt(BUNDLE_MINER_ID)
        mMiner = App.getMiners().read()[minerId]
    }

    internal fun Refresh() {
        tlAvgHashrate.removeAllViews()
        tlCalc.removeAllViews()
        llWorkers.removeAllViews()

        val worker1 = npWorker.newInstance(minerId)
        WorkManager.getInstance().getWorkInfoByIdLiveData(worker1.id).observe(this, Observer<WorkInfo> { worker ->
            if (worker != null) {
                Log.d(TAG_WORKER, worker.state.toString())
                when (worker.state) {
                    WorkInfo.State.RUNNING -> swiperefresh.isRefreshing = true
                    WorkInfo.State.SUCCEEDED -> {
                        swiperefresh.isRefreshing = false
                        mMiner = App.getMiners().read()[minerId]

                        if (mMiner!!.gen != null) {
                            settingBalances(mMiner!!.gen!!)
                            settingWorkers(mMiner!!.gen!!)
                            if (mMiner!!.calc != null) {
                                settingEarnings(mMiner!!.calc!!)
                            }
                        }
                    }
                    WorkInfo.State.FAILED -> {
                        swiperefresh.isRefreshing = false
                        //todo
                        // Toast.makeText(this, toastMessage, Toast.LENGTH_LONG).show()
                    }
                    else -> {
                        //todo
                    }
                }
            }
        })
        WorkManager.getInstance().enqueue(worker1)
    }

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_general, parent, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        swiperefresh.setOnRefreshListener { Refresh() }
        ltInflater = getActivity()!!.layoutInflater
        Refresh()
    }

    internal fun settingBalances(data: NpGeneral.Data) {
        tvBalance!!.text = data.balance
        tvUnconfirmedBalance!!.text = data.unconfirmedBalance
        tvCurH!!.text = data.hashrate
        val h = data.avgHashrate
        tlAvgHashrate.removeAllViews()
        tlAvgHashrate.addView(getViewAVGHashRate(h.h1, "1 hour"))
        tlAvgHashrate.addView(getViewAVGHashRate(h.h3, "3 hour"))
        tlAvgHashrate.addView(getViewAVGHashRate(h.h6, "6 hour"))
        tlAvgHashrate.addView(getViewAVGHashRate(h.h12, "12 hour"))
        tlAvgHashrate.addView(getViewAVGHashRate(h.h24, "24 hour"))
    }


    internal fun getViewAVGHashRate(hashrate: String, period: String): View {
        val viewCalc = ltInflater!!.inflate(R.layout.avg_data, null, false)
        //                Period
        val c1 = viewCalc.findViewById<TextView>(R.id.tvCol1)
        c1.text = period
        //                VGHashRate
        val c2 = viewCalc.findViewById<TextView>(R.id.tvCol2)
        c2.text = hashrate
        return viewCalc
    }

    internal fun settingEarnings(data: NpCalc.Data) {
        tlCalc!!.removeAllViews()
        tlCalc!!.addView(ltInflater.inflate(R.layout.calc_data_header, null, false))
        tlCalc!!.addView(getViewEarnings(data.day))
        tlCalc!!.addView(getViewEarnings(data.week))
        tlCalc!!.addView(getViewEarnings(data.month))
    }

    internal fun getViewEarnings(o: NpCalc.Earnings): View {

        val viewCalc = ltInflater!!.inflate(R.layout.calc_data, null, false)
        //                Period
        val c1 = viewCalc.findViewById<TextView>(R.id.tvCol1)
        c1.text = o.period
        //                Coin
        val c2 = viewCalc.findViewById<TextView>(R.id.tvCol2)
        c2.text = o.coins
        //                BTC
        val c3 = viewCalc.findViewById<TextView>(R.id.tvCol3)
        c3.text = o.bitcoins
        //                USD
        val c4 = viewCalc.findViewById<TextView>(R.id.tvCol4)
        c4.text = o.dollars
        //                RUR
        val c5 = viewCalc.findViewById<TextView>(R.id.tvCol5)
        c5.text = o.rubles
        return viewCalc
    }

    internal fun settingWorkers(data: NpGeneral.Data) {
        llWorkers!!.removeAllViews()
        val mListWorkers = data.workers
        var wCount = mListWorkers.size - 1
        val workersLimit = 20
        if (wCount > workersLimit) {
            limitWorkerNotify.text = "Sorry, max $workersLimit workers showing"
            limitWorkerNotify.visibility = View.VISIBLE
            wCount = workersLimit - 1
        } else {
            limitWorkerNotify.visibility = View.GONE
        }
        if (wCount != 0) {
            var w: NpGeneral.Worker
            for (i in 0..wCount) {
                w = mListWorkers[i]
                val view = ltInflater.inflate(R.layout.worker, null, false)

                (view.findViewById<View>(R.id.tvWorker) as TextView).text = w.id

                (view.findViewById<View>(R.id.tvCurH) as TextView).text = StringBuilder()
                        .append("[ Current Hashrate  ")
                        .append(w.hashrate)
                        .append(" ]")
                (view.findViewById<View>(R.id.tvAVGH6) as TextView).text = StringBuilder()
                        .append("[ AVG (6h) Hashrate ")
                        .append(w.h6)
                        .append(" ]")

                val diffMinutes = (System.currentTimeMillis() / 1000 - w.lastshare!!) / 60
                val hours = diffMinutes / 60
                val minutes = diffMinutes % 60
                (view.findViewById<View>(R.id.tvLastShare) as TextView).text = StringBuilder()
                        .append("[ last share ")
                        .append(hours)
                        .append("h. ")
                        .append(minutes)
                        .append("min. ago ]")
                llWorkers!!.addView(view)
            }
        }

    }


    companion object {
        val TAG = "GeneralFragment"
        val TAG_WORKER = "GeneralFragment_Worker"
        var BUNDLE_MINER_ID = "BUNDLE_MINER_ID"

        fun newInstance(i: Int): GeneralFragment {
            val args = Bundle()
            args.putInt(GeneralFragment.BUNDLE_MINER_ID, i)
            val f = GeneralFragment()
            f.arguments = args
            return f
        }
    }
}
