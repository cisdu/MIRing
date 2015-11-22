package com.xd.miring;

import java.util.Arrays;

import android.animation.Animator.AnimatorListener;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DrawFilter;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
/**
 * create by feijie.xfj
 * 2015-11-22
 * */
public class RingView extends View {

	/**
	 * ����View�Ŀ���
	 * */
	private int mTotalHeight, mTotalWidth;

	/**
	 * �����ߵ��ܿ��� -- Բ���Ŀ���
	 * */
	private int mHeartBeatWidth;
	
	/**
	 * Բ���뾶 ����view�Ŀ��ȼ���
	 * */
	private int mRadius = 200;

	/**
	 * Բ�������ĵ� -- ��Բ������ת����ʱ��Ҫʹ��
	 * */
	private int x, y;

	/**
	 * Բ��ʹ��
	 * */
	private Paint mRingPaint;

	/**
	 * Բ������ʹ�� -- ��mRingPaintΨһ��ͬ�÷�������ɫ
	 * */
	private Paint mRingAnimPaint;

	/**
	 * Բ����С ����
	 * */
	private RectF mRectf;

	private Context mContext;

	/**
	 * Բ�� ����
	 * */
	private final int mHeartPaintWidth = 50;

	/**
	 * Բ��������ʼʱ ������ƫ����
	 * */
	private int mAnimAngle = -1;

	/**
	 * ������ Y������
	 * */
	private float[] mOriginalYPositon;
	
	/**
	 * ������ Y������ -- Ĭ������
	 * */
	private float [] mDefaultYPostion;
	// y = Asin(w*x)+Y
	/**
	 * sin���� ��������
	 * */
	private float mPeriodFraction = 0;
	
	/**
	 * �ڳ���ƫ����
	 * */
	private final int OFFSET_Y = 0;
	
	/**
	 * canvas����ݿ�����Ҫ
	 * */
	private DrawFilter mDrawFilter;
	
	/**
	 * ��������ƫ����
	 * */
	private int mOffset=0;
	
	/**
	 * ���
	 * */
	private float AmplitudeA = 200;// ���
	
	/**
	 * ���������ٶ�
	 * */
	private final int SPEED = 5;
	
	/**
	 * ��SPEEDת��Ϊʵ���ٶ�
	 * */
	private int mOffsetSpeed;
	
	private Paint mHeartBeatPaint;
	
	private void init() {
		mRingPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		if (!isInEditMode()) {
			// ��ɴ���Ĵ����
			mRingPaint.setColor(mContext.getResources().getColor(R.color.heart_default));
		}
		mRingPaint.setStrokeWidth(mHeartPaintWidth);
		mRingPaint.setStyle(Style.STROKE);
		mRingAnimPaint = new Paint(mRingPaint);
		mRingAnimPaint.setColor(Color.WHITE);
		
		
		//��ʼ����������
		mDrawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG);
		mOffsetSpeed = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, SPEED, mContext.getResources().getDisplayMetrics());
		mHeartBeatPaint  =new Paint(Paint.ANTI_ALIAS_FLAG);
		mHeartBeatPaint.setStrokeWidth(5);
		if (!isInEditMode()) {
			mHeartBeatPaint.setColor(mContext.getResources().getColor(R.color.heartbeat));
		}
		//startRingAnim();
		//startHeartBeatAnmi();
		//startSecondFrameAnmi();
	}


	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		mTotalHeight = h;
		mTotalWidth = w;
		mHeartBeatWidth = w - mHeartPaintWidth*2-40; //��Բ����
		mFirstFrameOffset  =mHeartBeatWidth-1;
		AmplitudeA = (mTotalHeight-2*mHeartPaintWidth)/4;
		mOriginalYPositon = new float[mHeartBeatWidth];//�������� Y����
		mDefaultYPostion = new float[mHeartBeatWidth];
		Arrays.fill(mOriginalYPositon, 0);
		Arrays.fill(mDefaultYPostion, -1);
		// ���ڶ�λ�ܿ��ȵ�1/4
		mPeriodFraction = (float) (Math.PI * 2 / mHeartBeatWidth * 3);
		for (int i =  mHeartBeatWidth/3*2; i < mHeartBeatWidth; i++) {
			mOriginalYPositon[i] = (float) (AmplitudeA * Math.sin(mPeriodFraction * i) + OFFSET_Y);
		}
		
		x = w / 2;
		y = h / 2;
		mRadius = w / 2 - mHeartPaintWidth / 2; //��Ϊ�ƶ���Paint�Ŀ��ȣ���˼���뾶��Ҫ��ȥ���
		mRectf = new RectF(x - mRadius, y - mRadius, x + mRadius, y + mRadius);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.setDrawFilter(mDrawFilter);//��canvas�Ͽ����
		//����drawArcĬ�ϴ�x�Ὺʼ���������Ҫ��������ת���߻��ƽǶ���ת��2�ַ���
		//int level = canvas.save();
		//canvas.rotate(-90, x, y);// ��ת��ʱ��һ��Ҫָ������
		for (int i = 0; i < 360; i += 3) {
			canvas.drawArc(mRectf, i, 1, false, mRingPaint);
		}
		if (mAnimAngle != -1) {// ��������˶���
			for (int i = -90; i < mAnimAngle-90; i += 3) {
				canvas.drawArc(mRectf, i, 1, false, mRingAnimPaint);
			}
		}
		//canvas.restoreToCount(level);
		if(StartHeartBeatAnmiFlag){
			//����������
			int interval = mHeartBeatWidth - mOffset;
			for(int i=mOffset,j=mHeartPaintWidth+20;i<mHeartBeatWidth;i++,j++){
				canvas.drawPoint(j, mTotalHeight/2-mOriginalYPositon[i], mHeartBeatPaint);
			}
			for(int i=0,j=interval+mHeartPaintWidth+20;i<mOffset;i++,j++){
				canvas.drawPoint(j, mTotalHeight/2-mOriginalYPositon[i], mHeartBeatPaint);
			}
			canvas.drawCircle(mHeartBeatWidth+20+mHeartPaintWidth, mTotalHeight/2-mOriginalYPositon[mOffset], 10, mHeartBeatPaint);
		}
		if(StartFirstFrameFlag){
			for(int i=0,j=mHeartPaintWidth+20;i<mHeartBeatWidth;i++,j++){
				if(mDefaultYPostion[i]==-1)
					continue;
				else
					canvas.drawPoint(j, mTotalHeight/2-mDefaultYPostion[i], mHeartBeatPaint);
			}
		}
	}


	


	
	/*---------------------------------����-----------------------------------------*/
	private volatile boolean StartHeartBeatAnmiFlag = false;
	
	private volatile boolean StartFirstFrameFlag = false;
	
	private int mFirstFrameOffset = 0;
	
	private volatile boolean StopHeartBeatAnmiFlag = false;
	
	/**
	 * ������������
	 * */
	private void startHeartBeatAnmi(){
		StartFirstFrameFlag = true;
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				while (mFirstFrameOffset > 0) {
					System.arraycopy(mOriginalYPositon,0,mDefaultYPostion, mFirstFrameOffset,mOriginalYPositon.length - mFirstFrameOffset );
					mFirstFrameOffset--;
					try {
						Thread.sleep(5);
					} catch (InterruptedException e) {
						;
					}
					postInvalidate();
				}
				StartFirstFrameFlag = false;
				StartHeartBeatAnmiFlag = true;
				startSecondFrameAnmi();
			}
		}).start();;
	}
	/**
	 * ѭ������ͼ
	 * */
	private void startSecondFrameAnmi(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				 while (!StopHeartBeatAnmiFlag) {  
	                	mOffset += mOffsetSpeed;
	            		if(mOffset>=mHeartBeatWidth)
	            			mOffset = 0;
	                    try {  
	                        Thread.sleep(80);  
	                    } catch (InterruptedException e) {  
	                    }  
	                    postInvalidate();  
	                }
			}
		}).start();
	}
	
	/**
	 * ����Բ������
	 * */
	private void startRingAnim() {
		mAnimAngle = 0;
		new Thread(new Runnable() {

			@Override
			public void run() {
				while (mAnimAngle < 360) {
					mAnimAngle++;
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					postInvalidate();
				}
				mAnimAngle = -1;// ֹͣ����
				stopAnim();
			}
		}).start();
	}
	
	//public void startAnim

	public void stopAnim(){
		StopHeartBeatAnmiFlag = true;
		StartHeartBeatAnmiFlag = false;
		
	}
	
	public void startAnim(){
		startRingAnim();
		startHeartBeatAnmi();
	}
	
	/*---------------------------------����  end------------------------------------*/
	
	/*---------------------------------���캯��-----------------------------------*/
	public RingView(Context context) {
		super(context);
		mContext = context;
		init();
	}

	public RingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init();
	}

	public RingView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		mContext = context;
		init();
	}

}