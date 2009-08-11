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
    
    private synchronized WorkerThread getThread() {
    	return mThread;
    }
    
    public void pause() {
    	WorkerThread thread = getThread();
    	if(thread != null) {
    		thread.setState(WorkerThread.STATE_PAUSE);
    		setMsg("Pause");
    	}
    }
    
    public void resume() {
    	WorkerThread thread = getThread();
    	if(thread != null) {
    		thread.setState(WorkerThread.STATE_RUNNING);
    		setMsg("Running");
    	}
    }
    
    public void stop() {
    	WorkerThread thread = getThread();
    	if(thread != null && thread.isAlive()) {
    		
	        int retry = 0;
	        thread.setRunning(false);
	        while (retry >= 0 && retry < 10) {
	            try {
	            	thread.join(100);
	                retry = thread.isAlive()? retry + 1:-1;
	            } catch (InterruptedException e) {
	            	android.util.Log.e("MainView", e.toString());
	            }
	        }
	        
	        // force to stop
	        if(thread.isAlive()) {
	        	thread.interrupt();
	        	try {
					thread.join();
				} catch (InterruptedException e) {
					android.util.Log.e("MainView", e.toString());
				}
	        }
	        
    	}
    }
        
    public void load(String fileName) {
    	LavApp lavApp = null;
    	
        try {
        	InputStream in = new FileInputStream(fileName);
            lavApp = LavApp.createLavApp(in);
        } catch (Exception ex) {
            android.util.Log.e("MainView", ex.toString());
            return;
        }
        
        stop();
        mVM.loadApp(lavApp);
        synchronized(this) {
        	WorkerThread thread = new WorkerThread();
        	thread.setRunning(true);
        	thread.setSurfaceHolder(getHolder());
        	thread.start();
        	mThread = thread;
        }
        resume();
    }
    
    class WorkerThread extends Thread {

		/** Handle to the surface manager object we interact with */
		private SurfaceHolder mSurfaceHolder;
		private boolean mRun;
		
		public static final int STATE_PAUSE = 1;
        public static final int STATE_RUNNING = 2;
        public static final int STATE_EXITED = 3;
		private int mState = STATE_PAUSE;

		public WorkerThread() {
		}
		
		public synchronized void setSurfaceHolder(SurfaceHolder surfaceHolder) {
			mSurfaceHolder = surfaceHolder;
		}

		/* Callback invoked when the surface dimensions change. */
		public synchronized void setSurfaceSize(int width, int height) {
			mScreen.setSize(width, height);
		}
		
		public synchronized SurfaceHolder getSurfaceHolder() {
			return mSurfaceHolder;
		}

		public void setRunning(boolean b) {
			mRun = b;
		}
		
		public void setState(int state) {
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
				android.util.Log.e("WorkerThread", ex.toString());
			} finally {
				mVM.dispose();
				setState(STATE_EXITED);
				setMsg("Exited");
			}
		}
		
		private void refreshOnDirty() {
			Canvas c = null;
			if(mScreen.isDirty()) {
				SurfaceHolder holder = getSurfaceHolder();
				if( holder != null ) {
					mScreen.update();
					try {
						c = holder.lockCanvas(null);
						synchronized (this) {
							mScreen.refresh(c);
						}
					} catch (Exception ex) {
						android.util.Log.e("WorkerThread", ex.toString());
					} finally {
						if (c != null) {
							holder.unlockCanvasAndPost(c);
						}
					}
				}
			}
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		synchronized(this) {
			WorkerThread thread = getThread();
			if(thread != null) {
				thread.setSurfaceSize(width, height);
			}
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		synchronized(this) {
			WorkerThread thread = getThread();
			if(thread != null) {
				thread.setSurfaceHolder(holder);
			}
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
        // we have to tell thread to shut down & wait for it to finish, or else
        // it might touch the Surface after we return and explode
		synchronized(this) {
			WorkerThread thread = getThread();
			if(thread != null) {
				thread.setSurfaceHolder(null);
			}
		}
	}
}
