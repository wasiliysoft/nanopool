package ru.wasiliysoft.zcashnanopoolorg.Frafment


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
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
    private var minerId: Int = 0
    private lateinit var ltInflater: LayoutInflater

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        minerId = arguments!!.getInt(BUNDLE_MINER_ID)
    }

    fun resetViews() {
        lvBalances.visibility = View.GONE

        lvCurHashRate.visibility = View.GONE

        lvAvgHashrate.visibility = View.GONE
        lvAvgHashrate.removeAllViews()

        tlCalc.visibility = View.GONE
        tlCalc.removeAllViews()

        lvWorkers.visibility = View.GONE
        lvWorkers.removeAllViews()
    }

    fun settingsViews(ninew: Miner) {
        if (ninew.gen != null) {
            settingGeneral(ninew.gen!!)
            if (ninew.calc != null) {
                settingCalc(ninew.calc!!)
            }
        }
    }

    fun Refresh() {
        val worker1 = npWorker.newInstance(minerId)
        WorkManager.getInstance().getWorkInfoByIdLiveData(worker1.id).observe(this, Observer<WorkInfo> { worker ->
            if (worker != null) {
                Log.d(TAG_WORKER, worker.state.toString())
                when (worker.state) {
                    WorkInfo.State.RUNNING -> {
                        swiperefresh.isRefreshing = true
                        resetViews()
                    }
                    WorkInfo.State.SUCCEEDED -> {
                        tvFailedLoadData.visibility = View.GONE
                        swiperefresh.isRefreshing = false
                        settingsViews(App.getMiners().read()[minerId])
                    }
                    WorkInfo.State.FAILED -> {
                        tvFailedLoadData.visibility = View.VISIBLE
                        swiperefresh.isRefreshing = false
                        resetViews()
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

    fun settingGeneral(data: NpGeneral.Data) {
        tvBalance!!.text = data.balance
        tvUnconfirmedBalance!!.text = data.unconfirmedBalance
        lvBalances.visibility = View.VISIBLE

        tvCurH!!.text = data.hashrate
        lvCurHashRate.visibility = View.VISIBLE

        val h = data.avgHashrate

        lvAvgHashrate.addView(getViewAVGHashRate(h.h1, "01 hour"))
        lvAvgHashrate.addView(getViewAVGHashRate(h.h3, "03 hour"))
        lvAvgHashrate.addView(getViewAVGHashRate(h.h6, "06 hour"))
        lvAvgHashrate.addView(getViewAVGHashRate(h.h12, "12 hour"))
        lvAvgHashrate.addView(getViewAVGHashRate(h.h24, "24 hour"))
        lvAvgHashrate.visibility = View.VISIBLE

        settingWorkers(data)
    }


    fun getViewAVGHashRate(hashrate: String, period: String): View {
        val viewCalc = ltInflater.inflate(R.layout.avg_data, null, false)
        //                Period
        val c1 = viewCalc.findViewById<TextView>(R.id.tvCol1)
        c1.text = period
        //                VGHashRate
        val c2 = viewCalc.findViewById<TextView>(R.id.tvCol2)
        c2.text = hashrate
        return viewCalc
    }

    fun settingCalc(data: NpCalc.Data) {
        tlCalc.addView(ltInflater.inflate(R.layout.calc_data_header, null, false))
        tlCalc.addView(getViewEarnings(data.day))
        tlCalc.addView(getViewEarnings(data.week))
        tlCalc.addView(getViewEarnings(data.month))
        tlCalc.visibility = View.VISIBLE
    }

    fun getViewEarnings(o: NpCalc.Earnings): View {

        val viewCalc = ltInflater.inflate(R.layout.calc_data, null, false)
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

    fun settingWorkers(data: NpGeneral.Data) {
        val mListWorkers = data.workers
        var wCount = mListWorkers.size - 1
        val workersLimit = 100
        if (wCount > workersLimit) {
            limitWorkerNotify.text = "Sorry, max $workersLimit workers showing"
            limitWorkerNotify.visibility = View.VISIBLE
            wCount = workersLimit - 1
        } else {
            limitWorkerNotify.visibility = View.GONE
        }
        if (wCount != 0) {
            var w: NpGeneral.Worker
            var diffMinutes: Long = 0
            var hours: Long = 0
            var minutes: Long = 0
            var hashrate: String
            var h6: String
            for (i in 0..wCount) {
                w = mListWorkers[i]
                val view = ltInflater.inflate(R.layout.worker, null, false)

                diffMinutes = (System.currentTimeMillis() / 1000 - w.lastshare!!) / 60
                hours = diffMinutes / 60
                minutes = diffMinutes % 60
                hashrate = w.hashrate
                h6 = w.h6

                val tvWorker: TextView = view.findViewById<View>(R.id.tvWorker) as TextView
                tvWorker.text = "[" + (i + 1) + "] " + w.id
                if (diffMinutes >= 20) {
                    tvWorker.setTextColor(resources.getColor(R.color.offlineWorker))
                }
                (view.findViewById<View>(R.id.tvCurH) as TextView).text = "[ Current Hashrate  $hashrate ]"
                (view.findViewById<View>(R.id.tvAVGH6) as TextView).text = "[ AVG (6h) Hashrate $h6 ]"
                (view.findViewById<View>(R.id.tvLastShare) as TextView).text = "[ last share $hours h. $minutes min. ago ]"
                lvWorkers.addView(view)
            }
        }
        lvWorkers.visibility = View.VISIBLE
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
