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
import android.media.AudioManager;
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
	AudioManager audioManager;
	AudioVariable audioVariable;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_view);

		settingpref = getSharedPreferences(
				this.getResources().getString(R.string.NAME_SHAREDPREFERENCE),
				MODE_PRIVATE);
		historydb = (new HistoryDatabaseHandler(WorkoutActivity.this))
				.getWritableDatabase();
		audioManager = (AudioManager) getApplicationContext().getSystemService(
				AUDIO_SERVICE);

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
		resetStatus(pageViews.get(0));
		initButtons(pageViews.get(0));
		initMode(pageViews.get(0));

		gMapH = new GmapHandler(pageViews.get(1), this, vpWorkout);
		gpsH = new GpsHandler(this);

		statusHandler = new StatusHandler(WorkoutActivity.this, settingpref);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode != RESULT_OK) return;
		Bundle extras = (data==null) ? null : data.getExtras();
		if(requestCode == 1) {
			saveHistory(extras);
		} else if(requestCode == 0) {
		} else if(requestCode == 2) {
			editPreference(getString(R.string.KEY_TIMEGOAL), getString(R.string.KEY_TIME), extras);
		} else if(requestCode == 3) {
			editPreference(getString(R.string.KEY_DISTANCEGOAL), getString(R.string.KEY_DISTANCE), extras);
		} else if(requestCode == 4) {
			editPreference(getString(R.string.KEY_SPEEDGOAL), getString(R.string.KEY_SPEED), extras);
			editPreference(getString(R.string.KEY_PACEGOAL), null, extras);
		}
		resetStatus(pageViews.get(0));
	}

	private void editPreference(String keyGoal, String keyDisplay, Bundle extras) {
		SharedPreferences.Editor edt = settingpref.edit();
		if(keyGoal != null) edt.putString(keyGoal, extras.getString(keyGoal));
		if(keyDisplay != null) edt.putString(keyDisplay, extras.getString("display"));
		edt.commit();
	}

	private void saveHistory(Bundle bundle) {
		String[] dbTableColumn = new String[5];
		dbTableColumn[0] = this.getResources().getString(R.string.KEY_MODE);
		dbTableColumn[1] = this.getResources().getString(R.string.KEY_DATE);
		dbTableColumn[2] = this.getResources().getString(
				R.string.KEY_DISTANCE);
		dbTableColumn[3] = this.getResources().getString(
				R.string.KEY_DURATION);
		dbTableColumn[4] = this.getResources()
				.getString(R.string.KEY_SPEED);
		String date = DateFormat.getDateInstance(DateFormat.MEDIUM).format(
				new Date());

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
				this.getResources().getString(R.string.KEY_MODE), this
						.getResources().getString(R.string.INIT_MODE)));
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
					audioVariable = new AudioVariable(audioManager
							.getStreamVolume(AudioManager.STREAM_MUSIC));
					btWorkout.setEnabled(false);
					int countdown = Integer.parseInt(settingpref.getString(
							WorkoutActivity.this.getResources().getString(
									R.string.KEY_COUNTDOWNVALUE),
							WorkoutActivity.this.getResources().getString(
									R.string.INIT_COUNTDOWNVALUE)));
					setCountdown(countdown);
					if (countdown > 0) {
						btStart.setClickable(false);
						ARTimer timer = new ARTimer(countdown);
						timer.start();
					}
				} else if (statusHandler.isStateWorking()) {
					statusHandler.pause();
					btStart.setText("Resume");
				} else if (statusHandler.isStatePause()) {
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
				if (!statusHandler.isStateBeforeStart()) {
					Bundle bun = statusHandler.stop();
					bun.putString(WorkoutActivity.this.getResources()
							.getString(R.string.KEY_MODE), settingpref
							.getString(WorkoutActivity.this.getResources()
									.getString(R.string.KEY_MODE),
									WorkoutActivity.this.getResources()
											.getString(R.string.INIT_MODE)));
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
		statDuration.setNumber("0:00:00");
		statCalorie.setNumber("0");
		statDistance.setNumber("0.00");
		String goalRemain = null;
		String initGoalValues = getString(R.string.INIT_GOALVALUES);
		String goal = settingpref.getString(getString(R.string.KEY_TIMEGOAL), initGoalValues);
		if(goal.equals(initGoalValues)) {
			goal = settingpref.getString(getString(R.string.KEY_DISTANCEGOAL), initGoalValues);
			float distance = Float.parseFloat(goal);
			goalRemain = distanceToString(distance) + getUnit();
		} else {
			long duration = Long.parseLong(goal);
			goalRemain = durationToString(duration);
		}
		progressBar.setProgress(0, "remain: "+goalRemain);
	}

	private void resetStatus(View v) {
		speedChart.setExpectedValue(Double.parseDouble(settingpref.getString(
				getString(R.string.KEY_SPEEDGOAL), getString(R.string.INIT_GOALVALUES))));

		StatusItemLayout statMajor = (StatusItemLayout) v
				.findViewById(R.id.statMajor);
		StatusItemLayout statMinor = (StatusItemLayout) v
				.findViewById(R.id.statMinor);
		if (settingpref.getString(
				this.getResources().getString(R.string.KEY_TIMEGOAL),
				this.getResources().getString(R.string.INIT_GOALVALUES))
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
		statDistance.setUnit(getUnit());
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

	private String getUnit() {
		String nowUnit = settingpref.getString(
				this.getResources().getString(R.string.KEY_UNIT), this
						.getResources().getString(R.string.INIT_UNIT));
		if (nowUnit.equals("Kilometer"))
			return "Km";
		else
			return "Mile";
	}

	@Override
	protected void onResume() {
		super.onResume();
		String mode = settingpref.getString(
				this.getResources().getString(R.string.KEY_MODE), this
						.getResources().getString(R.string.INIT_MODE));
		tvMode.setText(mode);
		statDistance.setUnit(getUnit());
		speedChart.setMaxValue(MathUtil.getMaxSpeedForMode(mode));
	}

	@Override
	public void onDestroy() {
		speedChart.cleanUp();
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

		double goalvalue = Double.parseDouble(settingpref.getString(this
				.getResources().getString(R.string.KEY_SPEEDGOAL), this
				.getResources().getString(R.string.INIT_GOALVALUES)));
		double fastT = audioVariable.getTooFastThreshold();
		double slowT = audioVariable.getTooSlowThreshold();
		if (speed <= fastT * goalvalue && speed >= slowT * goalvalue) {
			audioManager
					.setStreamVolume(AudioManager.STREAM_MUSIC,
							audioVariable.getInitVolume(),
							AudioManager.FLAG_PLAY_SOUND);
		} else if (speed > fastT * goalvalue) {
			audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
					AudioManager.ADJUST_LOWER, AudioManager.FLAG_PLAY_SOUND);
			audioVariable.setTooFastThreshold(fastT + 0.1);
			audioVariable.setTooSlowThreshold(fastT);
		} else if (speed < slowT * goalvalue) {
			audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
					AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND);
			audioVariable.setTooSlowThreshold(slowT - 0.1);
			audioVariable.setTooFastThreshold(slowT);
		}
		// audioManager.adjustVolume(AudioManager.ADJUST_RAISE,
		// AudioManager.FLAG_PLAY_SOUND);
		// audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
		// AudioManager.ADJUST_LOWER, AudioManager.FLAG_PLAY_SOUND);
	}

	void updateDistanceDisplay(double distance) {
		if(getUnit().equals("Mile")) {
			// TODO
		}
		final String str = distanceToString(distance);
		WorkoutActivity.this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
					statDistance.setNumber(str);
			}
		});
		updateProgressDisplay(distance, -1);
	}

	void updateProgressDisplay(double distance, double duration) {
		float prog = 0.0f;
		String remain = null;
		String keyTimeGoal = getString(R.string.KEY_TIMEGOAL);
		String keyDistanceGoal = getString(R.string.KEY_DISTANCEGOAL);
		String initGoalValues = getString(R.string.INIT_GOALVALUES);
		if (settingpref.getString(keyTimeGoal, initGoalValues).equals("0")
				&& distance > 0) {
			float goal = Float.parseFloat(settingpref.getString(keyDistanceGoal, initGoalValues));
			if (goal > 0) {
				prog = (float) distance / goal;
			}
			remain = String.format("%.2f %s", Math.max(0,goal-distance), getUnit());
		} else if (duration > 0) {
			float goal = Float.parseFloat(settingpref.getString(keyTimeGoal, initGoalValues));
			if (goal > 0) {
				prog = (float) duration / goal;
			}
			int dt = Math.max(0, (int)(goal-duration+0.5));
			remain = String.format("%d:%02d:%02d", dt/3600, (dt/60)%60, dt%60);
		}
		if (prog > 1.0f) prog = 1.0f;
		progressBar.setProgress(prog*100.0f, "remain: "+remain);
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

	private String distanceToString(double distance) {
		return String.format("%.2f", distance);
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
		}
	}

	class ARTimer extends Timer {
		int countdown;

		public ARTimer(int cd) {
			countdown = cd;
		}

		private TimerTask newTimerTask() {
			return new TimerTask() {
				@Override
				public void run() {
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

		void start() {
			this.schedule(newTimerTask(), 1000);
		}
	}

	class AudioVariable {
		int initVolume;
		double tooFastThreshold;
		double tooSlowThreshold;

		public AudioVariable(int volume) {
			initVolume = volume;
			tooFastThreshold = 1.1;
			tooSlowThreshold = 0.9;
		}

		double getTooFastThreshold() {
			return tooFastThreshold;
		}

		void setTooFastThreshold(double tooFastThreshold) {
			this.tooFastThreshold = tooFastThreshold;
		}

		double getTooSlowThreshold() {
			return tooSlowThreshold;
		}

		void setTooSlowThreshold(double tooSlowThreshold) {
			this.tooSlowThreshold = tooSlowThreshold;
		}

		int getInitVolume() {
			return initVolume;
		}
	}
}
