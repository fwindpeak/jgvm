package eastsun.jgvm.plaf.android;

import eastsun.jgvm.plaf.android.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

public class GVmakerAD extends Activity {
	
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

    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        return mView.doCreateOptionsMenu(menu);
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
    	return mView.doOptionsItemSelected(item);
    }
}