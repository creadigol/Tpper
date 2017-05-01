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

import Model.MyReviewsListItem;
import Model.ResturantServeritem;
import Utils.CommonUtils;
import creadigol.com.tipper.R;
import creadigol.com.tipper.RatingView;
import creadigol.com.tipper.ServerDeatailsActivity;
import creadigol.com.tipper.TipperActivity;


/**
 * Created by Creadigol on 12-09-2016.
 */
public class MyReviewListAdapter extends RecyclerView.Adapter<MyReviewListAdapter.MyViewHolder> {
    Context context;
    ArrayList<MyReviewsListItem> myReviewsListItems;

    public MyReviewListAdapter(Context context, ArrayList<MyReviewsListItem> myReviewsListItems) {
        this.context = context;
        this.myReviewsListItems = myReviewsListItems;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_myyreviewlist_activity, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);

        return myViewHolder;

    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final MyReviewsListItem myReviewsListItem = myReviewsListItems.get(position);

        holder.tvUserName.setText(CommonUtils.getCapitalize(myReviewsListItem.getServerName()));
        holder.textResturantName.setText(CommonUtils.getCapitalize(myReviewsListItem.getServerResName()));

        if (myReviewsListItem.getTotalTips() != null && !myReviewsListItem.getTotalTips().equalsIgnoreCase("")) {
            if (myReviewsListItem.getTotalTips().toString().length() == 4) {
                String x = myReviewsListItem.getTotalTips();
                x = x.substring(0, 1) + "," + x.substring(1, x.length());
                holder.textTotalTip.setText("$ " + x);
            } else if (myReviewsListItem.getTotalTips().toString().length() > 4) {
                String x = myReviewsListItem.getTotalTips();
                x = x.substring(0, 2) + "," + x.substring(2, x.length());
                holder.textTotalTip.setText("$ " + x);
            } else {
                holder.textTotalTip.setText("$ " + myReviewsListItem.getTotalTips());
            }
        } else {
            holder.textTotalTip.setText("$ 0");
        }
        if (myReviewsListItem.getAvgRatingCount() != null && !myReviewsListItem.getAvgRatingCount().equalsIgnoreCase("")) {
            if (myReviewsListItem.getAvgRatingCount().length() == 1) {
                holder.avgRatingCount.setText(myReviewsListItem.getAvgRatingCount() + ".0");
            } else {
                holder.avgRatingCount.setText(myReviewsListItem.getAvgRatingCount());
            }
        }
        if (myReviewsListItem.getTotalRatingCount() != null && !myReviewsListItem.getTotalRatingCount().equalsIgnoreCase("")) {
            if (myReviewsListItem.getTotalRatingCount().toString().length() == 4) {
                String x = myReviewsListItem.getTotalRatingCount();
                x = x.substring(0, 1) + "," + x.substring(1, x.length());
                holder.MyTotalReview.setText(x);
            } else if (myReviewsListItem.getTotalRatingCount().toString().length() > 4) {
                String x = myReviewsListItem.getTotalRatingCount();
                x = x.substring(0, 2) + "," + x.substring(2, x.length());
                holder.MyTotalReview.setText(x);
            } else {
                holder.MyTotalReview.setText(myReviewsListItem.getTotalRatingCount());
            }
        } else {
            holder.MyTotalReview.setText("0");
        }
        holder.MyReviewDesc.setText(Html.fromHtml("&ldquo; " + CommonUtils.getCapitalize(myReviewsListItem.getUserReviewDesc()) + " &rdquo;"));
        holder.MyReviewCount.setEnabled(false);
        if (myReviewsListItem.getAvgRatingCount() != null && !myReviewsListItem.getAvgRatingCount().equalsIgnoreCase("")) {
            float rating = Float.parseFloat(myReviewsListItem.getAvgRatingCount());
            holder.MyReviewCount.setRating(rating);
        }
        TipperActivity.getInstance().getImageLoader().displayImage(myReviewsListItem.getServerImage(), holder.ivUserImaeg, getDisplayImageOptions());
        holder.rlMyReviewList.setTag(position);
        holder.rlMyReviewList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ServerDeatailsActivity.class);
                intent.putExtra(ServerDeatailsActivity.EXTRA_KEY_SERVERID, myReviewsListItem.getServerId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return myReviewsListItems.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvUserName, textResturantName, avgRatingCount, MyTotalReview, MyReviewDesc, textTotalTip;
        public ImageView ivUserImaeg;
        public RatingView MyReviewCount;
        public RelativeLayout rlMyReviewList;

        public MyViewHolder(View v) {
            super(v);

            tvUserName = (TextView) v.findViewById(R.id.tvUserName);
            textResturantName = (TextView) v.findViewById(R.id.textResturantName);
            MyReviewCount = (RatingView) v.findViewById(R.id.MyReviewCount);
            ivUserImaeg = (ImageView) v.findViewById(R.id.ivUserImaeg);
            avgRatingCount = (TextView) v.findViewById(R.id.avgRatingCount);
            MyTotalReview = (TextView) v.findViewById(R.id.MyTotalReview);
            textTotalTip = (TextView) v.findViewById(R.id.textTotalTip);
            MyReviewDesc = (TextView) v.findViewById(R.id.MyReviewDesc);
            rlMyReviewList = (RelativeLayout) v.findViewById(R.id.rlMyReviewList);
        }
    }

    public void modifyDataSet(ArrayList<MyReviewsListItem> myReviewsListItems) {
        this.myReviewsListItems = myReviewsListItems;
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
                .displayer(new RoundedBitmapDisplayer(100))
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        return options;
    }

}