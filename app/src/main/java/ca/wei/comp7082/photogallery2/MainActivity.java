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
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class MainActivity extends Activity {

    private static final int REQUEST_IMAGE_CAPTURE = 3001;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1001;

    private static final int REQUEST_SEARCH = 4001;
    private static final String EXTRA_START = "StartLimit";
    private static final String EXTRA_END = "EndLimit";
    private static final String EXTRA_KEYWORD = "Keyword";
    private static final int RESULT_SUCCESS = 6000;

    private Date startLimit = null;
    private Date endLimit = null;
    private String keywords = "Testing";
    private int imgSelectNdx = 0;
    String mCurrentPhotoPath;

    Photo currentPhoto;

    private ImageView previewImage;
    protected List<Photo> imageList;
    protected List<Photo> filteredList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        previewImage = findViewById(R.id.previewImage);
        imageList = new ArrayList<>();
        filteredList = new ArrayList<>();
        populateList();
    }

    public void btnSearchClicked(View view ){
        Intent intent = new Intent(MainActivity.this, SearchActivity.class);
        intent.putExtra(EXTRA_START, startLimit);
        intent.putExtra(EXTRA_END, endLimit);
        intent.putExtra(EXTRA_KEYWORD, keywords);
        startActivityForResult(intent, REQUEST_SEARCH);
    }

    public void btnNextClicked(View view) {
        if (filteredList.size() != 0) {
            if ((filteredList.size() - 1) > imgSelectNdx) {
                setImage(filteredList.get(++imgSelectNdx).getPhotoPath());
            } else if ((filteredList.size() - 1) == imgSelectNdx) {
                imgSelectNdx = 0;
                setImage(filteredList.get(imgSelectNdx).getPhotoPath());
            }
        }
    }

    public void btnPrevClicked(View view) {
        if (filteredList.size() != 0) {
            if (imgSelectNdx != 0) {
                setImage(filteredList.get(--imgSelectNdx).getPhotoPath());
            } else {
                imgSelectNdx = filteredList.size() - 1;
                setImage(filteredList.get(imgSelectNdx).getPhotoPath());
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_IMAGE_CAPTURE:
                    setImage(currentPhoto.getPhotoPath());
                    imageList.add(currentPhoto);
                    populateList();
                    break;
                case REQUEST_SEARCH:
                    startLimit = (Date)data.getSerializableExtra(EXTRA_START);
                    endLimit = (Date)data.getSerializableExtra(EXTRA_END);
                    keywords = data.getStringExtra(EXTRA_KEYWORD);
                    populateList();
            }
        }
    }

    private void setImage(String path){
        previewImage.setImageBitmap(BitmapFactory.decodeFile(path));
    }

    private void populateList(){
        Iterator<Photo> itr = imageList.iterator();
        filteredList.clear();

        if (startLimit == null || endLimit == null) {
            while(itr.hasNext()){
                filteredList.add(new Photo(itr.next()));
            }
        } else {
            while(itr.hasNext()){
                Photo nextPhoto = new Photo(itr.next());
                if (startLimit.compareTo(nextPhoto.getDate()) *  nextPhoto.getDate().compareTo(endLimit) >= 0) {
                    filteredList.add(nextPhoto);
                }
            }
        }

        onDraw(new Canvas());
    }

    protected void onDraw(Canvas canvas){
        canvas.drawColor(Color.BLACK);
        //Paint p = new Paint();
        int y = 0;
    }

    private File createImageFile() throws IOException {

        // Create an image file name
        Date date = new Date();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(date);
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        currentPhoto = new Photo(image, image.getAbsolutePath(), date, "");

        // Save a file: path for use with ACTION_VIEW intents
        //mCurrentPhotoPath = image.getAbsolutePath();
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
