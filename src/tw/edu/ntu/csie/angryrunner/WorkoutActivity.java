package tw.edu.ntu.csie.angryrunner;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
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
	SQLiteDatabase historydb;
	StatusHandler statusHandler;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_view);
        
        settingpref = getSharedPreferences("PREF_ANGRYRUNNER_SETTING", MODE_PRIVATE);
        historydb = (new HistoryDatabaseHandler(WorkoutActivity.this)).getWritableDatabase();
        
        LayoutInflater infla = getLayoutInflater();
        pageViews = new ArrayList<View>();
        pageViews.add(infla.inflate(R.layout.workout, null));
        pageViews.add(infla.inflate(R.layout.map, null));
        
        vpWorkout = (ViewPager) findViewById(R.id.vp1);
        vpAdapter = new WorkoutPagerAdapter(pageViews);
        vpWorkout.setAdapter(vpAdapter);
        
        speedChart = new SpeedChartHandler(this, (ViewGroup) pageViews.get(0).findViewById(R.id.frDialChart));
        speedChart.setExpectedValue(Double.parseDouble(settingpref.getString("SpeedGoal", "0.0")));
        progressBar = (ProgressBarView) pageViews.get(0).findViewById(R.id.progressBar);
        initStatus(pageViews.get(0));
        initButtons(pageViews.get(0));
        initMode(pageViews.get(0));
        
        gMapH = new GmapHandler(pageViews.get(1), this, vpWorkout);
        gpsH = new GpsHandler(this);
        
        statusHandler = new StatusHandler(WorkoutActivity.this, settingpref);
        
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	if(requestCode == 0 && resultCode == RESULT_OK){
    		speedChart.setExpectedValue(Double.parseDouble(settingpref.getString("SpeedGoal", "0.0")));
    		initStatus(pageViews.get(0));
    	}else if(requestCode == 1 && resultCode == RESULT_OK){
    		String date = DateFormat.getDateInstance(DateFormat.MEDIUM).format(new Date());

    		ContentValues record = new ContentValues();
    		record.put("mode", data.getExtras().getString("Mode"));
    		record.put("date", date);
    		record.put("distance", data.getExtras().getString("Distance"));
    		record.put("duration", data.getExtras().getString("Duration"));
    		record.put("speed", data.getExtras().getString("Speed"));
    		historydb.insert("ARhistory", null, record);
    	}
    }
    
    @Override
    public void onBackPressed() {
    	if(statusHandler.isStateBeforeStart()) super.onBackPressed();
    	else {
    		AlertDialog.Builder builder = new AlertDialog.Builder(this);
    		builder.setMessage("Current session would lost. Are you sure you want to exit?")
    			.setCancelable(false)
    			.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						WorkoutActivity.this.finish();
					}
				})
				.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
    		AlertDialog alert = builder.create();
    		alert.show();
    	}
    }

	private void initMode(View v) {
		tvMode = (TextView) v.findViewById(R.id.tvMode);
		tvMode.setText(settingpref.getString("Mode", "Walking"));
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
					btWorkout.setEnabled(false);
					int countdown = Integer.parseInt(settingpref.getString("CountdownValue", "0"));
					setCountdown(countdown);
					if(countdown > 0){						
						btStart.setClickable(false);
						ARTimer timer = new ARTimer(countdown);
						timer.start();
					}
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
				startActivityForResult(it, 0);
			}
		});
        btStop.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!statusHandler.isStateBeforeStart()){
					Bundle bun = statusHandler.stop();
					bun.putString("Mode", settingpref.getString("Mode", "Walking"));
					btStart.setText("Start");
					zeroStatus();
					btWorkout.setEnabled(true);
					
					Intent it = new Intent();
					it.setClass(WorkoutActivity.this, ResultActivity.class);
					it.putExtras(bun);
					startActivityForResult(it, 1);
				}
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
		progressBar.setProgress(0);
	}

	private void initStatus(View v) {
		StatusItemLayout statMajor = (StatusItemLayout) v.findViewById(R.id.statMajor);
		StatusItemLayout statMinor = (StatusItemLayout) v.findViewById(R.id.statMinor);
		if(settingpref.getString("TimeGoal", "0").equals("0")) {
			statDistance = statMajor;
			statDuration = statMinor;
		} else {
			statDuration = statMajor;
			statDistance = statMinor;
		}
		statDuration.setType("Duration");
		statDuration.setUnit("h:mm:ss");
		statDistance.setType("Distance");
		statDistance.setUnit(getUnit());
		statCalorie = (StatusItemLayout) v.findViewById(R.id.statCalorie);
		statCalorie.setType("Calories");
		statCalorie.setUnit("kcal");
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
		String mode = settingpref.getString("Mode", "Walking");
		tvMode.setText(mode);
		statDistance.setUnit(getUnit());
		speedChart.setMaxValue(MathUtil.getMaxSpeedForMode(mode));
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
    	updateProgressDisplay(01, duration);
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
    	updateProgressDisplay(distance, -1);
    }
    
    void updateProgressDisplay(double distance, double duration) {
    	float prog = 0.0f;
    	if(settingpref.getString("TimeGoal", "0").equals("0") && distance > 0) {
    		float goal = Float.parseFloat(settingpref.getString("DistanceGoal", "0"));
    		if(goal > 0) prog = (float )distance / goal; 
    	} else if(duration > 0) {
    		float goal = Float.parseFloat(settingpref.getString("TimeGoal", "0"));
    		if(goal > 0) prog = (float) duration / goal;
    	}
    	if(prog > 1.0f) prog = 1.0f;
    	progressBar.setProgress(prog*100.0f);
    }
    
    void updateCaloriesDisplay(double calorie) {
    	final String s = String.format("%.0f", calorie);
    	WorkoutActivity.this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				statCalorie.setNumber(s);
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
	
	void setCountdown(int nowcd){
		if(nowcd > 0){
			btStart.setText(nowcd+"");
		}else{
			statusHandler.start();
			btStart.setText("Pause");
			btStart.setClickable(true);
		}
	}
	
	class ARTimer extends Timer{
		int countdown;
		
		public ARTimer(int cd) {
			countdown = cd;
		}

		private TimerTask newTimerTask() {
			return new TimerTask(){
				@Override
				public void run() {
					--countdown;
					WorkoutActivity.this.runOnUiThread(new Runnable(){
						@Override
						public void run() {
							setCountdown(countdown);
						}
					});
					if(countdown > 0){
						ARTimer.this.schedule(newTimerTask(), 1000);
					}
				}
			};
		}
		
		void start(){		
			this.schedule(newTimerTask(), 1000);
		}
	}
}
