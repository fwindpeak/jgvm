package eastsun.jgvm.plaf.android;

import eastsun.jgvm.module.GvmConfig;
import eastsun.jgvm.module.JGVM;
import eastsun.jgvm.module.LavApp;
import eastsun.jgvm.module.io.DefaultFileModel;

import java.io.FileInputStream;
import java.io.InputStream;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * @version Aug 10, 2009
 * @author FantasyDR
 */
public class MainView extends SurfaceView implements SurfaceHolder.Callback {

    JGVM mVM;
    
    ScreenPane mScreen;
    KeyBoard mKeyBoard;
    WorkerThread mThread;
    
    private static MainView sCurrent;
    public static MainView getCurrentView() {
    	return sCurrent;
    }
    
    public MainView(Context context, AttributeSet attrs) {
    	super(context, attrs);
    	
    	mKeyBoard = new KeyBoard();
        mVM = JGVM.newGVM( new GvmConfig(), 
        				   new DefaultFileModel(new FileSys(getRoot())),
        				   mKeyBoard.getKeyModel()
        				  );
        mScreen = new ScreenPane(mVM);
                
        // register our interest in hearing about changes to our surface
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);

        setFocusable(true); // make sure we get key events
        
        sCurrent = this;
    }
    
    public String getRoot() {
    	return "/sdcard/gvm/";
    }
    
	public KeyBoard getKeyBoard() {
		return mKeyBoard;
	}

    private void setMsg(final String msg) {

    }
    
    public void pause() {
    	if(mThread != null && mThread.isAlive()) {
    		mThread.setState(WorkerThread.STATE_PAUSE);
    	}
		setMsg("Pause");
    }
    
    public void resume() {
    	if(mThread != null && mThread.isAlive()) {
    		mThread.setState(WorkerThread.STATE_RUNNING);
    	}
        setMsg("Running");
    }
    
    public void stop() {
    	
    	if(mThread != null && mThread.isAlive()) {
    		
	        boolean retry = true;
	        mThread.setRunning(false);
	        while (retry) {
	            try {
	                mThread.join();
	                retry = false;
	            } catch (InterruptedException e) {
	            	//
	            }
	        }
	        
	        mThread = null;
    	}
    }
    
    public void load(String fileName) {
    	InputStream in = null;
        try {
            in = new FileInputStream(fileName);
            LavApp lavApp = LavApp.createLavApp(in);
            mVM.loadApp(lavApp);
            
            if(mThread == null || !mThread.isAlive()) {
            	mThread = new WorkerThread(getHolder());
            	mThread.setState(WorkerThread.STATE_RUNNING);
            	mThread.setRunning(true);
            	mThread.start();
            }
            else
            {
            	mThread.setState(WorkerThread.STATE_RUNNING);
            }
            
            setMsg("Running");
        } catch (Exception ex) {
            System.err.println(ex);
        }
    }
    
    class WorkerThread extends Thread {

		/** Handle to the surface manager object we interact with */
		private SurfaceHolder mSurfaceHolder;
		private boolean mRun;
		
		public static final int STATE_PAUSE = 1;
        public static final int STATE_RUNNING = 2;
        public static final int STATE_EXITED = 3;
		private int mState = STATE_PAUSE;

		public WorkerThread(SurfaceHolder surfaceHolder) {
			// get handles to some important objects
			mSurfaceHolder = surfaceHolder;
		}

		/* Callback invoked when the surface dimensions change. */
		public void setSurfaceSize(int width, int height) {
			// synchronized to make sure these all change atomically
			synchronized (mSurfaceHolder) {
				mScreen.setSize(width, height);
			}
		}

		public void setRunning(boolean b) {
			mRun = b;
		}
		
		public synchronized void setState(int state) {
			mState = state;
		}
		
        public boolean isPaused() {
            return STATE_PAUSE == mState;
        }

		@Override
		public void run() {			
			try {
				int step = 0;
				
				while (mRun && !isInterrupted()) {
					
					if (isPaused()) {
						continue;
					}
					
					if (!mVM.isEnd()) {

						mVM.nextStep();
						step++;

						refreshOnDirty();

						if (step == 100) {
							step = 0;
							Thread.sleep(0, 100);
						}
					}
				}
			} catch (Exception ex) {
				System.out.println(ex);
			} finally {
				mVM.dispose();
				setState(STATE_EXITED);
				setMsg("Exited");
			}
		}
		
		private void refreshOnDirty() {
			Canvas c = null;
			if(mScreen.isDirty()) {
				mScreen.update();
				try {
					c = mSurfaceHolder.lockCanvas(null);
					//synchronized (mSurfaceHolder) {
						// if (mMode == STATE_RUNNING) updatePhysics();
						mScreen.refresh(c);
					//}
				} finally {
					if (c != null) {
						mSurfaceHolder.unlockCanvasAndPost(c);
					}
				}
			}
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		if(mThread != null) {
			mThread.setSurfaceSize(width, height);
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		//
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
        // we have to tell mThread to shut down & wait for it to finish, or else
        // it might touch the Surface after we return and explode
        stop();
	}
}
