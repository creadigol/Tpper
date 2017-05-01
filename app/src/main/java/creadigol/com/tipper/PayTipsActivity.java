package creadigol.com.tipper;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.creadigol.admin.library.CirclePageIndicator;

import Adapter.ViewPagerAdapter;
import Fragments.FirstRatingFragment;

/**
 * Created by ravi on 28-04-2017.
 */

public class PayTipsActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {
    CardView llRatingCount;
    CirclePageIndicator circlePageIndicator;
    public static ViewPager viewPager;
    public static ViewPagerAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.paytip_activity);
        llRatingCount = (CardView) findViewById(R.id.cvRating);
        viewPager = (ViewPager) findViewById(R.id.ratingPager);
        setupViewPager(viewPager);
        Toolbar();
    }

    public void Toolbar() {
        Toolbar toolbarRestDetails = (Toolbar) findViewById(R.id.toolbarpaytip);
        TextView toolbarName = (TextView) toolbarRestDetails.findViewById(R.id.tv_toolbarName);
        toolbarName.setText("Pay Tips");
        setSupportActionBar(toolbarRestDetails);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new FirstRatingFragment(), "");
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(this);
        circlePageIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
        circlePageIndicator.setViewPager(viewPager);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        circlePageIndicator.setCurrentItem(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
