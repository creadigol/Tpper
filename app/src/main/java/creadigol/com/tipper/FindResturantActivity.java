package creadigol.com.tipper;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AddPlaceRequest;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import Adapter.FindResturantAdapter;
import Halper.GPSTracker;
import Model.FindResturantItem;
import Model.FindResturantObject;
import Model.ResturantListItem;
import Model.ResturantListObject;
import Utils.AppUrl;
import Utils.CommonUtils;
import Utils.Constants;
import Utils.Jsonkey;
import Utils.ParamsKey;
import Utils.PreferenceSettings;

/**
 * Created by ravi on 27-03-2017.
 */

public class FindResturantActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {
    EditText edFindResturant;
    RecyclerView rvFindResturant;
    ProgressBar progressBar;
    LinearLayout googleData, ll_resturant;
    FindResturantAdapter findResturantAdapter;
    ArrayList<FindResturantItem> findResturantItems;
    PreferenceSettings preferenceSettings;
    TextView txtSearch;
    ImageView placeImage;
    Button btfindFromgoogle;
    String ResturantId, ResturantName, ResturantAddress, ResturantNumber = "", ResturantWebsite = "";
    double ResturantLatitut, ResturantLongitut;
    private static final int PLACE_PICKER_REQUEST = 1;
    private static LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
            new LatLng(37.0902, -95.712891), new LatLng(37.0902, -95.712891));
    private final String TAG = FindResturantActivity.class.getSimpleName();
    GPSTracker gps;
    private static GoogleApiClient mGoogleApiClient;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.findresturant_activity);
        edFindResturant = (EditText) findViewById(R.id.edSearch);
        rvFindResturant = (RecyclerView) findViewById(R.id.rvSearch);
        rvFindResturant.setNestedScrollingEnabled(false);
        progressBar = (ProgressBar) findViewById(R.id.progressSearch);
        googleData = (LinearLayout) findViewById(R.id.googleData);
        ll_resturant = (LinearLayout) findViewById(R.id.ll_resturant);
        txtSearch = (TextView) findViewById(R.id.txtSearchmsg);
        placeImage = (ImageView) findViewById(R.id.placeImage);
        btfindFromgoogle = (Button) findViewById(R.id.btfindFromgoogle);
        btfindFromgoogle.setOnClickListener(this);
        googleData.setVisibility(View.GONE);
        btfindFromgoogle.setVisibility(View.GONE);
        ll_resturant.setVisibility(View.VISIBLE);
        gps = new GPSTracker(FindResturantActivity.this);

        TextView place = (TextView) findViewById(R.id.place);
        place.setOnClickListener(this);

        ImageView backImg = (ImageView) findViewById(R.id.backImg);
        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        ImageView ImgBack = (ImageView) findViewById(R.id.ImgBack);
        ImgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        Button addResturant = (Button) findViewById(R.id.addResturant);
        addResturant.setOnClickListener(this);
        getResturantList(0);
        preferenceSettings = TipperActivity.getInstance().getPreferenceSettings();
        edFindResturant.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (edFindResturant.getText().toString().trim().length() == 0) {
                    canclePreviousSearchRequest();
                    progressBar.setVisibility(View.GONE);
                    rvFindResturant.setVisibility(View.GONE);
                    txtSearch.setVisibility(View.VISIBLE);
                    getResturantList(1);
                } else {
                    if (editable.toString().trim() != null) {
                        getResturantList(edFindResturant.getText().toString().trim());
                    }

                }
            }
        });
    }

    void getResturantList(final int type) {
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);
        pDialog.show();
        if (type == 1) {
            pDialog.dismiss();
        }
        final StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST, AppUrl.URL_FINDRESTURANT, new Response.Listener<String>() {
            @Override
            public void onResponse(String Response) {
                Log.e("Place Response", Response);

                try {
                    JSONObject jsonObj = new JSONObject(Response);
                    FindResturantObject findResturantObject = new FindResturantObject();

                    try {
                        findResturantObject.setStatus_code(jsonObj.getInt(Jsonkey.statusCode));
                    } catch (JSONException e) {
                        findResturantObject.setStatus_code(0);

                    }
                    findResturantObject.setMessage(jsonObj.getString(Jsonkey.message));

                    if (findResturantObject.getStatus_code() == 1) {

                        try {
                            findResturantObject.setFindResturantItems(jsonObj.getJSONArray(Jsonkey.resturantData));
                        } catch (JSONException e) {
                            findResturantObject.setFindResturantItems(null);
                        }

                        findResturantItems = findResturantObject.getFindResturantItems();
                        Collections.sort(findResturantItems, new Comparator<FindResturantItem>() {
                            public int compare(FindResturantItem obj1, FindResturantItem obj2) {
                                // ## Ascending order
                                return obj1.getDistance().compareToIgnoreCase(obj2.getDistance()); // To compare string values
                            }
                        });
                        setResultView();
                        pDialog.dismiss();
                    } else if (findResturantObject.getStatus_code() == 0) {
                        try {
                            findResturantItems.clear();
                            progressBar.setVisibility(View.INVISIBLE);
                            setResultView();
                            pDialog.dismiss();
                        } catch (Exception e) {
                            pDialog.dismiss();
                            Log.e("Error", "" + e);
                        }
                    } else {

                        progressBar.setVisibility(View.INVISIBLE);
                        Log.e("Error_in", "else");
                    }
                } catch (JSONException e) {
                    progressBar.setVisibility(View.INVISIBLE);
                    Log.e("Error_in", "onErrorResponse");

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
                params.put(ParamsKey.KeyRegistrationType, Constants.TYPE_SERVER);
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


    public void showTryAgainAlert(String title, String message) {
        CommonUtils.showAlertWithNegativeButton(FindResturantActivity.this, title, message, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                if (CommonUtils.isNetworkAvailable())
                    getResturantList(0);
                else
                    CommonUtils.showToast("Please check your internet connection");
            }
        });

    }

    public void canclePreviousSearchRequest() {
        TipperActivity.getInstance().cancelPendingRequests(TAG);
    }

    public void setResultView() {
        if (findResturantItems != null && findResturantItems.size() > 0) {
            rvFindResturant.setVisibility(View.VISIBLE);
            btfindFromgoogle.setVisibility(View.VISIBLE);
            setResturantList();
            txtSearch.setVisibility(View.GONE);
        } else {
            txtSearch.setVisibility(View.VISIBLE);
            btfindFromgoogle.setVisibility(View.VISIBLE);
            rvFindResturant.setVisibility(View.GONE);
            if (findResturantAdapter != null) {
                findResturantAdapter.modifyDataSet(findResturantItems);
            }
        }
    }

    void setResturantList() {
        if (findResturantAdapter == null) {
            rvFindResturant.setVisibility(View.VISIBLE);
            rvFindResturant.setHasFixedSize(true);
            rvFindResturant.setItemAnimator(new DefaultItemAnimator());
            rvFindResturant.setLayoutManager(new LinearLayoutManager(this));
            findResturantAdapter = new FindResturantAdapter(this, findResturantItems, onClickData);
            rvFindResturant.setAdapter(findResturantAdapter);
        } else {
            findResturantAdapter.modifyDataSet(findResturantItems);
        }
    }

    View.OnClickListener onClickData = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (int) v.getTag();
            FindResturantItem findResturantItem = findResturantItems.get(position);
            preferenceSettings.setResturantName(findResturantItem.getResturantName());
            preferenceSettings.setResturantAddrees(findResturantItem.getResturantAddress());
            preferenceSettings.setResturantEmail(findResturantItem.getResturantEmail());
            preferenceSettings.setResturantNo(findResturantItem.getResturantNumber());
            preferenceSettings.setResturantWebsite(findResturantItem.getResturantWebsite());
            ProfileActivity.resturantId = findResturantItem.getResturantId();
            ProfileActivity.editProfileDeatils.setVisibility(View.VISIBLE);
            ProfileActivity.isSave = true;
            finish();
        }
    };

    void getResturantList(final String search) {

        progressBar.setVisibility(View.VISIBLE);
        final StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST, AppUrl.URL_SEARCHRESTURANT, new Response.Listener<String>() {
            @Override
            public void onResponse(String Response) {
                Log.e("Place Response", Response);
                progressBar.setVisibility(View.GONE);

                try {
                    JSONObject jsonObj = new JSONObject(Response);
                    FindResturantObject findResturantObject = new FindResturantObject();

                    try {
                        findResturantObject.setStatus_code(jsonObj.getInt(Jsonkey.statusCode));
                    } catch (JSONException e) {
                        findResturantObject.setStatus_code(0);

                    }
                    findResturantObject.setMessage(jsonObj.getString(Jsonkey.message));

                    if (findResturantObject.getStatus_code() == 1) {

                        try {
                            findResturantObject.setFindResturantItems(jsonObj.getJSONArray(Jsonkey.SerchKey));
                        } catch (JSONException e) {
                            findResturantObject.setFindResturantItems(null);
                        }

                        findResturantItems = findResturantObject.getFindResturantItems();
                        Collections.sort(findResturantItems, new Comparator<FindResturantItem>() {
                            public int compare(FindResturantItem obj1, FindResturantItem obj2) {
                                // ## Ascending order
                                return obj1.getDistance().compareToIgnoreCase(obj2.getDistance()); // To compare string values
                            }
                        });
                        setResultView();

                    } else if (findResturantObject.getStatus_code() == 0) {
                        try {
                            findResturantItems.clear();
                            progressBar.setVisibility(View.INVISIBLE);
                            setResultView();

                        } catch (Exception e) {
                            Log.e("Error", "" + e);
                        }
                    } else {

                        progressBar.setVisibility(View.INVISIBLE);
                        Log.e("Error_in", "else");
                    }
                } catch (JSONException e) {
                    progressBar.setVisibility(View.INVISIBLE);
                    Log.e("Error_in", "onErrorResponse");

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error_in", "onErrorResponse");
                progressBar.setVisibility(View.INVISIBLE);
                showTryAgainAlert("Network error", "please check your network try again", search);
                Log.e("Place", "Error Response: " + error.getMessage());
            }
        })

        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(ParamsKey.KeyUserId, preferenceSettings.getUserId());
                params.put(ParamsKey.KeyRegistrationType, Constants.TYPE_SERVER);
                params.put(ParamsKey.Keysearch, search);
//                params.put(ParamsKey.Keylat, gps.getLatitude() + "");
//                params.put(ParamsKey.KeyLong, gps.getLongitude() + "");
                params.put(ParamsKey.Keylat, "37.0902");
                params.put(ParamsKey.KeyLong, "-95.712891");
                Log.e("search", "Posting params: " + params.toString());
                return params;

            }
        };


        // Adding request to request queue

        TipperActivity.getInstance().addToRequestQueue(jsonObjectRequest, TAG);
    }

    public void showTryAgainAlert(String title, String message, final String search) {
        CommonUtils.showAlertWithNegativeButton(FindResturantActivity.this, title, message, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                if (CommonUtils.isNetworkAvailable())
                    getResturantList(search);
                else
                    CommonUtils.showToast("Please check your internet connection");
            }
        });

    }

    void AddResturantList() {

        progressBar.setVisibility(View.VISIBLE);
        final StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST, AppUrl.URL_ADDRESTURANT, new Response.Listener<String>() {
            @Override
            public void onResponse(String Response) {
                Log.e("Place Response", Response);
                progressBar.setVisibility(View.GONE);

                try {
                    JSONObject jsonObj = new JSONObject(Response);
                    int status_code = (jsonObj.optInt("status_code"));
                    String massage = (jsonObj.optString("message"));
                    if (status_code == 1) {
                        int restaurantid = jsonObj.optInt("restaurantid");
                        Toast.makeText(FindResturantActivity.this, "" + massage, Toast.LENGTH_SHORT).show();
                        preferenceSettings.setResturantName(ResturantName);
                        preferenceSettings.setResturantAddrees(ResturantAddress);
                        preferenceSettings.setResturantNo(ResturantNumber);
                        preferenceSettings.setResturantWebsite(ResturantWebsite);
                        ProfileActivity.resturantId = String.valueOf(restaurantid);
                        ProfileActivity.editProfileDeatils.setVisibility(View.VISIBLE);
                        ProfileActivity.isSave = true;
                        finish();
                    } else if (status_code == 0) {
                        Toast.makeText(FindResturantActivity.this, "" + massage, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(FindResturantActivity.this, "" + massage, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("Error_in", "onErrorResponse");

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error_in", "onErrorResponse");
                error.printStackTrace();
                Log.e("Place", "Error Response: " + error.getMessage());
            }
        })

        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(ParamsKey.KeyserverId, preferenceSettings.getUserId());
                params.put(ParamsKey.KeyEmail, "");
                params.put(ParamsKey.KeyPhone, ResturantNumber);
                params.put(ParamsKey.KeyGoogleId, ResturantId);
                params.put(ParamsKey.KeyRegistrationType, Constants.TYPE_SERVER);
                params.put(ParamsKey.KeyName, ResturantName);
                params.put(ParamsKey.KeyAddress, ResturantAddress);
                params.put(ParamsKey.Keylatitude, String.valueOf(ResturantLatitut));
                params.put(ParamsKey.Keylongitude, String.valueOf(ResturantLongitut));
                params.put(ParamsKey.KeyImage, "");
                params.put(ParamsKey.Keywebsite, ResturantWebsite);
                Log.e("search", "Posting params: " + params.toString());
                return params;

            }
        };


        // Adding request to request queue

        TipperActivity.getInstance().addToRequestQueue(jsonObjectRequest, TAG);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btfindFromgoogle:
                try {
                    PlacePicker.IntentBuilder intentBuilder =
                            new PlacePicker.IntentBuilder();
                    intentBuilder.setLatLngBounds(BOUNDS_MOUNTAIN_VIEW);
                    Intent intent = intentBuilder.build(FindResturantActivity.this);
                    startActivityForResult(intent, PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException
                        | GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.place:
                BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
                        new LatLng(ResturantLatitut, ResturantLongitut), new LatLng(ResturantLatitut, ResturantLongitut));
                try {
                    PlacePicker.IntentBuilder intentBuilder =
                            new PlacePicker.IntentBuilder();
                    intentBuilder.setLatLngBounds(BOUNDS_MOUNTAIN_VIEW);
                    Intent intent = intentBuilder.build(FindResturantActivity.this);
                    startActivityForResult(intent, PLACE_PICKER_REQUEST);

                } catch (GooglePlayServicesRepairableException
                        | GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.addResturant:
                AddResturantList();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode, Intent data) {

        if (requestCode == PLACE_PICKER_REQUEST
                && resultCode == Activity.RESULT_OK) {

            final Place place = PlacePicker.getPlace(this, data);
            final CharSequence name = place.getName();
            final double latitute = place.getLatLng().latitude;
            final double longitute = place.getLatLng().longitude;
            final CharSequence number = place.getPhoneNumber();
            final CharSequence address = place.getAddress();
            final Uri add = place.getWebsiteUri();
            final String id = place.getId();
            String attributions = (String) place.getAttributions();
            if (attributions == null) {
                attributions = "";
            }
            googleData.setVisibility(View.VISIBLE);
            TextView resturantName = (TextView) findViewById(R.id.name);
            TextView resturantAddress = (TextView) findViewById(R.id.address);
            TextView resturantNumber = (TextView) findViewById(R.id.number);
            TextView resturantWebsite = (TextView) findViewById(R.id.website);
            ll_resturant.setVisibility(View.GONE);
            resturantName.setText(name);
            resturantAddress.setText(address);
            if (add != null && !add.equals("")) {
                resturantWebsite.setText("" + add);
            } else {
                resturantWebsite.setText("N/A");
            }
            if (number != null && !number.equals("")) {
                resturantNumber.setText(number);
            } else {
                resturantNumber.setText("N/A");
            }

            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Places.GEO_DATA_API)
                    .addApi(Places.PLACE_DETECTION_API)
                    .addOnConnectionFailedListener(this)
                    .build();
            final String placeId = id; // Australian Cruise Group
            Places.GeoDataApi.getPlacePhotos(mGoogleApiClient, placeId)
                    .setResultCallback(new ResultCallback<PlacePhotoMetadataResult>() {


                        @Override
                        public void onResult(PlacePhotoMetadataResult photos) {
                            if (!photos.getStatus().isSuccess()) {
                                return;
                            }

                            PlacePhotoMetadataBuffer photoMetadataBuffer = photos.getPhotoMetadata();
                            if (photoMetadataBuffer.getCount() > 0) {
                                // Display the first bitmap in an ImageView in the size of the view
                                photoMetadataBuffer.get(0)
                                        .getScaledPhoto(mGoogleApiClient, placeImage.getWidth(),
                                                placeImage.getHeight());
                            }
                            photoMetadataBuffer.release();
                        }
                    });

           /* final String placeId = id; // Australian Cruise Group

            // Create a new AsyncTask that displays the bitmap and attribution once loaded.
            new PhotoTask(placeImage.getWidth(), placeImage.getHeight()) {
                @Override
                protected void onPreExecute() {
                    // Display a temporary image to show while bitmap is loading.
                    placeImage.setImageResource(R.drawable.noimage);
                }

                @Override
                protected void onPostExecute(AttributedPhoto attributedPhoto) {
                    if (attributedPhoto != null) {
                        // Photo has been loaded, display it.
                        placeImage.setImageBitmap(attributedPhoto.bitmap);

                        // Display the attribution as HTML content if set.
                        if (attributedPhoto.attribution == null) {
//                            mText.setVisibility(View.GONE);
                        } else {
                            Toast.makeText(getApplicationContext(), "no", Toast.LENGTH_SHORT).show();
//                            mText.setText(Html.fromHtml(attributedPhoto.attribution.toString()));
                        }

                    }
                }
            }.execute(placeId);*/
            /*PlacePhotoMetadataResult result = Places.GeoDataApi
                    .getPlacePhotos(mGoogleApiClient, id);
            if (result != null && result.getStatus().isSuccess()) {
                PlacePhotoMetadataBuffer photoMetadataBuffer = result.getPhotoMetadata();
                Log.e("photo", "" + photoMetadataBuffer);
                PlacePhotoMetadata photo = photoMetadataBuffer.get(0);
                Bitmap image = photo.getPhoto(mGoogleApiClient).await()
                        .getBitmap();
                CharSequence attribution = photo.getAttributions();
                Log.e("image", "" + image);
                Log.e("image", "" + attribution);
            }*/
            ResturantId = id;
            ResturantName = resturantName.getText().toString();
            ResturantAddress = resturantAddress.getText().toString();
            ResturantNumber = resturantNumber.getText().toString();
            ResturantLatitut = latitute;
            ResturantLongitut = longitute;
            ResturantWebsite = String.valueOf(resturantWebsite.getText());

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    abstract class PhotoTask extends AsyncTask<String, Void, PhotoTask.AttributedPhoto> {

        private int mHeight;

        private int mWidth;

        public PhotoTask(int width, int height) {
            mHeight = height;
            mWidth = width;
        }

        /**
         * Loads the first photo for a place id from the Geo Data API.
         * The place id must be the first (and only) parameter.
         */
        @Override
        protected AttributedPhoto doInBackground(String... params) {
            if (params.length != 1) {
                return null;
            }
            final String placeId = params[0];
            AttributedPhoto attributedPhoto = null;

            PlacePhotoMetadataResult result = Places.GeoDataApi
                    .getPlacePhotos(mGoogleApiClient, placeId).await();

            if (result.getStatus().isSuccess()) {
                PlacePhotoMetadataBuffer photoMetadataBuffer = result.getPhotoMetadata();
                if (photoMetadataBuffer.getCount() > 0 && !isCancelled()) {
                    // Get the first bitmap and its attributions.
                    PlacePhotoMetadata photo = photoMetadataBuffer.get(0);
                    CharSequence attribution = photo.getAttributions();
                    // Load a scaled bitmap for this photo.
                    Bitmap image = photo.getScaledPhoto(mGoogleApiClient, mWidth, mHeight).await()
                            .getBitmap();

                    attributedPhoto = new AttributedPhoto(attribution, image);
                }
                // Release the PlacePhotoMetadataBuffer.
                photoMetadataBuffer.release();
            }
            return attributedPhoto;
        }

        /**
         * Holder for an image and its attribution.
         */
        class AttributedPhoto {

            public final CharSequence attribution;

            public final Bitmap bitmap;

            public AttributedPhoto(CharSequence attribution, Bitmap bitmap) {
                this.attribution = attribution;
                this.bitmap = bitmap;
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
