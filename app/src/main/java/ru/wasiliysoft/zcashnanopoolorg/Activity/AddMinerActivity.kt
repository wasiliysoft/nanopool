package ru.wasiliysoft.zcashnanopoolorg.Activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.fragment_add_miner.*
import ru.wasiliysoft.zcashnanopoolorg.Model.Miner
import ru.wasiliysoft.zcashnanopoolorg.Model.Miners
import ru.wasiliysoft.zcashnanopoolorg.R
import java.util.regex.Pattern

class AddMinerActivity : AppCompatActivity(), View.OnClickListener {


    private var miners: Miners? = null
    private val TAG = "MainActivity2"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_add_miner)
        miners = Miners(applicationContext)
        bAddMiner.setOnClickListener(this)
        qrBtn.setOnClickListener(this)

        minerUrlAddress.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                updateMinerPrefUi(p0.toString())
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })
    }


    override fun onClick(view: View) {
        when (view.id) {
            R.id.bAddMiner -> {

                if (minerName.text.toString().trim().isEmpty()) {
                    return
                }
                if (tickerTextView.text.toString().trim().isEmpty()) {
                    return
                }
                if (accountTextView.text.toString().trim().isEmpty()) {
                    return
                }
                val m = Miner(minerName.text.toString().trim(), tickerTextView.text.toString().trim(), accountTextView.text.toString().trim())
                miners!!.add(m)

                val i = Intent(this, MainActivity::class.java)
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK) //todo надо уточнить
                startActivity(i)
                return
            }
            R.id.qrBtn -> {
                val integrator = IntentIntegrator(this)
                val i = integrator.createScanIntent()
                startActivityForResult(i, IntentIntegrator.REQUEST_CODE)
                return
            }
        }
    }

    fun updateMinerPrefUi(url: String) {
        val uri = Uri.parse(url)
        val host = uri.host
        if (host != null) {
            val hostParams = host.split(regex = Pattern.compile("\\."))
            Log.d(TAG, hostParams.size.toString())
            if (hostParams.size >= 2) {
                val ticker = hostParams[0]
                Log.d(TAG, ticker)
                val domain = hostParams[1]
                Log.d(TAG, host)
                if (domain.equals("nanopool")) {
                    if (uri.pathSegments.size >= 2) {
                        val account = uri.pathSegments[1]
                        tickerTextView!!.text = ticker
                        accountTextView!!.text = account
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {

        val scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (scanResult != null) {
            if (scanResult.contents != null) {
                val re = scanResult.contents
                Log.d(TAG, "Barcode read: $re")
                val scanedUrl = Uri.parse(re)
                if (scanedUrl != null) {
                    minerUrlAddress!!.setText(scanedUrl.toString())
                } else {
                    Toast.makeText(this, getString(R.string.unsupported_url), Toast.LENGTH_LONG).show()
                }
                return
            }
        } else {
            Toast.makeText(this, R.string.barcode_error, Toast.LENGTH_LONG).show()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}
