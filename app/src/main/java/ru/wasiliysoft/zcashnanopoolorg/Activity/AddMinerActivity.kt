package ru.wasiliysoft.zcashnanopoolorg.Activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.zxing.integration.android.IntentIntegrator
import ru.wasiliysoft.zcashnanopoolorg.Model.Miner
import ru.wasiliysoft.zcashnanopoolorg.Model.Miners
import ru.wasiliysoft.zcashnanopoolorg.R

class AddMinerActivity : AppCompatActivity(), View.OnClickListener {

    private var etName: EditText? = null
    private var etAddress: EditText? = null
    private var miners: Miners? = null
    private val TAG = "MainActivity2"
    private var data: Uri? = null
    private var tvAccount: TextView? = null
    private var tvTicker: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_add_miner)
        miners = Miners(applicationContext)

        etName = findViewById(R.id.minerName)
        etAddress = findViewById(R.id.minerUrlAddress)
        tvAccount = findViewById(R.id.accountTextView)
        tvTicker = findViewById(R.id.tickerTextView)
        data = intent.data
        if (data != null) {
            val params = data!!.pathSegments
            etAddress!!.setText(params[1])
        }
        (findViewById<View>(R.id.bAddMiner) as Button).setOnClickListener(this)
        (findViewById<View>(R.id.qrBtn) as Button).setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.bAddMiner -> {

                if (etName!!.text.toString().trim { it <= ' ' }.isEmpty()) {
                    return
                }
                if (tvTicker!!.text.toString().trim { it <= ' ' }.isEmpty()) {
                    return
                }
                if (tvAccount!!.text.toString().trim { it <= ' ' }.isEmpty()) {
                    return
                }
                val m = Miner(etName!!.text.toString().trim { it <= ' ' }, tvTicker!!.text.toString().trim { it <= ' ' }, tvAccount!!.text.toString().trim { it <= ' ' })
                miners!!.add(m)
                setResult(Activity.RESULT_OK)
                if (data != null) {
                    val i = Intent(this, MainActivity::class.java)
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(i)
                }
                finish()

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {

        val scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (scanResult != null) {
            if (scanResult.contents != null) {
                val re = scanResult.contents
                Log.d(TAG, "Barcode read: $re")
                val scanedUrl = Uri.parse(re)
                if (scanedUrl != null) {
                    val host = scanedUrl.host
                    val domain = host!!.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
                    Log.d(TAG, host)

                    Log.d(TAG, domain)
                    if (domain == "nanopool") {
                        etAddress!!.setText(scanedUrl.toString())
                        val ticker = host.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
                        Log.d(TAG, ticker)
                        val account = scanedUrl.pathSegments[1]
                        tvTicker!!.text = ticker
                        tvAccount!!.text = account


                    } else {
                        Toast.makeText(this, getString(R.string.unsupported_url) + getString(R.string.host), Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(this, getString(R.string.unsupported_url) + getString(R.string.host), Toast.LENGTH_LONG).show()
                }
                return
            }
        } else {
            Toast.makeText(this, R.string.barcode_error, Toast.LENGTH_LONG).show()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}
