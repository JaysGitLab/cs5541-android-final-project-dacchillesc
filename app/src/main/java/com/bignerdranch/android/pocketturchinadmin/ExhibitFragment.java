package com.bignerdranch.android.pocketturchinadmin;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudrail.si.interfaces.CloudStorage;
import com.cloudrail.si.services.Dropbox;
import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * .
 */

public class ExhibitFragment extends Fragment {
    public static final String EXTRA_EXHIBIT_ID = "com.bigranch.android.pocketturchin.exhibit_id";
    private static final String TAG = "ExhibitFragment";
    private static final String DIALOG_START_DATE = "start_date";
    private static final String DIALOG_END_TIME = "end_date";
    private static final int REQUEST_START_DATE = 0;
    private static final int REQUEST_FILE_PICKER = 1;
    private static final int REQUEST_END_DATE = 2;
    private Exhibit mExhibit;
    private ImageView mImageView;
    private EditText mTitleField;
    private EditText mArtistField;
    private EditText mGalleryField;
    private EditText mShortDescriptionField;
    private EditText mFullDescriptionField;
    private EditText mIDField;
    private TextView mExhibitTime;
    private Button mImageButton;
    private Button mStartDateButton;
    private Button mEndDateButton;
    private CloudStorage cs;


    public static ExhibitFragment newInstance(UUID exhibitId){
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_EXHIBIT_ID, exhibitId);
        ExhibitFragment fragment = new ExhibitFragment();
        fragment.setArguments(args);
        return fragment;
    }
    private void updateDateAndTime() {
        mStartDateButton.setText(mExhibit.getFormattedStartDate());
        mEndDateButton.setText(mExhibit.getFormattedEndDate());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        Toast.makeText(getActivity(), "Result Code [" + resultCode + "]", Toast.LENGTH_LONG).show();
        if(resultCode != Activity.RESULT_OK){
            Toast.makeText(getActivity(), "Result Code not RESULT_OK", Toast.LENGTH_LONG).show();
            return;
        }
        if(requestCode == REQUEST_START_DATE){
            Date date = (Date)data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mExhibit.setStartDate(date);
            updateDateAndTime();
        }else if(requestCode == REQUEST_END_DATE){
            Date date = (Date)data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mExhibit.setEndDate(date);
            updateDateAndTime();
        }else if (requestCode == REQUEST_FILE_PICKER) {
            String srcFilePath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
            Toast.makeText(getActivity(), "File path [" + srcFilePath + "]", Toast.LENGTH_LONG).show();
            uploadFile(srcFilePath);
            Log.e(TAG, srcFilePath);
            /*String[] fp = srcFilePath.split("\\/");
            for(String k : fp){
                Log.e(TAG, k);
            }
            String fileName = fp[fp.length - 1];
            String dstFilePath = getString(R.string.image_loc) + "/images/" + fileName;
            try{
                File src = new File(srcFilePath);
                File dst = new File(dstFilePath);
                FileUtils.copyFile(src,dst);
                mExhibit.setImage(dstFilePath);
                if(dst.exists())
                { mImageView.setImageURI(Uri.fromFile(dst)); }
                else
                { mImageView.setImageResource(R.mipmap.turchin_center_r470x260); }
                Log.e(TAG, dstFilePath);
            }catch(IOException e){
                Toast.makeText(getActivity(), "Error", Toast.LENGTH_LONG).show();
                System.out.println("error in try catch");
                e.printStackTrace();
            }*/
        }
    }

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

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        getActivity().setTitle(R.string.app_name);
        UUID exhibitId = (UUID)getArguments().getSerializable(EXTRA_EXHIBIT_ID);
        mExhibit = ExhibitLab.get(getActivity()).getExhibit(exhibitId);
        setHasOptionsMenu(true);
        cs = getDropBoxStorage();
    }

    private CloudStorage getDropBoxStorage()
    { return new Dropbox(getActivity(), getResources().getString(R.string.dropBoxClient), getResources().getString(R.string.dropBoxSecretKey)); }

    @TargetApi(11)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_exhibit, parent, false);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
            if(NavUtils.getParentActivityName(getActivity()) != null)
            { getActivity().getActionBar().setDisplayHomeAsUpEnabled(true); }
        }
        mImageView = (ImageView) v.findViewById(R.id.exhibit_pic);
        File imgFile = new File(mExhibit.getImage());
        if(imgFile.exists())
        { mImageView.setImageURI(Uri.fromFile(imgFile)); }
        else
        { mImageView.setImageResource(R.mipmap.turchin_center_r470x260); }
        mImageView.setAdjustViewBounds(true);
        mImageButton = (Button) v.findViewById(R.id.get_pic);
        mImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialFilePicker fp = new MaterialFilePicker();
                Activity act = getActivity();
                act.setResult(Activity.RESULT_OK);
                fp.withActivity(act);
                fp.withRequestCode(REQUEST_FILE_PICKER);
                fp.withFilter(Pattern.compile(".*\\.jpg$"));
                fp.withFilterDirectories(false);
                fp.withHiddenFiles(true);
                fp.start();
            }
        });
        mTitleField = (EditText) v.findViewById(R.id.exhibit_title);
        mTitleField.setText(mExhibit.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence c, int start, int before, int count)
            { mExhibit.setTitle(c.toString()); }

            @Override
            public void beforeTextChanged(CharSequence c, int start, int count, int after) { }

            @Override
            public void afterTextChanged(Editable c) { }
        });
        mArtistField = (EditText) v.findViewById(R.id.exhibit_artist);
        mArtistField.setText(mExhibit.getArtist());;
        mArtistField.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence c, int start, int before, int count)
            { mExhibit.setArtist(c.toString()); }

            @Override
            public void beforeTextChanged(CharSequence c, int start, int count, int after) { }

            @Override
            public void afterTextChanged(Editable c) { }
        });
        mIDField = (EditText) v.findViewById(R.id.exhibit_id);
        mIDField.setFilters(new InputFilter[] {new InputFilter.LengthFilter(4)});
        mIDField.setText(Integer.toString(mExhibit.getId()));
        mIDField.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence c, int start, int before, int count){
                try{
                    int s = Integer.parseInt(c.toString());
                    mExhibit.setID(s);
                }
                catch(NumberFormatException ex){mExhibit.setID(0);}
            }

            @Override
            public void beforeTextChanged(CharSequence c, int start, int count, int after) { }

            @Override
            public void afterTextChanged(Editable c) { }
        });
        mStartDateButton = (Button)v.findViewById(R.id.exhibit_start_date);
        mStartDateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(mExhibit.getStartDate());
                dialog.setTargetFragment(ExhibitFragment.this, REQUEST_START_DATE);
                dialog.show(fm, DIALOG_START_DATE);
            }
        });
        mEndDateButton = (Button)v.findViewById(R.id.exhibit_end_date);
        mEndDateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(mExhibit.getEndDate());
                dialog.setTargetFragment(ExhibitFragment.this, REQUEST_END_DATE);
                dialog.show(fm, DIALOG_END_TIME);
            }
        });
        updateDateAndTime();
        mExhibitTime = (TextView) v.findViewById(R.id.exhibit_gallery_time);
        mExhibitTime.setText(mExhibit.getFormatedStayRange());
        mGalleryField = (EditText) v.findViewById(R.id.exhibit_gallery);
        mGalleryField.setText(mExhibit.getGallery());
        mGalleryField.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence c, int start, int before, int count)
            { mExhibit.setGallery(c.toString()); }

            @Override
            public void beforeTextChanged(CharSequence c, int start, int count, int after) { }

            @Override
            public void afterTextChanged(Editable c) { }
        });
        mShortDescriptionField = (EditText) v.findViewById(R.id.exhibit_short_description);
        mShortDescriptionField.setText(mExhibit.getShortDescription());
        mShortDescriptionField.addTextChangedListener(new TextWatcher(){
            @Override
            public void onTextChanged(CharSequence c, int start, int before, int count)
            { mExhibit.setShortDescription(c.toString()); }

            @Override
            public void beforeTextChanged(CharSequence c, int start, int count, int after) { }

            @Override
            public void afterTextChanged(Editable c) { }
        });
        mFullDescriptionField = (EditText) v.findViewById(R.id.exhibit_full_description);
        mFullDescriptionField.setText(mExhibit.getFullDescription());
        mFullDescriptionField.addTextChangedListener(new TextWatcher(){
            @Override
            public void onTextChanged(CharSequence c, int start, int before, int count)
            { mExhibit.setFullDescription(c.toString()); }

            @Override
            public void beforeTextChanged(CharSequence c, int start, int count, int after) { }

            @Override
            public void afterTextChanged(Editable c) { }
        });
        return v;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                if(NavUtils.getParentActivityName(getActivity()) != null)
                { NavUtils.navigateUpFromSameTask(getActivity()); }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        ExhibitLab.get(getActivity()).saveExhibits();
    }
}
