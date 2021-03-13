package com.adamsousa.mygreenhouse.ui;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.adamsousa.mygreenhouse.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PictureCaptureActivity extends AppCompatActivity {

    private Uri mImageUri;
    private static final int REQUEST_TAKE_PHOTO = 1;
    private String mFolderDirectory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_upload_loading);

        mFolderDirectory = getIntent().getStringExtra("folder_name");

        dispatchTakePictureIntent();
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                mImageUri = FileProvider.getUriForFile(this,
                        "com.adamsousa.mygreenhouse.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_TAKE_PHOTO) {
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mImageUri);
                    formatAndSaveImage(bitmap, mImageUri, .3, 0.3);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void formatAndSaveImage(Bitmap bitmap, Uri imageUri, double width, double height) {
        Bitmap scaleBitmap = Bitmap.createScaledBitmap(bitmap, (int) Math.round(bitmap.getWidth() * width), (int) Math.round(bitmap.getHeight() * height), false);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        // Compress image to lower quality scale 1 - 100
        scaleBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);

        String storagePath = System.currentTimeMillis() + "." + getFileExtension(imageUri);
        StorageReference storageMainReference = FirebaseStorage.getInstance().getReference(mFolderDirectory);
        StorageReference fileReference = storageMainReference.child(storagePath);
        fileReference.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
            returnImagePath(storagePath, fileReference);
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to create image", Toast.LENGTH_LONG).show();
            Intent intent = new Intent();
            setResult(RESULT_CANCELED, intent);
            finish();
        });
    }

    private void returnImagePath(String imagePath, StorageReference storageRef) {
        Intent intent = new Intent();
        Bundle data = new Bundle();
        data.putString("storage_ref", storageRef.getPath());
        data.putString("image_path", imagePath);
        intent.putExtras(data);
        setResult(RESULT_OK, intent);
        finish();
    }
}
