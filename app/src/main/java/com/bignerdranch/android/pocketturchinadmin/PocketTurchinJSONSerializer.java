package com.bignerdranch.android.pocketturchinadmin;

import android.content.Context;
import android.util.Log;

import com.cloudrail.si.interfaces.CloudStorage;
import com.cloudrail.si.services.Dropbox;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Class for handling loading and saving to JSON.
 */

public class PocketTurchinJSONSerializer {
    private static final String TAG = "JSONSERIALIZER";
    private Context mContext;
    private String mFilename;
    private CloudStorage cs;

    public PocketTurchinJSONSerializer(Context c, String f){
        mContext = c;
        mFilename = f;
        cs = getDropBoxStorage();
    }

    private CloudStorage getDropBoxStorage()
    { return new Dropbox(mContext, mContext.getString(R.string.dropBoxClient), mContext.getString(R.string.dropBoxSecretKey)); }

    private void uploadFile(final String path){
        new Thread(){
            @Override
            public void run(){
                InputStream is;
                try {
                    File f = new File(path);
                    String name = f.getName();
                    is = new FileInputStream(f);
                    long size = f.length();
                    System.out.println("IS [" + is + "] - Size [" + size + "]");
                    cs.upload("/" + name, is, size, true);
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }.start();
    }

    private File downloadFile(final String path){
        File file = null;
        ExecutorService es = Executors.newSingleThreadExecutor();
        Future<File> result = es.submit(new Callable<File>() {
            public File call() throws Exception {
                File fl = new File(path);
                OutputStream ot = new FileOutputStream(fl);
                try {
                    File f = new File(path);
                    String name = f.getName();
                    InputStream is = cs.download("/" + name);
                    IOUtils.copy(is, ot);
                    is.close();
                    ot.close();
                }catch (Exception e) {
                    e.printStackTrace();
                }
                return fl;
            }
        });
        try {
            file = result.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        es.shutdown();
        return file;
    }

    public ArrayList<Exhibit> loadExhibits() throws IOException, JSONException {
        ArrayList<Exhibit> exhibits = new ArrayList<Exhibit>();
        BufferedReader reader = null;
        Boolean isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
        File extExhibitFile = new File(mContext.getExternalFilesDir(null), mFilename);
        FileInputStream extFileInputStream = null;
        /*if(extExhibitFile.exists())
        { extFileInputStream = downloadFile(extExhibitFile.getPath()); }
        else
        { extFileInputStream = downloadFile(mFilename); } */
        if (isSDPresent && extExhibitFile.exists()) {
            Log.e(TAG, "The loadExhibits method found the SD Card mounted and found that the exhibits file exists");
            try {
                if(extFileInputStream == null){
                    extFileInputStream = new FileInputStream(extExhibitFile);
                }
                reader = new BufferedReader(new InputStreamReader(extFileInputStream));
                StringBuilder jsonString = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null)
                { jsonString.append(line); }
                JSONArray array = (JSONArray) new JSONTokener(jsonString.toString()).nextValue();
                for (int i = 0; i < array.length(); i++)
                { exhibits.add(new Exhibit(array.getJSONObject(i))); }
            } catch (FileNotFoundException e) {
            } finally {
                if (reader != null)
                { reader.close(); }
            }
            return exhibits;
        } else {
            if(!isSDPresent)
            { Log.e(TAG, "The loadExhibits method did not find the SD Card mounted"); }
            if(!extExhibitFile.exists())
            { Log.e(TAG, "The loadExhibits method found that the exhibits file does not exist"); }
            try {
                InputStream in;
                if(extFileInputStream == null)
                { in = mContext.openFileInput(mFilename); }
                else
                { in = extFileInputStream; }
                reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder jsonString = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null)
                { jsonString.append(line); }
                JSONArray array = (JSONArray) new JSONTokener(jsonString.toString()).nextValue();
                for (int i = 0; i < array.length(); i++)
                { exhibits.add(new Exhibit(array.getJSONObject(i))); }
            } catch (FileNotFoundException e) {
                Log.e(TAG,"File not found: ",e);
            } finally {
                if (reader != null)
                { reader.close(); }
            }
            return exhibits;

        }
    }

    public void saveExhibits(ArrayList<Exhibit> exhibits) throws JSONException, IOException{
        JSONArray array = new JSONArray();
        Boolean isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
        if (isSDPresent) {
            Log.e(TAG, "The saveExhibits method found that the SD card is mounted");
            File extDataDir = new File(mContext.getExternalFilesDir(null), mFilename);
            Log.e(TAG, "The external files dir is: " + extDataDir.toString());
            for (Exhibit c : exhibits)
            { array.put(c.toJSON()); }
            Writer writer = null;
            File extExhibitFile = null;
            try {
                extExhibitFile = new File(extDataDir.toString());
                FileOutputStream extFOS = new FileOutputStream(extExhibitFile);
                writer = new OutputStreamWriter(extFOS);
                writer.write(array.toString());

            } finally {
                if (writer != null)
                { writer.close(); }
                if(extExhibitFile != null){
                    uploadFile(extExhibitFile.getPath());
                }
            }
        } else {
            Log.e(TAG, "The SD card is not mounted");
            for (Exhibit c : exhibits)
            { array.put(c.toJSON());}
            Writer writer = null;
            try {
                OutputStream out = mContext.openFileOutput(mFilename, Context.MODE_PRIVATE);
                writer = new OutputStreamWriter(out);
                writer.write(array.toString());
            } finally {
                if (writer != null)
                { writer.close(); }
            }
        }
    }
}
