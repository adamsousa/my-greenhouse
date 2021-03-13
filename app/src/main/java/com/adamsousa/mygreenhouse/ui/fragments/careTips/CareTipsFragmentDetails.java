package com.adamsousa.mygreenhouse.ui.fragments.careTips;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.transition.TransitionInflater;

import com.adamsousa.mygreenhouse.R;
import com.adamsousa.mygreenhouse.model.CareTipModel;
import com.adamsousa.mygreenhouse.ui.fragments.GlideApp;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CareTipsFragmentDetails extends Fragment {

    @BindView(R.id.care_tips_details_imageview)
    ImageView toolbarImage;
    @BindView(R.id.care_tips_details_toolbar)
    Toolbar toolbar;
    @BindView(R.id.care_tips_details_description_text_view)
    TextView descriptionTextView;
    @BindView(R.id.care_tips_details_full_layout)
    RelativeLayout fullLayout;

    private CareTipModel mCareTip;
    private HideMainToolbar hideMainToolbar;

    public interface HideMainToolbar {
        void hideToolbar(boolean show);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        hideMainToolbar.hideToolbar(false);

        mCareTip = getArguments().getParcelable("care_tip");

        postponeEnterTransition();
        setSharedElementEnterTransition(TransitionInflater.from(getContext()).inflateTransition(android.R.transition.move));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        StorageReference storageReference = FirebaseStorage.getInstance().getReference(mCareTip.getPictureFilePath());

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
        View view = inflater.inflate(R.layout.fragment_care_tips_details, container, false);
        ButterKnife.bind(this, view);

        toolbar.setTitle(mCareTip.getTitle());
        descriptionTextView.setText(mCareTip.getDescription());

        return view;
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