package com.example.coen268project.View;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.coen268project.Firebase.CallBack;
import com.example.coen268project.Presentation.Utility;
import com.example.coen268project.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.app.Activity.RESULT_OK;

public class UploadFragment extends Fragment {

    Button button, butcam, butupload;
    private static final int CAMERA_REQUEST_CODE = 102;
    private static final int PICK_IMAGE_REQUEST = 103;
    private Utility utility;

    ImageView imageView;
    private static final int CAMERA_PERMISSION_CODE=100;
    String currentPhotoPath;
    String imgDecodableString;
    Uri contentUri;
    File f = null;
    final String[] picture_name = {""};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_upload, container, false);
        button = (Button) view.findViewById(R.id.button_con);
        butcam = (Button) view.findViewById(R.id.button_camera);
        butupload = (Button) view.findViewById(R.id.button_choose);
        imageView = (ImageView) view.findViewById(R.id.upload_image);
        utility = new Utility();
        Bundle bundle = getArguments();
        final String item = bundle.getString("Item");
        final String Location = bundle.getString("Location");

        //Log.d("tag"," Item is "+ item +" Location is "+Location);

        butupload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        butcam.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                askCameraPermission();
            }
        });

        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick (View v) {
                Intent intent = new Intent(getActivity(), HomeActivity.class);
                intent.putExtra("from", SellDescriptionFragment.class.getSimpleName());
                intent.putExtra("Item", item);
                intent.putExtra("Location", Location);
                intent.putExtra("Path", picture_name[0]);
                startActivity(intent);
            }
        });
        return view;
    }

    public void openFileChooser()
    {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);
    }

    private void askCameraPermission()
    {
        if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.CAMERA},CAMERA_PERMISSION_CODE);
        }
        else {
            dispatchTakePictureIntent();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==CAMERA_PERMISSION_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                dispatchTakePictureIntent();
            }
            else {
                Log.d("tag","Camera permission is required to use camera");
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE)
        {
            f = new File(currentPhotoPath);
            imageView.setImageURI(Uri.fromFile(f));
            Log.d("tag", "Absolute url of image" + Uri.fromFile(f));
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            contentUri = Uri.fromFile(f);
            mediaScanIntent.setData(contentUri);
            getActivity().sendBroadcast(mediaScanIntent);

        }

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null)
        {
            contentUri = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            // Get the cursor
            Cursor cursor = getContext().getContentResolver().query(contentUri, filePathColumn, null, null, null);
            // Move to first row
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            imgDecodableString = cursor.getString(columnIndex);
            f = new File(imgDecodableString);
            cursor.close();
            Glide.with(getActivity()).load(contentUri).into(imageView);
            //Toast.makeText(getContext(),"File Path --> "+ f.getName(),Toast.LENGTH_LONG).show();

        }

        uploadPictureToFirebase();
    }

    private void uploadPictureToFirebase() {
        if(f!= null)
        {
            picture_name[0] = f.getName();
            utility.uploadImageToStorage(picture_name[0], contentUri, new CallBack() {
                @Override
                public void onSuccess(Object object) {
                    button.setEnabled(true);
                    picture_name[0] =  object.toString();
                    Toast.makeText(getContext(), "Image upload succeeded ", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onError(Object object) {
                    Toast.makeText(getContext(), "Image upload in progresss " + (int) object, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    private File createImageFile() throws IOException
    {
        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";

            File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            File image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = image.getAbsolutePath();
            // Create an image file name
            return image;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return null;

    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent

        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go

            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getContext(),
                        "com.example.android.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }
        }
    }
}