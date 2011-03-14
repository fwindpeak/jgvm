package eastsun.jgvm.plaf.android;

import static android.view.KeyEvent.KEYCODE_0;
import static android.view.KeyEvent.KEYCODE_A;
import static android.view.KeyEvent.KEYCODE_ALT_LEFT;
import static android.view.KeyEvent.KEYCODE_ALT_RIGHT;
import static android.view.KeyEvent.KEYCODE_AT;
import static android.view.KeyEvent.KEYCODE_B;
import static android.view.KeyEvent.KEYCODE_C;
import static android.view.KeyEvent.KEYCODE_COMMA;
import static android.view.KeyEvent.KEYCODE_D;
import static android.view.KeyEvent.KEYCODE_DEL;
import static android.view.KeyEvent.KEYCODE_E;
import static android.view.KeyEvent.KEYCODE_ENTER;
import static android.view.KeyEvent.KEYCODE_EXPLORER;
import static android.view.KeyEvent.KEYCODE_F;
import static android.view.KeyEvent.KEYCODE_G;
import static android.view.KeyEvent.KEYCODE_H;
import static android.view.KeyEvent.KEYCODE_I;
import static android.view.KeyEvent.KEYCODE_J;
import static android.view.KeyEvent.KEYCODE_K;
import static android.view.KeyEvent.KEYCODE_L;
import static android.view.KeyEvent.KEYCODE_M;
import static android.view.KeyEvent.KEYCODE_N;
import static android.view.KeyEvent.KEYCODE_NUM;
import static android.view.KeyEvent.KEYCODE_O;
import static android.view.KeyEvent.KEYCODE_P;
import static android.view.KeyEvent.KEYCODE_PERIOD;
import static android.view.KeyEvent.KEYCODE_Q;
import static android.view.KeyEvent.KEYCODE_R;
import static android.view.KeyEvent.KEYCODE_S;
import static android.view.KeyEvent.KEYCODE_SEARCH;
import static android.view.KeyEvent.KEYCODE_SHIFT_LEFT;
import static android.view.KeyEvent.KEYCODE_SHIFT_RIGHT;
import static android.view.KeyEvent.KEYCODE_SPACE;
import static android.view.KeyEvent.KEYCODE_SYM;
import static android.view.KeyEvent.KEYCODE_T;
import static android.view.KeyEvent.KEYCODE_TAB;
import static android.view.KeyEvent.KEYCODE_U;
import static android.view.KeyEvent.KEYCODE_V;
import static android.view.KeyEvent.KEYCODE_VOLUME_DOWN;
import static android.view.KeyEvent.KEYCODE_VOLUME_UP;
import static android.view.KeyEvent.KEYCODE_W;
import static android.view.KeyEvent.KEYCODE_X;
import static android.view.KeyEvent.KEYCODE_Y;
import static android.view.KeyEvent.KEYCODE_Z;
import eastsun.jgvm.module.ScreenModel;
import eastsun.jgvm.module.event.Area;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

/**
 * @version Aug 10, 2009
 * @author FantasyDR
 */
public class VirtualKey extends View {

	
	public VirtualKey(Context context, AttributeSet attrs, KeyBoard key) {
		super(context, attrs);
		int FullKeyX=240;
		int FullKeyY=80;
		mFullKeyRect = new Rect(0, 0, FullKeyX,FullKeyY);
		setSize(FullKeyX, FullKeyY);
		mKeyBoard = key;
		mFullKey=BitmapFactory.decodeResource(getResources(),R.drawable.board);
		mPadKey=BitmapFactory.decodeResource(getResources(),R.drawable.dpad);
		mButtonKey=BitmapFactory.decodeResource(getResources(),R.drawable.buttons);
	}
	KeyBoard mKeyBoard;

    private Bitmap mFullKey;
    private Bitmap mPadKey;
    private Bitmap mButtonKey;
    private boolean mShowAllKey=false;
 
    private Rect mFullKeyRect;
    private Rect mScreenRect;
    private Rect mPadRect;
    private Rect mButtonRect;
    private Rect mPadScaleRect;
    private Rect mButtonScaleRect;
    
    private int keycode=-1;
    
    private float mScale = 3.5f;
    private float mScaleCurrentX = 3.5f;
    private float mScaleCurrentY= 3.5f;
    
    public void setSize(int width, int height) {
    	float maxScaleW = width / (float)mFullKeyRect.right;
    	float maxScaleH = height / (float)mFullKeyRect.bottom;
    	
    	// use the minimum value
    	mScaleCurrentX = maxScaleW < mScale ? maxScaleW:mScale; 
    	mScaleCurrentY = maxScaleH < mScale ? maxScaleH:mScale;
    	
    	// set to center
    	mScreenRect = new Rect( 0, 0, 
    						    (int)(mScaleCurrentX * mFullKeyRect.right), 
    						    (int)(mScaleCurrentY * mFullKeyRect.bottom));
    	mScreenRect.left = (width - mScreenRect.right) / 2;
    	mScreenRect.right += mScreenRect.left;
    	mScreenRect.top = (height - mScreenRect.bottom) / 2;
    	mScreenRect.bottom += mScreenRect.top;
    	
    	int KeyPosX=0;
    	int KeyPosY=160;
    	int PadHeight=120;
    	int PadWidth=120;
    	int ButtonHeight=48;
    	int PadWitdh=112;
    	mPadRect=new Rect(0,0,PadWidth,PadHeight);
    	mPadScaleRect=new Rect();
    	mPadScaleRect.left=KeyPosX;
    	mPadScaleRect.top=KeyPosY;
    	mPadScaleRect.bottom=mPadScaleRect.top+PadHeight;
    	mPadScaleRect.right=mPadScaleRect.left+PadWidth;
    	
    	mButtonRect=new Rect(0,0,PadWitdh,ButtonHeight);
    	mButtonScaleRect=new Rect();
    	mButtonScaleRect.left=width-PadWitdh-60;
    	mButtonScaleRect.top=KeyPosY+(PadHeight-ButtonHeight)/2;
    	mButtonScaleRect.bottom=mButtonScaleRect.top+ButtonHeight;
    	mButtonScaleRect.right=mButtonScaleRect.left+PadWitdh;
    	
    	
    }

    public void screenChanged(ScreenModel screenModel, Area area) {	
		synchronized (this) {
			
		}
	}
    public synchronized void refresh(Canvas canvas) { 
    	Paint paint=new Paint();
    	paint.setAlpha(80);
    	if(mShowAllKey)
    		canvas.drawBitmap(mFullKey,mFullKeyRect, mScreenRect, paint);
    	else
    	{
    		canvas.drawBitmap(mPadKey,mPadRect, mPadScaleRect, paint);
    		canvas.drawBitmap(mButtonKey,mButtonRect, mButtonScaleRect, paint);
    	}
    }
    @Override
	public boolean onTouchEvent(MotionEvent event) {
    	boolean bValue=false;
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			bValue=  HitKey(event.getX(),event.getY());
			break;
		case MotionEvent.ACTION_UP:
			mKeyBoard.doKeyUp(keycode,new KeyEvent(KeyEvent.ACTION_DOWN, keycode));
			break;
		case MotionEvent.ACTION_MOVE:
			break;
		}

		return bValue;
	}
    
	private boolean HitKey(float x, float y) {
		boolean bNeedUpdata=false;
		keycode = -1;
		
		int KeyRowNum=5;
		int KeyArrayNum=10;
		int cx=(int)(x/(mScreenRect.width()/KeyArrayNum));
		int cy=(int)(y/(mScreenRect.height()/KeyRowNum));
		
		if (!mShowAllKey) {
			int PadCenterX = mPadScaleRect.left + mPadScaleRect.width() / 2;
			int PadCenterY = mPadScaleRect.top + mPadScaleRect.height() / 2;
			int PadThresholdX = mPadScaleRect.width() / 2;
			int PadThresholdY = mPadScaleRect.height() / 2;
			int PadValue = 30;
			
			int ButtonCenterX = mButtonScaleRect.left + mButtonScaleRect.width() / 2;
			int ButtonCenterY = mButtonScaleRect.top + mButtonScaleRect.height() / 2;
			int ButtonThresholdX = mButtonScaleRect.width() / 2;
			int ButtonThresholdY = mButtonScaleRect.height() / 2;
			int ButtonValue = 30;
				
			if (x >= PadCenterX - PadValue && x <= PadCenterX + PadValue
					&& y >= PadCenterY - PadThresholdY && y <= PadCenterY) // up
				keycode = KEYCODE_COMMA;
			else if (x >= PadCenterX - PadValue && x <= PadCenterX + PadValue
					&& y >= PadCenterY && y <= PadCenterY + PadThresholdY) // down
				keycode = KEYCODE_ALT_RIGHT;
			else if (x >= PadCenterX - PadThresholdX && x <= PadCenterX
					&& y >= PadCenterY - PadValue && y <= PadCenterY + PadValue) // left
				keycode = KEYCODE_PERIOD;
			else if (x >= PadCenterX && x <= PadCenterX + PadThresholdX
					&& y >= PadCenterY - PadValue && y <= PadCenterY + PadValue) // right
				keycode = KEYCODE_SHIFT_RIGHT;
			
			
			else if(x >= ButtonCenterX - ButtonThresholdX && x <= ButtonCenterX 
					&& y >= ButtonCenterY - ButtonValue && y <= ButtonCenterY+ButtonValue) // button a
				keycode = KEYCODE_ENTER; //enter
			else if (x >= ButtonCenterX && x <= ButtonCenterX + ButtonThresholdX
					&& y >= ButtonCenterY - ButtonValue && y <= ButtonCenterY + ButtonValue) // button b
				keycode = KEYCODE_DEL;//esc
			
			else {
				if (cy < 1) {
					mShowAllKey = true;
					bNeedUpdata = true;
				}
			}
		}
		else
		{		
			if(cx<KeyArrayNum&&cy<KeyRowNum)
				keycode=rawKeyCodes[cy][cx];
			else
			{
				mShowAllKey=false;
				bNeedUpdata=true;
			}	
		}
		if (keycode > 0) {
			mKeyBoard.doKeyDown(keycode, new KeyEvent(KeyEvent.ACTION_DOWN,
					keycode));
		}
		else if(keycode==0)
		{
			mShowAllKey=false;
			bNeedUpdata=true;
		}
		return bNeedUpdata;
	}

	private static int[][] rawKeyCodes = {
			{ 0, -1, -1, -1, -1, -1, KEYCODE_SHIFT_LEFT /* VK_F1 */,
					KEYCODE_ALT_LEFT /* VK_F2 */, KEYCODE_SEARCH /* VK_F3 */,
					KEYCODE_AT /* VK_F4 */},
			{ KEYCODE_Q, KEYCODE_W, KEYCODE_E, KEYCODE_R, KEYCODE_T, KEYCODE_Y,
					KEYCODE_U, KEYCODE_I, KEYCODE_O, KEYCODE_P },
			{ KEYCODE_A, KEYCODE_S, KEYCODE_D, KEYCODE_F, KEYCODE_G, KEYCODE_H,
					KEYCODE_J, KEYCODE_K, KEYCODE_L, KEYCODE_ENTER },
			{ KEYCODE_Z, KEYCODE_X, KEYCODE_C, KEYCODE_V, KEYCODE_B, KEYCODE_N,
					KEYCODE_M, KEYCODE_VOLUME_UP /* VK_PAGE_UP */,
					KEYCODE_COMMA/* KEYCODE_DPAD_UP */, KEYCODE_VOLUME_DOWN /* KEYCODE_PAGE_DOWN */},
			{ KEYCODE_EXPLORER /* VK_HELP */, KEYCODE_TAB /* VK_SHIFT */,
					KEYCODE_NUM /* VK_CAPS_LOCK */,
					KEYCODE_DEL/* VK_ESCAPE */, KEYCODE_0,
					KEYCODE_SYM/* VK_PERIOD */, KEYCODE_SPACE,

					// KEYCODE_DPAD_LEFT, KEYCODE_DPAD_DOWN, KEYCODE_DPAD_RIGHT,
					KEYCODE_PERIOD, KEYCODE_ALT_RIGHT, KEYCODE_SHIFT_RIGHT } };
}
