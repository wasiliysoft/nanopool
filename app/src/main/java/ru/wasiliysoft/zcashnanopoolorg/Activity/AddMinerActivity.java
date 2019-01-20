package ru.wasiliysoft.zcashnanopoolorg.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.List;

import ru.wasiliysoft.zcashnanopoolorg.Model.Miner;
import ru.wasiliysoft.zcashnanopoolorg.Model.Miners;
import ru.wasiliysoft.zcashnanopoolorg.R;

public class AddMinerActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etName;
    public EditText etAddress;
    private Miners miners;
    private String TAG = "MainActivity2";
    private Uri data;
    private TextView tvAccount;
    private TextView tvTicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_add_miner);
        miners = new Miners(getApplicationContext());

        etName = findViewById(R.id.minerName);
        etAddress = findViewById(R.id.minerUrlAddress);
        tvAccount = findViewById(R.id.accountTextView);
        tvTicker = findViewById(R.id.tickerTextView);
        data = getIntent().getData();
        if (data != null) {
            List<String> params = data.getPathSegments();
            etAddress.setText(params.get(1));
        }
        ((Button) findViewById(R.id.bAddMiner)).setOnClickListener(this);
        ((Button) findViewById(R.id.qrBtn)).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bAddMiner:

                if (etName.getText().toString().trim().isEmpty()) {
                    return;
                }
                if (tvTicker.getText().toString().trim().isEmpty()) {
                    return;
                }
                if (tvAccount.getText().toString().trim().isEmpty()) {
                    return;
                }
                Miner m = new Miner(etName.getText().toString().trim(), tvTicker.getText().toString().trim(), tvAccount.getText().toString().trim());
                miners.add(m);
                setResult(RESULT_OK);
                if (data != null) {
                    Intent i = new Intent(this, MainActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }
                finish();

                return;
            case R.id.qrBtn:
                IntentIntegrator integrator = new IntentIntegrator(this);
                Intent i = integrator.createScanIntent();
                startActivityForResult(i, IntentIntegrator.REQUEST_CODE);
                return;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (scanResult != null) {
            if (scanResult.getContents() != null) {
                String re = scanResult.getContents();
                Log.d(TAG, "Barcode read: " + re);
                Uri scanedUrl = Uri.parse(re);
                if (scanedUrl != null) {
                    String host = scanedUrl.getHost();
                    String domain = host.split("\\.")[1];
                    Log.d(TAG, host);

                    Log.d(TAG, domain);
                    if (domain.equals("nanopool")) {
                        etAddress.setText(scanedUrl.toString());
                        String ticker = host.split("\\.")[0];
                        Log.d(TAG, ticker);
                        String account = scanedUrl.getPathSegments().get(1);
                        tvTicker.setText(ticker);
                        tvAccount.setText(account);


                    } else {
                        Toast.makeText(this, getString(R.string.unsupported_url) + getString(R.string.host), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(this, getString(R.string.unsupported_url) + getString(R.string.host), Toast.LENGTH_LONG).show();
                }
                return;
            }
        } else {
            Toast.makeText(this, R.string.barcode_error, Toast.LENGTH_LONG).show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
