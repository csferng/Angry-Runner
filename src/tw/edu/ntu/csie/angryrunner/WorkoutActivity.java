package tw.edu.ntu.csie.angryrunner;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;

public class WorkoutActivity extends MapActivity implements TextToSpeech.OnInitListener{
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
	AudioManager audioManager;
	AudioVariable audioVariable;
	ARTimer timer;
	Mplayer mplayer;
	UnitHandler unitHandler;
	TextToSpeech ttsHandler;
	TtsSpeakProgress ttsSpeakHandler;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_view);

		settingpref = getSharedPreferences(
				getString(R.string.NAME_SHAREDPREFERENCE),
				MODE_PRIVATE);
		historydb = (new HistoryDatabaseHandler(WorkoutActivity.this))
				.getWritableDatabase();
		audioManager = (AudioManager) getApplicationContext().getSystemService(
				AUDIO_SERVICE);
		audioVariable = new AudioVariable();
		timer = new ARTimer();
		mplayer = new Mplayer(this);
		unitHandler = new UnitHandler(this, settingpref);

		LayoutInflater infla = getLayoutInflater();
		pageViews = new ArrayList<View>();
		pageViews.add(infla.inflate(R.layout.workout, null));
		pageViews.add(infla.inflate(R.layout.map, null));

		vpWorkout = (ViewPager) findViewById(R.id.vp1);
		vpAdapter = new WorkoutPagerAdapter(pageViews);
		vpWorkout.setAdapter(vpAdapter);

		progressBar = (ProgressBarView) pageViews.get(0).findViewById(
				R.id.progressBar);

		initSpeedChart(pageViews.get(0));
		initButtons(pageViews.get(0));
		initMode(pageViews.get(0));

		gMapH = new GmapHandler(pageViews.get(1), this, vpWorkout);
		gpsH = new GpsHandler(this, (ImageView) pageViews.get(0).findViewById(R.id.imageView1));

		statusHandler = new StatusHandler(WorkoutActivity.this, settingpref, unitHandler);
		Intent checkit = new Intent();
		checkit.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
		startActivityForResult(checkit, 5);
		ttsSpeakHandler = new TtsSpeakProgress();
		
		resetStatus(pageViews.get(0));

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == 5){
			if(resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS){
				ttsHandler = new TextToSpeech(WorkoutActivity.this.getParent(), WorkoutActivity.this);
			}else{
				Intent installit = new Intent();
				installit.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
				startActivity(installit);
			}
		}else{
			if (resultCode != RESULT_OK)	return;
			Bundle extras = (data == null) ? null : data.getExtras();
			if (requestCode == 1) {
				saveHistory(extras);
			} else if (requestCode == 0) {
			} else if (requestCode == 2) {
				editPreference(getString(R.string.KEY_TIMEGOAL),
						getString(R.string.KEY_TIME), extras);
			} else if (requestCode == 3) {
				editPreference(getString(R.string.KEY_DISTANCEGOAL),
						getString(R.string.KEY_DISTANCE), extras);
			} else if (requestCode == 4) {
				editPreference(getString(R.string.KEY_SPEEDGOAL),
						getString(R.string.KEY_SPEED), extras);
				editPreference(getString(R.string.KEY_PACEGOAL), null, extras);
			}
			resetStatus(pageViews.get(0));
		}
	}

	private void editPreference(String keyGoal, String keyDisplay, Bundle extras) {
		SharedPreferences.Editor edt = settingpref.edit();
		if(keyGoal != null) edt.putString(keyGoal, extras.getString(keyGoal));
		if(keyDisplay != null) edt.putString(keyDisplay, extras.getString("display"));
		edt.commit();
	}

	private void saveHistory(Bundle bundle) {
		String[] dbTableColumn = new String[5];
		dbTableColumn[0] = getString(R.string.KEY_MODE);
		dbTableColumn[1] = getString(R.string.KEY_DATE);
		dbTableColumn[2] = getString(R.string.KEY_DISTANCE);
		dbTableColumn[3] = getString(R.string.KEY_DURATION);
		dbTableColumn[4] = getString(R.string.KEY_SPEED);
		String date = DateFormat.getDateInstance(DateFormat.MEDIUM).format(new Date());

		ContentValues record = new ContentValues();
		record.put(dbTableColumn[0], bundle.getString(dbTableColumn[0]));
		record.put(dbTableColumn[1], date);
		record.put(dbTableColumn[2], bundle.getString(dbTableColumn[2]));
		record.put(dbTableColumn[3], bundle.getString(dbTableColumn[3]));
		record.put(dbTableColumn[4], bundle.getString(dbTableColumn[4]));
		historydb.insert(getString(R.string.NAME_DATABASETABLE), null, record);
	}

	@Override
	public void onBackPressed() {
		if (statusHandler.isStateBeforeStart())
			super.onBackPressed();
		else {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(
					"Current session would lost. Are you sure you want to exit?")
					.setCancelable(false)
					.setPositiveButton("Confirm",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									WorkoutActivity.this.finish();
								}
							})
					.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.cancel();
								}
							});
			AlertDialog alert = builder.create();
			alert.show();
		}
	}

	private void initMode(View v) {
		tvMode = (TextView) v.findViewById(R.id.tvMode);
		tvMode.setText(settingpref.getString(
				getString(R.string.KEY_MODE), 
				getString(R.string.INIT_MODE)));
	}

	private void initButtons(View v) {
		btStart = (Button) v.findViewById(R.id.btStart);
		btStop = (Button) v.findViewById(R.id.btStop);
		btWorkout = (Button) v.findViewById(R.id.btWorkout);
		btMap = (Button) v.findViewById(R.id.btMap);

		btStart.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (statusHandler.isStateBeforeStart()) {
					audioVariable.setInitVolume(audioManager
							.getStreamVolume(AudioManager.STREAM_MUSIC));
					audioVariable.setGoalSpeed(Double.parseDouble(settingpref
							.getString(
									getString(R.string.KEY_SPEEDGOAL),
									getString(R.string.INIT_GOALVALUES))));
					btWorkout.setEnabled(false);
					int countdown = Integer.parseInt(settingpref.getString(
							getString(R.string.KEY_COUNTDOWNVALUE),
							getString(R.string.INIT_COUNTDOWNVALUE)));
					setCountdown(countdown);
					if (countdown > 0) {
						btStart.setClickable(false);
						timer.start(countdown);
					}
				} else if (statusHandler.isStateWorking()) {
					statusHandler.pause();
					mplayer.pause();
					btStart.setText("Resume");
				} else if (statusHandler.isStatePause()) {
					statusHandler.resume();
					mplayer.resume();
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
				if (!statusHandler.isStateBeforeStart()) {
					Bundle bun = statusHandler.stop();
					bun.putString(getString(R.string.KEY_MODE), 
							settingpref.getString(
									getString(R.string.KEY_MODE),
									getString(R.string.INIT_MODE)));
					
					audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 
							audioVariable.getInitVolume(), 
							AudioManager.FLAG_PLAY_SOUND);
					mplayer.stop();
					mplayer.reset();
					
					btStart.setText("Start");
					zeroStatus();
					btWorkout.setEnabled(true);

					Intent it = new Intent();
					it.setClass(WorkoutActivity.this, ResultActivity.class);
					it.putExtras(bun);
					startActivityForResult(it, 1);
				}else{
					if(timer.getCountdown() > 0){
						timer.setStopped(true);
						btStart.setText("Start");
						btStart.setClickable(true);
						btWorkout.setEnabled(true);
					}
				}
			}
		});
		btMap.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				vpWorkout.setCurrentItem(1);
			}
		});
	}
	
	private void callGoalSetting(Class<?> cls, int requestCode, String[] keys) {
		Bundle bun = new Bundle();
		for(String key : keys) { 
			bun.putString(key,  settingpref.getString(key, getString(R.string.INIT_GOALVALUES)));
		}
		Intent it = new Intent();
		it.setClass(this, cls);
		it.putExtras(bun);
		startActivityForResult(it, requestCode);
	}

	private void initSpeedChart(View v) {
		speedChart = new SpeedChartHandler(this, (ViewGroup) v.findViewById(R.id.frDialChart));
		speedChart.getDialView().setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(statusHandler.isStateBeforeStart() == false) return;
				String[] keys = new String[]{getString(R.string.KEY_PACEGOAL), getString(R.string.KEY_SPEEDGOAL)};
				callGoalSetting(PaceActivity.class, 4, keys);
			}
		});
	}

	private void zeroStatus() {
		updateProgressDisplay(0, 0);
		speedChart.setCurrentValue(0.0);
		statDuration.setNumber("0:00:00");
		statCalorie.setNumber("0");
		statDistance.setNumber("0.00");
	}

	private void resetStatus(View v) {
		speedChart.setExpectedValue(Double.parseDouble(settingpref.getString(
				getString(R.string.KEY_SPEEDGOAL), getString(R.string.INIT_GOALVALUES))));

		StatusItemLayout statMajor = (StatusItemLayout) v
				.findViewById(R.id.statMajor);
		StatusItemLayout statMinor = (StatusItemLayout) v
				.findViewById(R.id.statMinor);
		if (settingpref.getString(
				getString(R.string.KEY_TIMEGOAL),
				getString(R.string.INIT_GOALVALUES))
				.equals("0")) {
			statDistance = statMajor;
			statDuration = statMinor;
		} else {
			statDuration = statMajor;
			statDistance = statMinor;
		}
		statDuration.setType(getString(R.string.KEY_DURATION));
		statDuration.setUnit("h:mm:ss");
		statDistance.setType(getString(R.string.KEY_DISTANCE));
		statDistance.setUnit(unitHandler.getDisplayUnit());
		statCalorie = (StatusItemLayout) v.findViewById(R.id.statCalorie);
		statCalorie.setType(getString(R.string.KEY_CALORIE));
		statCalorie.setUnit("kcal");
		zeroStatus();
		statDuration.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(statusHandler.isStateBeforeStart() == false) return;
				String[] keys = new String[]{getString(R.string.KEY_TIMEGOAL)};
				callGoalSetting(TimeActivity.class, 2, keys);
			}
		});
		statDistance.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(statusHandler.isStateBeforeStart() == false) return;
				String[] keys = new String[]{getString(R.string.KEY_DISTANCEGOAL)};
				callGoalSetting(DistanceActivity.class, 3, keys);
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		String mode = settingpref.getString(
				getString(R.string.KEY_MODE), getString(R.string.INIT_MODE));
		tvMode.setText(mode);
		statusHandler.refreshDistanceDisplay();
		statDistance.setUnit(unitHandler.getDisplayUnit());
		speedChart.setMaxValue(MathUtil.getMaxSpeedForMode(mode));
	}

	@Override
	public void onDestroy() {
		statusHandler.cleanUp();
		if(timer.getCountdown() > 0) timer.setStopped(true);
		gpsH.unregister();
		speedChart.cleanUp();
		mplayer.cleanUp();
		if(ttsHandler != null)	ttsHandler.shutdown();
		super.onDestroy();
	}

	void updateDurationDisplay(long duration) { // seconds
		final String str = durationToString(duration);
		this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				statDuration.setNumber(str);
			}
		});
		updateProgressDisplay(-1, duration);
	}

	void updateSpeedDisplay(double speed) {
		speedChart.setCurrentValue(speed);

		int newVolume = audioVariable.newVolume(speed);
		if (newVolume != -1) {
			audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, newVolume,
					AudioManager.FLAG_PLAY_SOUND);
		}
	}

	void updateDistanceDisplay(double distance) {
		final String str = unitHandler.presentDistance(distance);
		WorkoutActivity.this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
					statDistance.setNumber(str);
			}
		});
		updateProgressDisplay(distance, -1);
	}

	void updateProgressDisplay(double distance, long duration) {
		float prog = 0.0f;
		String remain = null;
		String keyTimeGoal = getString(R.string.KEY_TIMEGOAL);
		String keyDistanceGoal = getString(R.string.KEY_DISTANCEGOAL);
		String initGoalValues = getString(R.string.INIT_GOALVALUES);
		if (settingpref.getString(keyTimeGoal, initGoalValues).equals(initGoalValues)) {
			if (distance < 0) return;
			float goal = Float.parseFloat(settingpref.getString(keyDistanceGoal, initGoalValues));
			if (goal > 0) {
				prog = (float) distance / goal;
			}
			remain = unitHandler.presentDistanceWithUnit(Math.max(0, goal-distance));
			
			ttsSpeakHandler.checkSpeakDistanceProgress(distance);
		} else {
			if (duration < 0) return;
			float goal = Float.parseFloat(settingpref.getString(keyTimeGoal, initGoalValues));
			if (goal > 0) {
				prog = (float) duration / goal;
			}
			int dt = Math.max(0, (int)(goal-duration+0.5));
			remain = durationToString(dt);
			
			ttsSpeakHandler.checkSpeakTimeProgress(duration);
		}
		if (prog > 1.0f) prog = 1.0f;
		progressBar.setProgress(prog*100.0f, "Remain: "+remain);
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

	private String durationToString(long duration) {
		long seconds = duration % 60;
		long minutes = (duration / 60) % 60;
		long hours = (duration / 60) / 60;
		final String str = String.format("%d:%02d:%02d", hours,	minutes, seconds);
		return str;
	}

	void gps2gmap(GeoPoint newgp) {
		statusHandler.addPosition(newgp);
		gMapH.updatePosition(statusHandler.getPositions());
	}

	GeoPoint getLastPosition() {
		return statusHandler.getLastPosition();
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	
	void setCountdown(int nowcd) {
		if (nowcd > 0) {
			btStart.setText(nowcd + "");
		} else {
			statusHandler.start();
			btStart.setText("Pause");
			btStart.setClickable(true);
			//ttsHandler.speak("Start", TextToSpeech.QUEUE_FLUSH, null);
			ttsSpeakHandler.init();
			playMusic();
		}
	}
	
	private void playMusic() {
		mplayer.init(this.settingpref);
		mplayer.playFromTheFirstSong();
	}

	class ARTimer extends Timer {
		int countdown;
		boolean isStopped;

		public ARTimer() {
			isStopped = false;
		}

		private TimerTask newTimerTask() {
			return new TimerTask() {
				@Override
				public void run() {
					if(isStopped)	return;
					
					--countdown;
					WorkoutActivity.this.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							setCountdown(countdown);
						}
					});
					
					if (countdown > 0) {
						ARTimer.this.schedule(newTimerTask(), 1000);
					}
				}
			};
		}

		void start(int cd) {
			isStopped = false;
			countdown = cd;
			this.schedule(newTimerTask(), 1000);
		}

		void setStopped(boolean isStopped) {
			this.isStopped = isStopped;
		}

		int getCountdown() {
			return countdown;
		}

	}

	class AudioVariable {
		int initVolume;
		double goalSpeed;
		int lastVolume;

		public AudioVariable() {
		}

		int newVolume(double nowspeed) {
			if(goalSpeed == 0.0)	return -1;
			
			double speedratio = nowspeed / goalSpeed;
			int nowVolume = audioManager
					.getStreamVolume(AudioManager.STREAM_MUSIC);
			initVolume += (nowVolume - lastVolume);

			if (speedratio > 1) {
				int quantize = (int) Math.floor((speedratio - 1) / 0.25);
				int newv = initVolume - quantize;
				if (newv < 0)
					newv = 0;

				if (newv != nowVolume){
					lastVolume = newv;
					return newv;
				}
			} else if (speedratio < 1) {
				int quantize = (int) Math.floor((1 - speedratio) / 0.25);
				int newv = initVolume + quantize;
				int max = audioManager
						.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
				if (newv > max)
					newv = max;

				if (newv != nowVolume){
					lastVolume = newv;
					return newv;
				}
			} else {
				if (initVolume != nowVolume){
					lastVolume = initVolume;
					return initVolume;
				}
			}

			return -1;
		}

		void setGoalSpeed(double goalSpeed) {
			this.goalSpeed = goalSpeed;
		}

		void setInitVolume(int initVolume) {
			this.initVolume = initVolume;
			lastVolume = initVolume;
		}

		int getInitVolume() {
			return initVolume;
		}

	}

	@Override
	public void onInit(int status) {
		if(status == TextToSpeech.SUCCESS){
			int langStatus = ttsHandler.isLanguageAvailable(Locale.US);
			
			switch (langStatus) {
				case TextToSpeech.LANG_AVAILABLE:
				case TextToSpeech.LANG_COUNTRY_AVAILABLE:
				case TextToSpeech.LANG_COUNTRY_VAR_AVAILABLE:
					ttsHandler.setLanguage(Locale.US);
					break;
				case TextToSpeech.LANG_MISSING_DATA:
				case TextToSpeech.LANG_NOT_SUPPORTED:
					Log.d("tts", "can not speak english");
					break;
				default:
					break;
			}
		}else if(status == TextToSpeech.ERROR){
			Log.d("tts", "init status error");
		}
		
	}
	
	class TtsSpeakProgress{
		int time;	// 30 minutes
		double distance;	// 1 km or mile
		
		public TtsSpeakProgress() {
			init();
		}
		
		void init(){
			time = 5;
			distance = 1.0;
		}
		
		void checkSpeakTimeProgress(long nowSeconds){
			if(nowSeconds/60 >= time){
				ttsHandler.speak(time + " minutes", TextToSpeech.QUEUE_FLUSH, null);
				time += 5;
			}
			
			return;
		}
		
		void checkSpeakDistanceProgress(double nowDistance){
			double tmpdis = unitHandler.distanceToUnit(nowDistance);
			
			if(tmpdis >= distance){
				ttsHandler.speak(distance + unitHandler.getDisplayUnit(), TextToSpeech.QUEUE_FLUSH, null);
				distance += 1.0;
			}
			
			return;
		}
		
	}
	
}
