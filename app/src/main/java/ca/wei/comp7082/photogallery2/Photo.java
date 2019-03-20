package ca.wei.comp7082.photogallery2;

import java.io.File;
import java.util.Date;

public class Photo {

    private File img;
    private Date mDate;
    private String mPhotoPath;
    private String mKeyword;

    public Photo(File imagefile, String photoPath){
        img = imagefile;
        mPhotoPath = photoPath;
        mDate = new Date();
        mKeyword = "default";
    }

    public Photo(File imagefile, String photoPath, Date date, String keyword){
        img = imagefile;
        mPhotoPath = photoPath;
        mDate = date;
        mKeyword = keyword;
    }

    public Photo(Photo photo){
        img = photo.getImg();
        mDate = photo.getDate();
        mPhotoPath = photo.getPhotoPath();
    }

    public File getImg(){
        return img;
    }

    public Date getDate(){
        if (mDate != null)
        return mDate;
        else return new Date();
    }

    public String getKeyword(){
        return mKeyword;
    }

    public String getPhotoPath(){
        return mPhotoPath;
    }

}
