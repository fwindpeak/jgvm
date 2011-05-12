package org.megazone.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class FileChooser extends ListActivity {

    private static final String TAG = "FileChooser";
        
    public static final String KEY_SELECTED = "key_selected";
    public static final String KEY_TITLE = "key_title";
    public static final String KEY_ROOT = "key_root";
    public static final String KEY_FILTER = "key_filter";
    
    private ArrayList<String> mFileList;
    private File mRoot;
    private String mFilter;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Intent i = getIntent();

        setTitle(i.getStringExtra(KEY_TITLE));
        mFileList = new ArrayList<String>();
        mFilter = i.getStringExtra(KEY_FILTER);
        
        if(getDirectory(i.getStringExtra(KEY_ROOT))) {
        	refresh();
        }
    }

    public void refresh() {
    	getFiles(mRoot);
        displayFiles();
    }
    
    private void displayFiles() {

        ArrayAdapter<String> fileAdapter;
        Collections.sort(mFileList, String.CASE_INSENSITIVE_ORDER);
        
        fileAdapter =
            new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mFileList);

        setListAdapter(fileAdapter);
    }
    

    private boolean getDirectory(String path) {

        // check to see if there's an sd card.
        String cardstatus = Environment.getExternalStorageState();
        if ( cardstatus.equals(Environment.MEDIA_REMOVED)
             || cardstatus.equals(Environment.MEDIA_UNMOUNTABLE)
             || cardstatus.equals(Environment.MEDIA_UNMOUNTED)
             || cardstatus.equals(Environment.MEDIA_MOUNTED_READ_ONLY))
        {        	
        	Log.e(TAG, "error on open SDCard");
            return false;
        }
        
        mRoot = new File(path);
        if (!mRoot.exists()) {
            return false;
        } else {
            return true;
        }
    }

    private void getFiles(File f) {
    	
    	// get all files recursively
        if (f.isDirectory()) {
            File[] childs = f.listFiles();
            for (File child : childs) {
                getFiles(child);
            }
            return;
        }
        
        String filename = f.getName();
        if (filename.matches(mFilter)) {
            mFileList.add(f.getAbsolutePath());
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
    	
    	Intent i = new Intent();
        i.putExtra(KEY_SELECTED, mFileList.get(position));
        setResult(RESULT_OK, i);
        
        finish();
    }
    
    @Override 
    public boolean onKeyDown(int keyCode, KeyEvent event) {  
            if(keyCode == KeyEvent.KEYCODE_BACK) {
                setResult(RESULT_CANCELED, new Intent());
            }
            return super.onKeyDown(keyCode, event);
    } 
}