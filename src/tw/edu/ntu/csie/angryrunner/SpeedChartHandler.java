package tw.edu.ntu.csie.angryrunner;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DialRenderer;
import org.achartengine.renderer.DialRenderer.Type;
import org.achartengine.renderer.SimpleSeriesRenderer;

import android.app.Activity;
import android.graphics.Color;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

public class SpeedChartHandler {
	Activity activity;
	private CategorySeries dialData;
	private GraphicalView dialView;
	private DialRenderer dialRenderer;
	private AnimateThread animateThread;
	
	public SpeedChartHandler(Activity activity, ViewGroup frame) {
		this.activity = activity;
		dialData = new CategorySeries("Speed");
	    dialData.add("current", 0);		// index 0
	    dialData.add("expected", 6);	// index 1
	    dialRenderer = new DialRenderer();
	    dialRenderer.setMargins(new int[] {0, 0, 0, 0});
	    dialRenderer.addSeriesRenderer(new SimpleSeriesRenderer());
	    dialRenderer.addSeriesRenderer(new SimpleSeriesRenderer());
	    dialRenderer.getSeriesRendererAt(0).setColor(Color.rgb(224, 224, 0));
	    dialRenderer.getSeriesRendererAt(1).setColor(Color.rgb(192, 128, 0));
	    dialRenderer.setChartTitleTextSize(20);
	    dialRenderer.setLegendTextSize(15);
	    dialRenderer.setLabelsTextSize(10);
	    dialRenderer.setLabelsColor(Color.WHITE);
	    dialRenderer.setShowLabels(true);
	    dialRenderer.setShowLegend(false);
	    dialRenderer.setZoomEnabled(false);
	    dialRenderer.setPanEnabled(false);
	    dialRenderer.setVisualTypes(new DialRenderer.Type[] {Type.ARROW, Type.ARROW});
	    dialRenderer.setMinValue(0);
	    dialRenderer.setMaxValue(10);
	    dialView = ChartFactory.getDialChartView(activity, dialData, dialRenderer);
	    dialView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
	    frame.addView(dialView);
	    animateThread = new AnimateThread();
	    animateThread.start();
	}

    public void setCurrentValue(double value) {
    	animateThread.setGoal(value);
    }
    
    public void setExpectedValue(double value) {
    	dialData.set(1, "expected", value);
    	invalidate();
    }
    
    public void setMaxValue(double value) {
    	dialRenderer.setMaxValue(value);
    	invalidate();
    }

	private void invalidate() {
		if(activity == null) return;
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				dialView.invalidate();
			}
		});
	}
	
	public void cleanUp() {
		animateThread.finish();
		activity = null;
	}
	
	private class AnimateThread extends Thread {
		private static final int ANIMATION_STEP = 50;

		private double goal;
		private boolean end = false;
		public void setGoal(double goal) {
			this.goal = goal;
		}
		public void finish() {
			end = true;
		}
		@Override
		public void run() {
			while(!end) {
				try {
					Thread.sleep(ANIMATION_STEP);
				} catch (InterruptedException e) {
				}
				if(dialData.getValue(0) != goal) {
					double d = goal - dialData.getValue(0);
					if(Math.abs(d) > dialRenderer.getMaxValue()/20.0)
						d = dialRenderer.getMaxValue()/20.0 * d / Math.abs(d);
					dialData.set(0, "current", dialData.getValue(0)+d);
					invalidate();
				}
			}
		}
	}
}
