package com.adamsousa.mygreenhouse.ui.fragments.plants;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.transition.TransitionInflater;

import com.adamsousa.mygreenhouse.R;
import com.adamsousa.mygreenhouse.custom.HourAxisValueFormatter;
import com.adamsousa.mygreenhouse.helper.ViewHelper;
import com.adamsousa.mygreenhouse.model.LocationModel;
import com.adamsousa.mygreenhouse.model.PhotonCaptureEvent;
import com.adamsousa.mygreenhouse.model.PlantModel;
import com.adamsousa.mygreenhouse.ui.saveForms.SavePlantActivity;
import com.adamsousa.mygreenhouse.ui.fragments.GlideApp;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;

public class PlantsFragmentDetails extends Fragment {

    @BindView(R.id.plant_details_imageview)
    ImageView toolbarImage;
    @BindView(R.id.plant_details_moisture_chart)
    LineChart moistureChart;
    @BindView(R.id.plant_details_temperature_chart)
    LineChart temperatureChart;
    @BindView(R.id.plant_details_humidity_chart)
    LineChart humidityChart;
    @BindView(R.id.plant_details_sunlight_chart)
    LineChart sunlightChart;
    @BindView(R.id.plant_details_toolbar)
    Toolbar toolbar;
    @BindView(R.id.plant_details_moisture_label)
    View moistureCurrentLabel;
    @BindView(R.id.plant_details_temperature_label)
    View temperatureCurrentLabel;
    @BindView(R.id.plant_details_humidity_label)
    View humidityCurrentLabel;
    @BindView(R.id.plant_details_sunlight_label)
    View sunlightCurrentLabel;
    @BindView(R.id.plant_details_scroll_view)
    NestedScrollView scrollView;
    @BindView(R.id.plant_details_full_layout)
    RelativeLayout fullLayout;

    private Context context;
    private String mLocationId;
    private PlantModel mPlant;
    private HideMainToolbar hideMainToolbar;

    public interface HideMainToolbar {
        void hideToolbar(boolean show);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        hideMainToolbar.hideToolbar(false);

        mLocationId = getArguments().getString("location_id");
        mPlant = getArguments().getParcelable("plant");

        postponeEnterTransition();
        setSharedElementEnterTransition(TransitionInflater.from(getContext()).inflateTransition(android.R.transition.move));
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.toolbar_edit, menu);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        StorageReference storageReference = FirebaseStorage.getInstance().getReference(mPlant.getPictureFilePath());

        GlideApp.with(view)
                .load(storageReference)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        startPostponedEnterTransition();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        startPostponedEnterTransition();
                        return false;
                    }
                })
                .into(toolbarImage);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.setRetainInstance(true);
        View view = inflater.inflate(R.layout.fragment_plant_details, container, false);
        ButterKnife.bind(this, view);
        context = view.getContext();

        scrollView.setVisibility(View.INVISIBLE);

        toolbar.inflateMenu(R.menu.toolbar_edit);
        toolbar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.toolbarmenu_edit:
                    Intent intent = new Intent(getActivity(), SavePlantActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("location_id", mLocationId);
                    bundle.putParcelable("plant", mPlant);
                    intent.putExtras(bundle);
                    startActivityForResult(intent, 1);
                    return true;
            }
            return false;
        });

        getFireBasePlantCaptures(mPlant);

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            getFragmentManager().popBackStack();
        }
    }

    private void getFireBasePlantCaptures(PlantModel plant) {
        //showProgressView(true, progressView);
        FirebaseFirestore fs = FirebaseFirestore.getInstance();

        Query query = fs.collection(plant.getDeviceId()).orderBy("published_at", Query.Direction.ASCENDING);
        query.get().addOnCompleteListener(task1 -> {
            QuerySnapshot querySnapshot = task1.getResult();
            List<PhotonCaptureEvent> plantCaptureDataList = new ArrayList<>();
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                byte[] bytes = document.getBlob("data").toBytes();
                Date publishedAt = null;
                try {
                    publishedAt = inputFormat.parse(document.getString("published_at"));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String[] values = parseValues(new String(bytes, StandardCharsets.UTF_8));
                plantCaptureDataList.add(new PhotonCaptureEvent(Double.valueOf(values[0]), Double.valueOf(values[1]), Double.valueOf(values[2]), Double.valueOf(values[3]), publishedAt));
            }
            setupCurrentPlantDataLabels(plantCaptureDataList.get(plantCaptureDataList.size() - 1));
            setGraphs(plantCaptureDataList);
            toolbar.setTitle(mPlant.getName());
            scrollView.setVisibility(View.VISIBLE);
        });
    }

    private void setupCurrentPlantDataLabels(PhotonCaptureEvent currentCapture) {
        double moistureRecent = currentCapture.getMoisture();
        int moistureTest = ViewHelper.calculateShowWarning(moistureRecent, mPlant.getMoistureMax(), mPlant.getMoistureMin());
        setupPlantDataLabel(moistureCurrentLabel, R.drawable.ic_moisture_icon,
                calculateStringResource(moistureTest), moistureTest != 0);

        double temperatureRecent = currentCapture.getTemperature();
        int temperatureTest = ViewHelper.calculateShowWarning(temperatureRecent, mPlant.getTemperatureMax(), mPlant.getTemperatureMin());
        setupPlantDataLabel(temperatureCurrentLabel, R.drawable.ic_temperature_icon,
                Math.round(mPlant.getLatestCapture().getTemperature()) + "°F", temperatureTest != 0);

        double humidityRecent = currentCapture.getHumidity();
        int humidityTest = ViewHelper.calculateShowWarning(humidityRecent, mPlant.getHumidityMax(), mPlant.getHumidityMin());
        setupPlantDataLabel(humidityCurrentLabel, R.drawable.ic_humidity_icon,
                calculateStringResource(humidityTest), humidityTest != 0);

        double sunlightRecent = currentCapture.getSunlight();
        int sunlightTest = ViewHelper.calculateShowWarning(sunlightRecent, mPlant.getSunlightMax(), mPlant.getSunlightMin());
        setupPlantDataLabel(sunlightCurrentLabel, R.drawable.ic_sunlight_icon,
                calculateStringResource(sunlightTest), sunlightTest != 0);
    }

    private String calculateStringResource(int value) {
        String display = "GOOD";
        if (value == ViewHelper.VALUE_OVER_MAX) {
            display = "HIGH";
        } else if (value == ViewHelper.VALUE_UNDER_MIN) {
            display = "LOW";
        }
        return display;
    }

    private void setupPlantDataLabel(View view, int iconId, String value, boolean showError) {
        ImageView imageView = view.findViewById(R.id.plant_info_icon);
        Glide.with(context)
                .load(context.getDrawable(iconId))
                .into(imageView);
        TextView valueTextView = view.findViewById(R.id.plant_info_value);
        valueTextView.setText(String.valueOf(value));
        if (showError) {
            ImageView errorIcon = view.findViewById(R.id.plant_info_error_icon);
            errorIcon.setVisibility(View.VISIBLE);
        }
    }

    private void setGraphs(List<PhotonCaptureEvent> photonCaptureEvents) {
        List<Entry> moistureEntries = new ArrayList<>();
        List<Entry> temperatureEntries = new ArrayList<>();
        List<Entry> humidityEntries = new ArrayList<>();
        List<Entry> sunlightEntries = new ArrayList<>();

        long referenceTime = (photonCaptureEvents.get(0).getPublishedAt().getTime() / 1000);
        long latestTime = (photonCaptureEvents.get(photonCaptureEvents.size() - 1).getPublishedAt().getTime() / 1000);

        for (PhotonCaptureEvent capture : photonCaptureEvents) {
            float unixTime = (float) (capture.getPublishedAt().getTime() / 1000) - referenceTime;
            moistureEntries.add(new Entry(unixTime, (float) capture.getMoisture()));
            temperatureEntries.add(new Entry(unixTime, (float) capture.getTemperature()));
            humidityEntries.add(new Entry(unixTime, (float) capture.getHumidity()));
            sunlightEntries.add(new Entry(unixTime, (float) capture.getSunlight()));
        }

        setupGraph(moistureChart, moistureEntries, referenceTime);
        setupGraph(temperatureChart, temperatureEntries, referenceTime);
        setupGraph(humidityChart, humidityEntries, referenceTime);
        setupGraph(sunlightChart, sunlightEntries, referenceTime);
    }

    private void setupGraph(LineChart chart, List<Entry> entries, long referenceTime) {
        ValueFormatter xAxisFormatter = new HourAxisValueFormatter(referenceTime);
        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(xAxisFormatter);

        chart.getLegend().setCustom(new ArrayList<>());
        chart.setDescription(null);
        chart.animateY(1000, Easing.EaseInOutCubic);
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        LineDataSet dataSet = new LineDataSet(entries, "Data"); // add entries to dataset
        dataSet.setColor(Color.RED);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(15f);
        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        chart.invalidate();
    }

    private String[] parseValues(String values) {
        return values.split(",");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        fullLayout.setVisibility(View.GONE);
        hideMainToolbar.hideToolbar(true);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            hideMainToolbar = (HideMainToolbar) context;
        } catch (ClassCastException castException) {
            /* The activity does not implement the listener. */
        }
    }
}
