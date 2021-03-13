package com.adamsousa.mygreenhouse.ui.fragments.careTips;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.adamsousa.mygreenhouse.R;
import com.adamsousa.mygreenhouse.model.CareTipModel;
import com.adamsousa.mygreenhouse.ui.fragments.GlideApp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class CareTipsGridLayoutAdapter extends BaseAdapter {

    private LayoutInflater layoutinflater;
    private List<CareTipModel> listOfCareTips;
    private Context context;

    CareTipsGridLayoutAdapter(Context context, List<CareTipModel> customizedListView) {
        this.context = context;
        layoutinflater =(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        listOfCareTips = customizedListView;
    }

    @Override
    public int getCount() {
        return listOfCareTips.size();
    }

    @Override
    public Object getItem(int position) {
        return listOfCareTips.get(position);
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
            convertView = layoutinflater.inflate(R.layout.layout_care_tips, parent, false);
            listViewHolder.careTipsTitleTextView = convertView.findViewById(R.id.care_tips_title_textview);
            listViewHolder.careTipsImageView = convertView.findViewById(R.id.care_tips_imageview);
            convertView.setTag(listViewHolder);
        }else{
            listViewHolder = (ViewHolder)convertView.getTag();
        }

        listViewHolder.careTipsTitleTextView.setText(listOfCareTips.get(position).getTitle());
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(listOfCareTips.get(position).getPictureFilePath());
        GlideApp.with(context)
                .load(storageReference)
                .into(listViewHolder.careTipsImageView)
                .onLoadFailed(context.getDrawable(R.drawable.ic_blurred_image));

        return convertView;
    }

    static class ViewHolder{
        TextView careTipsTitleTextView;
        ImageView careTipsImageView;
    }
}
