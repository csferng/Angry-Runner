package tw.edu.ntu.csie.angryrunner;

import java.util.Random;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class WorkoutActivity extends Activity {
	private Button btStart, btStop, btWorkout;
	private DialChartHandler dialChart;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.workout);
        btStart = (Button) findViewById(R.id.btStart);
        btStop = (Button) findViewById(R.id.btStop);
        btWorkout = (Button) findViewById(R.id.btWorkout);
        
        dialChart = new DialChartHandler(this, (ViewGroup) findViewById(R.id.frDialChart));
        btStart.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialChart.setCurrentValue((new Random()).nextDouble()*10);
			}
		});
    }
    
    @Override
    public void onDestroy() {
    	dialChart.cleanUp();
    }
}
