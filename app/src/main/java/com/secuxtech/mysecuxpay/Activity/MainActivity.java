package com.secuxtech.mysecuxpay.Activity;



import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.secuxtech.mysecuxpay.Fragment.LoginFragment;

import com.secuxtech.mysecuxpay.Fragment.RegisterFragment;
import com.secuxtech.mysecuxpay.Model.Setting;
import com.secuxtech.mysecuxpay.R;



public class MainActivity extends BaseActivity {


    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private LoginFragment mLoginFragment = new LoginFragment();
    private RegisterFragment mRegisterFragment = new RegisterFragment();

    boolean mShowCoinTokenSelList = false;

    private Dialog mCoinTokenSelDialog;

    private ViewPager.OnPageChangeListener mPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            if (position == 1){
                mRegisterFragment.loadCoinTokenArray();
            }
            mTabLayout.getTabAt(position).select();
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    private TabLayout.OnTabSelectedListener mTabSelListener = new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            //int tab = mTabLayout.getCurrentTab();
            //View view = mTabLayout.getTabWidget().getChildAt(tab).setBackgroundColor(Color.CYAN);
            /*
            for(int n = 0; n < mTabLayout.getTabCount(); n++){

                View tab = ((ViewGroup)mTabLayout.getChildAt(0)).getChildAt(n);

                if(tab != null){

                }
            }



            for (int i = 0; i < mTabLayout.getTabCount(); i++) {
                if (i == tab.getPosition()) {
                    mTabLayout.getTabAt(i).getCustomView().setBackgroundColor(Color.parseColor("#198C19"));
                } else {
                    mTabLayout.getTabAt(i).getCustomView().setBackgroundColor(Color.parseColor("#f4f4f4"));
                }
            }

            //tab.getCustomView().setBackgroundColor(Color.parseColor("#FFFFFF"));
*/
            mViewPager.setCurrentItem(tab.getPosition());
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {

        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //getWindow().setNavigationBarColor(Color.TRANSPARENT); // Navigation bar the soft bottom of some phones like nexus and some Samsung note series
        //getWindow().setStatusBarColor(Color.TRANSPARENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = this.getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        //ActionBar actionBar = getSupportActionBar();
        //actionBar.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //actionBar.setDisplayShowCustomEnabled(false);
        //actionBar.setTitle("");
        //actionBar.hide();

        mRegisterFragment.loadCoinTokenArray();

        mTabLayout = findViewById(R.id.tab_main_login_and_register);
        mTabLayout.addOnTabSelectedListener(mTabSelListener);
        mViewPager = findViewById(R.id.viewPage_main_tab);
        mViewPager.addOnPageChangeListener(mPageChangeListener);
        mViewPager.setAdapter(new TheFragmentAdapter(getSupportFragmentManager()));

    }


    /*
    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        //TabLayout里的TabItem被选中的时候触发
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        //viewPager滑动之后显示触发
        mTabLayout.getTabAt(position).select();
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
    */

    public void onCoinTokenSelClick(View v){
        mRegisterFragment.toggleCoinTokenListView();

    }


    public class TheFragmentAdapter extends FragmentPagerAdapter {

        public TheFragmentAdapter(FragmentManager fm) {
            super(fm, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @Override
        public Fragment getItem(int i) {
            switch (i) {
                case 0:
                    return mLoginFragment;
                case 1:
                    return mRegisterFragment;

            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }


    }

}
