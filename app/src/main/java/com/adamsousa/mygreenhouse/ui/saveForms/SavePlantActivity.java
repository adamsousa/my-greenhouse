package com.adamsousa.mygreenhouse.ui.saveForms;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.adamsousa.mygreenhouse.R;
import com.adamsousa.mygreenhouse.model.LocationModel;
import com.adamsousa.mygreenhouse.model.PlantModel;
import com.adamsousa.mygreenhouse.ui.PictureCaptureActivity;
import com.adamsousa.mygreenhouse.ui.fragments.GlideApp;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SavePlantActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.name_text_view_label)
    TextView nameTextViewLabel;
    @BindView(R.id.name_edit_text)
    EditText nameEditText;
    @BindView(R.id.plant_image_view)
    ImageView plantImageView;
    @BindView(R.id.plant_image_add_button)
    ImageButton plantImageAddButton;
    @BindView(R.id.moisture_min)
    EditText moistureMin;
    @BindView(R.id.moisture_max)
    EditText moistureMax;
    @BindView(R.id.temperature_min)
    EditText temperatureMin;
    @BindView(R.id.temperature_max)
    EditText temperatureMax;
    @BindView(R.id.humidity_min)
    EditText humidityMin;
    @BindView(R.id.humidity_max)
    EditText humidityMax;
    @BindView(R.id.sunlight_min)
    EditText sunlightMin;
    @BindView(R.id.sunlight_max)
    EditText sunlightMax;
    @BindView(R.id.device_id_edit_text)
    EditText deviceIdEditText;

    private String mImagePath;
    private String mLocationId;
    private PlantModel mPlant;
    private LocationModel mLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_plant);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);

        mLocation = getIntent().getParcelableExtra("location");
        mLocationId = getIntent().getStringExtra("location_id");
        mPlant = getIntent().getParcelableExtra("plant");

        if (savedInstanceState == null && mPlant != null) {
            nameEditText.setText(mPlant.getName());
            deviceIdEditText.setText(mPlant.getDeviceId());
            moistureMax.setText(String.valueOf(mPlant.getMoistureMax()));
            moistureMin.setText(String.valueOf(mPlant.getMoistureMin()));
            temperatureMax.setText(String.valueOf(mPlant.getTemperatureMax()));
            temperatureMin.setText(String.valueOf(mPlant.getTemperatureMin()));
            humidityMax.setText(String.valueOf(mPlant.getHumidityMax()));
            humidityMin.setText(String.valueOf(mPlant.getHumidityMin()));
            sunlightMax.setText(String.valueOf(mPlant.getSunlightMax()));
            sunlightMin.setText(String.valueOf(mPlant.getHumidityMin()));
            mImagePath = mPlant.getPictureFilePath();

            StorageReference storageReference = FirebaseStorage.getInstance().getReference(mPlant.getPictureFilePath());
            loadPhotoIntoImageView(storageReference);
        }

        setOnClickListener();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.toolbarmenu_save:
                validatePlantCreation();
                return true;
        }
        return false;
    }

    public void validatePlantCreation() {
        String name = nameEditText.getText().toString();
        String deviceId = deviceIdEditText.getText().toString();

        boolean error = false;
        if (validateSetPointsEditText(moistureMax, moistureMin)) {
            error = true;
        }
        if (validateSetPointsEditText(temperatureMax, temperatureMin)) {
            error = true;
        }
        if (validateSetPointsEditText(humidityMax, humidityMin)) {
            error = true;
        }
        if (validateSetPointsEditText(sunlightMax, sunlightMin)) {
            error = true;
        }

        if (name.equals("")) {
            error = true;
            nameEditText.setError("Invalid name");
        }
        if (deviceId.equals("")) {
            error = true;
            deviceIdEditText.setError("Invalid device Id");
        }
        if (mImagePath == null) {
            error = true;
            Toast.makeText(this, "Please add photo", Toast.LENGTH_LONG).show();
        }

        if (!name.equals("") && mImagePath != null && !error) {
            int moistureMaxInt = Integer.parseInt(moistureMax.getText().toString());
            int moistureMinInt = Integer.parseInt(moistureMin.getText().toString());
            int temperatureMaxInt = Integer.parseInt(temperatureMax.getText().toString());
            int temperatureMinInt = Integer.parseInt(temperatureMin.getText().toString());
            int humidityMaxInt = Integer.parseInt(humidityMax.getText().toString());
            int humidityMinInt = Integer.parseInt(humidityMin.getText().toString());
            int sunlightMaxInt = Integer.parseInt(sunlightMax.getText().toString());
            int sunlightMinInt = Integer.parseInt(sunlightMin.getText().toString());

            saveLocation(name, mImagePath, deviceId, moistureMaxInt, moistureMinInt,
                    temperatureMaxInt, temperatureMinInt, humidityMaxInt, humidityMinInt,
                    sunlightMaxInt, sunlightMinInt);
        }
    }

    public boolean validateSetPointsEditText(EditText maxText, EditText minText) {
        String maxErrorText = null;
        String minErrorText = null;
        boolean valid = true;
        if (maxText.getText().toString().equals("")) {
            maxErrorText = "Invalid Set Point";
            valid = false;
        } else if (Integer.valueOf(maxText.getText().toString()) <= Integer.valueOf(minText.getText().toString())) {
            maxErrorText = "Max Set Point must be large than min";
            valid = false;
        }
        if (minText.getText().toString().equals("")) {
            minErrorText = "Invalid Set Point";
            valid = false;
        }
        if (maxErrorText != null) {
            maxText.setError(maxErrorText);
        }
        if (minErrorText != null) {
            minText.setError(minErrorText);
        }
        return !valid;
    }

    private void setOnClickListener() {
        plantImageAddButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, PictureCaptureActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("folder_name", "plants");
            intent.putExtras(bundle);
            startActivityForResult(intent, 1);
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            String imageLocation = "plants/";
            mImagePath = imageLocation + data.getStringExtra("image_path");
            String refString = data.getStringExtra("storage_ref");
            StorageReference reference = FirebaseStorage.getInstance().getReference(refString);
            loadPhotoIntoImageView(reference);
        } else {
            Toast.makeText(this, "Failed to add photo, please try again", Toast.LENGTH_LONG).show();
        }
    }

    private void loadPhotoIntoImageView(StorageReference reference) {
        GlideApp.with(this)
                .load(reference)
                .into(plantImageView)
                .onLoadFailed(getDrawable(R.drawable.ic_blurred_image));
    }

    private void saveLocation(String plantName, String imageFilePath, String deviceId, int moistureMax, int moistureMin,
                              int temperatureMax, int temperatureMin, int humidityMax, int humidityMin,
                              int sunlightMax, int sunlightMin) {
        Map<String, Object> plant = new HashMap<>();
        plant.put("name", plantName);
        plant.put("picture_filepath", imageFilePath);
        plant.put("device_id", deviceId);
        plant.put("moisture_max", moistureMax);
        plant.put("moisture_min", moistureMin);
        plant.put("temperature_max", temperatureMax);
        plant.put("temperature_min", temperatureMin);
        plant.put("humidity_max", humidityMax);
        plant.put("humidity_min", humidityMin);
        plant.put("sunlight_max", sunlightMax);
        plant.put("sunlight_min", sunlightMin);

        String childName;
        if (mPlant == null) {
            childName = plantName.toLowerCase().replace(" ", "_");
        } else {
            childName = mPlant.getId();
        }

        FirebaseFirestore fs = FirebaseFirestore.getInstance();
        fs.collection("locations").document(mLocationId).collection("plants").document(childName).set(plant)
                .addOnSuccessListener(aVoid -> {
                    if (mPlant == null) {
                        mLocation.setPlantCount(mLocation.getPlantCount() + 1);
                        Map<String, Object> location = new HashMap<>();
                        location.put("name", mLocation.getRoom());
                        location.put("picture_filepath", mLocation.getPictureFilePath());
                        location.put("plant_count", mLocation.getPlantCount());
                        fs.collection("locations").document(mLocationId).set(location)
                                .addOnSuccessListener(aVoid1 -> {
                                    setResult(RESULT_OK);
                                    finish();
                                });
                    } else {
                        setResult(RESULT_OK);
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to save plant", Toast.LENGTH_LONG).show();
                });
    }
}
