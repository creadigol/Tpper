package creadigol.com.tipper;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import Adapter.ResturantServerListAdapter;
import Halper.GPSTracker;
import Model.ResturantDetailsItem;
import Model.ResturantServeritem;
import Utils.AppUrl;
import Utils.CommonUtils;
import Utils.Jsonkey;
import Utils.ParamsKey;
import Utils.PreferenceSettings;

/**
 * Created by ravi on 03-04-2017.
 */

public class ResturantDetailsActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener {
    public static final String EXTRA_KEY_RESTURANTID = "resturantId";
    public static final String EXTRA_KEY_TYPE = "type";
    RecyclerView serverList;
    PreferenceSettings preferenceSettings;
    String resturantId, type;
    ImageView restrantImage;
    TextView toolbarName;
    LinearLayout llNodata;
    ResturantServerListAdapter resturantServerListAdapter;
    ProgressDialog pDialog;
    GPSTracker gps;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.resturantdetails_activity);
        preferenceSettings = TipperActivity.getInstance().getPreferenceSettings();
        serverList = (RecyclerView) findViewById(R.id.rvServerList);
        serverList.setNestedScrollingEnabled(false);
        restrantImage = (ImageView) findViewById(R.id.restrantImage);
        llNodata = (LinearLayout) findViewById(R.id.llNodata);
        gps = new GPSTracker(getApplicationContext());
        try {
            Bundle extras = getIntent().getExtras();
            resturantId = extras.getString(EXTRA_KEY_RESTURANTID);
            type = extras.getString(EXTRA_KEY_TYPE);
            getResturantDetails();
        } catch (Exception e) {
            Log.e("Exception", " " + e);
        }
        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_resturant);
        collapsingToolbar.setExpandedTitleGravity(View.TEXT_ALIGNMENT_CENTER);
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar_place_resturant);
        appBarLayout.addOnOffsetChangedListener(this);
        Toolbar();
    }

    public void Toolbar() {
        Toolbar toolbarRestDetails = (Toolbar) findViewById(R.id.toolbarresturant);
        toolbarName = (TextView) toolbarRestDetails.findViewById(R.id.tv_app_name);
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

    void getResturantDetails() {
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);
        pDialog.show();
        final StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST, AppUrl.URL_RESTURANTDETAILS, new Response.Listener<String>() {
            @Override
            public void onResponse(String Response) {
                Log.e("Place Response", Response);
                try {
                    JSONObject jsonObj = new JSONObject(Response);
                    ResturantDetailsItem resturantDetailsItem = new ResturantDetailsItem();

                    try {
                        resturantDetailsItem.setStatus_code(jsonObj.getInt(Jsonkey.statusCode));
                    } catch (JSONException e) {
                        resturantDetailsItem.setStatus_code(0);

                    }
                    resturantDetailsItem.setMessage(jsonObj.getString(Jsonkey.message));

                    if (resturantDetailsItem.getStatus_code() == 1) {
                        resturantDetailsItem.setId(jsonObj.optString(Jsonkey.resId));
                        resturantDetailsItem.setName(jsonObj.optString(Jsonkey.fullName));
                        resturantDetailsItem.setAddress(jsonObj.optString(Jsonkey.resturantAddress));
                        resturantDetailsItem.setPhoto(jsonObj.optString(Jsonkey.image));
                        resturantDetailsItem.setLat(jsonObj.optString(Jsonkey.latitute));
                        resturantDetailsItem.setLog(jsonObj.optString(Jsonkey.longitut));
                        resturantDetailsItem.setDistance(jsonObj.optString(Jsonkey.distance));
                        try {
                            JSONArray serverList = jsonObj.getJSONArray(Jsonkey.serverKey);
                            resturantDetailsItem.setResturantServeritems(serverList);
                        } catch (JSONException e) {
                            resturantDetailsItem.setResturantServeritems(null);
                        }
                        setResturantDetails(resturantDetailsItem);
                        pDialog.dismiss();
                    } else if (resturantDetailsItem.getStatus_code() == 0) {
                        finish();
                        pDialog.dismiss();
                        llNodata.setVisibility(View.VISIBLE);
                        serverList.setVisibility(View.GONE);
                        Log.e("Error_in", "else");
                        CommonUtils.showToast(resturantDetailsItem.getMessage());
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
                showTryAgainAlert("Network error", "please check your network try again");
                Log.e("Place", "Error Response: " + error.getMessage());
            }
        })

        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(ParamsKey.KeyUserId, preferenceSettings.getUserId());
                params.put(ParamsKey.KeyRegistrationType, type);
                params.put(ParamsKey.KeyRestuarntId, resturantId);
                params.put(ParamsKey.Keylat, gps.getLatitude() + "");
                params.put(ParamsKey.KeyLong, gps.getLongitude() + "");
//                params.put(ParamsKey.Keylat, "37.090240");
//                params.put(ParamsKey.KeyLong, "-95.712891");
                Log.e("search", "Posting params: " + params.toString());
                return params;
            }
        };


        // Adding request to request queue

        TipperActivity.getInstance().addToRequestQueue(jsonObjectRequest, "review");
    }

    public void showTryAgainAlert(String title, String message) {
        CommonUtils.showAlertWithNegativeButton(ResturantDetailsActivity.this, title, message, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                if (CommonUtils.isNetworkAvailable())
                    getResturantDetails();
                else
                    CommonUtils.showToast("Please check your internet connection");
            }
        });

    }

    public void setResturantDetails(final ResturantDetailsItem resturantDetails) {
        toolbarName.setText(resturantDetails.getName());
        TextView restuarantName = (TextView) findViewById(R.id.resturantNameBottom);
        TextView resturantAddress = (TextView) findViewById(R.id.resturantAddress);
        restuarantName.setText(resturantDetails.getName());
        resturantAddress.setText(resturantDetails.getAddress());
        setImage(resturantDetails.getPhoto());
        LinearLayout llKms = (LinearLayout) findViewById(R.id.llKms);
        View view = (View) findViewById(R.id.view);
        TextView distance = (TextView) findViewById(R.id.distance);
        if (resturantDetails.getDistance() != null && !resturantDetails.getDistance().equalsIgnoreCase("") && !resturantDetails.getDistance().equalsIgnoreCase("null")&&!resturantDetails.getDistance().equalsIgnoreCase("0")) {
            llKms.setVisibility(View.VISIBLE);
            view.setVisibility(View.VISIBLE);
            distance.setText(resturantDetails.getDistance());
        } else {
            llKms.setVisibility(View.GONE);
            view.setVisibility(View.GONE);
        }
        if (resturantDetails.getResturantServeritems() != null && resturantDetails.getResturantServeritems().size() > 0) {
            setServerList(resturantDetails.getResturantServeritems());
        } else {
            llNodata.setVisibility(View.VISIBLE);
            serverList.setVisibility(View.GONE);
        }
        ImageView googleMap=(ImageView)findViewById(R.id.googleMap);
        googleMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + resturantDetails.getLat() + "," + resturantDetails.getLog());
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });
    }

    void setServerList(ArrayList<ResturantServeritem> resturantServeritems) {
        if (resturantServeritems != null && resturantServeritems.size() > 0) {
            if (resturantServerListAdapter == null) {
                serverList.setVisibility(View.VISIBLE);
                serverList.setHasFixedSize(true);
                serverList.setItemAnimator(new DefaultItemAnimator());
                serverList.setLayoutManager(new LinearLayoutManager(ResturantDetailsActivity.this));
                resturantServerListAdapter = new ResturantServerListAdapter(ResturantDetailsActivity.this, resturantServeritems);
                serverList.setAdapter(resturantServerListAdapter);
            } else {
                resturantServerListAdapter.modifyDataSet(resturantServeritems);
            }
        } else {
            if (resturantServerListAdapter != null) {
                resturantServerListAdapter.modifyDataSet(resturantServeritems);
            }
        }
    }

    public void setImage(String userImageUrl) {
        com.nostra13.universalimageloader.core.ImageLoader imageLoader = TipperActivity.getInstance().getImageLoader();
        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisk(true).resetViewBeforeLoading(true)
                .showImageForEmptyUri(R.drawable.restaurantimage)
                .showImageOnFail(R.drawable.restaurantimage)
                .showImageOnLoading(R.drawable.restaurantimage)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        //download and display image from url
        imageLoader.displayImage(userImageUrl, restrantImage, options);

    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        if (i == (-maxScroll)) {
            Log.e("maxscroll", "" + maxScroll);
            toolbarName.setVisibility(View.VISIBLE);
        } else {
            toolbarName.setVisibility(View.GONE);
        }

    }
}
