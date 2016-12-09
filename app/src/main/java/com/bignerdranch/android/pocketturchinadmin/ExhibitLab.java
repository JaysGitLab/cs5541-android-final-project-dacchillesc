package com.bignerdranch.android.pocketturchinadmin;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Class for managing the list of exhibits.
 */

public class ExhibitLab{
    private static final String TAG = "ExhibitLab";
    private static final String FILENAME = "exhibits.json";
    private ArrayList<Exhibit> mExhibits;
    private PocketTurchinJSONSerializer mSerializer;
    private static ExhibitLab sExhibitLab;
    private Context mAppContext;

    private ExhibitLab(Context appContext){
        mAppContext = appContext;
        mSerializer = new PocketTurchinJSONSerializer(mAppContext, FILENAME);
        try
        { mExhibits= mSerializer.loadExhibits(); }
        catch (Exception e){
            mExhibits = new ArrayList<Exhibit>();
            Log.e(TAG, "Error loading exhibitss: ", e);
        }
    }

    public static ExhibitLab get(Context c){
        if(sExhibitLab == null){
            synchronized (ExhibitLab.class) {
                if (sExhibitLab == null)
                { sExhibitLab = new ExhibitLab(c.getApplicationContext()); }
            }
        }
        return sExhibitLab;
    }

    public void addExhibit(Exhibit c)
    { mExhibits.add(c); }

    public void deleteExhibit(Exhibit c)
    { mExhibits.remove(c);  }

    public boolean saveExhibits(){
        try{
            mSerializer.saveExhibits(mExhibits);
            Log.d(TAG, "exhibits saved to file");
            return true;
        }catch(Exception e){
            Log.e(TAG, "Error saving exhibits: ", e);
            return false;
        }
    }

    public ArrayList<Exhibit> getExhibits()
    { return mExhibits; }

    public Exhibit getExhibit(UUID id){
        for(Exhibit c : mExhibits) {
            if(c.getUUId().equals(id))
            { return c; }
        }
        return null;
    }
}
