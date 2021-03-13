package com.adamsousa.mygreenhouse.ui.saveForms;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.adamsousa.mygreenhouse.R;
import com.adamsousa.mygreenhouse.model.LocationModel;
import com.adamsousa.mygreenhouse.ui.PictureCaptureActivity;
import com.adamsousa.mygreenhouse.ui.fragments.GlideApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SaveLocationActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.name_text_view_label)
    TextView nameTextViewLabel;
    @BindView(R.id.name_edit_text)
    EditText nameEditText;
    @BindView(R.id.name_edit_text_view)
    ConstraintLayout nameEditTextView;
    @BindView(R.id.plant_image_view)
    ImageView plantImageView;
    @BindView(R.id.plant_image_add_button)
    ImageButton plantImageAddButton;
    @BindView(R.id.plant_image_view_layout)
    RelativeLayout plantImageViewLayout;

    private String mImagePath;
    private LocationModel mLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_location);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);

        mLocation = getIntent().getParcelableExtra("location");

        if (savedInstanceState == null && mLocation != null) {
            nameEditText.setText(mLocation.getRoom());
            mImagePath = mLocation.getPictureFilePath();

            StorageReference storageReference = FirebaseStorage.getInstance().getReference(mLocation.getPictureFilePath());
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
                boolean error = false;
                String name = nameEditText.getText().toString();
                if (name.equals("")) {
                    error = true;
                    nameEditText.setError("Invalid name");
                } if (mImagePath == null) {
                    error = true;
                    Toast.makeText(this, "Please add photo", Toast.LENGTH_LONG).show();
                }

                if (!error){
                    saveLocation(name, mImagePath);
                }

                return true;
        }
        return false;
    }

    private void setOnClickListener() {
        plantImageAddButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, PictureCaptureActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("folder_name", "locations");
            intent.putExtras(bundle);
            startActivityForResult(intent, 1);
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            String imageLocation = "locations/";
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

    private void saveLocation(String locationName, String imageFilePath) {
        int totalCount = 0;
        if (mLocation != null) {
            totalCount = (int) mLocation.getPlantCount();
        }

        Map<String, Object> location = new HashMap<>();
        location.put("name", locationName);
        location.put("plant_count", totalCount);
        location.put("picture_filepath", imageFilePath);

        String childName;
        if (mLocation == null) {
            childName = locationName.toLowerCase().replace(" ", "_");
        } else {
            childName = mLocation.getId();
        }

        FirebaseFirestore fs = FirebaseFirestore.getInstance();
        fs.collection("locations").document(childName).set(location)
                .addOnSuccessListener(aVoid -> {
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    intent.putExtras(intent);
                    setResult(RESULT_OK, intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to save location", Toast.LENGTH_LONG).show();
                });
    }
}
