package Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.util.ArrayList;

import Model.ResturantServeritem;
import Model.ServerListItem;
import Utils.CommonUtils;
import creadigol.com.tipper.R;
import creadigol.com.tipper.RatingView;
import creadigol.com.tipper.ServerDeatailsActivity;
import creadigol.com.tipper.TipperActivity;


/**
 * Created by Creadigol on 12-09-2016.
 */
public class ResturantServerListAdapter extends RecyclerView.Adapter<ResturantServerListAdapter.MyViewHolder> {
    Context context;
    ArrayList<ResturantServeritem> resturantServeritems;

    public ResturantServerListAdapter(Context context, ArrayList<ResturantServeritem> resturantServeritems) {
        this.context = context;
        this.resturantServeritems = resturantServeritems;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_resturantserverlist_activity, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);

        return myViewHolder;

    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final ResturantServeritem resturantServeritem = resturantServeritems.get(position);

        holder.tvResServerName.setText(CommonUtils.getCapitalize(resturantServeritem.getServerName()));

        if (resturantServeritem.getServerRating() != null && !resturantServeritem.getServerRating().equalsIgnoreCase("")) {
            if (resturantServeritem.getServerRating().toString().length() == 4) {
                String x = resturantServeritem.getServerRating();
                x = x.substring(0, 1) + "," + x.substring(1, x.length());
                holder.serverRating.setText(x);
            } else if (resturantServeritem.getServerRating().toString().length() > 4) {
                String x = resturantServeritem.getServerRating();
                x = x.substring(0, 2) + "," + x.substring(2, x.length());
                holder.serverRating.setText(x);
            } else {
                holder.serverRating.setText(resturantServeritem.getServerRating());
            }
        } else {
            holder.serverRating.setText("0");
        }
        if (resturantServeritem.getServerTotalTip() != null && !resturantServeritem.getServerTotalTip().equalsIgnoreCase("")) {
            if (resturantServeritem.getServerTotalTip().toString().length() == 4) {
                String x = resturantServeritem.getServerTotalTip();
                x = x.substring(0, 1) + "," + x.substring(1, x.length());
                holder.totalTip.setText("Total Tips = $" + x);
            } else if (resturantServeritem.getServerTotalTip().toString().length() > 4) {
                String x = resturantServeritem.getServerTotalTip();
                x = x.substring(0, 2) + "," + x.substring(2, x.length());
                holder.totalTip.setText("Total Tips = $" + x);
            } else {
                holder.totalTip.setText("Total Tips = $" + resturantServeritem.getServerTotalTip());
            }
        } else {
            holder.totalTip.setText("Total Tips = $" + "0");
        }
        if (resturantServeritem.getServerDesc() != null && !resturantServeritem.getServerDesc().equalsIgnoreCase("")) {
            holder.serverDesc.setText(Html.fromHtml("&ldquo; " + CommonUtils.getCapitalize(resturantServeritem.getServerDesc()) + " &rdquo;"));
        }
        holder.serverRatingCount.setEnabled(false);

        if (resturantServeritem.getServerAvgRating() != null && !resturantServeritem.getServerAvgRating().equalsIgnoreCase("")) {
            float rating = Float.parseFloat(resturantServeritem.getServerAvgRating());
            if (rating >= 4) {
                holder.serverRatingCount.setRating(rating+1);
            } else {
                holder.serverRatingCount.setRating(rating);
            }
        }
        TipperActivity.getInstance().getImageLoader().displayImage(resturantServeritem.getServerPhoto(), holder.resServerImage, getDisplayImageOptions());
        holder.rlresServerlist.setTag(position);
        holder.rlresServerlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ServerDeatailsActivity.class);
                intent.putExtra(ServerDeatailsActivity.EXTRA_KEY_SERVERID, resturantServeritem.getServerId());
                context.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return resturantServeritems.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvResServerName, serverRating, totalTip, serverDesc;
        public ImageView resServerImage;
        public RatingView serverRatingCount;
        public RelativeLayout rlresServerlist;

        public MyViewHolder(View v) {
            super(v);

            tvResServerName = (TextView) v.findViewById(R.id.tvResServerName);
            serverRating = (TextView) v.findViewById(R.id.serverTotalRating);
            serverRatingCount = (RatingView) v.findViewById(R.id.serverRatingCount);
            resServerImage = (ImageView) v.findViewById(R.id.resServerImage);
            totalTip = (TextView) v.findViewById(R.id.totalTip);
            serverDesc = (TextView) v.findViewById(R.id.serverDesc);
            rlresServerlist = (RelativeLayout) v.findViewById(R.id.rlresServerlist);
        }
    }

    public void modifyDataSet(ArrayList<ResturantServeritem> resturantServeritem) {
        this.resturantServeritems = resturantServeritem;
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
                .displayer(new RoundedBitmapDisplayer(100))
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        return options;
    }

}