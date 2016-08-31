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
	
	private Bitmap heart; //记录用户触碰点的位图

	@Override
	public Engine onCreateEngine() //实现WallpaperService必须实现的抽象方法
	{
		heart = BitmapFactory.decodeResource(getResources(), R.drawable.heart);//加载心形图片
		return new MyEngine(); //返回自定义的Engine
	}
	
	class MyEngine extends Engine
	{
		private boolean mVisible; //记录登录界面是否可见
		//记录当前用户动作事件的发生位置
		private float mTouchX = -1;
		private float mTouchY = -1;
		private int count = 1; //记录当前需要绘制的矩形的数量
		//记录绘制第一个矩形所需坐标变换的X、Y坐标的偏移
		private int originX = 50;
		private int originY = 50;
		
		private Paint mPaint = new Paint(); //定义画笔
		Handler mHandler = new Handler(); //定义一个Handler
		
		//定义一个周期性执行的任务
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
			//初始化画笔
			mPaint.setARGB(76, 0, 0, 255);
			mPaint.setAntiAlias(true);
			mPaint.setStyle(Paint.Style.FILL);
			
			setTouchEventsEnabled(true); //设置处理触摸事件
		}
		
		@Override
		public void onDestroy() 
		{
			super.onDestroy();
			mHandler.removeCallbacks(drawTarget); //删除回调
		}
		
		@Override
		public void onVisibilityChanged(boolean visible) 
		{
			mVisible = visible;
			
			if (visible) //当界面可见的时候，执行drawFrame()方法
			{
				 drawFrame(); //动态的绘制图形
			}
			else {
				mHandler.removeCallbacks(drawTarget); //当界面不可见时，删除回调
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
			if (event.getAction() == MotionEvent.ACTION_MOVE) //如果检测到滑动操作
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
		
		//定义绘制图形的工具方法
		private void drawFrame() 
		{
			final SurfaceHolder holder = getSurfaceHolder(); //获取该壁纸的SurfaceHolder
			Canvas canvas = null;
			
			try 
			{
				canvas = holder.lockCanvas(); //对画布加锁
				if (canvas != null)
				{
					canvas.drawColor(0xffffffff); //绘制背景色
					drawTouchPoint(canvas); //在触碰点绘制心形
					mPaint.setAlpha(76);  //设置画笔的透明度
					canvas.translate(originX, originY);
					
					//采用循环绘制count个矩形
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
			
			//调度下一次重绘
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
				
				mHandler.postDelayed(drawTarget, 100); //指定0.1秒后重新执行mDrawCube一次
			}
		}
		
		//在屏幕触碰点绘制圆圈
		private void drawTouchPoint(Canvas canvas)
		{
			if (mTouchX >= 0 && mTouchY >= 0)
			{
				mPaint.setAlpha(255); //设置画笔的透明度
				canvas.drawBitmap(heart, mTouchY, mTouchY, mPaint);
			}
		}
	}
}
