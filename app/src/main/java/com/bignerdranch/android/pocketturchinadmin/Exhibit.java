package com.bignerdranch.android.pocketturchinadmin;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.format.DateFormat;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;
import java.util.UUID;

/**
 * The class that defines Exhibit objects.
 */

public class Exhibit {
    private static final String JSON_UUID = "uuid";
    private static final String JSON_ID = "id";
    private static final String JSON_PIC = "pic";
    private static final String JSON_TITLE = "title";
    private static final String JSON_ARTIST = "artist";
    private static final String JSON_GALLERY = "gallery";
    private static final String JSON_SHORT_DESCRIPTION = "short_description";
    private static final String JSON_FULL_DESCRIPTION = "full_description";
    private static final String JSON_START_DATE = "start_date";
    private static final String JSON_END_DATE = "end_date";
    private UUID mUUId;
    private int mId;
    private String mPicLocation;
    private String mTitle;
    private String mArtist;
    private String mGallery;
    private Date mStartDate;
    private Date mEndDate;
    private String mFullDescription;
    private String mShortDescription;

    public Exhibit(Context context){
        int max = 1000;
        int min = 0;
        mUUId = UUID.randomUUID();
        mId = new Random().nextInt(max - min + 1) + min;
        /*mUUId = UUID.randomUUID();
        mId = new Random().nextInt();*/
        mTitle = "Title";
        mPicLocation = "/storage/sdcard/Android/data/com.bignerdranch.android.pocketturchinadmin/files/images/turchin_center_r470x260.jpg";
        mArtist = "Artist";
        mGallery = "Gallery 001";
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        mStartDate = new GregorianCalendar(year, month, day, 0, 0, 0).getTime();
        mEndDate = new GregorianCalendar(year, month, day, 23, 59,59).getTime();
        mShortDescription = "Short Description";
        mFullDescription = "Here we have a long description for the exhibit";
    }

    public Exhibit(JSONObject json)throws JSONException{
        mUUId = UUID.fromString(json.getString(JSON_UUID));
        mId = json.getInt(JSON_ID);
        mPicLocation = json.getString(JSON_PIC);
        mTitle = json.getString(JSON_TITLE);
        mArtist = json.getString(JSON_ARTIST);
        mGallery = json.getString(JSON_GALLERY);
        mStartDate = new Date(json.getLong(JSON_START_DATE));
        mEndDate = new Date(json.getLong(JSON_END_DATE));
        mShortDescription = json.getString(JSON_SHORT_DESCRIPTION);
        mFullDescription = json.getString(JSON_FULL_DESCRIPTION);
    }

    public JSONObject toJSON()throws JSONException{
        JSONObject json = new JSONObject();
        json.put(JSON_UUID, mUUId.toString());
        json.put(JSON_ID,mId);
        json.put(JSON_PIC, mPicLocation);
        json.put(JSON_TITLE ,mTitle);
        json.put(JSON_ARTIST,mArtist);
        json.put(JSON_GALLERY,mGallery);
        json.put(JSON_START_DATE,mStartDate.getTime());
        json.put(JSON_END_DATE,mEndDate.getTime());
        json.put(JSON_SHORT_DESCRIPTION, mShortDescription);
        json.put(JSON_FULL_DESCRIPTION, mFullDescription);
        return json;
    }

    public String getImage()
    { return mPicLocation; }

    public void setImage(String imageLoc){
        if(imageLoc != null)
        { mPicLocation = imageLoc; }
    }

    public int getId()
    { return mId; }

    public void setID(int id)
    { mId = id; }

    public String getTitle()
    { return mTitle; }

    public void setTitle(String title)
    { mTitle = title; }

    public String getArtist()
    { return mArtist; }

    public void setArtist(String artist)
    { mArtist = artist; }

    public String getGallery()
    { return mGallery; }

    public void setGallery(String gallery)
    { mGallery = gallery; }

    public UUID getUUId()
    { return mUUId; }

    public Date getStartDate()
    { return mStartDate; }

    public void setStartDate(Date date)
    { mStartDate = date; }

    public String getFormattedStartDate()
    { return DateFormat.format("EEEE, MMM d, yyyy",mStartDate).toString(); }

    public Date getEndDate()
    { return mEndDate; }

    public void setEndDate(Date date)
    { mEndDate = date; }

    public String getFormattedEndDate()
    { return DateFormat.format("EEEE, MMM d, yyyy",mEndDate).toString(); }

    public String getFormatedStayRange(){
        return DateFormat.format("MMM d, yyyy",mStartDate).toString() +
                " - " + DateFormat.format("MMM d, yyyy",mEndDate).toString();
    }

    public String getShortDescription()
    { return mShortDescription; }

    public void setShortDescription(String shortDescription)
    { mShortDescription = shortDescription; }

    public String getFullDescription()
    { return mFullDescription; }

    public void setFullDescription(String fullDescription)
    { mFullDescription = fullDescription; }

    @Override
    public String toString()
    { return mTitle; }
}
