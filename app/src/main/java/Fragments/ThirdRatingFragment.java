package Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import Utils.Constants;
import creadigol.com.tipper.PayTipsActivity;
import creadigol.com.tipper.R;
import creadigol.com.tipper.RatingView;
import creadigol.com.tipper.ServerDeatailsActivity;

/**
 * Created by ravi on 10-04-2017.
 */

public class ThirdRatingFragment extends Fragment {
    RatingBar RatingBarAppearance;
    TextView  ratingDescAppearance;
    Button SubmitAppearance;
    private RatingView ratingView;
    String ratingCount;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.third_rating_activity, container, false);

        ratingDescAppearance = (TextView) v.findViewById(R.id.ratingDescAppearance);
        SubmitAppearance = (Button) v.findViewById(R.id.SubmitAppearance);
        SubmitAppearance.setEnabled(false);

        ratingView = (RatingView) v.findViewById(R.id.ratingbar);

        ratingView.setOnRatingChangedListener(new RatingView.OnRatingChangedListener() {
            @Override
            public void onRatingChange(float oldRating, float newRating) {
                Log.e("s", "rating=="+ Float.toString(newRating));
                if (newRating == 0.0) {
                    SubmitAppearance.setTextColor(getResources().getColor(R.color.colorGray));
                    SubmitAppearance.setEnabled(false);
                    ratingDescAppearance.setText("");
                } else if (newRating == 0.5 || newRating == 1) {
                    ratingDescAppearance.setText(Constants.RATING_HATE);
                } else if (newRating == 1.5 || newRating == 2) {
                    ratingDescAppearance.setText(Constants.RATING_GOOD);
                } else if (newRating == 2.5 || newRating == 3) {
                    ratingDescAppearance.setText(Constants.RATING_LIKEIT);
                } else if (newRating == 3.5||newRating == 4 ) {
                    ratingDescAppearance.setText(Constants.RATING_LOVEIT);
                }
                if (newRating > 0) {
                    SubmitAppearance.setTextColor(getResources().getColor(R.color.black));
                    if (newRating > 4) {
                        ratingCount = String.valueOf(4);
                    } else {
                        ratingCount = String.valueOf(newRating);
                    }
                    SubmitAppearance.setEnabled(true);
                }
            }
        });


        SubmitAppearance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (PayTipsActivity.adapter.getCount() == 3) {
                    PayTipsActivity.adapter.addFragment(new FourRatingFragment(), "");
                    PayTipsActivity.viewPager.setAdapter(PayTipsActivity.adapter);
                }
                ServerDeatailsActivity.appearance = ratingCount;
                PayTipsActivity.viewPager.setCurrentItem(3, true);
            }
        });
        return v;
    }

}
