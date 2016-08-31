package com.example.livewallpaper;

import java.util.Random;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

public class LiveWallpaper extends WallpaperService {
	
	private Bitmap heart; //��¼�û��������λͼ

	@Override
	public Engine onCreateEngine() //ʵ��WallpaperService����ʵ�ֵĳ��󷽷�
	{
		heart = BitmapFactory.decodeResource(getResources(), R.drawable.heart);//��������ͼƬ
		return new MyEngine(); //�����Զ����Engine
	}
	
	class MyEngine extends Engine
	{
		private boolean mVisible; //��¼��¼�����Ƿ�ɼ�
		//��¼��ǰ�û������¼��ķ���λ��
		private float mTouchX = -1;
		private float mTouchY = -1;
		private int count = 1; //��¼��ǰ��Ҫ���Ƶľ��ε�����
		//��¼���Ƶ�һ��������������任��X��Y�����ƫ��
		private int originX = 50;
		private int originY = 50;
		
		private Paint mPaint = new Paint(); //���廭��
		Handler mHandler = new Handler(); //����һ��Handler
		
		//����һ��������ִ�е�����
		private final Runnable drawTarget = new Runnable() 
		{	
			@Override
			public void run() {
				drawFrame();
			}
		};
		
		@Override
		public void onCreate(SurfaceHolder surfaceHolder) 
		{
			super.onCreate(surfaceHolder);
			//��ʼ������
			mPaint.setARGB(76, 0, 0, 255);
			mPaint.setAntiAlias(true);
			mPaint.setStyle(Paint.Style.FILL);
			
			setTouchEventsEnabled(true); //���ô������¼�
		}
		
		@Override
		public void onDestroy() 
		{
			super.onDestroy();
			mHandler.removeCallbacks(drawTarget); //ɾ���ص�
		}
		
		@Override
		public void onVisibilityChanged(boolean visible) 
		{
			mVisible = visible;
			
			if (visible) //������ɼ���ʱ��ִ��drawFrame()����
			{
				 drawFrame(); //��̬�Ļ���ͼ��
			}
			else {
				mHandler.removeCallbacks(drawTarget); //�����治�ɼ�ʱ��ɾ���ص�
			}
		}
		
		@Override
		public void onOffsetsChanged(float xOffset, float yOffset,
				float xOffsetStep, float yOffsetStep, int xPixelOffset, int yPixelOffset) 
		{
			drawFrame();
		}
		
		@Override
		public void onTouchEvent(MotionEvent event) 
		{
			if (event.getAction() == MotionEvent.ACTION_MOVE) //�����⵽��������
			{
				mTouchX = event.getX();
				mTouchY = event.getY();
			}
			else 
			{
				mTouchX = -1;
				mTouchY = -1;
			}
			super.onTouchEvent(event);
		}
		
		//�������ͼ�εĹ��߷���
		private void drawFrame() 
		{
			final SurfaceHolder holder = getSurfaceHolder(); //��ȡ�ñ�ֽ��SurfaceHolder
			Canvas canvas = null;
			
			try 
			{
				canvas = holder.lockCanvas(); //�Ի�������
				if (canvas != null)
				{
					canvas.drawColor(0xffffffff); //���Ʊ���ɫ
					drawTouchPoint(canvas); //�ڴ������������
					mPaint.setAlpha(76);  //���û��ʵ�͸����
					canvas.translate(originX, originY);
					
					//����ѭ������count������
					for (int i = 0; i < count; i++)
					{
						canvas.translate(80, 0);
						canvas.scale(0.95f, 0.95f);
						canvas.rotate(20f);
						canvas.drawRect(0, 0, 150, 75, mPaint);
					}
				}
			} 
			finally 
			{
				if (canvas != null)
				{
					holder.unlockCanvasAndPost(canvas);
				}
			}
			
			mHandler.removeCallbacks(drawTarget);
			
			//������һ���ػ�
			if (mVisible)
			{
				count ++;
				if (count >= 50)
				{
					Random random = new Random();
					count = 1;
					originX += (random.nextInt(60) - 30);
					originY += (random.nextInt(60) - 30);
					
					try 
					{
						Thread.sleep(500);
					} 
					catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
				mHandler.postDelayed(drawTarget, 100); //ָ��0.1�������ִ��mDrawCubeһ��
			}
		}
		
		//����Ļ���������ԲȦ
		private void drawTouchPoint(Canvas canvas)
		{
			if (mTouchX >= 0 && mTouchY >= 0)
			{
				mPaint.setAlpha(255); //���û��ʵ�͸����
				canvas.drawBitmap(heart, mTouchY, mTouchY, mPaint);
			}
		}
	}
}
