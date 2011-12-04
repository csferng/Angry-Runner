package tw.edu.ntu.csie.angryrunner;

import java.util.Random;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class WorkoutActivity extends Activity {
	private Button btStart, btStop, btWorkout;
	private SpeedChartHandler speedChart;
	private StatusItemLayout statDuration, statCalorie, statDistance;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.workout);
        btStart = (Button) findViewById(R.id.btStart);
        btStop = (Button) findViewById(R.id.btStop);
        btWorkout = (Button) findViewById(R.id.btWorkout);
        
        speedChart = new SpeedChartHandler(this, (ViewGroup) findViewById(R.id.frDialChart));
        btStart.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				speedChart.setCurrentValue((new Random()).nextDouble()*10);
			}
		});
        statDuration = (StatusItemLayout) findViewById(R.id.statDuration);
        statCalorie = (StatusItemLayout) findViewById(R.id.statCalorie);
        statDistance = (StatusItemLayout) findViewById(R.id.statDistance);
		statDuration.setType("Duration");
		statDuration.setUnit("sec");
		statCalorie.setType("Calories");
		statCalorie.setUnit("kcal");
		statDistance.setType("Distance");
		statDistance.setUnit("km");
		statDistance.setNumber("0.00");
        btWorkout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				statDuration.setNumber(Integer.toString((new Random()).nextInt(60)));
			}
		});
        btStop.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				statCalorie.setNumber(Integer.toString((new Random()).nextInt(1000)));
			}
		});
    }
    
    @Override
    public void onDestroy() {
    	speedChart.cleanUp();
    	super.onDestroy();
    }
}
