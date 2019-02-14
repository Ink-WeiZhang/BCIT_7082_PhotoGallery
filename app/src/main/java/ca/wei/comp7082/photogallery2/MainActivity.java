package ca.wei.comp7082.photogallery2;

import android.Manifest;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends Activity {

    private static final int REQUEST_IMAGE_CAPTURE = 3001;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1001;

    private static final int REQUEST_SEARCH = 4001;
    private static final String EXTRA_START = "StartLimit";
    private static final String EXTRA_END = "EndLimit";
    private static final String EXTRA_KEYWORD = "Keyword";
    private static final int RESULT_SUCCESS = 6000;

    private Date startLimit = new Date();
    private Date endLimit = new Date();
    private String keywords = "Testing";
    private int imgSelectNdx = 0;
    String mCurrentPhotoPath;

    private ImageView previewImage;
    protected List<String> imageList;
    protected List<String> filteredList;
    String[] proj = { MediaStore.Images.Media._ID,MediaStore.Audio.Media.DISPLAY_NAME };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        previewImage = findViewById(R.id.previewImage);
        imageList = new ArrayList<>();
        filteredList = new ArrayList<>();

        String mpath = getURLForResource(R.drawable.img_blur).toString();
        imageList.add(mpath);
        populateList();
    }

    public Uri getURLForResource (int resourceId) {
        return Uri.parse("android.resource://"+R.class.getPackage().getName()+"/" +resourceId);
    }

    public void btnSearchClicked(View view ){
        Intent intent = new Intent(MainActivity.this, SearchActivity.class);
        intent.putExtra(EXTRA_START, startLimit);
        intent.putExtra(EXTRA_END, endLimit);
        intent.putExtra(EXTRA_KEYWORD, keywords);
        startActivityForResult(intent, REQUEST_SEARCH);
    }

    public void btnNextClicked(View view) {
        if (filteredList.size() > imgSelectNdx) {
            setImage(filteredList.get(++imgSelectNdx));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_IMAGE_CAPTURE:
                    //galleryAddPic();
                    //setPic();
                    setImage(mCurrentPhotoPath);
                    imageList.add(mCurrentPhotoPath);
                    //addAllFilesToArray();
                    populateList();
                    break;
            }
        }
    }

    private void setImage(String path){
        previewImage.setImageBitmap(BitmapFactory.decodeFile(path));
    }

    private void populateList(){
        Iterator<String> itr = imageList.iterator();
        filteredList.clear();
        while(itr.hasNext()){
            filteredList.add(new String(itr.next()));
        }
    }

    private void addAllFilesToArray(){
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Explain to the user why we need to read the contacts
            }

            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

            // MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an
            // app-defined int constant that should be quite unique

            return;
        }
        Cursor imageCursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, proj, null, null, null);
        if(imageCursor != null){
            if(imageCursor.moveToFirst()){
                do{
                    int imageIndex = imageCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
                    Uri imgUri = ContentUris
                            .withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                    imageCursor.getInt(imageCursor.getColumnIndex(MediaStore.Images.ImageColumns._ID)));
                    imageList.add(getRealPathFromURI(this, imgUri));
                }while(imageCursor.moveToNext());
            }
        }
        imageCursor.close();
    }

    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public void dispatchTakePictureIntent(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
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
                Uri photoURI = FileProvider.getUriForFile(this,
                        "ca.wei.comp7082.photogallery2.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }
}
