package eastsun.jgvm.plaf.android;

import java.nio.IntBuffer;

import eastsun.jgvm.module.JGVM;
import eastsun.jgvm.module.ScreenModel;
import eastsun.jgvm.module.event.Area;
import eastsun.jgvm.module.event.ScreenChangeListener;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

/**
 * @version Aug 10, 2009
 * @author FantasyDR
 */
public class ScreenPane {

    int[] mBuffer = new int[ScreenModel.WIDTH * ScreenModel.HEIGHT];
    private Bitmap mBitmap;
    private boolean mDirty;
    
    public ScreenPane(JGVM gvm) {
    	mBitmap = Bitmap.createBitmap(ScreenModel.WIDTH, ScreenModel.HEIGHT, Bitmap.Config.ARGB_8888);
        gvm.setColor(0xff000000, 0xffffffff);
        
        mBufferRect = new Rect(0, 0, ScreenModel.WIDTH, ScreenModel.HEIGHT);
        setSize(ScreenModel.WIDTH, ScreenModel.HEIGHT);
        
        gvm.addScreenChangeListener(new ScreenChangeListener() {
        	
            public void screenChanged(ScreenModel screenModel, Area area) {
                screenModel.getRGB(mBuffer, area, 1, 0);
                mDirty = true;
            }
            
        });
    }
    
    public boolean isDirty() {
    	return mDirty;
    }
    
    public void update() {
        // TODO: use buffer directly
    	IntBuffer buffer = IntBuffer.allocate(ScreenModel.WIDTH * ScreenModel.HEIGHT);
    	buffer.put(mBuffer);
    	
        buffer.position(0);
        mBitmap.copyPixelsFromBuffer(buffer);
        mDirty = false;
    }
    
    private Rect mBufferRect;
    private Rect mScreenRect;
    
    private float mScale = 2.0f;
    private float mScaleCurrent = 2.0f;
    
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

    public void refresh(Canvas canvas) {
//        canvas.drawBitmap( mBuffer, 0, ScreenModel.WIDTH, 
//        				   0, 0, ScreenModel.WIDTH, ScreenModel.HEIGHT, 
//        				   false, null);
    	canvas.drawBitmap(mBitmap, mBufferRect, mScreenRect, null);
    }
}
