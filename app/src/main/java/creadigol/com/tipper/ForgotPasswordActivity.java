package creadigol.com.tipper;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import Model.UserObject;
import Utils.AppUrl;
import Utils.CommonUtils;
import Utils.Constants;
import Utils.Jsonkey;
import Utils.ParamsKey;

/**
 * Created by ravi on 01-05-2017.
 */

public class ForgotPasswordActivity extends AppCompatActivity implements View.OnClickListener {
    Button submit;
    EditText edEmailId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword);
        Toolbar();
        edEmailId = (EditText) findViewById(R.id.edEmailId);
        submit = (Button) findViewById(R.id.submit);
        submit.setOnClickListener(this);
    }

    public void Toolbar() {
        Toolbar toolbarRestDetails = (Toolbar) findViewById(R.id.toolbarForgot);
        TextView tv_toolbarName = (TextView) toolbarRestDetails.findViewById(R.id.tv_toolbarName);
        tv_toolbarName.setText("Forgot Password");
        setSupportActionBar(toolbarRestDetails);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                finish();
        }
        return true;
    }

    public void CheckValideation() {
        String emailId = edEmailId.getText().toString().trim();
        if (emailId == null && emailId.equalsIgnoreCase("")) {
            edEmailId.setError("Enter EmailId");
        } else if (!CommonUtils.isValidEmail(emailId)) {
            edEmailId.setError("Enter Proper emailId");
        } else {
            forgotPassword();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submit:
                CheckValideation();
                break;
        }
    }

    public void forgotPassword() {
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);
        pDialog.show();
        HttpsTrustManager.allowAllSSL();
        String url;

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, AppUrl.URL_LOGIN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pDialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Log.e("Response: ", "" + response);

                    String status_code = jsonObject.optString("status_code");
                    String massage = jsonObject.optString("massage");

                    if (status_code.equalsIgnoreCase("1")) {
                        CommonUtils.showToast("Success");
                        finish();
                    } else if (status_code.equalsIgnoreCase("0")) {
                        CommonUtils.showToast(massage);
                        pDialog.dismiss();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    CommonUtils.showToast("Something went wrong!.please try again");
                    Log.e("Error_in", "catch");
                    pDialog.dismiss();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("", "Error: " + error.getMessage());
                Log.e("Error_in", "onErrorResponse");
                error.printStackTrace();
                pDialog.dismiss();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(ParamsKey.KeyRegistrationSignupType, "");
                params.put(ParamsKey.KeyImage, "");
                Log.e("", "reqUserLogin params: " + params.toString());

                return params;
            }
        };
        // Adding request to request queue
        TipperActivity.getInstance().addToRequestQueue(jsonObjReq, "Forgot");
    }
}
