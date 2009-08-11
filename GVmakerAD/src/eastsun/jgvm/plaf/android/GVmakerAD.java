package eastsun.jgvm.plaf.android;

import mega.utils.FileChooser;
import eastsun.jgvm.plaf.android.R;
import eastsun.jgvm.plaf.android.MainView.WorkerThread;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

public class GVmakerAD extends Activity {
	
	public static final int REQUEST_SELECT = 0x01;
	private MainView mView;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	 super.onCreate(savedInstanceState);
         
         // turn off the window's title bar
         requestWindowFeature(Window.FEATURE_NO_TITLE);
         
         setContentView(R.layout.main);
         mView = (MainView) findViewById(R.id.mainview);
         
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
    private static final int MENU_RESUME = 3;
    private static final int MENU_EXIT = 4;
        
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        
        menu.add(Menu.NONE, MENU_OPEN, Menu.NONE, "Open");
        menu.add(Menu.NONE, MENU_PAUSE, Menu.NONE, "Pause");
        menu.add(Menu.NONE, MENU_RESUME, Menu.NONE, "Resume");
        menu.add(Menu.NONE, MENU_EXIT, Menu.NONE, "Exit");
        
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
    	    break;
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
        	mView.pause();
        	
        	Intent i = new Intent(this, FileChooser.class);
        	
        	i.putExtra(FileChooser.KEY_TITLE, mView.getRoot() + "*.lav");
        	i.putExtra(FileChooser.KEY_ROOT, mView.getRoot());
        	i.putExtra(FileChooser.KEY_FILTER, "[ _\\-A-Za-z0-9]*.lav");
        	
        	startActivityForResult(i, REQUEST_SELECT);
        	
            return true;
            
        case MENU_PAUSE:
        	mView.pause();
            return true;
            
        case MENU_RESUME:
        	mView.resume();
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