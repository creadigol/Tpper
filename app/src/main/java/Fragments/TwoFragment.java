package Fragments;

/**
 * Created by ADMIN on 17-Oct-16.
 */


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
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

import Model.ServerListItem;
import Model.ServerListObject;
import Utils.AppUrl;
import Utils.CommonUtils;
import Utils.Jsonkey;
import Utils.ParamsKey;
import Utils.PreferenceSettings;
import creadigol.com.tipper.HomeActivity;
import creadigol.com.tipper.R;
import creadigol.com.tipper.RatingView;
import creadigol.com.tipper.ServerDeatailsActivity;
import creadigol.com.tipper.TipperActivity;

public class TwoFragment extends Fragment {
    public static Context context;
    static RecyclerView serverList;
    ArrayList<ServerListItem> serverListItems;
    ArrayList<ServerListItem> listItemArrayList;
    ServerListAdapter serverListAdapter;
    TextView noserverList;
    LinearLayout loadMore;
    PreferenceSettings preferenceSettings;
    boolean load = true;

    public TwoFragment(Context context, ArrayList<ServerListItem> serverListItems) {
        this.serverListItems = serverListItems;
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_two, container, false);
        preferenceSettings = TipperActivity.getInstance().getPreferenceSettings();
        serverList = (RecyclerView) view.findViewById(R.id.serverList);
        noserverList = (TextView) view.findViewById(R.id.noserverList);
        loadMore = (LinearLayout) view.findViewById(R.id.loadMore);
        setResultView();
        return view;
    }

    public void setResultView() {
        if (serverListItems != null && serverListItems.size() > 0) {
            noserverList.setVisibility(View.GONE);
            serverList.setVisibility(View.VISIBLE);
            setResturantList();
        } else {
            if (serverListAdapter != null) {
                serverListAdapter.modifyDataSet(serverListItems);
            } else {
                noserverList.setVisibility(View.VISIBLE);
                serverList.setVisibility(View.GONE);
            }
        }
    }

    void setResturantList() {
        if (serverListAdapter == null) {
            serverList.setVisibility(View.VISIBLE);
            serverList.setHasFixedSize(true);
            serverList.setItemAnimator(new DefaultItemAnimator());
            serverList.setLayoutManager(new LinearLayoutManager(getActivity()));
            serverListAdapter = new ServerListAdapter(context, serverListItems);
            serverList.setAdapter(serverListAdapter);
        } else {
            serverListAdapter.modifyDataSet(serverListItems);
        }
    }

    public void addNewData(ArrayList<ServerListItem> listItems) {
        for (int i = 0; i < listItems.size(); i++) {
            serverListItems.add(listItems.get(i));
            serverListAdapter.notifyItemInserted(serverListItems.size());
        }
    }

    void getServerList() {
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
                    String page = jsonObj.optString(Jsonkey.page);
                    serverListObject.setMessage(jsonObj.getString(Jsonkey.message));

                    if (serverListObject.getStatus_code() == 1) {
                        HomeActivity.loadServer = page;
                        try {
                            serverListObject.setServerListItems(jsonObj.getJSONArray(Jsonkey.serverKey));
                        } catch (JSONException e) {
                            serverListObject.setServerListItems(null);
                        }
                        listItemArrayList = serverListObject.getServerListItems();
                        addNewData(listItemArrayList);
                        load = true;
                        loadMore.setVisibility(View.GONE);
                    } else if (serverListObject.getStatus_code() == 0) {
                        load = false;
                        loadMore.setVisibility(View.GONE);
                        Log.e("Error", "ggg");
                    } else {
                        Log.e("Error_in", "else");
                        loadMore.setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    loadMore.setVisibility(View.GONE);
                    Log.e("Error_in", "onErrorResponse");

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error_in", "onErrorResponse");
                loadMore.setVisibility(View.GONE);
                Log.e("Place", "Error Response: " + error.getMessage());
            }
        })

        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(ParamsKey.KeyUserid, preferenceSettings.getUserId());
                params.put(ParamsKey.KeyRegistrationType, "user");
                params.put(ParamsKey.page, HomeActivity.loadServer);
                Log.e("server", "Posting params: " + params.toString());
                return params;
            }
        };


        // Adding request to request queue

        TipperActivity.getInstance().addToRequestQueue(jsonObjectRequest, "HOME");
    }

    public class ServerListAdapter extends RecyclerView.Adapter implements View.OnClickListener {
        Context context;
        ArrayList<ServerListItem> serverListItems;


        public ServerListAdapter(Context context, ArrayList<ServerListItem> serverListItems) {
            this.context = context;
            this.serverListItems = serverListItems;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.customserverlist_activity, parent, false);
            RecyclerView.ViewHolder myViewHolder = new MyViewHolder(view);

            return myViewHolder;

        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            MyViewHolder viewHolder = (MyViewHolder) holder;

            final ServerListItem serverListItem = serverListItems.get(position);
            if (serverListItems.size() == position + 1 && HomeActivity.searchData && load) {
                loadMore.setVisibility(View.VISIBLE);
                getServerList();
            }
            viewHolder.tvServerName.setText(CommonUtils.getCapitalize(serverListItem.getServerName()));
            viewHolder.tvServerResName.setText(CommonUtils.getCapitalize(serverListItem.getServerResturantName()));
            viewHolder.ratingCount.setEnabled(false);
            if (serverListItem.getServerAvgRating() != null && !serverListItem.getServerAvgRating().equalsIgnoreCase("")) {
                float rating = Float.parseFloat((serverListItem.getServerAvgRating()));

                viewHolder.ratingCount.setRating(rating);

            }

            if (serverListItem.getServerAvgRating() != null && !serverListItem.getServerAvgRating().equalsIgnoreCase("")) {
                if (serverListItem.getServerAvgRating().length() == 1) {
                    viewHolder.serverRating.setText(serverListItem.getServerAvgRating() + ".0");
                } else {
                    viewHolder.serverRating.setText(serverListItem.getServerAvgRating());
                }
            }

            if (serverListItem.getServerRating() != null && !serverListItem.getServerRating().equalsIgnoreCase("")) {
                if (serverListItem.getServerRating().toString().length() == 4) {
                    String x = serverListItem.getServerRating();
                    x = x.substring(0, 1) + "," + x.substring(1, x.length());
                    viewHolder.totalRating.setText(x);
                } else if (serverListItem.getServerRating().toString().length() > 4) {
                    String x = serverListItem.getServerRating();
                    x = x.substring(0, 2) + "," + x.substring(2, x.length());
                    viewHolder.totalRating.setText(x);
                } else {
                    viewHolder.totalRating.setText(serverListItem.getServerRating());
                }
            } else {
                viewHolder.totalRating.setText("0");
            }
            if (serverListItem.getServerTip() != null && !serverListItem.getServerTip().equalsIgnoreCase("")) {
                if (serverListItem.getServerTip().toString().length() == 4) {
                    String x = serverListItem.getServerTip();
                    x = x.substring(0, 1) + "," + x.substring(1, x.length());
                    viewHolder.tvTipsCount.setText("$ " + x);
                } else if (serverListItem.getServerTip().toString().length() > 4) {
                    String x = serverListItem.getServerTip();
                    x = x.substring(0, 2) + "," + x.substring(2, x.length());
                    viewHolder.tvTipsCount.setText("$ " + x);
                } else {
                    viewHolder.tvTipsCount.setText("$ " + serverListItem.getServerTip());
                }
            } else {
                viewHolder.tvTipsCount.setText("$ 0");
            }
            TipperActivity.getInstance().getImageLoader().displayImage(serverListItem.getServerImage(), viewHolder.serverImage, getDisplayImageOptions());
            viewHolder.rlServerList.setTag(position);
            viewHolder.rlServerList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ServerDeatailsActivity.class);
                    intent.putExtra(ServerDeatailsActivity.EXTRA_KEY_SERVERID, serverListItem.getServerId());
                    context.startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return serverListItems.size();
        }

        @Override
        public void onClick(View view) {

        }


        class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView tvServerName, tvServerResName, serverRating, totalRating, tvTipsCount;
            public ImageView serverImage;
            //            public RatingBar ratingCount;
            public RatingView ratingCount;
            public LinearLayout rlServerList;

            public MyViewHolder(View v) {
                super(v);

                tvServerName = (TextView) v.findViewById(R.id.tvServerName);
                tvServerResName = (TextView) v.findViewById(R.id.tvServerResName);
                serverRating = (TextView) v.findViewById(R.id.serverRating);
                serverImage = (ImageView) v.findViewById(R.id.serverImage);
                totalRating = (TextView) v.findViewById(R.id.totalRating);
                tvTipsCount = (TextView) v.findViewById(R.id.tvTipsCount);
//                ratingCount = (RatingBar) v.findViewById(R.id.ratingCount);
                ratingCount = (RatingView) v.findViewById(R.id.ratingCount);
                rlServerList = (LinearLayout) v.findViewById(R.id.rlServerList);
            }
        }

        public void modifyDataSet(ArrayList<ServerListItem> serverListItems) {
            this.serverListItems = serverListItems;
            this.notifyDataSetChanged();
        }

        public DisplayImageOptions getDisplayImageOptions() {
            DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                    .cacheOnDisk(true).resetViewBeforeLoading(true)
                    .showImageForEmptyUri(R.drawable.noserver)
                    .showImageOnFail(R.drawable.noserver)
                    .showImageOnLoading(R.drawable.noserver)
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