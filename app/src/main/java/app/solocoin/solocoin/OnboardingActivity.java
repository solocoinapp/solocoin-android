package app.solocoin.solocoin;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.viewpager.widget.ViewPager;

import app.solocoin.solocoin.databinding.ActivityMainBinding;

import java.util.ArrayList;

public class OnboardingActivity extends AppCompatActivity {


    ActivityMainBinding binding;
    public static final int MULTIPLE_PERMISSION_REQUEST = 102;

    //Onboarding
    private LinearLayout pager_indicator;
    private int dotsCount;
    private ImageView[] dots;
    private ViewPager onboard_pager;
    private OnBoard_Adapter mAdapter;
    ArrayList<OnBoardItem> onBoardItems=new ArrayList<>();





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_on_boarding);
        //check if the user is logging for the first time.
        /*initView();*/
        ask_permissions();

        //Buttons
        final Button abutton = findViewById(R.id.button0);
        abutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(OnboardingActivity.this, Phone1Verification.class));
            }
        });

        /*final Button  bbutton= findViewById(R.id.skipbuttonO);
        bbutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Phone1Verification.class));
            }
        });*/

        //Onboarding
        onboard_pager = (ViewPager) findViewById(R.id.pager_introduction);
        pager_indicator = (LinearLayout) findViewById(R.id.viewPagerCountDots);

        loadData();

        mAdapter = new OnBoard_Adapter(this,onBoardItems);
        onboard_pager.setAdapter(mAdapter);
        onboard_pager.setCurrentItem(0);
        onboard_pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                // Change the current position intimation

                for (int i = 0; i < dotsCount; i++) {
                    dots[i].setImageDrawable(ContextCompat.getDrawable(OnboardingActivity.this, R.drawable.non_selected_item_dot));
                }

                dots[position].setImageDrawable(ContextCompat.getDrawable(OnboardingActivity.this, R.drawable.selected_item_dot));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        setUiPageViewController();

    }

// Load data into the viewpager

    public void loadData()
    {

        int[] header = {R.string.ob_header1, R.string.ob_header2, R.string.ob_header3};
        int[] imageId = {R.mipmap.intro1, R.mipmap.intro2, R.mipmap.intro3};

        for(int i=0;i<imageId.length;i++)
        {
            OnBoardItem item=new OnBoardItem();
            item.setImageID(imageId[i]);
            item.setTitle(getResources().getString(header[i]));

            onBoardItems.add(item);
        }
    }



    // setup the
    private void setUiPageViewController() {

        dotsCount = mAdapter.getCount();
        dots = new ImageView[dotsCount];

        for (int i = 0; i < dotsCount; i++) {
            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(ContextCompat.getDrawable(OnboardingActivity.this, R.drawable.non_selected_item_dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            params.setMargins(6, 0, 6, 0);

            pager_indicator.addView(dots[i], params);
        }

        dots[0].setImageDrawable(ContextCompat.getDrawable(OnboardingActivity.this, R.drawable.selected_item_dot));
    }


    /*private void initView() {


        binding.button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Phone1Verification.class));
            }
        });
        /*binding.button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Main2Activity.class));
            }
        });
        binding.button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Main3Activity.class));
            }
        });
        binding.skipbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Phone1Verification.class));
            }
        });
    }*/


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MULTIPLE_PERMISSION_REQUEST) {
            if (grantResults.length > 0) {
               //todo if no location permission is given
            }
        }
    }

    private void ask_permissions() {
        int locationPermissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (locationPermissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MULTIPLE_PERMISSION_REQUEST);
        }
    }
}
