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
		float x = ((width-1)*progress) / 100.0f;
		String sProgress = String.format("%.0f%%", Math.floor(progress));
		
		Paint paint = new Paint();
		paint.getTextBounds("100%", 0, "100%".length(), rect);
		float tw = Math.abs(rect.right-rect.left);
		float th = Math.abs(rect.bottom-rect.top);
		float scale = (width/2f) / tw;
		float h1 = th*scale + 1;
		float h2 = (h1*2+height-2)/3f;
		float d = (progress>=50) ? (h1-h2) : (h2-h1);
		float pts[] = new float[]{x, h2, x+d, h2, x+d, h2, x, h1};
		paint.getTextBounds(sProgress, 0, sProgress.length(), rect);
		float halfw = Math.abs(rect.right-rect.left)*scale / 2.0f;
		float x2 = Math.min(Math.max(x, halfw), width-halfw);

		paint.setTextSize(paint.getTextSize()*scale);
		//paint.setTextAlign(Paint.Align.RIGHT);
		paint.setTextAlign(Paint.Align.CENTER);
		paint.setColor(Color.WHITE);
		//canvas.drawText(sProgress, width-1, th*scale, paint);
		canvas.drawText(sProgress, x2, th*scale, paint);
		paint.setColor(Color.GREEN);
		canvas.drawLine(0, height-1, width-1, height-1, paint);
		paint.setColor(Color.RED);
		canvas.drawLine(x, h1, x, height-2, paint);
		canvas.drawLines(pts, paint);
	}
}
