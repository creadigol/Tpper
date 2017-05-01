package creadigol.com.tipper;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import Adapter.MyReviewListAdapter;
import Model.MyReviewsListItem;
import Model.MyReviewsListObject;
import Utils.AppUrl;
import Utils.CommonUtils;
import Utils.Constants;
import Utils.Jsonkey;
import Utils.ParamsKey;
import Utils.PreferenceSettings;

/**
 * Created by ravi on 06-04-2017.
 */

public class UserReviewListActivity extends AppCompatActivity {
    RecyclerView rvUserReviewList;
    PreferenceSettings preferenceSettings;
    MyReviewListAdapter myReviewListAdapter;
    TextView noReviewMsg;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userreviewlist_activity);
        preferenceSettings = TipperActivity.getInstance().getPreferenceSettings();
        rvUserReviewList = (RecyclerView) findViewById(R.id.rvUserReviewList);
        noReviewMsg = (TextView) findViewById(R.id.noReviewMsg);
        toolbar();
        getUserReviewList();
    }

    public void toolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.userReviewToolbar);
        TextView textView = (TextView) toolbar.findViewById(R.id.tv_toolbarName);
        textView.setText("My Reviews");
        setSupportActionBar(toolbar);
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

    void getUserReviewList() {
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);
        pDialog.show();
        final StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST, AppUrl.URL_USERREVIEW, new Response.Listener<String>() {
            @Override
            public void onResponse(String Response) {
                Log.e("Place Response", Response);
                try {
                    JSONObject jsonObj = new JSONObject(Response);

                    MyReviewsListObject myReviewsListObject = new MyReviewsListObject();
                    try {
                        myReviewsListObject.setStatus_code(jsonObj.getInt(Jsonkey.statusCode));
                    } catch (JSONException e) {
                        myReviewsListObject.setStatus_code(0);

                    }
                    myReviewsListObject.setMessage(jsonObj.getString(Jsonkey.message));

                    if (myReviewsListObject.getStatus_code() == 1) {
                        try {
                            JSONArray serverList = jsonObj.getJSONArray(Jsonkey.historyKey);
                            myReviewsListObject.setMyReviewsListItems(serverList);
                        } catch (JSONException e) {
                            myReviewsListObject.setMyReviewsListItems(null);
                        }
                        setResturantDetails(myReviewsListObject);
                        pDialog.dismiss();
                    } else if (myReviewsListObject.getStatus_code() == 0) {
                        pDialog.dismiss();
                        noReviewMsg.setVisibility(View.VISIBLE);
                        rvUserReviewList.setVisibility(View.GONE);
                        Log.e("Error_in", "else");
                        Toast.makeText(UserReviewListActivity.this, "" + myReviewsListObject.getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        pDialog.dismiss();
                        noReviewMsg.setVisibility(View.VISIBLE);
                        rvUserReviewList.setVisibility(View.GONE);
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
                showTryAgainAlert("Network error", "please check your network try again");
                Log.e("Place", "Error Response: " + error.getMessage());
            }
        })

        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(ParamsKey.KeyUserId, preferenceSettings.getUserId());
                params.put(ParamsKey.KeyRegistrationType, Constants.TYPE_USER);
                Log.e("search", "Posting params: " + params.toString());
                return params;

            }
        };


        // Adding request to request queue

        TipperActivity.getInstance().addToRequestQueue(jsonObjectRequest, "review");
    }

    public void showTryAgainAlert(String title, String message) {
        CommonUtils.showAlertWithNegativeButton(UserReviewListActivity.this, title, message, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                if (CommonUtils.isNetworkAvailable())
                    getUserReviewList();
                else
                    CommonUtils.showToast("Please check your internet connection");
            }
        });

    }

    public void setResturantDetails(final MyReviewsListObject myReviewsListObject) {
        if (myReviewsListObject.getMyReviewsListItems() != null && myReviewsListObject.getMyReviewsListItems().size() > 0) {
            setReviewList(myReviewsListObject.getMyReviewsListItems());
        }
    }

    void setReviewList(ArrayList<MyReviewsListItem> reviewList) {
        if (reviewList != null && reviewList.size() > 0) {
            if (myReviewListAdapter == null) {
                rvUserReviewList.setVisibility(View.VISIBLE);
                rvUserReviewList.setHasFixedSize(true);
                rvUserReviewList.setItemAnimator(new DefaultItemAnimator());
                rvUserReviewList.setLayoutManager(new LinearLayoutManager(UserReviewListActivity.this));
                myReviewListAdapter = new MyReviewListAdapter(UserReviewListActivity.this, reviewList);
                rvUserReviewList.setAdapter(myReviewListAdapter);
            } else {
                myReviewListAdapter.modifyDataSet(reviewList);
            }
        } else {
            if (myReviewListAdapter != null) {
                myReviewListAdapter.modifyDataSet(reviewList);
            }
        }
    }
}
