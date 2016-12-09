package com.bignerdranch.android.pocketturchinadmin;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.cloudrail.si.CloudRail;
import com.cloudrail.si.interfaces.CloudStorage;
import com.cloudrail.si.services.Dropbox;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * .
 */

public class ExhibitListFragment extends ListFragment {
    private ArrayList<Exhibit> mExhibits;
    private Button addExhibitButton;
    private static final String TAG = "ExhibitListFragment";
    private CloudStorage cs;


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getActivity().setTitle(R.string.app_name);
        mExhibits = ExhibitLab.get(getActivity()).getExhibits();
        ExhibitAdapter adapter = new ExhibitAdapter(mExhibits);
        setListAdapter(adapter);
        setRetainInstance(true);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id){
        Exhibit c = ((ExhibitAdapter)getListAdapter()).getItem(position);
        Intent i = new Intent(getActivity(),ExhibitPagerActivity.class);
        i.putExtra(ExhibitFragment.EXTRA_EXHIBIT_ID,c.getUUId());
        startActivity(i);
    }

    private class ExhibitAdapter extends ArrayAdapter<Exhibit> {
        public ExhibitAdapter(ArrayList<Exhibit> exhibits)
        { super(getActivity(), 0, exhibits); }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            if(convertView == null)
            { convertView = getActivity().getLayoutInflater().inflate(R.layout.list_item_exhibit, null); }
            Exhibit c = getItem(position);
            ImageView picImageView = (ImageView)convertView.findViewById(R.id.exhibit_list_item_pic);
            File imgFile = new File(c.getImage());
            if(imgFile.exists())
            { picImageView.setImageURI(Uri.fromFile(imgFile)); }
            else
            { picImageView.setImageResource(R.mipmap.turchin_center_r470x260); }
            TextView titleTextView = (TextView)convertView.findViewById(R.id.exhibit_list_item_titleTextView);
            titleTextView.setText(c.getTitle());
            TextView artistTextView = (TextView)convertView.findViewById(R.id.exhibit_list_item_artistTextView);
            artistTextView.setText(c.getArtist());
            TextView galleryTextView = (TextView)convertView.findViewById(R.id.exhibit_list_item_galleryTextView);
            galleryTextView.setText(c.getGallery());
            TextView dateTextView = (TextView)convertView.findViewById(R.id.exhibit_list_item_dateTextView);
            dateTextView.setText(c.getFormatedStayRange());
            return convertView;
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        ((ExhibitAdapter)getListAdapter()).notifyDataSetChanged();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    { ((ExhibitAdapter)getListAdapter()).notifyDataSetChanged();}

    @TargetApi(11)
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_exhibit_list, menu);
    }

    @TargetApi(11)
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.menu_item_new_exhibit:
                Exhibit exhibit = new Exhibit(getActivity().getBaseContext());
                ExhibitLab.get(getActivity()).addExhibit(exhibit);
                Intent i = new Intent(getActivity(), ExhibitPagerActivity.class);
                i.putExtra(ExhibitFragment.EXTRA_EXHIBIT_ID, exhibit.getUUId());
                startActivityForResult(i,0);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    { getActivity().getMenuInflater().inflate(R.menu.exhibit_list_item_context, menu); }

    @Override
    public boolean onContextItemSelected(MenuItem item){
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int position = info.position;
        ExhibitAdapter adapter = (ExhibitAdapter)getListAdapter();
        Exhibit exhibit = adapter.getItem(position);
        switch (item.getItemId()){
            case R.id.menu_item_delete_exhibit:
                ExhibitLab.get(getActivity()).deleteExhibit(exhibit);
                adapter.notifyDataSetChanged();
                return true;
        }
        return super.onContextItemSelected(item);
    }

    @TargetApi(11)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_empty_list, parent, false);
        ListView listView = (ListView)v.findViewById(android.R.id.list);
        listView.setEmptyView(v.findViewById(android.R.id.empty));
        addExhibitButton = (Button)v.findViewById(R.id.initialExhibitButton);
        addExhibitButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Exhibit exhibit = new Exhibit(getActivity().getBaseContext());
                ExhibitLab.get(getActivity()).addExhibit(exhibit);
                Intent i = new Intent(getActivity(), ExhibitPagerActivity.class);
                i.putExtra(ExhibitFragment.EXTRA_EXHIBIT_ID, exhibit.getUUId());
                startActivityForResult(i, 0);
            }
        });
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB)
        { registerForContextMenu(listView); }
        else {
            listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
            listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
                @Override
                public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) { }

                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    MenuInflater inflater = mode.getMenuInflater();
                    inflater.inflate(R.menu.exhibit_list_item_context, menu);
                    return true;
                }

                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu)
                { return false; }

                @Override
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    switch (item.getItemId()){
                        case R.id.menu_item_delete_exhibit:
                            ExhibitAdapter adapter = (ExhibitAdapter)getListAdapter();
                            ExhibitLab exhibitLab = ExhibitLab.get(getActivity());
                            for(int i = adapter.getCount()-1;i>=0;i--){
                                if(getListView().isItemChecked(i))
                                { exhibitLab.deleteExhibit(adapter.getItem(i)); }
                            }
                            mode.finish();
                            adapter.notifyDataSetChanged();
                            return true;
                        default:
                            return false;
                    }
                }
                @Override
                public void onDestroyActionMode(ActionMode mode) { }
            });
        }
        return v;
    }

    @Override
    public void onPause(){
        super.onPause();
        ExhibitLab.get(getActivity()).saveExhibits();
    }
}
