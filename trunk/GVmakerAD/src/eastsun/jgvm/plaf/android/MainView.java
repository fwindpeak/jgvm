package eastsun.jgvm.plaf.android;

import eastsun.jgvm.module.GvmConfig;
import eastsun.jgvm.module.JGVM;
import eastsun.jgvm.module.LavApp;
import eastsun.jgvm.module.io.DefaultFileModel;

import java.io.FileInputStream;
import java.io.InputStream;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;

/**
 * @version Aug 10, 2009
 * @author FantasyDR
 */
public class MainView extends SurfaceView implements SurfaceHolder.Callback {

    JGVM mVM;
    
    ScreenPane mScreen;
    KeyBoard mKeyBoard;
    WorkerThread mThread;
    TextView mStatusText;
    
    
    // TODO: move this hack for resource loading
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
	
    public void setTextView(TextView textView) {
        mStatusText = textView;
    }
        
    private synchronized WorkerThread getThread() {
    	return mThread;
    }
    
    private synchronized void createThread() {
    	WorkerThread thread = new WorkerThread(new Handler() {
        	@Override
            public void handleMessage(Message m) {
                mStatusText.setText(m.getData().getString("text"));
            }
        });
    	
    	thread.setRunning(true);
    	thread.setSurfaceHolder(getHolder());
    	thread.start();
    	mThread = thread;
    }
    
    public void pause() {
    	WorkerThread thread = getThread();
    	if(thread != null) {
    		thread.setState(WorkerThread.STATE_PAUSE);
    		thread.sendMessage("Pause");
    	}
    }
    
    public void resume() {
    	WorkerThread thread = getThread();
    	if(thread != null) {
    		thread.setState(WorkerThread.STATE_RUNNING);
    		thread.sendMessage("Running");
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
        
    public boolean load(String fileName) {
    	LavApp lavApp = null;
    	
        try {
        	InputStream in = new FileInputStream(fileName);
            lavApp = LavApp.createLavApp(in);
        } catch (Exception ex) {
            android.util.Log.e("MainView", ex.toString());
            return false;
        }
        
        stop();
        mVM.loadApp(lavApp);
        createThread();
        return true;
    }
    
    class WorkerThread extends Thread {

		/** Handle to the surface manager object we interact with */
		private SurfaceHolder mSurfaceHolder;
		private boolean mRun;
		
		public static final int STATE_PAUSE = 1;
        public static final int STATE_RUNNING = 2;
        public static final int STATE_EXITED = 3;
		private int mState = STATE_PAUSE;

		Handler mHandler;
		
		public WorkerThread(Handler handler) {
			mHandler = handler;
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
				long step = 0;
				long lastTick = 0;
				final int inteval = 100;
				
				while (mRun && !isInterrupted()) {
					
					if (isPaused()) {
						continue;
					}
					
					if (!mVM.isEnd()) {
						
						mVM.nextStep();
						step++;

						refreshOnDirty();

						if (step % inteval == 0) {
							
							long currentTick = System.currentTimeMillis();
							long elapsedTick = currentTick - lastTick;
							if( elapsedTick > 1000 ) {
								float frenquence = (step * 1000 / (float)elapsedTick);
								sendMessage("Frenquence: " + String.valueOf(frenquence) );
								step = 0;
								lastTick = currentTick;
							}
							
							Thread.sleep(0, 100);
						}
						
					} else {
						if(step != 0) {
							sendMessage("Stopped");
						}
						step = 0;
					}
				}
			} catch (Exception ex) {
				android.util.Log.e("WorkerThread", ex.toString());
			} finally {
				mVM.dispose();
				setState(STATE_EXITED);
				sendMessage("Exited");
			}
		}
		
	    public void sendMessage(final String text) {
	    	Message msg = mHandler.obtainMessage();
	        Bundle b = new Bundle();
	        b.putString("text", text);
	        msg.setData(b);
	        mHandler.sendMessage(msg);
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
