package com.adamsousa.mygreenhouse.ui.fragments.plants;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.adamsousa.mygreenhouse.R;
import com.adamsousa.mygreenhouse.helper.ViewHelper;
import com.adamsousa.mygreenhouse.model.LocationModel;
import com.adamsousa.mygreenhouse.model.PhotonCaptureEvent;
import com.adamsousa.mygreenhouse.model.PlantModel;
import com.adamsousa.mygreenhouse.ui.saveForms.SaveLocationActivity;
import com.adamsousa.mygreenhouse.ui.saveForms.SavePlantActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;

public class PlantsFragment extends Fragment {

    @BindView(R.id.progress_view)
    ProgressBar progressView;
    @BindView(R.id.mainGrid)
    GridView gridView;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.grid_empty_textview)
    TextView gridEmptyTextview;

    private Context context;
    private LocationModel mLocation;
    private PlantsGridLayoutAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.setRetainInstance(true);
        View view = inflater.inflate(R.layout.fragment_grid_layout, container, false);
        ButterKnife.bind(this, view);
        context = view.getContext();
        setHasOptionsMenu(true);

        if (savedInstanceState == null) {
            mLocation = getArguments().getParcelable("location");
        } else {
            mLocation = savedInstanceState.getParcelable("location");
        }
        getFireBaseLocationPlants(mLocation);

        getActivity().setTitle(mLocation.getRoom() + " Plants");

        fab.setOnClickListener(view1 -> {
            Intent intent = new Intent(getActivity(), SavePlantActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("location_id", mLocation.getId());
            bundle.putParcelable("location", mLocation);
            intent.putExtras(bundle);
            startActivityForResult(intent, 1);
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.toolbar_edit, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.toolbarmenu_edit:
                Intent intent = new Intent(getActivity(), SaveLocationActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("location", mLocation);
                intent.putExtras(bundle);
                startActivityForResult(intent, 2);
                return true;
        }
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 2) {
                getFragmentManager().popBackStack();
            } else {
                getFireBaseLocationPlants(mLocation);
            }
        }
    }

    public void getFireBaseLocationPlants(LocationModel location) {
        showProgressView(true, progressView);
        FirebaseFirestore fs = FirebaseFirestore.getInstance();

        CollectionReference plantColl = fs.collection("locations").document(location.getId()).collection("plants");
        plantColl.get().addOnCompleteListener(task -> {
            QuerySnapshot document = task.getResult();
            List<PlantModel> plants = new ArrayList<>();
            for (DocumentSnapshot snap : document.getDocuments()) {
                String id = snap.getId();
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
                PlantModel plant = new PlantModel(id, name, deviceId, picturePath, moistureMax, moistureMin, temperatureMax, temperatureMin
                        , humidityMax, humidityMin, sunlightMax, sunlightMin);
                plants.add(plant);

                Query query = fs.collection(deviceId).limit(1).orderBy("published_at", Query.Direction.DESCENDING);
                query.get().addOnCompleteListener(task1 -> {
                    QuerySnapshot querySnapshot = task1.getResult();
                    plant.setCaptureRetrievalDone(true);
                    if (querySnapshot.getDocuments().size() > 0) {
                        DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0);
                        byte[] bytes = documentSnapshot.getBlob("data").toBytes();
                        String[] values = parseValues(new String(bytes, StandardCharsets.UTF_8));
                        plant.setLatestCapture(new PhotonCaptureEvent(Double.valueOf(values[0]), Double.valueOf(values[1]), Double.valueOf(values[2]), Double.valueOf(values[3]), null));
                    }
                    mAdapter.notifyDataSetChanged();
                });
            }
            setupGridView(plants);
            if (plants.size() == 0) {
                gridEmptyTextview.setVisibility(View.VISIBLE);
            } else {
                gridEmptyTextview.setVisibility(View.GONE);
            }
            showProgressView(false, progressView);
        });
    }

    private String[] parseValues(String values) {
        return values.split(",");
    }

    private void setupGridView(List<PlantModel> listOfItems) {
        mAdapter = new PlantsGridLayoutAdapter(context, listOfItems);
        gridView.setAdapter(mAdapter);

        gridView.setOnItemClickListener((adapterView, view, position, l) -> {
            PlantModel plant = (PlantModel) adapterView.getItemAtPosition(position);
            ImageView plantImage = view.findViewById(R.id.plant_imageview);

            if (plant.getLatestCapture() != null) {
                PlantsFragmentDetails fragment = new PlantsFragmentDetails();
                Bundle bundle = new Bundle();
                bundle.putString("location_id", mLocation.getId());
                bundle.putParcelable("plant", plant);
                fragment.setArguments(bundle);

                FragmentTransaction transaction = ViewHelper.addFragmentInFragment(this, fragment, R.id.frame_container);
                if (transaction != null) {
                    transaction.addSharedElement(plantImage, plantImage.getTransitionName());
                    //transaction.setCustomAnimations(android.R.anim.fade_out, android.R.anim.fade_in);
                    transaction.commit();
                }
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelable("location", mLocation);
    }

    private void showProgressView(boolean show, ProgressBar progressView) {
        ViewHelper.showProgress(context, progressView, show);
    }
}
