package eastsun.jgvm.plaf.android;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.megazone.utils.FileChooser;

import eastsun.jgvm.plaf.android.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class GVmakerAD extends Activity {
	
	public static final int REQUEST_SELECT = 0x01;
	private MainView mView;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
		// turn off the window's title bar
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, 
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		setContentView(R.layout.main);
		mView = (MainView) findViewById(R.id.mainview);
		mView.setTextView((TextView) findViewById(R.id.message));
	}
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	super.onKeyDown(keyCode, event);
    	
    	return mView.getKeyBoard().doKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
    	super.onKeyUp(keyCode, event);
    	
    	return mView.getKeyBoard().doKeyUp(keyCode, event);
    }

    private static final int MENU_OPEN = 1;
    private static final int MENU_PAUSE = 2;
    private static final int MENU_HELP = 3;
    private static final int MENU_ABOUT = 4;
    private static final int MENU_EXIT = 5;
    private MenuItem mPause;
        
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        
        menu.add(Menu.NONE, MENU_OPEN, Menu.NONE, getString(R.string.MENU_OPEN));
        
        mPause = menu.add(Menu.NONE, MENU_PAUSE, Menu.NONE, getString(R.string.MENU_PAUSE));
        mPause.setCheckable(true);
        
        menu.add(Menu.NONE, MENU_HELP, Menu.NONE, getString(R.string.MENU_HELP));
        menu.add(Menu.NONE, MENU_ABOUT, Menu.NONE, getString(R.string.MENU_ABOUT));
        menu.add(Menu.NONE, MENU_EXIT, Menu.NONE, getString(R.string.MENU_EXIT));
        
        return true;
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
    	super.onActivityResult(requestCode, resultCode, intent);
    	Bundle extras = intent.getExtras();

    	switch(requestCode) {
    	case REQUEST_SELECT:
    		if(resultCode == RESULT_OK) {
    			String fileName = extras.getString(FileChooser.KEY_SELECTED);
    			mView.load(fileName);
    		}
    		
    		restorePause();
			
    	    break;
    	}
    }
    
    private void restorePause() {
    	mPause.setEnabled(true);
		mPause.setChecked(true);
		switchPauseResume();
    }
    
    private void switchPauseResume() {
    	if(mPause.isChecked()) {
    		mView.resume();
    		mPause.setChecked(false);
    		mPause.setTitle(getString(R.string.MENU_PAUSE));
    	} else {
    		mPause.setChecked(true);
    		mView.pause();
    		mPause.setTitle(getString(R.string.MENU_RESUME));
    	}
    }
    
    /**
     * Invoked when the user selects an item from the Menu.
     * 
     * @param item the Menu entry which was selected
     * @return true if the Menu item was legit (and we consumed it), false
     *         otherwise
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
	        case MENU_OPEN:
	        	
	        	mPause.setEnabled(false);
	        	mView.pause();
	        	
	        	Intent i = new Intent(this, FileChooser.class);
	        	
	        	i.putExtra(FileChooser.KEY_TITLE, mView.getRoot() + "*.lav");
	        	i.putExtra(FileChooser.KEY_ROOT, mView.getRoot());
	        	i.putExtra(FileChooser.KEY_FILTER, "\\w+.lav");
	        	
	        	startActivityForResult(i, REQUEST_SELECT);
	        	
	            return true;
	            
	        case MENU_PAUSE:
	        	switchPauseResume();
	            return true;
	            
	        case MENU_ABOUT:
	        	
	        	//Get the app version
	    		String version = "(Unknown)";
	    		try {
	    		        PackageInfo pi = getPackageManager().getPackageInfo(getPackageName(), 0);
	    		        version = pi.versionName;
	    		} catch (PackageManager.NameNotFoundException e) {
	    		        android.util.Log.e(getString(R.string.app_name), "Package name not found", e);
	    		}
	    		
	        	onPopup(getString(R.string.app_name) + " " + version, 
	        			getTextResource(R.raw.about));
	            return true;
	        
	        case MENU_HELP:
	        	onPopup("Help", getTextResource(R.raw.help));
	        	return true;
	        	
	        case MENU_EXIT:
	        	// TODO: exit by send message
	        	mView.stop();
	            System.exit(0);
	            return true;
        }

        return false;
    }

    private String getTextResource(int resourceid) {
		BufferedReader in = new BufferedReader(
								new InputStreamReader(
										getResources().openRawResource(resourceid)));
		
		StringBuilder sb = new StringBuilder();
		try {
			String line;
			while ((line = in.readLine()) != null) { // Read line per line.
				sb.append(line);
				sb.append("\n");
			}
			return sb.toString();
		} catch (IOException e) {
			//Should not happen.
			e.printStackTrace();
			return "";
		}
		
    }
    
    
    private void onPopup(String title, String message) {
				
		// about dialog builder
        OnClickListener okListener = new OnClickListener() { 
            // @Override 
            public void onClick(DialogInterface dialog, int which) { 
                dialog.cancel(); 
                return; 
            } 
        }; 
        
        final Builder popupBuilder = new AlertDialog.Builder(this)
						        		 .setIcon(R.drawable.icon) 
						                 .setTitle(title)
						                 .setMessage(message)
						                 .setPositiveButton("Done", okListener);
    
        Dialog popupDialog; 
        popupDialog = popupBuilder.create();
        popupDialog.show(); 
    }
}