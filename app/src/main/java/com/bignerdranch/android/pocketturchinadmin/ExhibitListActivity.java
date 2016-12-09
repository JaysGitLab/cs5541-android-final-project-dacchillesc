package com.bignerdranch.android.pocketturchinadmin;

import android.support.v4.app.Fragment;
import com.cloudrail.si.CloudRail;
import com.cloudrail.si.interfaces.CloudStorage;
import com.cloudrail.si.services.Dropbox;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * The Exhibit List Activity.  Creates a ExhibitListFragment.
 */

public class ExhibitListActivity extends SingleFragmentActivity{

    @Override
    protected Fragment createFragment() {
        CloudRail.setAppKey("5848c2c0b002261b9873fb76");
        return new ExhibitListFragment();
    }
}

