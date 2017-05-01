package Fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import Utils.AppUrl;
import Utils.Constants;
import Utils.ParamsKey;
import Utils.PreferenceSettings;
import creadigol.com.tipper.R;
import creadigol.com.tipper.ServerDeatailsActivity;
import creadigol.com.tipper.TipperActivity;

/**
 * Created by ravi on 10-04-2017.
 */

public class FourRatingFragment extends Fragment {
    EditText edReview;
    Button SubmitReview;
    PreferenceSettings preferenceSettings;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.four_rating_activity, container,
                false);
        preferenceSettings = TipperActivity.getInstance().getPreferenceSettings();
        edReview = (EditText) rootView.findViewById(R.id.edRatingDesc);
        SubmitReview = (Button) rootView.findViewById(R.id.SubmitReview);
        SubmitReview.setEnabled(false);
        edReview.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (edReview.getText().toString() == "0") {
                    SubmitReview.setEnabled(false);
                    SubmitReview.setTextColor(getResources().getColor(R.color.colorGray));
                } else {
                    SubmitReview.setEnabled(true);
                    SubmitReview.setTextColor(getResources().getColor(R.color.black));
                }
            }
        });
        SubmitReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String review = edReview.getText().toString();
                if (review.equalsIgnoreCase("")) {
                    edReview.setError("enter Review");
                } else {
                    AddReview(review);
                }
            }
        });
        return rootView;
    }

    void AddReview(final String review) {
        final ProgressDialog pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);
        pDialog.show();

        Calendar calendar = Calendar.getInstance();
        final String date = String.valueOf(calendar.getTimeInMillis());
        final StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST, AppUrl.URL_ADDREVIEW, new Response.Listener<String>() {
            @Override
            public void onResponse(String Response) {
                Log.e("Place Response", Response);
                try {
                    JSONObject jsonObj = new JSONObject(Response);
                    String status_code = jsonObj.optString("status_code");
                    String massage = jsonObj.optString("message");

                    if (status_code.equalsIgnoreCase("1")) {
                        pDialog.dismiss();
                        ServerDeatailsActivity.isNeeded = true;
                        getActivity().finish();
                    } else if (status_code.equalsIgnoreCase("0")) {
                        Log.e("Error_in", "else");
                        pDialog.dismiss();
                        Toast.makeText(getActivity(), "" + massage, Toast.LENGTH_SHORT).show();
                    } else {
                        pDialog.dismiss();
                        Log.e("Error_in", "else");
                    }
                } catch (JSONException e) {
                    pDialog.dismiss();
                    Log.e("Error_in", "onErrorResponse");

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();
                Log.e("Error_in", "onErrorResponse");
                Log.e("Place", "Error Response: " + error.getMessage());
            }
        })

        {
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<String, String>();
                params.put(ParamsKey.KeyUserid, preferenceSettings.getUserId());
                params.put(ParamsKey.KeyDate, date);
                params.put(ParamsKey.KeyserverId, ServerDeatailsActivity.sId);
                params.put(ParamsKey.menuKnowledge, ServerDeatailsActivity.menuKnowledge);
                params.put(ParamsKey.hospitality, ServerDeatailsActivity.hospitality);
                params.put(ParamsKey.appearance, ServerDeatailsActivity.appearance);
                params.put(ParamsKey.review, review);
                params.put(ParamsKey.tip, "100");
                params.put(ParamsKey.KeyRegistrationType, Constants.TYPE_USER);
                Log.e("search", "Posting params: " + params.toString());
                return params;
            }
        };

        // Adding request to request queue

        TipperActivity.getInstance().addToRequestQueue(jsonObjectRequest, "review");
    }
}
