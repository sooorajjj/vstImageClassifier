package com.vst.image.vehiclestimageclassifier;

import android.Manifest;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.SimpleMultiPartRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import gun0912.tedbottompicker.TedBottomPicker;

public class MainActivity extends AppCompatActivity {


    public RequestManager mGlideRequestManager;
    ArrayList<Uri> selectedUriList;
    private String[] imagePath;
    private ViewGroup mSelectedImagesContainer;

    private String FOLDER_NAME = "SomeFolder";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGlideRequestManager = Glide.with(this);

        mSelectedImagesContainer = (ViewGroup) findViewById(R.id.selected_photos_container);

        setMultiShowButton();

//        if(selectedUriList!=null){
            uploadButton();
//        }

    }
    private void setMultiShowButton() {

        Button btn_multi_show = (Button) findViewById(R.id.btn_multi_show);
        btn_multi_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                PermissionListener permissionlistener = new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {

                        TedBottomPicker bottomSheetDialogFragment = new TedBottomPicker.Builder(MainActivity.this)
                                .setOnMultiImageSelectedListener(new TedBottomPicker.OnMultiImageSelectedListener() {
                                    @Override
                                    public void onImagesSelected(ArrayList<Uri> uriList) {
                                        selectedUriList = uriList;
                                        showUriList(uriList);
                                    }
                                })
                                //.setPeekHeight(getResources().getDisplayMetrics().heightPixels/2)
                                .setPeekHeight(1600)
                                .showTitle(false)
                                .setCompleteButtonText("Done")
                                .setEmptySelectionText("No Select")
                                .setSelectedUriList(selectedUriList)
                                .create();

                        bottomSheetDialogFragment.show(getSupportFragmentManager());


                    }

                    @Override
                    public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                        Toast.makeText(MainActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
                    }


                };

                TedPermission.with(MainActivity.this)
                        .setPermissionListener(permissionlistener)
                        .setDeniedMessage("If you reject permission,you can not use this service\n\n" +
                                "Please turn on permissions at [Setting] > [Permission]")
                        .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA,
                                Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.INTERNET,
                                Manifest.permission.ACCESS_NETWORK_STATE)
                        .check();

            }
        });

    }

    private void showUriList(ArrayList<Uri> uriList) {
        // Remove all views before
        // adding the new ones.
        mSelectedImagesContainer.removeAllViews();

        mSelectedImagesContainer.setVisibility(View.VISIBLE);

        int wdpx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics());
        int htpx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics());


        for (Uri uri : uriList) {
            View imageHolder = LayoutInflater.from(this).inflate(R.layout.image_item, null);
            ImageView thumbnail = (ImageView) imageHolder.findViewById(R.id.media_image);

            Glide.with(this)
                    .load(uri.toString())
                    .fitCenter()
                    .into(thumbnail);

            mSelectedImagesContainer.addView(imageHolder);

            thumbnail.setLayoutParams(new FrameLayout.LayoutParams(wdpx, htpx));

            imagePath = new String[selectedUriList.size()];


        }

    }
    private void uploadButton(){
        Button uploadImageServer = (Button) findViewById(R.id.btn_upload);
        uploadImageServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                GetImageNameEditText = imageName.getText().toString();

            getPaths(selectedUriList);

            imageUpload(imagePath);

            }
        });
    }
    private void getPaths(ArrayList<Uri> selectedUriList){

        int i = 0;
        for (Uri uri : selectedUriList) {
            imagePath[i] = uri.toString();
            i++;
        }
    }

    private void imageUpload(String[] imagePath) {

        SimpleMultiPartRequest smr = new SimpleMultiPartRequest(Request.Method.POST, Constants.UPLOAD_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response", response);
                        try {
                            JSONObject jObj = new JSONObject(response);
                            String message = jObj.getString("message");

                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

                        } catch (JSONException e) {
                            // JSON error
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        int b = 0;
        for (String strImagePath : imagePath){
            b++;
            smr.addFile(FOLDER_NAME+"/image"+b+".jpg", strImagePath);
            VolleyQueue.getInstance().addToRequestQueue(smr);
        }


    }
}
