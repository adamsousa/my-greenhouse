package com.adamsousa.mygreenhouse.custom;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;

import com.adamsousa.mygreenhouse.R;
import com.adamsousa.mygreenhouse.model.LocationModel;
import com.adamsousa.mygreenhouse.model.PhotonCaptureEvent;
import com.adamsousa.mygreenhouse.model.PlantModel;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class PlantCareJobScheduler extends JobService {

    private static final String TAG = "PlantCareService";

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.d(TAG, "Job started");
        doBackgroundWork(jobParameters);
        return true;
    }

    private void doBackgroundWork(final JobParameters params) {
        NotificationHelper notificationHelper = new NotificationHelper(this);
        FirebaseFirestore fs = FirebaseFirestore.getInstance();

        Query queryLocation = fs.collection("locations");
        queryLocation.get().addOnCompleteListener(task -> {
            QuerySnapshot documentLocation = task.getResult();
            if (documentLocation != null) {
                List<LocationModel> locations = new ArrayList<>();
                for (DocumentSnapshot snap : documentLocation.getDocuments()) {
                    String locationName = snap.getString("name");
                    Double plantCount = snap.getDouble("plant_count");
                    String pictureFilePath = snap.getString("picture_filepath");
                    String id = snap.getId();
                    locations.add(new LocationModel(id, locationName, plantCount, pictureFilePath));
                }
                checkAllLocationsPlants(locations, fs, notificationHelper, params);
            } else  {
                jobFinished(params, true);
            }
        });
    }

    private void checkAllLocationsPlants(List<LocationModel> locations, FirebaseFirestore fs, NotificationHelper notificationHelper, JobParameters params) {
        for (LocationModel location : locations) {
            CollectionReference plantsCollectionReference = fs.collection("locations").document(location.getId()).collection("plants");
            plantsCollectionReference.get().addOnCompleteListener(task1 -> {
                QuerySnapshot documentPlant = task1.getResult();
                if (documentPlant != null) {
                    List<PlantModel> plants = new ArrayList<>();
                    for (DocumentSnapshot snap : documentPlant.getDocuments()) {
                        String id = snap.getString("id");
                        String name = snap.getString("name");
                        String deviceId = snap.getString("device_id");
                        String picturePath = snap.getString("picture_filepath");
                        Integer moistureMax = snap.getDouble("moisture_max").intValue();
                        Integer moistureMin = snap.getDouble("moisture_min").intValue();
                        Integer temperatureMax = snap.getDouble("temperature_max").intValue();
                        Integer temperatureMin = snap.getDouble("temperature_min").intValue();
                        Integer humidityMax = snap.getDouble("humidity_max").intValue();
                        Integer humidityMin = snap.getDouble("humidity_min").intValue();
                        Integer sunlightMax = snap.getDouble("sunlight_max").intValue();
                        Integer sunlightMin = snap.getDouble("sunlight_min").intValue();
                        plants.add(new PlantModel(id, name, deviceId, picturePath, moistureMax, moistureMin, temperatureMax, temperatureMin, humidityMax, humidityMin, sunlightMax, sunlightMin));
                    }
                    checkAllPlantLatestCaptures(plants, fs, notificationHelper, params);
                } else {
                    jobFinished(params, true);
                }
            });
        }
        jobFinished(params, false);
    }

    private void checkAllPlantLatestCaptures(List<PlantModel> plants, FirebaseFirestore fs, NotificationHelper notificationHelper, JobParameters params) {
        for (PlantModel plant : plants) {
            Query queryCapture = fs.collection(plant.getDeviceId()).limit(1).orderBy("published_at", Query.Direction.DESCENDING);
            queryCapture.get().addOnCompleteListener(task2 -> {
                QuerySnapshot querySnapshotCapture = task2.getResult();
                if (querySnapshotCapture != null) {
                    if (querySnapshotCapture.getDocuments().size() > 0) {
                        DocumentSnapshot documentCapture = querySnapshotCapture.getDocuments().get(0);
                        byte[] bytes = documentCapture.getBlob("data").toBytes();
                        String[] values = parseValues(new String(bytes, StandardCharsets.UTF_8));
                        plant.setLatestCapture(new PhotonCaptureEvent(Double.valueOf(values[0]), Double.valueOf(values[1]), Double.valueOf(values[2]), Double.valueOf(values[3]), null));

                        checkPlantSetPoints(plant, notificationHelper);
                    }
                } else {
                    jobFinished(params, true);
                }
            });
        }
    }

    private void checkPlantSetPoints(PlantModel plant, NotificationHelper notificationHelper) {
        if (isGreaterThanMaxSetPoint(plant.getLatestCapture().getMoisture(), plant.getMoistureMax())) {
            notificationHelper.buildPlantCareMoistureNotification(plant.getName(), true);
        } else if (isLessThanMinSetPoint(plant.getLatestCapture().getMoisture(), plant.getMoistureMin())) {
            notificationHelper.buildPlantCareMoistureNotification(plant.getName(), false);
        }

        if (isGreaterThanMaxSetPoint(plant.getLatestCapture().getTemperature(), plant.getTemperatureMax())) {
            notificationHelper.buildPlantCareTemperatureNotification(plant.getName(), true);
        } else if (isLessThanMinSetPoint(plant.getLatestCapture().getTemperature(), plant.getTemperatureMin())) {
            notificationHelper.buildPlantCareTemperatureNotification(plant.getName(), false);
        }

        if (isGreaterThanMaxSetPoint(plant.getLatestCapture().getHumidity(), plant.getHumidityMax())) {
            notificationHelper.buildPlantCareHumidityNotification(plant.getName(), true);
        } else if (isLessThanMinSetPoint(plant.getLatestCapture().getHumidity(), plant.getHumidityMin())) {
            notificationHelper.buildPlantCareHumidityNotification(plant.getName(), false);
        }

        if (isGreaterThanMaxSetPoint(plant.getLatestCapture().getSunlight(), plant.getSunlightMax())) {
            notificationHelper.buildPlantCareSunlightNotification(plant.getName(), true);
        } else if (isLessThanMinSetPoint(plant.getLatestCapture().getSunlight(), plant.getSunlightMin())) {
            notificationHelper.buildPlantCareSunlightNotification(plant.getName(), false);
        }
    }

    private boolean isGreaterThanMaxSetPoint(double value, double setPoint) {
        return value > setPoint;
    }

    private boolean isLessThanMinSetPoint(double value, double setPoint) {
        return value < setPoint;
    }

    private String[] parseValues(String values) {
        return values.split(",");
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Log.d(TAG, "Job cancelled");
        return true;
    }
}

