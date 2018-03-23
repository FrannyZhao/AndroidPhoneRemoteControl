package com.frannyzhao.mqttlib.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.frannyzhao.mqttlib.R;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private List<Fragment> fragments = new ArrayList<>();
    private NoScrollViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MainAdapter adapter = new MainAdapter(getSupportFragmentManager());
        mViewPager = findViewById(R.id.main_view_pager);
        mViewPager.setNoScroll(true);
        mViewPager.setOffscreenPageLimit(2);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        fragments.add(HomeFragment.newInstance());
        fragments.add(SettingsFragment.newInstance());
        fragments.add(TestFragment.newInstance());
        adapter.setFragments(fragments);
        mViewPager.setAdapter(adapter);
        mViewPager.setCurrentItem(0);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    int lastClickedItemId = 0;
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        int clickedItemId = item.getItemId();
                        if (clickedItemId != lastClickedItemId) {
                            switch (item.getItemId()) {
                                case R.id.action_nav_bar_home:
                                    mViewPager.setCurrentItem(MainAdapter.HOME_PAGE);
                                    break;
                                case R.id.action_nav_bar_settings:
                                    mViewPager.setCurrentItem(MainAdapter.SETTINGS_PAGE);
                                    break;
                                case R.id.action_nav_bar_test:
                                    mViewPager.setCurrentItem(MainAdapter.TEST_PAGE);
                                    break;
                                default:
                                    break;
                            }
                        }
                        lastClickedItemId = clickedItemId;
                        return true;
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

    }






}
