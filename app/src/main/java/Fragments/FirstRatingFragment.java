package Fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import Utils.Constants;
import creadigol.com.tipper.PayTipsActivity;
import creadigol.com.tipper.R;
import creadigol.com.tipper.RatingView;
import creadigol.com.tipper.ServerDeatailsActivity;

/**
 * Created by ravi on 10-04-2017.
 */

public class FirstRatingFragment extends Fragment {
    String ratingCount;
    private RatingView ratingView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.firstrating_activity, container, false);

        final TextView tv = (TextView) v.findViewById(R.id.ratingDesc);
        final Button SubmitKnowledge = (Button) v.findViewById(R.id.SubmitKnowledge);
        ratingView = (RatingView) v.findViewById(R.id.ratingbar);

        ratingView.setOnRatingChangedListener(new RatingView.OnRatingChangedListener() {
            @Override
            public void onRatingChange(float oldRating, float newRating) {
                Log.e("s", "rating=="+ Float.toString(newRating));
                if (newRating == 0.0) {
                    SubmitKnowledge.setTextColor(getResources().getColor(R.color.colorGray));
                    SubmitKnowledge.setEnabled(false);
                    tv.setText("");
                } else if (newRating == 0.5 || newRating == 1) {
                    tv.setText(Constants.RATING_HATE);
                } else if (newRating == 1.5 || newRating == 2) {
                    tv.setText(Constants.RATING_GOOD);
                } else if (newRating == 2.5 || newRating == 3) {
                    tv.setText(Constants.RATING_LIKEIT);
                } else if (newRating == 3.5||newRating == 4 ) {
                    tv.setText(Constants.RATING_LOVEIT);
                }
                if (newRating > 0) {
                    SubmitKnowledge.setTextColor(getResources().getColor(R.color.black));
                    if (newRating > 4) {
                        ratingCount = String.valueOf(4);
                    } else {
                        ratingCount = String.valueOf(newRating);
                    }
                    SubmitKnowledge.setEnabled(true);
                }
                //Toast.makeText(MainActivity.this, "Old rating = " + oldRating + "; New rating = " + newRating, Toast.LENGTH_SHORT).show();
            }
        });
        SubmitKnowledge.setEnabled(false);

        SubmitKnowledge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ServerDeatailsActivity.addRating = "1";
                ServerDeatailsActivity.menuKnowledge = ratingCount;
                if (PayTipsActivity.adapter.getCount() == 1) {
                    PayTipsActivity.adapter.addFragment(new SecondRatingFragment(), "");
                    PayTipsActivity.viewPager.setAdapter(PayTipsActivity.adapter);
                }
                PayTipsActivity.viewPager.setCurrentItem(1, true);
            }
        });
        return v;
    }

}
