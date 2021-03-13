package com.adamsousa.mygreenhouse.ui.fragments.settings;

import android.app.Activity;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import com.adamsousa.mygreenhouse.R;
import com.adamsousa.mygreenhouse.custom.PlantCareJobScheduler;

import static android.content.Context.JOB_SCHEDULER_SERVICE;

public class SettingsFragment extends PreferenceFragmentCompat {

    private static final int PLANT_NOTIFICATIONS_ID = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTitle("Settings");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings_preference, rootKey);

        try {
            PackageInfo pInfo = getContext().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            findPreference(getString(R.string.key_app_version)).setSummary(pInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        final SwitchPreference plantNotificationsSwitch = (SwitchPreference) findPreference(this.getResources().getString(R.string.key_plant_notifications));

        plantNotificationsSwitch.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (plantNotificationsSwitch.isChecked()) {
                    removeNotificationsJob(getActivity());
                    plantNotificationsSwitch.setChecked(false);
                } else {
                    addNotificationsJob(getActivity());
                    plantNotificationsSwitch.setChecked(true);
                }
                return false;
            }
        });
    }

    public static void addNotificationsJob(Activity activity) {
        if (activity != null) {
            ComponentName componentName = new ComponentName(activity, PlantCareJobScheduler.class);
            JobInfo info = new JobInfo.Builder(PLANT_NOTIFICATIONS_ID, componentName)
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .setPersisted(true)
                    .build();
            //.requiresDeviceIdle(true)

            JobScheduler scheduler = (JobScheduler) activity.getSystemService(JOB_SCHEDULER_SERVICE);
            int resultCode = scheduler.schedule(info);
            if (resultCode == JobScheduler.RESULT_SUCCESS) {
                Log.d("JOB", "Job scheduled");
            } else {
                Log.d("JOB", "Job scheduling failed");
            }
        }
    }

    public static void removeNotificationsJob(Activity activity) {
        if (activity != null) {
            JobScheduler scheduler = (JobScheduler) activity.getSystemService(JOB_SCHEDULER_SERVICE);
            scheduler.cancel(PLANT_NOTIFICATIONS_ID);
        }
    }
}
