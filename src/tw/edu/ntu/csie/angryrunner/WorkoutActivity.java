package tw.edu.ntu.csie.angryrunner;

import java.util.ArrayList;
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
	GmapHandler gMapH;
	GpsHandler gpsH;
	SharedPreferences settingpref;
	StatusHandler statusHandler;
	
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
        
        gMapH = new GmapHandler(pageViews.get(1), this, vpWorkout);
        gpsH = new GpsHandler(this);
        
        statusHandler = new StatusHandler(WorkoutActivity.this);
        
    }

	private void initMode(View v) {
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
				if(statusHandler.isStateBeforeStart()){
					statusHandler.start();
					btStart.setText("Pause");
					btWorkout.setEnabled(false);
				}else if(statusHandler.isStateWorking()){
					statusHandler.pause();
					btStart.setText("Resume");
				}else if(statusHandler.isStatePause()){
					statusHandler.resume();
					btStart.setText("Pause");
				}
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
				/*
				Random rand = new Random();
				statCalorie.setNumber(Integer.toString(rand.nextInt(1000)));
				statDuration.setNumber(Integer.toString(rand.nextInt(60)));
				progressBar.setProgress(rand.nextFloat()*100.0f);
				*/
				if(!statusHandler.isStateBeforeStart()){
					statusHandler.stop();
					btStart.setText("Start");
					zeroStatus();
					btWorkout.setEnabled(true);
				}
				// show result
			}
		});
        btMap.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View arg0) {
				vpWorkout.setCurrentItem(1);
			}
        });
	}
	
	private void zeroStatus(){
		statDuration.setNumber("0:00:00");
		statCalorie.setNumber("0");
		statDistance.setNumber("0.00");
	}

	private void initStatus(View v) {
		statDuration = (StatusItemLayout) v.findViewById(R.id.statDuration);
        statCalorie = (StatusItemLayout) v.findViewById(R.id.statCalorie);
        statDistance = (StatusItemLayout) v.findViewById(R.id.statDistance);
		statDuration.setType("Duration");
		statDuration.setUnit("h:mm:ss");
		statCalorie.setType("Calories");
		statCalorie.setUnit("kcal");
		statDistance.setType("Distance");
		statDistance.setUnit(getUnit());
		zeroStatus();
	}
	
	private String getUnit(){
		String nowUnit = settingpref.getString("Unit", "Kilometer");
		if(nowUnit.equals("Kilometer"))
			return "Km";
		else
			return "Mile";
	}
    
	@Override
	protected void onResume() {
		super.onResume();
		tvMode.setText(settingpref.getString("Mode", "walking"));
		statDistance.setUnit(getUnit());
	}
	
    @Override
    public void onDestroy() {
    	speedChart.cleanUp();
    	super.onDestroy();
    }
    
    void updateDurationDisplay(long duration){	// seconds
    	final long seconds = duration % 60;
    	final long minutes = (duration / 60) % 60;
    	final long hours = (duration / 60) / 60;
    	WorkoutActivity.this.runOnUiThread(new Runnable(){
			@Override
			public void run() {
		    	statDuration.setNumber(String.format("%d:%02d:%02d", hours, minutes, seconds));
			}
    	});
    }
    
    void updateSpeedDisplay(double speed){
    	speedChart.setCurrentValue(speed);
    }
    
    void updateDistanceDisplay(final double distance){
    	WorkoutActivity.this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if(settingpref.getString("Unit", "Kilometer").equals("Kilometer")){
		    		statDistance.setNumber(String.format("%.2f", distance));
		    	}else{
		    		// TODO
		    	}
			}
		});
    }

    void gps2gmap(GeoPoint newgp){
		statusHandler.addPosition(newgp);
		gMapH.updatePosition(statusHandler.getPositions());
	}
	
	GeoPoint getLastPosition(){
		return statusHandler.getLastPosition();
	}
    
	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
}
