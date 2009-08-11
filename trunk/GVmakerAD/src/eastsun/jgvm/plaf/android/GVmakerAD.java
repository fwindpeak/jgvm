package eastsun.jgvm.plaf.android;

import mega.utils.FileChooser;
import eastsun.jgvm.plaf.android.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TextView;

public class GVmakerAD extends Activity {
	
	public static final int REQUEST_SELECT = 0x01;
	private MainView mView;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
		// turn off the window's title bar
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		super.onCreate(savedInstanceState);

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

    private static final int MENU_ABOUT = 1;
    private static final int MENU_OPEN = 2;
    private static final int MENU_PAUSE = 3;
    private static final int MENU_EXIT = 4;
    private MenuItem mPause;
        
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        
        menu.add(Menu.NONE, MENU_ABOUT, Menu.NONE, getString(R.string.MENU_ABOUT));
        menu.add(Menu.NONE, MENU_OPEN, Menu.NONE, getString(R.string.MENU_OPEN));
        
        mPause = menu.add(Menu.NONE, MENU_PAUSE, Menu.NONE, getString(R.string.MENU_PAUSE));
        mPause.setCheckable(true);
        
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
        	i.putExtra(FileChooser.KEY_FILTER, "[ _\\-A-Za-z0-9\u4e00-\u9fa5]*.lav");
        	
        	startActivityForResult(i, REQUEST_SELECT);
        	
            return true;
            
        case MENU_PAUSE:
        	switchPauseResume();
            return true;
            
        case MENU_ABOUT:
        	// TODO: pop about window
            return true;
            
        case MENU_EXIT:
        	// TODO: exit by send message
        	mView.stop();
            System.exit(0);
            return true;
    }

    return false;
    }
}