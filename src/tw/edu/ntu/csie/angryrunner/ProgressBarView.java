package tw.edu.ntu.csie.angryrunner;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

public class ProgressBarView extends View {

	private float progress;
	private int width;
	private int height;
	private String remain;

	public ProgressBarView(Context context, AttributeSet attrs) {
		super(context, attrs);
		progress = 0;
		remain = "";
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		width = w;
		height = h;
	}
	
	public void setProgress(float progress, String remain) {
		this.progress = progress;
		this.remain = remain;
		// must set area to avoid clearing other views
		this.postInvalidate(0, 0, width, height);
	}

	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		Rect rect = new Rect();
		float x = ((width-1)*progress) / 100.0f;
		Paint paint = new Paint();
		paint.getTextBounds("M", 0, "M".length(), rect);
		float tw = Math.abs(rect.right-rect.left) * 12;
		float th = Math.abs(rect.bottom-rect.top);
		float scale = Math.min(((height-1)/2f)/th, (width-2)/tw);
		float h1 = th*scale + 1;
		float h2 = (h1*2+height-2)/3f;
		float d = (progress>=50) ? (h1-h2) : (h2-h1);
		float pts[] = new float[]{x, h2, x+d, h2, x+d, h2, x, h1};

		paint.setTextSize(paint.getTextSize()*scale);
		paint.setTextAlign(Paint.Align.LEFT);
		paint.setColor(Color.WHITE);
		canvas.drawText(remain, 0, th*scale, paint);
		paint.setColor(Color.GREEN);
		canvas.drawLine(0, height-1, width-1, height-1, paint);
		paint.setColor(Color.RED);
		canvas.drawLine(x, h1, x, height-2, paint);
		canvas.drawLines(pts, paint);
	}
}
