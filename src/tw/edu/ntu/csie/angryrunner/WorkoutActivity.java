package tw.edu.ntu.csie.angryrunner;

import java.util.Random;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DialRenderer;
import org.achartengine.renderer.DialRenderer.Type;
import org.achartengine.renderer.SimpleSeriesRenderer;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;

public class WorkoutActivity extends Activity {
	private Button btStart, btStop, btWorkout;
	
	private CategorySeries dialData;

	private GraphicalView dialView;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.workout);
        btStart = (Button) findViewById(R.id.btStart);
        btStop = (Button) findViewById(R.id.btStop);
        btWorkout = (Button) findViewById(R.id.btWorkout);
        
        initDialChart((ViewGroup) findViewById(R.id.frDialChart));
        btStart.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				updateCurrentValue((new Random()).nextDouble()*10);
			}
		});
    }
    
    private void updateCurrentValue(double value) {
    	dialData.set(0, "current", value);
    	dialView.invalidate();
    }

	private void initDialChart(ViewGroup frame) {
		dialData = new CategorySeries("Speed");
	    dialData.add("current", 0);
	    dialData.add("expected", 6);
	    DialRenderer dialRenderer = new DialRenderer();
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
	    dialView = ChartFactory.getDialChartView(this, dialData, dialRenderer);
	    dialView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
	    frame.addView(dialView);
	}
}
