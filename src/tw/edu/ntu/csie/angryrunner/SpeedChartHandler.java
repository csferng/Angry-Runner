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
	    dialData.add("expected", 0);	// index 1
	    dialRenderer = new DialRenderer();
	    dialRenderer.setMargins(new int[] {0, 0, 0, 0});
	    dialRenderer.addSeriesRenderer(new SimpleSeriesRenderer());
	    dialRenderer.addSeriesRenderer(new SimpleSeriesRenderer());
	    dialRenderer.getSeriesRendererAt(0).setColor(Color.rgb(224, 224, 0));
	    dialRenderer.getSeriesRendererAt(1).setColor(Color.rgb(192, 128, 0));
	    dialRenderer.setLegendTextSize(15);
	    dialRenderer.setLabelsTextSize(10);
	    dialRenderer.setLabelsColor(Color.WHITE);
	    dialRenderer.setShowLabels(true);
	    dialRenderer.setShowLegend(false);
	    dialRenderer.setZoomEnabled(false);
	    dialRenderer.setPanEnabled(false);
	    dialRenderer.setChartTitle(activity.getString(R.string.DISPLAY_SPEED)+" (m/s)");
	    dialRenderer.setChartTitleTextSize(20);
	    dialRenderer.setVisualTypes(new DialRenderer.Type[] {Type.ARROW, Type.ARROW});
	    dialRenderer.setMinValue(0);
	    dialRenderer.setMaxValue(10);
	    dialRenderer.setInScroll(true);		// prevent shrinking when sliding
	    dialView = ChartFactory.getDialChartView(activity, dialData, dialRenderer);
	    dialView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
	    frame.addView(dialView);
	    animateThread = new AnimateThread(dialRenderer.getMaxValue());
	    animateThread.start();
	}

    public GraphicalView getDialView() {
		return dialView;
	}

	public void setCurrentValue(double value) {
		if(Double.isNaN(value)) return;
    	animateThread.setGoal(adjustValue(value));
    }
    
    public void setExpectedValue(double exp) {
    	adjustExpectedValue(exp);
    	invalidate();
    }
    
    public void setMaxValue(double max) {
    	dialRenderer.setMaxValue(max);
    	adjustExpectedValue(dialData.getValue(1));
    	animateThread.setMaxValue(max);
    	invalidate();
    }
    
    private void adjustExpectedValue(double exp) {
    	dialData.set(1, "expected", adjustValue(exp));
    }

	private double adjustValue(double val) {
		double max = dialRenderer.getMaxValue();
    	if(val > max) val = max * 1.05;
    	if(val < 0) val = -max * 0.05;
		return val;
	}

	private void invalidate() {
		if(activity == null) return;
		dialView.postInvalidate();
	}
	
	public void cleanUp() {
		animateThread.finish();
		boolean retry = true;
		while(retry) {
			try {
				animateThread.join();
				retry = false;
			} catch (InterruptedException e) {
				// simply try again until the thread finish
			}
		}
		activity = null;
	}
	
	private class AnimateThread extends Thread {
		private static final int ANIMATION_PERIOD = 50;
		private static final int MILLISECONDS_PER_ROUND = 5000;

		private double goal;
		private double step;
		private boolean end = false;
		
		public AnimateThread(double maxval) {
			super();
			step = maxval / MILLISECONDS_PER_ROUND * ANIMATION_PERIOD;
		}
		public void setGoal(double goal) {
			this.goal = goal;
		}
		public void setMaxValue(double maxval) {
			this.step = maxval / MILLISECONDS_PER_ROUND * ANIMATION_PERIOD;
		}
		public void finish() {
			end = true;
		}
		@Override
		public void run() {
			while(!end) {
				try {
					Thread.sleep(ANIMATION_PERIOD);
				} catch (InterruptedException e) {
				}
				if(dialData.getValue(0) != goal) {
					double d = goal - dialData.getValue(0);
					if(Math.abs(d) > step)
						d = step * Math.signum(d);
					dialData.set(0, "current", dialData.getValue(0)+d);
					invalidate();
				}
			}
		}
	}
}
