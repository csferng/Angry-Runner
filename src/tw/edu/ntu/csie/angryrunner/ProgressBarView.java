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

	public ProgressBarView(Context context, AttributeSet attrs) {
		super(context, attrs);
		progress = 0;
	}
	
	public void setProgress(float progress) {
		this.progress = progress;
		this.invalidate();
	}

	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int width = this.getWidth();
		int height = this.getHeight();
		float x = (width*progress) / 100.0f;
		String sProgress = String.format("%.0f%%", Math.floor(progress));
		
		Paint paint = new Paint();
		Rect rect = new Rect();
		paint.getTextBounds("100%", 0, "100%".length(), rect);
		float tw = Math.abs(rect.right-rect.left);
		float th = Math.abs(rect.bottom-rect.top);
		float scale = (width/2f) / tw;
		float h1 = th * scale;
		float h2 = (h1*2+height-2) / 3f;
		float pts[] = new float[]{x, h2, x+(h2-h1), h2, x+(h2-h1), h2, x, h1};

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
