package com.adamsousa.mygreenhouse.ui.fragments.locations;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.adamsousa.mygreenhouse.R;
import com.adamsousa.mygreenhouse.helper.ViewHelper;
import com.adamsousa.mygreenhouse.model.LocationModel;
import com.adamsousa.mygreenhouse.ui.saveForms.SaveLocationActivity;
import com.adamsousa.mygreenhouse.ui.fragments.plants.PlantsFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;

public class LocationsFragment extends Fragment {

    @BindView(R.id.mainGrid)
    GridView gridView;
    @BindView(R.id.progress_view)
    ProgressBar progressView;
    @BindView(R.id.fab)
    FloatingActionButton fab;

    private Context context;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.setRetainInstance(true);
        View view = inflater.inflate(R.layout.fragment_grid_layout, container, false);
        ButterKnife.bind(this, view);
        context = view.getContext();

        getActivity().setTitle("My Locations");

        if (savedInstanceState == null) {
            getFireBaseLocations();
        }

        fab.setOnClickListener(view1 -> {
           Intent intent = new Intent(getActivity(), SaveLocationActivity.class);
           startActivityForResult(intent, 1);
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if (resultCode == RESULT_OK) {
            getFireBaseLocations();
        }
    }

    public void getFireBaseLocations() {
        gridView.setVisibility(View.INVISIBLE);
        showProgressView(true, progressView);
        FirebaseFirestore fs = FirebaseFirestore.getInstance();

        Query query = fs.collection("locations");
        query.get().addOnCompleteListener(task -> {
            QuerySnapshot document = task.getResult();
            List<LocationModel> locations = new ArrayList<>();
            for (DocumentSnapshot snap : document.getDocuments()) {
                String locationName = snap.getString("name");
                Double plantCount = snap.getDouble("plant_count");
                String pictureFilePath = snap.getString("picture_filepath");
                String id = snap.getId();
                locations.add(new LocationModel(id, locationName, plantCount, pictureFilePath));
            }
            setupGridView(locations);
            showProgressView(false, progressView);
            gridView.setVisibility(View.VISIBLE);
        });
    }

    private void setupGridView(List<LocationModel> listOfItems) {
        LocationsGridLayoutAdapter adapter = new LocationsGridLayoutAdapter(context, listOfItems);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener((adapterView, view, position, l) -> {
            LocationModel location = (LocationModel) adapterView.getItemAtPosition(position);

            PlantsFragment fragment = new PlantsFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelable("location", location);
            fragment.setArguments(bundle);

            FragmentTransaction transaction = ViewHelper.addFragmentInFragment(this, fragment,  R.id.frame_container);
            if (transaction != null) {
                transaction.setCustomAnimations(android.R.anim.fade_out, android.R.anim.fade_in);
                transaction.commit();
            }
        });
    }

    private void showProgressView(boolean show, ProgressBar progressView) {
        ViewHelper.showProgress(context, progressView, show);
    }
}
