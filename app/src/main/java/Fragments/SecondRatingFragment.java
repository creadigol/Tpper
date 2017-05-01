package Fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
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

public class SecondRatingFragment extends Fragment {
    RatingBar RatingBarHospitality;
    TextView ratingDescHospitality;
    Button SubmitHospitality;
    String ratingCount;
    private RatingView ratingView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.secoundrating_activity, container, false);

        ratingDescHospitality = (TextView) v.findViewById(R.id.ratingDescHospitality);
        SubmitHospitality = (Button) v.findViewById(R.id.SubmitHospitality);
        SubmitHospitality.setEnabled(false);

        ratingView = (RatingView) v.findViewById(R.id.ratingbar);

        ratingView.setOnRatingChangedListener(new RatingView.OnRatingChangedListener() {
            @Override
            public void onRatingChange(float oldRating, float newRating) {
                Log.e("s", "rating=="+ Float.toString(newRating));
                if (newRating == 0.0) {
                    SubmitHospitality.setTextColor(getResources().getColor(R.color.colorGray));
                    SubmitHospitality.setEnabled(false);
                    ratingDescHospitality.setText("");
                } else if (newRating == 0.5 || newRating == 1) {
                    ratingDescHospitality.setText(Constants.RATING_HATE);
                } else if (newRating == 1.5 || newRating == 2) {
                    ratingDescHospitality.setText(Constants.RATING_GOOD);
                } else if (newRating == 2.5 || newRating == 3) {
                    ratingDescHospitality.setText(Constants.RATING_LIKEIT);
                } else if (newRating == 3.5||newRating == 4 ) {
                    ratingDescHospitality.setText(Constants.RATING_LOVEIT);
                }
                if (newRating > 0) {
                    SubmitHospitality.setTextColor(getResources().getColor(R.color.black));
                    if (newRating > 4) {
                        ratingCount = String.valueOf(4);
                    } else {
                        ratingCount = String.valueOf(newRating);
                    }
                    SubmitHospitality.setEnabled(true);
                }
                //Toast.makeText(MainActivity.this, "Old rating = " + oldRating + "; New rating = " + newRating, Toast.LENGTH_SHORT).show();
            }
        });

        SubmitHospitality.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (PayTipsActivity.adapter.getCount() == 2) {
                    PayTipsActivity.adapter.addFragment(new ThirdRatingFragment(), "");
                    PayTipsActivity.viewPager.setAdapter(PayTipsActivity.adapter);
                }
                ServerDeatailsActivity.hospitality = ratingCount;
                PayTipsActivity.viewPager.setCurrentItem(2, true);
            }
        });
        return v;
    }

}
