package eastsun.jgvm.plaf.android;


import eastsun.jgvm.module.JGVM;
import eastsun.jgvm.module.ScreenModel;
import eastsun.jgvm.module.event.Area;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

/**
 * @version Aug 10, 2009
 * @author FantasyDR
 */
public class ScreenPane {

    int[] mBuffer = new int[ScreenModel.WIDTH * ScreenModel.HEIGHT];
    int[] mLastBuffer=new int[ScreenModel.WIDTH * ScreenModel.HEIGHT];
    private Bitmap mBitmap;
    private int mUpdataState=0;
    private boolean mOptimize=false;
    public ScreenPane(JGVM gvm) {
    	mBitmap = Bitmap.createBitmap(ScreenModel.WIDTH, ScreenModel.HEIGHT, Bitmap.Config.ARGB_8888);
        gvm.setColor(0xff000000, 0xff40c040);
        mBufferRect = new Rect(0, 0, ScreenModel.WIDTH, ScreenModel.HEIGHT);
        setSize(ScreenModel.WIDTH, ScreenModel.HEIGHT);
    }
    
   
    private Rect mBufferRect;
    private Rect mScreenRect;
    
    private float mScale = 3.5f;
    private float mScaleCurrent = 3.5f;
    
    public void setSize(int width, int height) {
    	float maxScaleW = width / (float)mBufferRect.right;
    	float maxScaleH = height / (float)mBufferRect.bottom;
    	
    	// use the minimum value
    	mScaleCurrent = maxScaleW < mScale ? maxScaleW:mScale; 
    	mScaleCurrent = maxScaleH < mScaleCurrent ? maxScaleH:mScaleCurrent;
    	
    	// set to center
    	mScreenRect = new Rect( 0, 0, 
    						    (int)(mScaleCurrent * mBufferRect.right), 
    						    (int)(mScaleCurrent * mBufferRect.bottom));
    	mScreenRect.left = (width - mScreenRect.right) / 2;
    	mScreenRect.right += mScreenRect.left;
    	mScreenRect.top = (height - mScreenRect.bottom) / 2;
    	mScreenRect.bottom += mScreenRect.top;
    }

    public void screenChanged(ScreenModel screenModel, Area area) {
    	screenModel.getRGB(mBuffer, area, 1, 0);
    	
		synchronized (this) {
			// TODO: area information is unused.
	
				mBitmap.setPixels(mBuffer, 0, ScreenModel.WIDTH, 0, 0,
						ScreenModel.WIDTH, ScreenModel.HEIGHT);	
				}
		}
    // refresh current screen to specific canvas
    public synchronized void refresh(Canvas canvas, Area area) {
    	//TODO: area information is unused. 
    	canvas.drawColor(-12533696);
    		canvas.drawBitmap(mBitmap,mBufferRect, mScreenRect, null);
    }
    public int GVMToScreent(int n){
    	return (int)(n*mScaleCurrent);
    }
}
