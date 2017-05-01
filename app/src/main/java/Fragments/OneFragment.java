package Fragments;

/**
 * Created by ADMIN on 17-Oct-16.
 */


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import Halper.GPSTracker;
import Model.ResturantListItem;
import Model.ResturantListObject;
import Utils.AppUrl;
import Utils.CommonUtils;
import Utils.Constants;
import Utils.Jsonkey;
import Utils.ParamsKey;
import Utils.PreferenceSettings;
import creadigol.com.tipper.HomeActivity;
import creadigol.com.tipper.R;
import creadigol.com.tipper.ResturantDetailsActivity;
import creadigol.com.tipper.TipperActivity;

public class OneFragment extends Fragment {
    public static Context context;
    RecyclerView resturantList;
    ArrayList<ResturantListItem> resturantListItems;
    ArrayList<ResturantListItem> listItems;
    ResturantListAdapter resturantListAdapter;
    TextView noResturantList;
    LinearLayout loadMore;
    PreferenceSettings preferenceSettings;
    GPSTracker gps;
    boolean load = true;

    public OneFragment() {
    }

    public OneFragment(Context context, ArrayList<ResturantListItem> resturantListItems) {
        // Required empty public constructor
        this.resturantListItems = resturantListItems;
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_one, container, false);
        preferenceSettings = TipperActivity.getInstance().getPreferenceSettings();
        gps = new GPSTracker(getActivity());
        resturantList = (RecyclerView) view.findViewById(R.id.resturantList);
        noResturantList = (TextView) view.findViewById(R.id.noResturantList);
        loadMore = (LinearLayout) view.findViewById(R.id.loadMore);
        // Inflate the layout for this fragment
        setResultView();
        return view;
    }

    public void setResultView() {
        if (resturantListItems != null && resturantListItems.size() > 0) {
            resturantList.setVisibility(View.VISIBLE);
            noResturantList.setVisibility(View.GONE);
            setResturantList();
        } else {
            if (resturantListAdapter != null) {
                resturantListAdapter.modifyDataSet(resturantListItems);
            } else {
                resturantList.setVisibility(View.GONE);
                noResturantList.setVisibility(View.VISIBLE);
            }
        }
    }

    void setResturantList() {
        if (resturantListAdapter == null) {
            resturantList.setVisibility(View.VISIBLE);
            resturantList.setHasFixedSize(true);
            resturantList.setItemAnimator(new DefaultItemAnimator());
            resturantList.setLayoutManager(new LinearLayoutManager(getActivity()));
            resturantListAdapter = new ResturantListAdapter(context, resturantListItems, onClick);
            resturantList.setAdapter(resturantListAdapter);
        } else {
            resturantListAdapter.modifyDataSet(resturantListItems);
        }
    }

    public void addNewData(ArrayList<ResturantListItem> listItems) {
        for (int i = 0; i < listItems.size(); i++) {
            resturantListItems.add(listItems.get(i));
            resturantListAdapter.notifyItemInserted(resturantListItems.size());
        }
    }

    View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (int) v.getTag();
            ResturantListItem resturantListItem = resturantListItems.get(position);
            Intent intent = new Intent(getActivity(), ResturantDetailsActivity.class);
            intent.putExtra(ResturantDetailsActivity.EXTRA_KEY_RESTURANTID, resturantListItem.getResturantId());
            intent.putExtra(ResturantDetailsActivity.EXTRA_KEY_TYPE, Constants.TYPE_USER);
            startActivity(intent);
        }
    };

    void LoadResturantList() {
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
                    String page = jsonObj.optString(Jsonkey.page);
                    resturantListObject.setMessage(jsonObj.getString(Jsonkey.message));

                    if (resturantListObject.getStatus_code() == 1) {
                        HomeActivity.loadResturant = page;
                        try {
                            resturantListObject.setResturantListItems(jsonObj.getJSONArray(Jsonkey.resturantData));
                        } catch (JSONException e) {
                            resturantListObject.setResturantListItems(null);
                        }
                        listItems = resturantListObject.getResturantListItems();
                        addNewData(listItems);
                        load = true;
                        loadMore.setVisibility(View.GONE);
                    } else if (resturantListObject.getStatus_code() == 0) {
                        loadMore.setVisibility(View.GONE);
                        load = false;
                    } else {
                        loadMore.setVisibility(View.GONE);
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
                params.put(ParamsKey.KeyUserId, preferenceSettings.getUserId());
                params.put(ParamsKey.KeyRegistrationType, "user");
                params.put(ParamsKey.Keylat, gps.getLatitude() + "");
                params.put(ParamsKey.KeyLong, gps.getLongitude() + "");
                params.put(ParamsKey.page, HomeActivity.loadResturant);
                Log.e("search", "Posting params: " + params.toString());
                return params;

            }
        };


        // Adding request to request queue

        TipperActivity.getInstance().addToRequestQueue(jsonObjectRequest, "HOME");
    }

    public class ResturantListAdapter extends RecyclerView.Adapter implements View.OnClickListener {
        Context context;
        View.OnClickListener onClickListener;
        ArrayList<ResturantListItem> resturantListItems;


        public ResturantListAdapter(Context context, ArrayList<ResturantListItem> resturantListItems, View.OnClickListener onClickListener) {
            this.resturantListItems = resturantListItems;
            this.context = context;
            this.onClickListener = onClickListener;

        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.customresturantlist_activity, parent, false);
            RecyclerView.ViewHolder myViewHolder = new MyViewHolder(view);
            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            MyViewHolder viewHolder = (MyViewHolder) holder;

            final ResturantListItem resturantListItem = resturantListItems.get(position);

            if (resturantListItems.size() == position + 1 && HomeActivity.searchData && load) {
                loadMore.setVisibility(View.VISIBLE);
                LoadResturantList();
            }
            viewHolder.resturantName.setText(CommonUtils.getCapitalize(resturantListItem.getResturantName()));
            viewHolder.resturantAddress.setText(CommonUtils.getCapitalize(resturantListItem.getResturantAddress()));
            if (resturantListItem.getResturantDistance() != null && !resturantListItem.getResturantDistance().equalsIgnoreCase("") && !resturantListItem.getResturantDistance().equalsIgnoreCase("null")&&!resturantListItem.getResturantDistance().equalsIgnoreCase("0")) {
                viewHolder.llKm.setVisibility(View.VISIBLE);
                viewHolder.miles.setText(resturantListItem.getResturantDistance());
            } else {
                viewHolder.llKm.setVisibility(View.GONE);
            }
            TipperActivity.getInstance().getImageLoader().displayImage(resturantListItem.getResturantImage(), viewHolder.resturantImage, getDisplayImageOptions());
            viewHolder.rlResturantList.setTag(position);
            viewHolder.rlResturantList.setOnClickListener(onClickListener);
        }

        @Override
        public int getItemCount() {
            return resturantListItems.size();
        }

        @Override
        public void onClick(View view) {

        }


        class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView resturantName, resturantAddress, miles;
            public ImageView resturantImage;
            public LinearLayout llKm;
            public RelativeLayout rlResturantList;

            public MyViewHolder(View v) {
                super(v);

                resturantName = (TextView) v.findViewById(R.id.tvresturantName);
                resturantAddress = (TextView) v.findViewById(R.id.tvresturantAddress);
                miles = (TextView) v.findViewById(R.id.tvMiles);
                llKm = (LinearLayout) v.findViewById(R.id.llKm);
                resturantImage = (ImageView) v.findViewById(R.id.resturantImage);
                rlResturantList = (RelativeLayout) v.findViewById(R.id.rlResturantList);
            }
        }

        public void modifyDataSet(ArrayList<ResturantListItem> listItems) {
            this.resturantListItems = listItems;
            this.notifyDataSetChanged();
        }

        public DisplayImageOptions getDisplayImageOptions() {
            DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                    .cacheOnDisk(true).resetViewBeforeLoading(true)
                    .showImageForEmptyUri(R.drawable.restaurantimage)
                    .showImageOnFail(R.drawable.restaurantimage)
                    .showImageOnLoading(R.drawable.restaurantimage)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .considerExifParams(true)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .displayer(new RoundedBitmapDisplayer(0))
                    .build();
            return options;
        }
    }
}