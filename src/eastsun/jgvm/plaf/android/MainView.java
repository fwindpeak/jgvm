package eastsun.jgvm.plaf.android;

import eastsun.jgvm.module.GvmConfig;
import eastsun.jgvm.module.JGVM;
import eastsun.jgvm.module.LavApp;
import eastsun.jgvm.module.ScreenModel;
import eastsun.jgvm.module.event.Area;
import eastsun.jgvm.module.event.ScreenChangeListener;
import eastsun.jgvm.module.io.DefaultFileModel;

import java.io.FileInputStream;
import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;

/**
 * @version Aug 10, 2009
 * @author FantasyDR
 */
public class MainView extends SurfaceView implements SurfaceHolder.Callback,
		ScreenChangeListener {

	JGVM mVM;

	ScreenPane mScreen;
	KeyBoard mKeyBoard;
	WorkerThread mThread;
	DrawThread mDrawThread;
	TextView mStatusText;
	VirtualKey mVirtualKey;
	// TODO: move this hack for resource loading
	private static MainView sCurrent;
	SurfaceHolder holder;

	public static MainView getCurrentView() {
		return sCurrent;
	}

	public MainView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mKeyBoard = new KeyBoard();
		mVM = JGVM.newGVM(new GvmConfig(), new DefaultFileModel(new FileSys(
				getRoot())), mKeyBoard.getKeyModel());
		mScreen = new ScreenPane(mVM);
		mVirtualKey=new VirtualKey(context, attrs,mKeyBoard);
		// make itself as the handler
		mVM.addScreenChangeListener(this);

		// register our interest in hearing about changes to our surface
		holder = getHolder();
		holder.addCallback(this);

		setFocusable(true); // make sure we get key events

		sCurrent = this;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		if(mVirtualKey.onTouchEvent(event))
		{
			mDrawThread.SetChange();
		}
		return true;
		
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

	private synchronized WorkerThread create() {
		WorkerThread thread = new WorkerThread(new Handler() {
			@Override
			public void handleMessage(Message m) {
				mStatusText.setText(m.getData().getString("text"));
			}
		});

		thread.setRunning(true);
		thread.start();
		mDrawThread = new DrawThread(new Handler() {
			@Override
			public void handleMessage(Message m) {
				mStatusText.setText(m.getData().getString("text"));
			}
		});
		mDrawThread.start();
		mThread = thread;
		return thread;
	}

	public void pause() {
		WorkerThread thread = getThread();
		if (thread != null) {
			if (thread.setState(WorkerThread.STATE_PAUSE)) {
				thread.sendMessage("Pause");
			}
		}
	}

	public void resume() {
		WorkerThread thread = getThread();
		if (thread != null) {
			if (thread.setState(WorkerThread.STATE_RUNNING)) {
				thread.sendMessage("Running");
			}
		}
	}

	public void stop() {
		WorkerThread thread = getThread();
		if (thread != null && thread.isAlive()) {

			int retry = 0;
			thread.setRunning(false);
			while (retry >= 0 && retry < 10) {
				try {
					thread.join(100);
					retry = thread.isAlive() ? retry + 1 : -1;
				} catch (InterruptedException e) {
					android.util.Log.e("MainView", e.toString());
				}
			}

			// force to stop
			if (thread.isAlive()) {
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

		pause();

		try {
			InputStream in = new FileInputStream(fileName);
			lavApp = LavApp.createLavApp(in);
		} catch (Exception ex) {
			android.util.Log.e("MainView", ex.toString());
			return false;
		}

		stop();
		mVM.loadApp(lavApp);
		create();

		return true;
	}

	protected void paint(Area area) {
		Canvas c = null;
		Paint paint = new Paint();
		SurfaceHolder holder = getHolder();
		try {
			c = holder.lockCanvas(null);
			mScreen.refresh(c, area);
			paint.setAlpha(100);
		} catch (Exception ex) {
			android.util.Log.e("MainView", ex.toString());
		} finally {
			if (c != null) {
				holder.unlockCanvasAndPost(c);
			}
		}
	}

	class WorkerThread extends Thread {
		private boolean mRun;

		public static final int STATE_READY = 0;
		public static final int STATE_PAUSE = 1;
		public static final int STATE_RUNNING = 2;
		public static final int STATE_STOPPED = 3;
		public static final int STATE_EXITED = 4;
		private int mState;

		Handler mHandler;

		public WorkerThread(Handler handler) {
			mHandler = handler;

			mState = STATE_READY;
			sendMessage("Ready");
		}

		public void setRunning(boolean b) {
			mRun = b;
		}

		public boolean setState(int state) {
			// must set to loading after exited
			if (mState >= STATE_STOPPED && state < mState) {
				return false;
			} else {
				mState = state;
				return true;
			}
		}

		public boolean isPaused() {
			return mState <= STATE_PAUSE;
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

						if (step % inteval == 0) {

							long currentTick = System.currentTimeMillis();
							long elapsedTick = currentTick - lastTick;
							if (elapsedTick > 1000) {
								float frenquence = (step * 1000 / (float) elapsedTick);
//								sendMessage("Frequency: "
//										+ String.valueOf(frenquence));
								sendMessage("");
								step = 0;
								lastTick = currentTick;
							}

							Thread.sleep(3, 100);
						}

					} else {
						if (step != 0) {
							if (setState(STATE_STOPPED)) {
								sendMessage("Stopped");
							}
						}
						step = 0;
					}
				}

			} catch (Exception ex) {
				android.util.Log.e("WorkerThread", ex.toString());
			} finally {
				mVM.dispose();
				if (setState(STATE_EXITED)) {
					sendMessage("Exited");
				}
			}
		}
		
		
		public void sendMessage(final String text) {
			Message msg = mHandler.obtainMessage();
			Bundle b = new Bundle();
			b.putString("text", text);
			msg.setData(b);
			mHandler.sendMessage(msg);
		}
	}

	class DrawThread extends Thread {

		private boolean mNeedChange = false;
		private ScreenModel mScreenModel;
		private Area mArea;
		Handler mHandler;

		public void SetChange(ScreenModel screenModel, Area area) {
			mArea = area;
			mScreenModel = screenModel;
			mNeedChange = true;
		}
		public void SetChange() {
			if(mNeedChange)
				return;
			mNeedChange = true;
			mArea=null;
		}


		public DrawThread(Handler handler) {
			mHandler = handler;
		}

		public void sendMessage(final String text) {
			Message msg = mHandler.obtainMessage();
			Bundle b = new Bundle();
			b.putString("text", text);
			msg.setData(b);
			mHandler.sendMessage(msg);
		}

		@Override
		public void run() {
			while (true) {
				if (mNeedChange == true) {
					//mScreen.screenChanged(mScreenModel, mArea);
					Canvas c = null;
					SurfaceHolder holder = getHolder();
					try {
						Thread.sleep(30, 0);
						mNeedChange = false;
						c = holder.lockCanvas(null);
						mScreen.refresh(c, mArea);
						mVirtualKey.refresh(c);
//						sendMessage(String.valueOf(mArea.getX()) + "y: "
//								+ String.valueOf(mArea.getY()));
					} catch (Exception ex) {
						android.util.Log.e("MainView", ex.toString());
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
		synchronized (this) {
			mScreen.setSize(width, height);
			mVirtualKey.setSize(width, height);
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		synchronized (this) {
			//paint(null);
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// we have to tell thread to shut down & wait for it to finish, or else
		// it might touch the Surface after we return and explode
	}

	@Override
	public void screenChanged(ScreenModel screenModel, Area area) {
		mScreen.screenChanged(screenModel, area);
		//paint(area);
		mDrawThread.SetChange(screenModel, area);
	}
}