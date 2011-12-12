package tw.edu.ntu.csie.angryrunner;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class ProgressBarView extends View {

	private float progress;
	private int width;
	private int height;

	public ProgressBarView(Context context, AttributeSet attrs) {
		super(context, attrs);
		progress = 0;
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		width = w;
		height = h;
	}
	
	public void setProgress(float progress) {
		this.progress = progress;
		// must set area to avoid clearing other views
		this.postInvalidate(0, 0, width, height);
	}

	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		Rect rect = new Rect();
		canvas.getClipBounds(rect);
		int top = rect.top;
		int left = rect.left;
		Log.i("ProgressBar", String.format("top %d left %d wh %dx%d %dx%d",
				top, left, rect.width(), rect.height(), width, height));
		float x = (width*progress) / 100.0f;
		String sProgress = String.format("%.0f%%", Math.floor(progress));
		
		Paint paint = new Paint();
		paint.getTextBounds("100%", 0, "100%".length(), rect);
		float tw = Math.abs(rect.right-rect.left);
		float th = Math.abs(rect.bottom-rect.top);
		float scale = (width/2f) / tw;
		float h1 = top + th*scale;
		float h2 = top + (h1*2+height-2)/3f;
		float d = (progress>=50) ? (h1-h2) : (h2-h1);
		float pts[] = new float[]{x, h2, x+d, h2, x+d, h2, x, h1};

		paint.setTextSize(paint.getTextSize()*scale);
		paint.setTextAlign(Paint.Align.RIGHT);
		paint.setColor(Color.WHITE);
		canvas.drawText(sProgress, width-1, th*scale, paint);
		paint.setColor(Color.GREEN);
		canvas.drawLine(0, height-1, width-1, height-1, paint);
		paint.setColor(Color.RED);
		canvas.drawLine(x, th*scale, x, height-2, paint);
		canvas.drawLines(pts, paint);
	}
}
