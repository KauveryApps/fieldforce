package com.kauveryhospital.fieldforce;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;


import com.google.android.material.tabs.TabLayout;
import com.kauveryhospital.fieldforce.UserAdmin.Manager;
import com.kauveryhospital.fieldforce.UseronlyAccess.Representative;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;


public class TabbedActivity extends AppCompatActivity {
    ViewPager viewPager;
    public static  final String PREFS_NAME="loginpref";
    TabLayout tabs;
    static TabbedActivity instance;
    String uname,pswd,uaa;
    private int[] tabIcons = {
            R.drawable.persons,
            R.drawable.admin
    };
ImageView loggedout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabbed);
        loggedout=findViewById(R.id.loggedout);

        SharedPreferences set=getSharedPreferences(PREFS_NAME,0);
        uname=set.getString("username","");
        pswd=set.getString("password","");
        uaa=set.getString("uaa","");
        Log.d("TAG", "onCreate: "+uaa+uname+pswd);

        viewPager = (ViewPager) findViewById(R.id.view_pager);


        tabs= (TabLayout) findViewById(R.id.tabs);
        if(uaa.equals("T")){
            setupViewPager(viewPager);
            tabs.setupWithViewPager(viewPager);
            setupTabIcons();
        }
        else{
            setupViewPager1(viewPager);
            tabs.setupWithViewPager(viewPager);
            setupTabIcons1();
        }
        loggedout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPrefManager.getInstance(getApplicationContext()).logout();
            }
        });
    }
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new Representative(), "REPRESENTATIVES");
        adapter.addFragment(new Manager(), "REGIONAL MANAGER");
        viewPager.setAdapter(adapter);
    }
    private void setupTabIcons() {
        tabs.getTabAt(0).setIcon(tabIcons[0]);
        tabs.getTabAt(1).setIcon(tabIcons[1]);

    }
    private void setupViewPager1(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new Representative(), "MEDICAL REPRESENTATIVES");
        viewPager.setAdapter(adapter);
    }
    private void setupTabIcons1() {
        tabs.getTabAt(0).setIcon(tabIcons[0]);


    }
    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
    public static TabbedActivity getInstance() {
        return instance;
    }
    public  void onBackPressed(){
        AlertDialog.Builder builder=new AlertDialog.Builder(TabbedActivity.this);
        builder.setTitle("");
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setMessage("Do you want to exit FieldForce?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        moveTaskToBack(true);
                        android.os.Process.killProcess(android.os.Process.myPid());
                        System.exit(1);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog=builder.create();
        alertDialog.show();
    }
}