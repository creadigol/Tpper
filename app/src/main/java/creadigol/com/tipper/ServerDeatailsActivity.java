package creadigol.com.tipper;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.creadigol.admin.library.CirclePageIndicator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.philjay.valuebar.ValueBar;
import com.philjay.valuebar.ValueBarSelectionListener;
import com.philjay.valuebar.colors.RedToGreenFormatter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import Adapter.ViewPagerAdapter;
import Fragments.FirstRatingFragment;
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
 * Created by ravi on 04-04-2017.
 */

public class ServerDeatailsActivity extends AppCompatActivity implements ValueBarSelectionListener, AppBarLayout.OnOffsetChangedListener {
    RecyclerView serverReviewList;
    public static final String EXTRA_KEY_SERVERID = "serverId";
    ImageView serverProfilePic, BackprofilePic;
    TextView toolbarName;
    private ValueBar[] mValueBars = new ValueBar[4];
    PreferenceSettings preferenceSettings;
    String serverId;
    LinearLayout loadMore;
    boolean load = false;
    Button btnPayTip;
    String pages = "0";
    ArrayList<UserReviewDetailsItems> userReviewDetailsItemses;
    ArrayList<UserReviewDetailsItems> listItems;
    ServerDetailsReviewListAdapter serverDetailsReviewListAdapter;
    public static String sId, menuKnowledge = "0", hospitality = "0", appearance = "0", addRating = "0";
    public static boolean isNeeded = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.serverdetails_activity);
        preferenceSettings = TipperActivity.getInstance().getPreferenceSettings();
        Toolbar();
        loadMore = (LinearLayout) findViewById(R.id.loadMore);
        btnPayTip = (Button) findViewById(R.id.btnPayTip);
        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_server);
        collapsingToolbar.setExpandedTitleGravity(View.TEXT_ALIGNMENT_CENTER);
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar_place_server);
        appBarLayout.addOnOffsetChangedListener(this);
        try {
            Bundle extras = getIntent().getExtras();
            serverId = extras.getString(EXTRA_KEY_SERVERID);
            sId = serverId;
            getServerDetails(0);
        } catch (Exception e) {
            Log.e("Exception", " " + e);
        }
        if (preferenceSettings.getLoginType() != null && preferenceSettings.getLoginType().equalsIgnoreCase(Constants.TYPE_SERVER)) {
            btnPayTip.setVisibility(View.GONE);
        } else {
            btnPayTip.setVisibility(View.VISIBLE);
        }
        btnPayTip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ServerDeatailsActivity.this, PayTipsActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isNeeded == true) {
            getServerDetails(2);
        }
    }

    public void Toolbar() {
        Toolbar toolbarRestDetails = (Toolbar) findViewById(R.id.toolbarservDetails);
        toolbarName = (TextView) toolbarRestDetails.findViewById(R.id.tv_toolbarName);
        toolbarName.setText("Ratings & Reviews");
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

    public void getServerDetails(final int type) {

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);
        pDialog.show();
        if (type == 1) {
            pDialog.dismiss();
        }
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
                        reviewHistoryItem.setUserReview(jsonObjectOutlet.optString(Jsonkey.userReview));

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
                            userReviewDetailsItemses = reviewHistoryItem.getUserReviewDetailsItemses();
                            setServerDetails(reviewHistoryItem);
                        }
                        pDialog.dismiss();
                    } else if (reviewHistoryItem.getStatus_code() == 0) {
                        Log.e("Error_in", "else");
                        pDialog.dismiss();
                        Toast.makeText(ServerDeatailsActivity.this, "" + reviewHistoryItem.getMessage(), Toast.LENGTH_SHORT).show();
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
                params.put(ParamsKey.KeyUserid, preferenceSettings.getUserId());
                params.put(ParamsKey.KeyserverId, serverId);
                params.put(ParamsKey.KeyRegistrationType, Constants.TYPE_SERVER);
                if (type == 2) {
                    params.put(ParamsKey.page, "");
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
            userReviewDetailsItemses.add(listItems.get(i));
            serverDetailsReviewListAdapter.notifyItemInserted(userReviewDetailsItemses.size());
        }
    }

    public void showTryAgainAlert(String title, String message) {
        CommonUtils.showAlertWithNegativeButton(ServerDeatailsActivity.this, title, message, new DialogInterface.OnClickListener() {
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
        serverProfilePic = (ImageView) findViewById(R.id.serverProfilePic);
        BackprofilePic = (ImageView) findViewById(R.id.BackprofilePic);
        serverReviewList = (RecyclerView) findViewById(R.id.serverReviewList);
        setServerImage(serverDetails.getServerImage(), serverProfilePic);
        setServerBack(serverDetails.getServerImage(), BackprofilePic);
        TextView serverName = (TextView) findViewById(R.id.serverName);
        TextView serverTotalTip = (TextView) findViewById(R.id.serverTotalTip);
        TextView serverResturantName = (TextView) findViewById(R.id.serverResturantName);
        TextView serverResAdddress = (TextView) findViewById(R.id.serverResAdddress);
        TextView serverDetailsRating = (TextView) findViewById(R.id.serverDetailsRating);
        TextView serverDetTotalRating = (TextView) findViewById(R.id.serverDetTotalRating);
        RatingView serverDetRatingBar = (RatingView) findViewById(R.id.serverDetRatingBar);
        LinearLayout llValuesBardetials = (LinearLayout) findViewById(R.id.llValuesBardetials);
        LinearLayout llRatingBarDetails = (LinearLayout) findViewById(R.id.llRatingBarDetails);
        LinearLayout llNoDataFound = (LinearLayout) findViewById(R.id.llNodataFound);

        mValueBars[0] = (ValueBar) findViewById(R.id.valueBar1);
        mValueBars[1] = (ValueBar) findViewById(R.id.valueBar2);
        mValueBars[2] = (ValueBar) findViewById(R.id.valueBar3);
        mValueBars[3] = (ValueBar) findViewById(R.id.valueBar4);
        serverDetRatingBar.setEnabled(false);
        if (serverDetails.getAverage() != null && !serverDetails.getAverage().equalsIgnoreCase("")) {
            float rating = Float.parseFloat(serverDetails.getAverage());
            serverDetRatingBar.setRating(rating);
        }

        serverName.setText(CommonUtils.getCapitalize(serverDetails.getServerName()));
        if (serverDetails.getTip() != null && !serverDetails.getTip().equalsIgnoreCase("")) {
            if (serverDetails.getTip().toString().length() == 4) {
                String x = serverDetails.getTip();
                x = x.substring(0, 1) + "," + x.substring(1, x.length());
                serverTotalTip.setText(x + " $");
            } else if (serverDetails.getTip().toString().length() > 4) {
                String x = serverDetails.getTip();
                x = x.substring(0, 2) + "," + x.substring(2, x.length());
                serverTotalTip.setText(x + " $");
            } else {
                serverTotalTip.setText(serverDetails.getTip() + " $");
            }
        } else {
            serverTotalTip.setText("0 $");
        }
        serverResturantName.setText(CommonUtils.getCapitalize(serverDetails.getRestaurantName()));
        serverResAdddress.setText(CommonUtils.getCapitalize(serverDetails.getRestaurantAddress()));
        if (serverDetails.getAverage() != null && !serverDetails.getAverage().equalsIgnoreCase("")) {
            if (serverDetails.getAverage().length() == 1) {
                serverDetailsRating.setText(serverDetails.getAverage() + ".0");
            } else {
                serverDetailsRating.setText(serverDetails.getAverage());
            }

        } else {
            serverDetailsRating.setText("0");
        }
        if (serverDetails.getTotal_review() != null && !serverDetails.getTotal_review().equalsIgnoreCase("")) {
            llRatingBarDetails.setVisibility(View.VISIBLE);
            if (serverDetails.getTotal_review().toString().length() == 4) {
                String x = serverDetails.getTotal_review();
                x = x.substring(0, 1) + "," + x.substring(1, x.length());
                serverDetTotalRating.setText(x);
            } else if (serverDetails.getTotal_review().toString().length() > 4) {
                String x = serverDetails.getTotal_review();
                x = x.substring(0, 2) + "," + x.substring(2, x.length());
                serverDetTotalRating.setText(x);
            } else {
                serverDetTotalRating.setText(serverDetails.getTotal_review());
            }
        } else {
            llRatingBarDetails.setVisibility(View.GONE);
        }

        if (serverDetails.getReviewItem() != null && serverDetails.getReviewItem().size() > 0) {
            llValuesBardetials.setVisibility(View.VISIBLE);
            setServerReview(serverDetails.getReviewItem());
        }
        if (serverDetails.getUserReviewDetailsItemses() != null && serverDetails.getUserReviewDetailsItemses().size() > 0) {
            serverReviewList.setVisibility(View.VISIBLE);
            serverReviewList.setNestedScrollingEnabled(false);
            llNoDataFound.setVisibility(View.GONE);
            setUserReviewList(serverDetails.getUserReviewDetailsItemses());
        } else {
            serverReviewList.setVisibility(View.GONE);
            loadMore.setVisibility(View.GONE);
            llNoDataFound.setVisibility(View.VISIBLE);
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
//            mValueBars[0].animate(0, 30, 1500);
            mValueBars[1].animate(0, Float.parseFloat(ratingItem.getRating2()), 1500);
//            mValueBars[1].animate(0, 50, 1500);
            mValueBars[2].animate(0, Float.parseFloat(ratingItem.getRating3()), 1500);
//            mValueBars[2].animate(0, 70, 1500);
            mValueBars[3].animate(0, Float.parseFloat(ratingItem.getRating4()), 1500);
//            mValueBars[3].animate(0, 90, 1500);
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

            if (serverDetailsReviewListAdapter == null) {
                serverReviewList.setVisibility(View.VISIBLE);
                serverReviewList.setHasFixedSize(true);
                serverReviewList.setItemAnimator(new DefaultItemAnimator());
                serverReviewList.setLayoutManager(new LinearLayoutManager(ServerDeatailsActivity.this));
                serverDetailsReviewListAdapter = new ServerDetailsReviewListAdapter(ServerDeatailsActivity.this, resturantServeritems);
                serverReviewList.setAdapter(serverDetailsReviewListAdapter);
            } else {
                serverDetailsReviewListAdapter.modifyDataSet(resturantServeritems);
            }
        } else {
            if (serverDetailsReviewListAdapter != null) {
                serverDetailsReviewListAdapter.modifyDataSet(resturantServeritems);
            }
        }
    }

    public void setServerImage(String serverImageUrl, ImageView imageView) {

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
        imageLoader.displayImage(serverImageUrl, imageView, options);
    }

    public void setServerBack(String serverImageUrl, ImageView imageView) {

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
        imageLoader.displayImage(serverImageUrl, imageView, options);
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


    public class ServerDetailsReviewListAdapter extends RecyclerView.Adapter implements View.OnClickListener {
        Context context;
        ArrayList<UserReviewDetailsItems> reviewDetailsItemses;
        View.OnClickListener onClickListener;


        public ServerDetailsReviewListAdapter(Context context, ArrayList<UserReviewDetailsItems> reviewDetailsItemses) {
            this.context = context;
            this.reviewDetailsItemses = reviewDetailsItemses;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.customuserreviewlist_activity, parent, false);
            RecyclerView.ViewHolder myViewHolder = new MyViewHolder(view);
            return myViewHolder;

        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            MyViewHolder viewHolder = (MyViewHolder) holder;
            final UserReviewDetailsItems userReviewDetailsItems = reviewDetailsItemses.get(position);
            if (reviewDetailsItemses.size() > 5) {
                if (reviewDetailsItemses.size() == position + 1 && !load) {
                    loadMore.setVisibility(View.VISIBLE);
                    load = true;
                    getServerDetails(1);
                }
            }
            viewHolder.userName.setText(CommonUtils.getCapitalize(userReviewDetailsItems.getUserName()));
            viewHolder.date.setText(CommonUtils.getFormatedDate(userReviewDetailsItems.getDate(), "dd-MMM-yyyy"));
            if (userReviewDetailsItems.getReview() != null && !userReviewDetailsItems.getReview().equalsIgnoreCase("")) {
                viewHolder.userReviewDesc.setText(Html.fromHtml("&ldquo; " + CommonUtils.getCapitalize(userReviewDetailsItems.getReview()) + " &rdquo;"));
            } else {
                viewHolder.userReviewDesc.setVisibility(View.GONE);
            }
            viewHolder.userRatingCount.setEnabled(false);
            if (userReviewDetailsItems.getAvarage() != null && !userReviewDetailsItems.getAvarage().equalsIgnoreCase("")) {
                float rating = Float.parseFloat(userReviewDetailsItems.getAvarage());
                viewHolder.userRatingCount.setRating(rating);
            }
            TipperActivity.getInstance().getImageLoader().displayImage(userReviewDetailsItems.getUserImage(), viewHolder.userReviewImage, getDisplayImageOptions());
            viewHolder.rlUserReviewList.setTag(position);
            viewHolder.rlUserReviewList.setOnClickListener(onClickListener);
        }

        @Override
        public int getItemCount() {
            return reviewDetailsItemses.size();
        }

        @Override
        public void onClick(View view) {

        }


        class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView userName, date, userReviewDesc;
            public ImageView userReviewImage;
            public RatingView userRatingCount;
            public RelativeLayout rlUserReviewList;

            public MyViewHolder(View v) {
                super(v);

                userName = (TextView) v.findViewById(R.id.userName);
                date = (TextView) v.findViewById(R.id.date);
                userReviewImage = (ImageView) v.findViewById(R.id.userReviewImage);
                userReviewDesc = (TextView) v.findViewById(R.id.userReviewDesc);
                userRatingCount = (RatingView) v.findViewById(R.id.userRatingCount);
                rlUserReviewList = (RelativeLayout) v.findViewById(R.id.rlUserReviewList);
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
}
