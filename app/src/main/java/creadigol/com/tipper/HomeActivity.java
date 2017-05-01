package creadigol.com.tipper;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import Adapter.ViewPagerAdapter;
import Fragments.OneFragment;
import Fragments.TwoFragment;
import Halper.GPSTracker;
import Model.ResturantListItem;
import Model.ResturantListObject;
import Model.ServerListItem;
import Model.ServerListObject;
import Utils.AppUrl;
import Utils.CommonUtils;
import Utils.Constants;
import Utils.Jsonkey;
import Utils.ParamsKey;
import Utils.PreferenceSettings;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, ViewPager.OnPageChangeListener, SearchView.OnQueryTextListener {
    PreferenceSettings preferenceSettings;
    ImageView imageProfilePic, backHomeProfile;
    private TabLayout tabLayout;
    ArrayList<ResturantListItem> resturantListItems = new ArrayList<>();
    ArrayList<ServerListItem> serverListItems = new ArrayList<>();
    private ViewPager viewPager;
    NavigationView navigationView;
    AlertDialog alert;
    TextView txtEmailId, txtUserName;
    GPSTracker gps;
    boolean resturentSearch = true, backPress = false;
    public static boolean searchData = true;
    private final String TAG = HomeActivity.class.getSimpleName();
    public static String loadServer = "0", loadResturant = "0";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        preferenceSettings = TipperActivity.getInstance().getPreferenceSettings();
        gps = new GPSTracker(HomeActivity.this);


        viewPager = (ViewPager) findViewById(R.id.viewpager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
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
                Intent i = new Intent(HomeActivity.this, UserProfileActivity.class);
                startActivity(i);
                drawer.closeDrawers();

            }
        });
        navigationView.setNavigationItemSelectedListener(this);


        //for marshmallw permission

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            final ArrayList<String> permissionsList = new ArrayList<>();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissionsList.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                permissionsList.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                permissionsList.add(Manifest.permission.CAMERA);
            }

            if (permissionsList != null && permissionsList.size() > 0) {
                final Dialog dialog = new Dialog(HomeActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.permission_dailog);
                dialog.setCancelable(false);
                dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(
                        new ColorDrawable(android.graphics.Color.TRANSPARENT));

                dialog.show();

                TextView permissionNo = (TextView) dialog.findViewById(R.id.permissionNo);
                // if decline button is clicked, close the custom dialog
                permissionNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Close dialog
                        dialog.dismiss();
                        finishAffinity();
                    }
                });
                TextView permissionYes = (TextView) dialog.findViewById(R.id.permissionYes);
                // if decline button is clicked, close the custom dialog
                permissionYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Close dialog
                        dialog.dismiss();
                        requestPermissions(permissionsList.toArray(new String[permissionsList.size()]), 100);
                    }
                });
                dialog.getWindow().getAttributes().windowAnimations =
                        R.style.dialog_animation;

                dialog.show();

            } else {
                EnableGPSIfPossible();
            }
        } else {
            EnableGPSIfPossible();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            final ArrayList<String> permissionsList = new ArrayList<>();
            if (grantResults != null && grantResults.length > 0) {
                for (int i = 0; i < grantResults.length; i++) {
                    Log.e("In Permission", " BottomTabActivity " + permissions[i] + " result " + grantResults[i]);
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        Log.e("sss", "fdg" + grantResults[i]);
                    } else if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        permissionsList.add(permissions[i]);
                    }
                }
            } else {
            }

            if (permissionsList != null && permissionsList.size() > 0) {
                final Dialog dialog = new Dialog(HomeActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.permission_dailog);
                dialog.setCancelable(false);
                dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(
                        new ColorDrawable(android.graphics.Color.TRANSPARENT));

                dialog.show();

                TextView permissionNo = (TextView) dialog.findViewById(R.id.permissionNo);
                // if decline button is clicked, close the custom dialog
                permissionNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Close dialog
                        dialog.dismiss();
                        finishAffinity();
                    }
                });
                TextView permissionYes = (TextView) dialog.findViewById(R.id.permissionYes);
                // if decline button is clicked, close the custom dialog
                permissionYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Close dialog
                        dialog.dismiss();
                        requestPermissions(permissionsList.toArray(new String[permissionsList.size()]), 100);
                    }
                });
                dialog.getWindow().getAttributes().windowAnimations =
                        R.style.dialog_animation;

                dialog.show();

            } else {
                getResturantList(0);
                getServerList(0);
            }
        } else {
            android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(this);
            alert.setTitle("Sorry");
            alert.setCancelable(false);
            alert.setMessage("Please give all permision ");
            alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 100);
                }
            });

            alert.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    finishAffinity();
                }
            });
            alert.show();
        }
    }

    public void canclePreviousSearchRequest() {
        TipperActivity.getInstance().cancelPendingRequests(TAG);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (backPress == false) {
                backPress = true;
                CommonUtils.Toast("Press again to exit");
            } else {
                finish();
            }
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new OneFragment(HomeActivity.this, resturantListItems), "Restaurants");
        adapter.addFragment(new TwoFragment(HomeActivity.this, serverListItems), "Servers");

        viewPager.setAdapter(adapter);
        new TabLayout.TabLayoutOnPageChangeListener(tabLayout);
        viewPager.addOnPageChangeListener(this);
    }

    private void EnableGPSIfPossible() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        } else {
            getResturantList(0);
            getServerList(0);
        }
    }

    public void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("info");
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                        getResturantList(0);
                        getServerList(0);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                        getResturantList(0);
                        getServerList(0);
                    }
                });
        alert = builder.create();
        alert.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        navigationView.getMenu().getItem(0).setChecked(true);
        setProfileImage(preferenceSettings.getUserPic(), imageProfilePic);
        setProfileBack(preferenceSettings.getUserPic(), backHomeProfile);
        txtUserName.setText(CommonUtils.getCapitalize(preferenceSettings.getFullName()));
        txtEmailId.setText(preferenceSettings.getEmailId());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_Home) {

        } else if (id == R.id.nav_Logout) {
            showAlear();
        } else if (id == R.id.nav_MyReview) {
            Intent intent = new Intent(HomeActivity.this, UserReviewListActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_About) {
            Intent intent = new Intent(HomeActivity.this, AboutusActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_share) {
//            Intent sendIntent = new Intent();
//            sendIntent.setAction(Intent.ACTION_SEND);
//            sendIntent.putExtra(Intent.EXTRA_TEXT, "Tipper-Android Apps on Google Play");
//            sendIntent.setType("text/plain");
//            sendIntent.putExtra(android.content.Intent.EXTRA_TEXT, "");
//            startActivity(Intent.createChooser(sendIntent, "Share Tipper via"));
            CommonUtils.showToast("Application is not available on playstore ,try next time");
        } else if (id == R.id.nav_Rateus) {
//            Intent intent = new Intent(Intent.ACTION_VIEW,
//                    Uri.parse("https://play.google.com/store/apps/details?id=com.creadigol.inshort"));
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
//            startActivity(intent);
//            finish();
            CommonUtils.showToast("Application is not available on playstore ,try next time");
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showAlear() {

        final Dialog dialog = new Dialog(HomeActivity.this);
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
                preferenceSettings.clearSession();
                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        dialog.getWindow().getAttributes().windowAnimations =
                R.style.dialog_animation;

        dialog.show();
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.nav_view:
                Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                startActivity(intent);
                break;

        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (position == 1) {
            resturentSearch = false;
        } else if (position == 0) {
            resturentSearch = true;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }


    void getResturantList(final int type) {
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);
        pDialog.show();
        if (type == 1 || type == 2) {
            pDialog.dismiss();
        }
        final StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST, AppUrl.URL_FINDRESTURANT, new Response.Listener<String>() {
            @Override
            public void onResponse(String Response) {
                Log.e("Place Response", Response);

                try {
                    JSONObject jsonObj = new JSONObject(Response);
                    ResturantListObject resturantListObject = new ResturantListObject();

                    try {
                        resturantListObject.setStatus_code(jsonObj.getInt(Jsonkey.statusCode));
                    } catch (JSONException e) {
                        resturantListObject.setStatus_code(0);

                    }
                    loadResturant = jsonObj.optString(Jsonkey.page);
                    resturantListObject.setMessage(jsonObj.getString(Jsonkey.message));

                    if (resturantListObject.getStatus_code() == 1) {
                        try {

                            resturantListObject.setResturantListItems(jsonObj.getJSONArray(Jsonkey.resturantData));
                        } catch (JSONException e) {
                            resturantListObject.setResturantListItems(null);
                        }
                        resturantListItems = resturantListObject.getResturantListItems();
                        Collections.sort(resturantListItems, new Comparator<ResturantListItem>() {
                            public int compare(ResturantListItem obj1, ResturantListItem obj2) {
                                // ## Ascending order
                                return obj1.getResturantDistance().compareToIgnoreCase(obj2.getResturantDistance()); // To compare string values
                            }
                        });
                        setupViewPager(viewPager);
                        if (type == 2) {
                            viewPager.setCurrentItem(1, true);
                        }
                        pDialog.dismiss();
                    } else if (resturantListObject.getStatus_code() == 0) {
                        try {
                            resturantListItems.clear();
                            setupViewPager(viewPager);
                        } catch (Exception e) {
                            Log.e("Error", "" + e);
                        }
                        pDialog.dismiss();

                    } else {
                        pDialog.dismiss();
                    }
                } catch (JSONException e) {
                    Log.e("Error_in", "onErrorResponse");
                    pDialog.dismiss();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();
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
                params.put(ParamsKey.Keylat, gps.getLatitude() + "");
                params.put(ParamsKey.KeyLong, gps.getLongitude() + "");
//                params.put(ParamsKey.Keylat, "37.090240");
//                params.put(ParamsKey.KeyLong, "-95.712891");
                params.put(ParamsKey.page, "");
                Log.e("search", "Posting params: " + params.toString());
                return params;

            }
        };


        // Adding request to request queue

        TipperActivity.getInstance().addToRequestQueue(jsonObjectRequest, "HOME");
    }

    void searchResturant(final String search) {
        final StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST, AppUrl.URL_SEARCHRESTURANT, new Response.Listener<String>() {
            @Override
            public void onResponse(String Response) {
                Log.e("Place Response", Response);

                try {
                    JSONObject jsonObj = new JSONObject(Response);
                    ResturantListObject resturantListObject = new ResturantListObject();

                    try {
                        resturantListObject.setStatus_code(jsonObj.getInt(Jsonkey.statusCode));
                    } catch (JSONException e) {
                        resturantListObject.setStatus_code(0);

                    }
                    resturantListObject.setMessage(jsonObj.getString(Jsonkey.message));

                    if (resturantListObject.getStatus_code() == 1) {
                        try {

                            resturantListObject.setResturantListItems(jsonObj.getJSONArray(Jsonkey.SerchKey));
                        } catch (JSONException e) {
                            resturantListObject.setResturantListItems(null);
                        }
                        resturantListItems = resturantListObject.getResturantListItems();
                        Collections.sort(resturantListItems, new Comparator<ResturantListItem>() {
                            public int compare(ResturantListItem obj1, ResturantListItem obj2) {
                                // ## Ascending order
                                return obj1.getResturantDistance().compareToIgnoreCase(obj2.getResturantDistance()); // To compare string values
                            }
                        });
                        setupViewPager(viewPager);
                    } else if (resturantListObject.getStatus_code() == 0) {
                        try {
                            resturantListItems.clear();
                        } catch (Exception e) {
                            Log.e("Error", "" + e);
                        }
                        setupViewPager(viewPager);
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
                Log.e("Place", "Error Response: " + error.getMessage());
            }
        })

        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(ParamsKey.Keysearch, search);
                params.put(ParamsKey.KeyUserId, preferenceSettings.getUserId());
                params.put(ParamsKey.KeyRegistrationType, Constants.TYPE_USER);
                params.put(ParamsKey.Keylat, gps.getLatitude() + "");
//                params.put(ParamsKey.Keylat, "37.0902");
                params.put(ParamsKey.KeyLong, gps.getLongitude() + "");
//                params.put(ParamsKey.KeyLong, "-95.712891");
                Log.e("search", "Posting params: " + params.toString());
                return params;

            }
        };


        // Adding request to request queue

        TipperActivity.getInstance().addToRequestQueue(jsonObjectRequest, "HOME");
    }


    void getServerList(final int type) {
        final StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST, AppUrl.URL_SERVERLIST, new Response.Listener<String>() {
            @Override
            public void onResponse(String Response) {
                Log.e("Place Response", Response);

                try {
                    JSONObject jsonObj = new JSONObject(Response);
                    ServerListObject serverListObject = new ServerListObject();

                    try {
                        serverListObject.setStatus_code(jsonObj.getInt(Jsonkey.statusCode));
                    } catch (JSONException e) {
                        serverListObject.setStatus_code(0);

                    }
                    loadServer = jsonObj.getString(Jsonkey.page);
                    serverListObject.setMessage(jsonObj.getString(Jsonkey.message));

                    if (serverListObject.getStatus_code() == 1) {
                        try {
                            serverListObject.setServerListItems(jsonObj.getJSONArray(Jsonkey.serverKey));
                        } catch (JSONException e) {
                            serverListObject.setServerListItems(null);
                        }
                        serverListItems = serverListObject.getServerListItems();
                        setupViewPager(viewPager);
                        if (type == 1) {
                            viewPager.setCurrentItem(1, true);
                        }
                    } else if (serverListObject.getStatus_code() == 0) {
                        serverListItems.clear();
                        setupViewPager(viewPager);
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
                showTryAgainAlert("Network error", "please check your network try again");
                Log.e("Place", "Error Response: " + error.getMessage());
            }
        })

        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(ParamsKey.KeyUserid, preferenceSettings.getUserId());
                params.put(ParamsKey.KeyRegistrationType, Constants.TYPE_USER);
                params.put(ParamsKey.page, "0");
                Log.e("server", "Posting params: " + params.toString());
                return params;
            }
        };


        // Adding request to request queue

        TipperActivity.getInstance().addToRequestQueue(jsonObjectRequest, "HOME");
    }

    void searchServer(final String search) {
        final StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST, AppUrl.URL_SEARCHSERVER, new Response.Listener<String>() {
            @Override
            public void onResponse(String Response) {
                Log.e("Place Response", Response);

                try {
                    JSONObject jsonObj = new JSONObject(Response);
                    ServerListObject serverListObject = new ServerListObject();

                    try {
                        serverListObject.setStatus_code(jsonObj.getInt(Jsonkey.statusCode));
                    } catch (JSONException e) {
                        serverListObject.setStatus_code(0);

                    }
                    serverListObject.setMessage(jsonObj.getString(Jsonkey.message));

                    if (serverListObject.getStatus_code() == 1) {
                        try {
                            serverListObject.setServerListItems(jsonObj.getJSONArray(Jsonkey.SerchKey));
                        } catch (JSONException e) {
                            serverListObject.setServerListItems(null);
                        }
                        serverListItems = serverListObject.getServerListItems();
                        setupViewPager(viewPager);
                        viewPager.setCurrentItem(1, true);
                    } else if (serverListObject.getStatus_code() == 0) {
                        serverListItems.clear();
                        setupViewPager(viewPager);
                        viewPager.setCurrentItem(1, true);
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
                if (search != null && !search.equalsIgnoreCase("")) {
                    params.put(ParamsKey.Keysearch, search);
                }
                params.put(ParamsKey.KeyUserid, preferenceSettings.getUserId());
                params.put(ParamsKey.KeyRegistrationType, Constants.TYPE_USER);
                Log.e("server", "Posting params: " + params.toString());
                return params;
            }
        };


        // Adding request to request queue

        TipperActivity.getInstance().addToRequestQueue(jsonObjectRequest, "HOME");
    }

    public void showTryAgainAlert(String title, String message) {
        CommonUtils.showAlertWithNegativeButton(HomeActivity.this, title, message, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                if (CommonUtils.isNetworkAvailable()) {
                    dialog.dismiss();
                    getResturantList(0);
                } else
                    CommonUtils.showToast("Please check your internet connection");
            }
        });

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (newText.trim().length() == 0) {
            canclePreviousSearchRequest();
            if (resturentSearch) {
                getResturantList(1);
                getServerList(0);
            } else {
                getResturantList(2);
                getServerList(1);
            }
            searchData = true;
        } else {
            if (newText.toString().trim() != null) {
                if (resturentSearch)
                    searchResturant(newText.toString().trim());
                else
                    searchServer(newText.toString().trim());
            }
            searchData = false;
        }
        return false;
    }

}