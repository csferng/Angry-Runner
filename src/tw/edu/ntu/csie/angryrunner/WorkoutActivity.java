package tw.edu.ntu.csie.angryrunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;

public class WorkoutActivity extends MapActivity {
	private Button btStart, btStop, btWorkout, btMap;
	private TextView tvMode;
	private SpeedChartHandler speedChart;
	private ProgressBarView progressBar;
	private StatusItemLayout statDuration, statCalorie, statDistance;
	ArrayList<View> pageViews;
	ViewPager vpWorkout;
	WorkoutPagerAdapter vpAdapter;
	List<GeoPoint> positions;
	GmapHandler gMapH;
	GpsHandler gpsH;
	SharedPreferences settingpref;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_view);
        
        settingpref = getSharedPreferences("PREF_ANGRYRUNNER_SETTING", MODE_PRIVATE);
        
        LayoutInflater infla = getLayoutInflater();
        pageViews = new ArrayList<View>();
        pageViews.add(infla.inflate(R.layout.workout, null));
        pageViews.add(infla.inflate(R.layout.map, null));
        
        vpWorkout = (ViewPager) findViewById(R.id.vp1);
        vpAdapter = new WorkoutPagerAdapter(pageViews);
        vpWorkout.setAdapter(vpAdapter);
        
        speedChart = new SpeedChartHandler(this, (ViewGroup) pageViews.get(0).findViewById(R.id.frDialChart));
        progressBar = (ProgressBarView) pageViews.get(0).findViewById(R.id.progressBar);
        initStatus(pageViews.get(0));
        initButtons(pageViews.get(0));
        initMode(pageViews.get(0));
        
        positions = new ArrayList<GeoPoint>();
        gMapH = new GmapHandler(pageViews.get(1), this, vpWorkout);
        gpsH = new GpsHandler(this);
    }

	private void initMode(View v) {
		// TODO Auto-generated method stub
		tvMode = (TextView) v.findViewById(R.id.tvMode);
		tvMode.setText(settingpref.getString("Mode", "walking"));
	}

	private void initButtons(View v) {
		btStart = (Button) v.findViewById(R.id.btStart);
        btStop = (Button) v.findViewById(R.id.btStop);
        btWorkout = (Button) v.findViewById(R.id.btWorkout);
        btMap = (Button) v.findViewById(R.id.btMap);
        
        btStart.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				speedChart.setCurrentValue((new Random()).nextDouble()*10);
			}
		});
        btWorkout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent it = new Intent();
				it.setClass(WorkoutActivity.this, WorkoutSettingActivity.class);
				startActivity(it);
			}
		});
        btStop.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Random rand = new Random();
				statCalorie.setNumber(Integer.toString(rand.nextInt(1000)));
				statDuration.setNumber(Integer.toString(rand.nextInt(60)));
				progressBar.setProgress(rand.nextFloat()*100.0f);
			}
		});
        btMap.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View arg0) {
				vpWorkout.setCurrentItem(1);
			}
        });
	}

	private void initStatus(View v) {
		statDuration = (StatusItemLayout) v.findViewById(R.id.statDuration);
        statCalorie = (StatusItemLayout) v.findViewById(R.id.statCalorie);
        statDistance = (StatusItemLayout) v.findViewById(R.id.statDistance);
		statDuration.setType("Duration");
		statDuration.setUnit("sec");
		statCalorie.setType("Calories");
		statCalorie.setUnit("kcal");
		statDistance.setType("Distance");
		statDistance.setUnit("km");
		statDistance.setNumber("0.00");
	}
    
	@Override
	protected void onResume() {
		super.onResume();
		tvMode.setText(settingpref.getString("Mode", "walking"));
	}
	
    @Override
    public void onDestroy() {
    	speedChart.cleanUp();
    	super.onDestroy();
    }

    void gps2gmap(GeoPoint newgp){
		positions.add(newgp);
		gMapH.updatePosition(positions);
	}
	
	GeoPoint getLastPosition(){
		if(positions.size() > 0)
			return positions.get(positions.size()-1);
		
		return null;
	}
    
	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
}
