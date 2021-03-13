package com.adamsousa.mygreenhouse.ui.fragments.plants;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.adamsousa.mygreenhouse.R;
import com.adamsousa.mygreenhouse.helper.ViewHelper;
import com.adamsousa.mygreenhouse.model.PlantModel;
import com.adamsousa.mygreenhouse.ui.fragments.GlideApp;
import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class PlantsGridLayoutAdapter extends BaseAdapter {

    private LayoutInflater layoutinflater;
    private List<PlantModel> listOfPlants;
    private Context context;

    PlantsGridLayoutAdapter(Context context, List<PlantModel> customizedListView) {
        this.context = context;
        layoutinflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        listOfPlants = customizedListView;
    }

    @Override
    public int getCount() {
        return listOfPlants.size();
    }

    @Override
    public Object getItem(int position) {
        return listOfPlants.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PlantsGridLayoutAdapter.ViewHolder listViewHolder;
        if (convertView == null) {
            listViewHolder = new PlantsGridLayoutAdapter.ViewHolder();
            convertView = layoutinflater.inflate(R.layout.layout_plant, parent, false);
            listViewHolder.plantNameTextView = convertView.findViewById(R.id.plant_name_textview);
            listViewHolder.plantImageView = convertView.findViewById(R.id.plant_imageview);
            listViewHolder.noDataTextView = convertView.findViewById(R.id.plant_no_data_label);
            convertView.setTag(listViewHolder);
        } else {
            listViewHolder = (PlantsGridLayoutAdapter.ViewHolder) convertView.getTag();
        }

        listViewHolder.plantNameTextView.setText(listOfPlants.get(position).getName());
        if (listViewHolder.plantImageView.getDrawable() == null) {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference(listOfPlants.get(position).getPictureFilePath());
            GlideApp.with(context)
                    .load(storageReference)
                    .into(listViewHolder.plantImageView)
                    .onLoadFailed(context.getDrawable(R.drawable.ic_blurred_image));
        }

        if (listOfPlants.get(position).isCaptureRetrievalDone()) {
            if (listOfPlants.get(position).getLatestCapture() != null) {
                int showMoistureWarning = ViewHelper.calculateShowWarning(listOfPlants.get(position).getLatestCapture().getMoisture(), listOfPlants.get(position).getMoistureMax(), listOfPlants.get(position).getMoistureMin());
                setupPlantDataLabel(convertView.findViewById(R.id.plant_moisture_label), R.drawable.ic_moisture_icon, calculateStringResource(showMoistureWarning), showMoistureWarning != 0);

                int showTemperatureWarning = ViewHelper.calculateShowWarning(listOfPlants.get(position).getLatestCapture().getTemperature(), listOfPlants.get(position).getTemperatureMax(), listOfPlants.get(position).getTemperatureMin());
                setupPlantDataLabel(convertView.findViewById(R.id.plant_temperature_label), R.drawable.ic_temperature_icon, Math.round(listOfPlants.get(position).getLatestCapture().getTemperature()) + "°F", showTemperatureWarning != 0);

                int showHumidityWarning = ViewHelper.calculateShowWarning(listOfPlants.get(position).getLatestCapture().getHumidity(), listOfPlants.get(position).getHumidityMax(), listOfPlants.get(position).getHumidityMin());
                setupPlantDataLabel(convertView.findViewById(R.id.plant_humidity_label), R.drawable.ic_humidity_icon, calculateStringResource(showHumidityWarning), showHumidityWarning != 0);

                int showSunlightWarning = ViewHelper.calculateShowWarning(listOfPlants.get(position).getLatestCapture().getSunlight(), listOfPlants.get(position).getSunlightMax(), listOfPlants.get(position).getSunlightMin());
                setupPlantDataLabel(convertView.findViewById(R.id.plant_sunlight_label), R.drawable.ic_sunlight_icon, calculateStringResource(showSunlightWarning), showSunlightWarning != 0);
            } else {
                listViewHolder.noDataTextView.setVisibility(View.VISIBLE);
            }
        }
        return convertView;
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
        valueTextView.setText(value);
        if (showError) {
            ImageView errorIcon = view.findViewById(R.id.plant_info_error_icon);
            errorIcon.setVisibility(View.VISIBLE);
        }
    }

    static class ViewHolder {
        TextView plantNameTextView;
        ImageView plantImageView;
        TextView noDataTextView;
    }
}
