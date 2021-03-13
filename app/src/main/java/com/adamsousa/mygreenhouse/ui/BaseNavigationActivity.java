package com.adamsousa.mygreenhouse.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;

import com.adamsousa.mygreenhouse.R;
import com.adamsousa.mygreenhouse.helper.ViewHelper;
import com.adamsousa.mygreenhouse.ui.fragments.careTips.CareTipsFragmentDetails;
import com.adamsousa.mygreenhouse.ui.fragments.locations.LocationsFragment;
import com.adamsousa.mygreenhouse.ui.fragments.plants.PlantsFragmentDetails;
import com.adamsousa.mygreenhouse.ui.fragments.careTips.CareTipsFragment;
import com.adamsousa.mygreenhouse.ui.fragments.settings.SettingsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BaseNavigationActivity extends AppCompatActivity implements PlantsFragmentDetails.HideMainToolbar, CareTipsFragmentDetails.HideMainToolbar {

    @BindView(R.id.base_toolbar)
    Toolbar toolbar;
    @BindView(R.id.frame_container)
    FrameLayout frameContainer;
    @BindView(R.id.nav_view)
    BottomNavigationView navView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_navigation);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
            navView.setSelectedItemId(R.id.navigation_home);

            setNotifications();
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
        switch (item.getItemId()) {
            case R.id.navigation_home:
                loadFragment(new LocationsFragment());
                return true;
            case R.id.navigation_dashboard:
                loadFragment(new CareTipsFragment());
                return true;
            case R.id.navigation_notifications:
                loadFragment(new SettingsFragment());
                return true;
        }
        return false;
    };

    public void loadFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(frameContainer.getId(), fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onBackPressed(){
        if (getSupportFragmentManager().getBackStackEntryCount() == 1){
            finish();
        }
        else {
            super.onBackPressed();
        }
    }

    private void setNotifications() {
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this /* Activity context */);
        boolean notificationOn = sharedPreferences.getBoolean(getResources().getString(R.string.key_plant_notifications), false);
        if (notificationOn) {
            SettingsFragment.addNotificationsJob(this);
        } else {
            SettingsFragment.removeNotificationsJob(this);
        }
    }

    @Override
    public void hideToolbar(boolean show) {
        if (show) {
            toolbar.setVisibility(View.VISIBLE);
        } else {
            toolbar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode,resultCode,data);
    }
}
