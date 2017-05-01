package creadigol.com.tipper;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.philjay.valuebar.ValueBar;
import com.philjay.valuebar.ValueBarSelectionListener;
import com.philjay.valuebar.colors.RedToGreenFormatter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import Model.RatingItem;
import Model.ReviewHistoryItem;
import Model.UserReviewDetailsItems;
import Utils.AppUrl;
import Utils.CommonUtils;
import Utils.Constants;
import Utils.Jsonkey;
import Utils.ParamsKey;
import Utils.PreferenceSettings;

/**
 * Created by ravi on 10-04-2017.
 */

public class ServerHomeActivity extends AppCompatActivity implements ValueBarSelectionListener, NavigationView.OnNavigationItemSelectedListener, AppBarLayout.OnOffsetChangedListener {
    NavigationView navigationView;
    ImageView imageProfilePic, backHomeProfile, homeServerPic;
    TextView txtEmailId, txtUserName, HomeserverName, tv_toolbarName;
    PreferenceSettings preferenceSettings;
    private ValueBar[] mValueBars = new ValueBar[4];
    RecyclerView rvHomeReviewList;
    ArrayList<UserReviewDetailsItems> userReviewDetailsItems;
    ArrayList<UserReviewDetailsItems> listItems;
    ServerHomeReviewListAdapter serverHomeReviewListAdapter;
    String resturantId;
    LinearLayout loadMore, llNoDataAvail;
    boolean load = false, backPress = false;
    String pages = "0";
    int Type;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serverhome);
        homeServerPic = (ImageView) findViewById(R.id.homeServerPic);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        loadMore = (LinearLayout) findViewById(R.id.loadMore);
        tv_toolbarName = (TextView) toolbar.findViewById(R.id.tv_toolbarName);
        setSupportActionBar(toolbar);
        tv_toolbarName.setVisibility(View.GONE);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_serverHome);
        collapsingToolbar.setExpandedTitleGravity(View.TEXT_ALIGNMENT_CENTER);
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar_place_serverHome);
        appBarLayout.addOnOffsetChangedListener(this);
        preferenceSettings = TipperActivity.getInstance().getPreferenceSettings();
        HomeserverName = (TextView) findViewById(R.id.HomeserverName);

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.serverDrawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_serverView);
        View headerView = LayoutInflater.from(this).inflate(R.layout.nav_header_main, navigationView, false);
        imageProfilePic = (ImageView) headerView.findViewById(R.id.home_profilepic);
        backHomeProfile = (ImageView) headerView.findViewById(R.id.backHomeProfile);

        txtEmailId = (TextView) headerView.findViewById(R.id.txtEmail);
        txtUserName = (TextView) headerView.findViewById(R.id.txtUsername);

        navigationView.addHeaderView(headerView);
        navigationView.getMenu().getItem(0).setChecked(true);

        headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO open profile
                Intent i = new Intent(ServerHomeActivity.this, ProfileActivity.class);
                startActivity(i);
                drawer.closeDrawers();

            }
        });
        navigationView.setNavigationItemSelectedListener(this);
        rvHomeReviewList = (RecyclerView) findViewById(R.id.rvHomeReviewList);
        rvHomeReviewList.setNestedScrollingEnabled(false);
        llNoDataAvail = (LinearLayout) findViewById(R.id.llNoDataAvail);
        getServerDetails(0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        navigationView.getMenu().getItem(0).setChecked(true);
        setProfileImage(preferenceSettings.getUserPic(), imageProfilePic);
        setProfileBack(preferenceSettings.getUserPic(), backHomeProfile);
        setProfileImage(preferenceSettings.getUserPic(), homeServerPic);
        txtUserName.setText(CommonUtils.getCapitalize(preferenceSettings.getFullName()));
        HomeserverName.setText(CommonUtils.getCapitalize(preferenceSettings.getFullName()));
        txtEmailId.setText(preferenceSettings.getEmailId());
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_serverLogout) {
            logoutAlear();
        } else if (id == R.id.nav_serverReview) {
        } else if (id == R.id.nav_myResturant) {
            Intent intent = new Intent(ServerHomeActivity.this, ResturantDetailsActivity.class);
            intent.putExtra(ResturantDetailsActivity.EXTRA_KEY_RESTURANTID, resturantId);
            intent.putExtra(ResturantDetailsActivity.EXTRA_KEY_TYPE, Constants.TYPE_SERVER);
            startActivity(intent);
        } else if (id == R.id.nav_serverShare) {
//            Intent sendIntent = new Intent();
//            sendIntent.setAction(Intent.ACTION_SEND);
//            sendIntent.putExtra(Intent.EXTRA_TEXT, "Tipper-Android Apps on Google Play");
//            sendIntent.setType("text/plain");
//            sendIntent.putExtra(android.content.Intent.EXTRA_TEXT, "");
//            startActivity(Intent.createChooser(sendIntent, "Share Tipper via"));
            CommonUtils.showToast("Application are not availalbe on Playstore,try again later");
        } else if (id == R.id.nav_ServerRateus) {
//            Intent intent = new Intent(Intent.ACTION_VIEW,
//                    Uri.parse(""));
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
//            startActivity(intent);
//            finish();
            CommonUtils.showToast("Application are not availalbe on Playstore,try again later");
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.serverDrawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return false;
    }

    public void setProfileImage(String userImageUrl, ImageView backHomeProfile) {

        com.nostra13.universalimageloader.core.ImageLoader imageLoader = TipperActivity.getInstance().getImageLoader();
        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisk(true).resetViewBeforeLoading(true)
                .showImageForEmptyUri(R.drawable.profile)
                .showImageOnFail(R.drawable.profile)
                .showImageOnLoading(R.drawable.profile)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        //download and display image from url
        imageLoader.displayImage(userImageUrl, backHomeProfile, options);
    }

    private void logoutAlear() {
        final Dialog dialog = new Dialog(ServerHomeActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setContentView(R.layout.logout_dailog);
        dialog.setCancelable(false);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));


        dialog.show();

        TextView tvNo = (TextView) dialog.findViewById(R.id.tvNo);
        // if decline button is clicked, close the custom dialog
        tvNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close dialog
                dialog.dismiss();
            }
        });
        TextView tvYes = (TextView) dialog.findViewById(R.id.tvYes);
        // if decline button is clicked, close the custom dialog
        tvYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close dialog
                dialog.dismiss();
                preferenceSettings.clearSession();
                Intent intent = new Intent(ServerHomeActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        dialog.getWindow().getAttributes().windowAnimations =
                R.style.dialog_animation;

        dialog.show();
    }


    public void setProfileBack(String userImageUrl, ImageView backHomeProfile) {

        com.nostra13.universalimageloader.core.ImageLoader imageLoader = TipperActivity.getInstance().getImageLoader();
        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisk(true).resetViewBeforeLoading(true)
                .showImageForEmptyUri(R.color.trasprantblack)
                .showImageOnFail(R.color.trasprantblack)
                .showImageOnLoading(R.color.trasprantblack)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        //download and display image from url
        imageLoader.displayImage(userImageUrl, backHomeProfile, options);
    }

    void getServerDetails(final int type) {
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);
        pDialog.show();
        if (type == 1) {
            pDialog.dismiss();
        }
        Type = type;
        final StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST, AppUrl.URL_REVIEWHISTORY, new Response.Listener<String>() {
            @Override
            public void onResponse(String Response) {
                Log.e("Place Response", Response);
                try {
                    JSONObject jsonObj = new JSONObject(Response);
                    ReviewHistoryItem reviewHistoryItem = new ReviewHistoryItem();

                    try {
                        reviewHistoryItem.setStatus_code(jsonObj.getInt(Jsonkey.statusCode));
                    } catch (JSONException e) {
                        reviewHistoryItem.setStatus_code(0);

                    }
                    reviewHistoryItem.setMessage(jsonObj.getString(Jsonkey.message));
                    String page = jsonObj.optString(Jsonkey.page);

                    if (reviewHistoryItem.getStatus_code() == 1) {
                        pages = page;
                        JSONObject jsonObjectOutlet = jsonObj.getJSONObject(Jsonkey.historyKey);

                        reviewHistoryItem.setTip(jsonObjectOutlet.optString(Jsonkey.tip));
                        reviewHistoryItem.setMenu_knowledge(jsonObjectOutlet.optString(Jsonkey.menu_knowledge));
                        reviewHistoryItem.setHospitality(jsonObjectOutlet.optString(Jsonkey.hospitality));
                        reviewHistoryItem.setAppearance(jsonObjectOutlet.optString(Jsonkey.appearance));
                        reviewHistoryItem.setAverage(jsonObjectOutlet.optString(Jsonkey.average));
                        reviewHistoryItem.setTotal_review(jsonObjectOutlet.optString(Jsonkey.total_review));
                        reviewHistoryItem.setTotal_tip(jsonObjectOutlet.optString(Jsonkey.total_tip));
                        reviewHistoryItem.setServerName(jsonObjectOutlet.optString(Jsonkey.serverName));
                        reviewHistoryItem.setServerImage(jsonObjectOutlet.optString(Jsonkey.serverImage));
                        reviewHistoryItem.setRestaurantName(jsonObjectOutlet.optString(Jsonkey.restaurantName));
                        reviewHistoryItem.setRestaurantAddress(jsonObjectOutlet.optString(Jsonkey.restaurantAddress));
                        reviewHistoryItem.setRestaurantId(jsonObjectOutlet.optString(Jsonkey.restaurantId));
                        try {
                            JSONObject Rating = jsonObj.getJSONObject(Jsonkey.ratingcount);
                            reviewHistoryItem.setReviewItem(Rating);
                        } catch (JSONException e) {
                            reviewHistoryItem.setReviewItem(null);
                        }
                        try {
                            JSONArray reviewdetails = jsonObj.getJSONArray(Jsonkey.reviewdetails);
                            reviewHistoryItem.setUserReviewDetailsItemses(reviewdetails);
                        } catch (JSONException e) {
                            reviewHistoryItem.setReviewItem(null);
                        }
                        if (load) {
                            listItems = reviewHistoryItem.getUserReviewDetailsItemses();
                            if (listItems != null && listItems.size() > 0) {
                                addNewData(listItems);
                                load = false;
                            } else {
                                load = true;
                            }
                            loadMore.setVisibility(View.GONE);
                        } else {
                            loadMore.setVisibility(View.GONE);
                            setServerDetails(reviewHistoryItem);
                        }
                        pDialog.dismiss();
                    } else if (reviewHistoryItem.getStatus_code() == 0) {
                        rvHomeReviewList.setVisibility(View.GONE);
                        llNoDataAvail.setVisibility(View.VISIBLE);
                        Log.e("Error_in", "else");
                        pDialog.dismiss();
                        CommonUtils.showToast(reviewHistoryItem.getMessage());
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
                params.put(ParamsKey.KeyserverId, preferenceSettings.getUserId());
                params.put(ParamsKey.KeyRegistrationType, Constants.TYPE_SERVER);
                if (type == 2) {
                    params.put(ParamsKey.page, "0");
                } else {
                    params.put(ParamsKey.page, pages);
                }

                Log.e("search", "Posting params: " + params.toString());
                return params;

            }
        };


        // Adding request to request queue

        TipperActivity.getInstance().addToRequestQueue(jsonObjectRequest, "review");
    }

    public void addNewData(ArrayList<UserReviewDetailsItems> listItems) {
        for (int i = 0; i < listItems.size(); i++) {
            userReviewDetailsItems.add(listItems.get(i));
            serverHomeReviewListAdapter.notifyItemInserted(userReviewDetailsItems.size());
        }
    }

    public void showTryAgainAlert(String title, String message) {
        CommonUtils.showAlertWithNegativeButton(ServerHomeActivity.this, title, message, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                if (CommonUtils.isNetworkAvailable())
                    getServerDetails(0);
                else
                    CommonUtils.showToast("Please check your internet connection");
            }
        });

    }

    public void setServerDetails(final ReviewHistoryItem serverDetails) {
        resturantId = serverDetails.getRestaurantId();
        RatingView serverHomeRatingBar = (RatingView) findViewById(R.id.serverHomeRatingBar);
        TextView serverHomeTip = (TextView) findViewById(R.id.serverHomeTip);
        TextView serverHomeRating = (TextView) findViewById(R.id.serverHomeRating);
        TextView serverHomeTotalRating = (TextView) findViewById(R.id.serverHomeTotalRating);
        LinearLayout llValueBar = (LinearLayout) findViewById(R.id.llValueBar);
        LinearLayout llNoDataAvail = (LinearLayout) findViewById(R.id.llNoDataAvail);
        mValueBars[0] = (ValueBar) findViewById(R.id.valueBar1);
        mValueBars[1] = (ValueBar) findViewById(R.id.valueBar2);
        mValueBars[2] = (ValueBar) findViewById(R.id.valueBar3);
        mValueBars[3] = (ValueBar) findViewById(R.id.valueBar4);

        serverHomeRatingBar.setEnabled(false);
        if (serverDetails.getAverage() != null && !serverDetails.getAverage().equalsIgnoreCase("")) {
            float rating = Float.parseFloat(serverDetails.getAverage());
            serverHomeRatingBar.setRating(rating);
        } else {
            serverHomeRatingBar.setRating(0);
        }

        HomeserverName.setText(CommonUtils.getCapitalize(serverDetails.getServerName()));
        preferenceSettings.setFullName(CommonUtils.getCapitalize(serverDetails.getServerName()));
        if (serverDetails.getTip() != null && !serverDetails.getTip().equalsIgnoreCase("")) {
            if (serverDetails.getTip().toString().length() == 4) {
                String x = serverDetails.getTip();
                x = x.substring(0, 1) + "," + x.substring(1, x.length());
                serverHomeTip.setText(x + " $");
            } else if (serverDetails.getTip().toString().length() > 4) {
                String x = serverDetails.getTip();
                x = x.substring(0, 2) + "," + x.substring(2, x.length());
                serverHomeTip.setText(x + " $");
            } else {
                serverHomeTip.setText(serverDetails.getTip() + " $");
            }
        } else {
            serverHomeTip.setText("0 $");
        }
        if (serverDetails.getAverage() != null && !serverDetails.getAverage().equalsIgnoreCase("")) {
            if (serverDetails.getAverage().length() == 1) {
                serverHomeRating.setText(serverDetails.getAverage() + ".0");
            } else {
                serverHomeRating.setText(serverDetails.getAverage());
            }
        } else {
            serverHomeRating.setText("0");
        }
        if (serverDetails.getTotal_review() != null && !serverDetails.getTotal_review().equalsIgnoreCase("")) {
            if (serverDetails.getTotal_review().toString().length() == 4) {
                String x = serverDetails.getTotal_review();
                x = x.substring(0, 1) + "," + x.substring(1, x.length());
                serverHomeTotalRating.setText(x);
            } else if (serverDetails.getTotal_review().toString().length() > 4) {
                String x = serverDetails.getTotal_review();
                x = x.substring(0, 2) + "," + x.substring(2, x.length());
                serverHomeTotalRating.setText(x);
            } else {
                serverHomeTotalRating.setText(serverDetails.getTotal_review());
            }
        } else {
            serverHomeTotalRating.setText("0");
        }

        setProfileImage(serverDetails.getServerImage(), imageProfilePic);
        setProfileBack(serverDetails.getServerImage(), backHomeProfile);
        setProfileImage(serverDetails.getServerImage(), homeServerPic);

        if (serverDetails.getReviewItem() != null && serverDetails.getReviewItem().size() > 0 && Type != 2) {
            llValueBar.setVisibility(View.VISIBLE);
            setServerReview(serverDetails.getReviewItem());
        }
        if (serverDetails.getUserReviewDetailsItemses() != null && serverDetails.getUserReviewDetailsItemses().size() > 0) {
            setUserReviewList(serverDetails.getUserReviewDetailsItemses());
        } else {
            llNoDataAvail.setVisibility(View.VISIBLE);
            rvHomeReviewList.setVisibility(View.GONE);
        }

    }

    void setServerReview(ArrayList<RatingItem> resultView) {

        for (RatingItem ratingItem : resultView) {
            for (ValueBar bar : mValueBars) {
                bar.setMinMax(0, 100);
                bar.setInterval(1f);
                bar.setClickable(false);
                bar.setSelected(false);
                bar.setDrawBorder(false);
                bar.setEnabled(false);
                bar.setTouchEnabled(false);
                bar.setValueBarSelectionListener(this);
                bar.setValueTextFormatter(new MyCustomValueTextFormatter());
                bar.setColorFormatter(new RedToGreenFormatter());
                bar.setOverlayColor(Color.BLACK);
                bar.setDrawMinMaxText(bar.isDrawMinMaxTextEnabled() ? false : true);
                bar.setDrawValueText(bar.isDrawValueTextEnabled() ? false : true);
                bar.invalidate();
            }
            mValueBars[0].animate(0, Float.parseFloat(ratingItem.getRating1()), 1500);
            mValueBars[1].animate(0, Float.parseFloat(ratingItem.getRating2()), 1500);
            mValueBars[2].animate(0, Float.parseFloat(ratingItem.getRating3()), 1500);
            mValueBars[3].animate(0, Float.parseFloat(ratingItem.getRating4()), 1500);
            TextView valueBar1Total = (TextView) findViewById(R.id.valueBar1Total);
            TextView valueBar2Total = (TextView) findViewById(R.id.valueBar2Total);
            TextView valueBar3Total = (TextView) findViewById(R.id.valueBar3Total);
            TextView valueBar4Total = (TextView) findViewById(R.id.valueBar4Total);
            valueBar1Total.setText(ratingItem.getRating1());
            valueBar2Total.setText(ratingItem.getRating2());
            valueBar3Total.setText(ratingItem.getRating3());
            valueBar4Total.setText(ratingItem.getRating4());
        }
    }

    void setUserReviewList(ArrayList<UserReviewDetailsItems> resturantServeritems) {
        if (resturantServeritems != null && resturantServeritems.size() > 0) {
            rvHomeReviewList.setVisibility(View.VISIBLE);
            llNoDataAvail.setVisibility(View.GONE);
            userReviewDetailsItems = resturantServeritems;
            if (serverHomeReviewListAdapter == null) {
                rvHomeReviewList.setVisibility(View.VISIBLE);
                rvHomeReviewList.setHasFixedSize(true);
                rvHomeReviewList.setItemAnimator(new DefaultItemAnimator());
                rvHomeReviewList.setLayoutManager(new LinearLayoutManager(ServerHomeActivity.this));
                serverHomeReviewListAdapter = new ServerHomeReviewListAdapter(ServerHomeActivity.this, resturantServeritems, onClick, onclickReview);
                rvHomeReviewList.setAdapter(serverHomeReviewListAdapter);
            } else {
                serverHomeReviewListAdapter.modifyDataSet(resturantServeritems);
            }
        } else {
            if (serverHomeReviewListAdapter != null) {
                serverHomeReviewListAdapter.modifyDataSet(resturantServeritems);
            } else {
                rvHomeReviewList.setVisibility(View.GONE);
                llNoDataAvail.setVisibility(View.VISIBLE);
            }
        }
    }

    View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (int) v.getTag();
            UserReviewDetailsItems reviewHistoryItem = userReviewDetailsItems.get(position);
            sendReplay(reviewHistoryItem.getReviweId());

        }
    };
    View.OnClickListener onclickReview = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (int) v.getTag();
            UserReviewDetailsItems reviewHistoryItem = userReviewDetailsItems.get(position);
            String review = reviewHistoryItem.getReview();
            String replay = reviewHistoryItem.getServerReplayDesc();
            String knowledge = reviewHistoryItem.getKnowledge();
            String hospitality = reviewHistoryItem.getHospitality();
            String apperence = reviewHistoryItem.getAppereance();
            String tips = reviewHistoryItem.getTips();
            showReview(knowledge, hospitality, apperence, tips, review, replay);

        }
    };

    public void showReview(final String Knowledge, final String hospitality, final String appereance, final String tips, final String review, final String replay) {
        final Dialog dialog = new Dialog(ServerHomeActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.userreview_dailog);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));

        dialog.getWindow().getAttributes().windowAnimations =
                R.style.dialog_animation;

        RatingView menuKnowledgeCount, hospitalityCount, appereanceCount;
        TextView tvKnowledge, tvHospitality, tvApperance, tvReview, tvReplay, tvTips;

        menuKnowledgeCount = (RatingView) dialog.findViewById(R.id.menuKnowledgeCount);
        hospitalityCount = (RatingView) dialog.findViewById(R.id.hospitalityCount);
        appereanceCount = (RatingView) dialog.findViewById(R.id.appereanceCount);
        tvKnowledge = (TextView) dialog.findViewById(R.id.tvKnowledge);
        tvHospitality = (TextView) dialog.findViewById(R.id.tvHospitality);
        tvApperance = (TextView) dialog.findViewById(R.id.tvApperance);
        tvReview = (TextView) dialog.findViewById(R.id.tvReview);
        tvReplay = (TextView) dialog.findViewById(R.id.tvReplay);
        tvTips = (TextView) dialog.findViewById(R.id.tvTips);
        if (Knowledge != null && !Knowledge.equalsIgnoreCase("")) {
            float rating1 = Float.parseFloat(Knowledge);
            menuKnowledgeCount.setRating(rating1);
            tvKnowledge.setText(Knowledge);
        } else {
            tvKnowledge.setText("0");
        }
        if (hospitality != null && !hospitality.equalsIgnoreCase("")) {
            float rating2 = Float.parseFloat(hospitality);
            hospitalityCount.setRating(rating2);
            tvHospitality.setText(hospitality);
        } else {
            tvHospitality.setText("0");
        }
        if (appereance != null && !appereance.equalsIgnoreCase("")) {
            float rating3 = Float.parseFloat(appereance);
            appereanceCount.setRating(rating3);
            tvApperance.setText(appereance);
        } else {
            tvApperance.setText("0");
        }
        if (review != null && !replay.equalsIgnoreCase("")) {
            tvReview.setText(Html.fromHtml("&ldquo; " + review + " &rdquo;"));
        }
        if (replay != null && !replay.equalsIgnoreCase("")) {
            tvReplay.setText(Html.fromHtml("&ldquo; " + replay + " &rdquo;"));
        }
        if (tips != null && !tips.equalsIgnoreCase("")) {
            tvTips.setText("$ " + tips);
        } else {
            tvTips.setText("$ 0");
        }

        dialog.show();
    }

    void addReview(final String reviewId, final String review) {
        final StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST, AppUrl.URL_SENDREPLAY, new Response.Listener<String>() {
            @Override
            public void onResponse(String Response) {
                Log.e("Response", Response);
                try {
                    JSONObject jsonObj = new JSONObject(Response);
                    String status_code = jsonObj.optString("status_code");
                    String massage = jsonObj.optString("message");
                    if (status_code.equalsIgnoreCase("1")) {
                        CommonUtils.showToast(massage);
                        getServerDetails(2);
                    } else if (status_code.equalsIgnoreCase("0")) {
                        Log.e("Error_in", "else");
                        CommonUtils.showToast(massage);
                    } else {
                        Log.e("Error_in", "else");
                    }
                } catch (JSONException e) {
                    Log.e("Error_in", "onErrorResponse");

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error_in", "onErrorResponse");
                Log.e("Place", "Error Response: " + error.getMessage());
            }
        })

        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(ParamsKey.reviewId, reviewId);
                params.put(ParamsKey.KeyReplay, review);
                Log.e("search", "Posting params: " + params.toString());
                return params;

            }
        };


        // Adding request to request queue

        TipperActivity.getInstance().addToRequestQueue(jsonObjectRequest, "review");
    }

    public void sendReplay(final String ReviewId) {
        final Dialog dialog = new Dialog(ServerHomeActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.send_reviewdialog);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        final EditText replay = (EditText) dialog.findViewById(R.id.replay);

        TextView tvSend = (TextView) dialog.findViewById(R.id.tvSend);
        TextView tvCancel = (TextView) dialog.findViewById(R.id.tvCancle);

        dialog.getWindow().getAttributes().windowAnimations =
                R.style.dialog_animation;

        tvSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String stReplay = replay.getText().toString();
                if (stReplay.equals("")) {
                    replay.setError("Please Enter proper replay");
                } else {
                    addReview(ReviewId, stReplay);
                    dialog.dismiss();
                }
            }
        });
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        if (i == (-maxScroll)) {
            tv_toolbarName.setVisibility(View.VISIBLE);
            tv_toolbarName.setText("My Reviews");
        } else {
            tv_toolbarName.setVisibility(View.GONE);
        }
    }

    @Override
    public void onSelectionUpdate(float v, float v1, float v2, ValueBar valueBar) {

    }

    @Override
    public void onValueSelected(float v, float v1, float v2, ValueBar valueBar) {
        valueBar.setClickable(false);
        valueBar.setFocusable(false);
        valueBar.setTouchEnabled(false);
    }

    private class ServerHomeReviewListAdapter extends RecyclerView.Adapter implements View.OnClickListener {
        Context context;
        ArrayList<UserReviewDetailsItems> reviewDetailsItemses;
        View.OnClickListener onClickListener;
        View.OnClickListener onclick;
        PreferenceSettings preferenceSettings;

        public ServerHomeReviewListAdapter(Context context, ArrayList<UserReviewDetailsItems> reviewDetailsItemses, View.OnClickListener onClickListener, View.OnClickListener onclick) {
            this.context = context;
            this.onClickListener = onClickListener;
            this.onclick = onclick;
            this.reviewDetailsItemses = reviewDetailsItemses;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.custom_serverreviewlist_activity, parent, false);
            RecyclerView.ViewHolder myViewHolder = new MyViewHolder(view);
            preferenceSettings = TipperActivity.getInstance().getPreferenceSettings();

            return myViewHolder;

        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            MyViewHolder viewHolder = (MyViewHolder) holder;
            final UserReviewDetailsItems userReviewDetailsItems = reviewDetailsItemses.get(position);
            if (reviewDetailsItemses.size() == position + 1 && !load && reviewDetailsItemses.size() > 5) {
                loadMore.setVisibility(View.VISIBLE);
                load = true;
                getServerDetails(1);

            }
            viewHolder.userName.setText(CommonUtils.getCapitalize(userReviewDetailsItems.getUserName()));
            viewHolder.date.setText(CommonUtils.getFormatedDate(userReviewDetailsItems.getDate(), "dd-MMM-yyyy"));
            if (userReviewDetailsItems.getReview() != null && !userReviewDetailsItems.getReview().equalsIgnoreCase("")) {
                viewHolder.userReviewDesc.setText(Html.fromHtml("&ldquo; " + CommonUtils.getCapitalize(userReviewDetailsItems.getReview()) + " &rdquo;"));
            } else {
                viewHolder.userReviewDesc.setVisibility(View.VISIBLE);

            }
            if (userReviewDetailsItems.getServerReplayDesc() != null && !userReviewDetailsItems.getServerReplayDesc().equalsIgnoreCase("")) {
                viewHolder.llReplay.setVisibility(View.GONE);
                viewHolder.llReview.setVisibility(View.VISIBLE);
                viewHolder.replayServerName.setText(preferenceSettings.getFullName());
                viewHolder.ServerReview.setText(Html.fromHtml("&ldquo; " + CommonUtils.getCapitalize(userReviewDetailsItems.getServerReplayDesc()) + " &rdquo;"));
            } else {
                viewHolder.llReview.setVisibility(View.GONE);
                viewHolder.llReplay.setVisibility(View.VISIBLE);
            }
            viewHolder.userRatingCount.setEnabled(false);
            if (userReviewDetailsItems.getAvarage() != null && !userReviewDetailsItems.getAvarage().equalsIgnoreCase("")) {
                float rating = Float.parseFloat(userReviewDetailsItems.getAvarage());
                viewHolder.userRatingCount.setRating(rating);
            }
            TipperActivity.getInstance().getImageLoader().displayImage(userReviewDetailsItems.getUserImage(), viewHolder.userReviewImage, getDisplayImageOptions());
            viewHolder.rlUserReviewList.setTag(position);
            viewHolder.rlUserReviewList.setOnClickListener(onclick);
            viewHolder.ivReplay.setTag(position);
            viewHolder.ivReplay.setOnClickListener(onClickListener);
        }

        @Override
        public int getItemCount() {
            return reviewDetailsItemses.size();
        }

        @Override
        public void onClick(View view) {

        }


        class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView userName, date, userReviewDesc, replayServerName, ServerReview;
            public ImageView userReviewImage, ivReplay;
            public RatingView userRatingCount;
            public RelativeLayout rlUserReviewList;
            public LinearLayout llReview, llReplay;

            public MyViewHolder(View v) {
                super(v);

                userName = (TextView) v.findViewById(R.id.userName);
                replayServerName = (TextView) v.findViewById(R.id.replayServerName);
                ServerReview = (TextView) v.findViewById(R.id.ServerReview);
                date = (TextView) v.findViewById(R.id.date);
                userReviewImage = (ImageView) v.findViewById(R.id.userReviewImage);
                ivReplay = (ImageView) v.findViewById(R.id.ivReplay);
                userReviewDesc = (TextView) v.findViewById(R.id.userReviewDesc);
                userRatingCount = (RatingView) v.findViewById(R.id.userRatingCount);
                rlUserReviewList = (RelativeLayout) v.findViewById(R.id.rlUserReviewList);
                llReview = (LinearLayout) v.findViewById(R.id.llReview);
                llReplay = (LinearLayout) v.findViewById(R.id.llReplay);
            }
        }

        public void modifyDataSet(ArrayList<UserReviewDetailsItems> reviewDetailsItemses) {
            this.reviewDetailsItemses = reviewDetailsItemses;
            this.notifyDataSetChanged();
        }

        public DisplayImageOptions getDisplayImageOptions() {
            DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                    .cacheOnDisk(true).resetViewBeforeLoading(true)
                    .showImageForEmptyUri(R.drawable.logo)
                    .showImageOnFail(R.drawable.logo)
                    .showImageOnLoading(R.drawable.logo)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .considerExifParams(true)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .displayer(new RoundedBitmapDisplayer(100))
                    .build();
            return options;
        }
    }

    @Override
    public void onBackPressed() {
        if (backPress == false) {
            backPress = true;
            CommonUtils.Toast("Press again to exit");
        } else {
            finish();
        }
    }
}
