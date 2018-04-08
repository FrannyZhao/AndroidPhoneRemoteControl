package com.frannyzhao.mqttlib.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.frannyzhao.mqttlib.MyAccessibilityService;
import com.frannyzhao.mqttlib.R;
import com.frannyzhao.mqttlib.utils.MLog;

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
        openAccessibility(MyAccessibilityService.class.getName(), this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

    }

    /**
     * 该辅助功能开关是否打开了
     * @param accessibilityServiceName：指定辅助服务名字
     */
    private boolean isAccessibilitySettingsOn(String accessibilityServiceName, Context context) {
        int accessibilityEnable = 0;
        String serviceName = context.getPackageName() + "/" +accessibilityServiceName;
        try {
            accessibilityEnable = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.ACCESSIBILITY_ENABLED, 0);
        } catch (Exception e) {
            MLog.e(TAG, "get accessibility enable failed, the err:" + e.getMessage());
        }
        if (accessibilityEnable == 1) {
            TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');
            String settingValue = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String accessibilityService = mStringColonSplitter.next();
                    MLog.d(TAG, "accessibilityService ", accessibilityService);
                    if (accessibilityService.equalsIgnoreCase(serviceName)) {
                        MLog.v(TAG, "Found the correct setting - ", serviceName, " accessibility is switched on!");
                        return true;
                    }
                }
            }
        } else {
            MLog.d(TAG,"Accessibility service ", serviceName, " disable");
        }
        return false;
    }

    /**
     * 跳转到系统设置页面开启辅助功能
     * @param accessibilityServiceName：指定辅助服务名字
     */
    private void openAccessibility(String accessibilityServiceName, Context context) {
        if (!isAccessibilitySettingsOn(accessibilityServiceName, context)) {
            // todo 提醒要打开辅助功能
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

}
