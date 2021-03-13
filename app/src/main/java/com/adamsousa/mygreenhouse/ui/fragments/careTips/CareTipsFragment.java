package com.adamsousa.mygreenhouse.ui.fragments.careTips;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.adamsousa.mygreenhouse.R;
import com.adamsousa.mygreenhouse.helper.ViewHelper;
import com.adamsousa.mygreenhouse.model.CareTipModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CareTipsFragment extends Fragment {

    @BindView(R.id.progress_view)
    ProgressBar progressView;
    @BindView(R.id.mainGrid)
    GridView gridView;
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

        getActivity().setTitle("Care Tips");

        if (savedInstanceState == null) {
            getFireBaseCareTips();
        }

        fab.hide();
        return view;
    }

    private void getFireBaseCareTips() {
        showProgressView(true, progressView);
        FirebaseFirestore fs = FirebaseFirestore.getInstance();

        Query query = fs.collection("care_tips");
        query.get().addOnCompleteListener(task -> {
            QuerySnapshot document = task.getResult();
            List<CareTipModel> careTips = new ArrayList<>();
            for (DocumentSnapshot snap : document.getDocuments()) {
                String title = snap.getString("title");
                String description = snap.getString("description");
                String pictureFilePath = snap.getString("picture_filepath");
                careTips.add(new CareTipModel(title, description, pictureFilePath));
            }
            setupGridView(careTips);
            showProgressView(false, progressView);
        });
    }

    private void setupGridView(List<CareTipModel> listOfItems) {
        CareTipsGridLayoutAdapter adapter = new CareTipsGridLayoutAdapter(context, listOfItems);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener((adapterView, view, position, l) -> {
            CareTipModel careTip = (CareTipModel) adapterView.getItemAtPosition(position);
            ImageView careImage = view.findViewById(R.id.care_tips_imageview);

            CareTipsFragmentDetails fragment = new CareTipsFragmentDetails();
            Bundle bundle = new Bundle();
            bundle.putParcelable("care_tip", careTip);
            fragment.setArguments(bundle);

            FragmentTransaction transaction = ViewHelper.addFragmentInFragment(this, fragment, R.id.frame_container);
            if (transaction != null) {
                transaction.addSharedElement(careImage, careImage.getTransitionName());
                //transaction.setCustomAnimations(android.R.anim.fade_out, android.R.anim.fade_in);
                transaction.commit();
            }
        });
    }

    private void showProgressView(boolean show, ProgressBar progressView) {
        ViewHelper.showProgress(context, progressView, show);
    }
}
