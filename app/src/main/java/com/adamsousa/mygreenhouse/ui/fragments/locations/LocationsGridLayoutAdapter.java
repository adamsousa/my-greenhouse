package com.adamsousa.mygreenhouse.ui.fragments.locations;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.adamsousa.mygreenhouse.R;
import com.adamsousa.mygreenhouse.model.LocationModel;
import com.adamsousa.mygreenhouse.ui.fragments.GlideApp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class LocationsGridLayoutAdapter extends BaseAdapter {

    private LayoutInflater layoutinflater;
    private List<LocationModel> listOfLocations;
    private Context context;

    LocationsGridLayoutAdapter(Context context, List<LocationModel> customizedListView) {
        this.context = context;
        layoutinflater =(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        listOfLocations = customizedListView;
    }

    @Override
    public int getCount() {
        return listOfLocations.size();
    }

    @Override
    public Object getItem(int position) {
        return listOfLocations.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder listViewHolder;
        if(convertView == null){
            listViewHolder = new ViewHolder();
            convertView = layoutinflater.inflate(R.layout.layout_location, parent, false);
            listViewHolder.locationNameTextView = convertView.findViewById(R.id.location_name_textview);
            listViewHolder.locationNumberTextView = convertView.findViewById(R.id.location_number_textview);
            listViewHolder.locationImageView = convertView.findViewById(R.id.location_imageview);
            convertView.setTag(listViewHolder);
        }else{
            listViewHolder = (ViewHolder)convertView.getTag();
        }

        listViewHolder.locationNameTextView.setText(listOfLocations.get(position).getRoom());
        listViewHolder.locationNumberTextView.setText(String.valueOf(((int)listOfLocations.get(position).getPlantCount())));
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(listOfLocations.get(position).getPictureFilePath());
        GlideApp.with(context)
                .load(storageReference)
                .into(listViewHolder.locationImageView)
                .onLoadFailed(context.getDrawable(R.drawable.ic_blurred_image));

        return convertView;
    }

    static class ViewHolder{
        TextView locationNameTextView;
        TextView locationNumberTextView;
        ImageView locationImageView;
    }
}
