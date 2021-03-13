package com.adamsousa.mygreenhouse.custom;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;

import androidx.core.app.NotificationCompat;

import com.adamsousa.mygreenhouse.R;
import com.adamsousa.mygreenhouse.ui.BaseNavigationActivity;

public class NotificationHelper extends ContextWrapper {
    public static final String moistureName = "moisture";
    public static final String temperatureName = "temperature";
    public static final String humidityName = "humidity";
    public static final String sunlightName = "sunlight";

    private NotificationManager mManager;

    //API 26 and Higher ONLY

    public NotificationHelper(Context base) {
        super(base);
        getManager();
    }

    public void validateCreatingChannel(NotificationManager manager, String channelId, String channelName) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel(manager, channelId, channelName);
        }
    }

    public String generateTimeStampId() {
        int id = (int) SystemClock.uptimeMillis();
        return String.valueOf(id);
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createChannel(NotificationManager manager, String channelId, String channelName) {
        NotificationChannel channel1 = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
        channel1.enableLights(true);
        channel1.enableVibration(true);
        channel1.setLightColor(R.color.colorPrimary);
        channel1.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        //Add more channels below

        manager.createNotificationChannel(channel1);
    }

    public NotificationManager getManager() {
        if(mManager == null) {
            mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return mManager;
    }

    public void buildPlantCareMoistureNotification(String plantName, boolean highOrLow) {
        String id = generateTimeStampId();
        validateCreatingChannel(mManager, id, moistureName);
        buildPlantCareNotification(plantName, highOrLow, "moisture", id);
    }

    public void buildPlantCareTemperatureNotification(String plantName, boolean highOrLow) {
        String id = generateTimeStampId();
        validateCreatingChannel(mManager, id, temperatureName);
        buildPlantCareNotification(plantName, highOrLow, "temperature", id);
    }

    public void buildPlantCareHumidityNotification(String plantName, boolean highOrLow) {
        String id = generateTimeStampId();
        validateCreatingChannel(mManager, id, humidityName);
        buildPlantCareNotification(plantName, highOrLow, "humidity", id);
    }

    public void buildPlantCareSunlightNotification(String plantName, boolean highOrLow) {
        String id = generateTimeStampId();
        validateCreatingChannel(mManager, id, sunlightName);
        buildPlantCareNotification(plantName, highOrLow, "sunlight", id);
    }

    public void buildPlantCareNotification(String plantName, boolean highOrLow, String measureType, String id) {
         buildNotificationChannel(plantName + " Care Warning!", "Your plant " +plantName+ " has " + (highOrLow ? "high " : "low ")+ measureType + " levels!", id);
    }

    public void buildNotificationChannel(String title, String description, String id) {
        Intent notificationIntent = new Intent(getApplicationContext(), BaseNavigationActivity.class);

        Notification notification = new NotificationCompat.Builder(getApplicationContext(), id)
                .setContentTitle(title)
                .setContentText(description)
                .setSmallIcon(R.drawable.ic_plant_icon)
                .setContentIntent(PendingIntent.getActivity(this, 0, notificationIntent, 0))
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(Notification.DEFAULT_ALL)
                .build();

        mManager.notify(Integer.parseInt(generateTimeStampId()), notification);
    }
}
