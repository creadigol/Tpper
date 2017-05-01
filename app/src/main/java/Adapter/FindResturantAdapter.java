package Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;

import java.util.ArrayList;

import Model.FindResturantItem;
import Halper.MyApplication;
import creadigol.com.tipper.R;
import creadigol.com.tipper.TipperActivity;


/**
 * Created by Creadigol on 12-09-2016.
 */
public class FindResturantAdapter extends RecyclerView.Adapter<FindResturantAdapter.MyViewHolder> {
    Context context;
    ArrayList<FindResturantItem> searchitems;
    View.OnClickListener onClickListener;

    public FindResturantAdapter(Context context, ArrayList<FindResturantItem> searchitems, View.OnClickListener onClickListener) {
        this.context = context;
        this.searchitems = searchitems;
        this.onClickListener = onClickListener;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.customefind_activity, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);

        return myViewHolder;

    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final FindResturantItem search = searchitems.get(position);

        holder.resturantName.setText(search.getResturantName());
        holder.resturantAddress.setText(search.getResturantAddress());
        if (search.getDistance() != null && !search.getDistance().equalsIgnoreCase("") && !search.getDistance().equalsIgnoreCase("0")) {
            holder.llKm.setVisibility(View.VISIBLE);
            holder.textMiles.setText(search.getDistance());

        } else {
            holder.llKm.setVisibility(View.GONE);
        }
        TipperActivity.getInstance().getImageLoader().displayImage(search.getResturantPhoto(), holder.imvlist, getDisplayImageOptions());
        holder.rv_search.setTag(position);
        holder.rv_search.setOnClickListener(onClickListener);
    }

    @Override
    public int getItemCount() {
        return searchitems.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView resturantName, resturantAddress, textMiles;
        public ImageView imvlist;
        public LinearLayout llKm;
        public RelativeLayout rv_search;

        public MyViewHolder(View v) {
            super(v);

            resturantName = (TextView) v.findViewById(R.id.tv_search_name);
            resturantAddress = (TextView) v.findViewById(R.id.tvResturantAddress);
            imvlist = (ImageView) v.findViewById(R.id.iv_search);
            textMiles = (TextView) v.findViewById(R.id.findMiles);
            llKm = (LinearLayout) v.findViewById(R.id.llfindKm);
            rv_search = (RelativeLayout) v.findViewById(R.id.rl_listing);
        }
    }

    public void modifyDataSet(ArrayList<FindResturantItem> search) {
        this.searchitems = search;
        this.notifyDataSetChanged();
    }

    public DisplayImageOptions getDisplayImageOptions() {
        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisk(true).resetViewBeforeLoading(true)
                .showImageForEmptyUri(R.drawable.resturantblank)
                .showImageOnFail(R.drawable.resturantblank)
                .showImageOnLoading(R.drawable.resturantblank)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        return options;
    }

}