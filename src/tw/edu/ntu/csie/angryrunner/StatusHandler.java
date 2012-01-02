package tw.edu.ntu.csie.angryrunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.content.SharedPreferences;
import android.os.Bundle;
import com.google.android.maps.GeoPoint;

public class StatusHandler {
	public static enum State {
		BEFORE_START, WORKING, PAUSE
	}

	private State state;
	private WorkoutActivity fromActivity;
	private List<GeoPoint> positions;
	private double distance;	// km
	private long startTime;		// millisecond
	private long pauseTime;		// millisecond
	private double calories;	// kcal
	private Timer timer;
	private SpeedCalculator speedCalculator;
	private SharedPreferences settingpref;
	private UnitHandler unitHandler;

	public StatusHandler(final WorkoutActivity activity, SharedPreferences pref, UnitHandler unitHandler) {
		fromActivity = activity;

		state = State.BEFORE_START;
		distance = 0.0;
		calories = 0.0;
		positions = new ArrayList<GeoPoint>();
		speedCalculator = new SpeedCalculator();
		settingpref = pref;
		this.unitHandler = unitHandler;
	}
	
	public void cleanUp() {
		fromActivity = null;
		if(timer != null) timer.cancel();
	}

	void start() {
		state = State.WORKING;
		distance = 0.0;
		calories = 0.0;
		startTime = System.currentTimeMillis();
		timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				long duration = (System.currentTimeMillis() - startTime) / 1000;
				if (state == State.WORKING) {
					fromActivity.updateDurationDisplay(duration);
					double speed = speedCalculator.getSpeed();
					fromActivity.updateSpeedDisplay(speed);
				}
			}
		}, 1000, 1000);
	}

	Bundle stop() {
		if(state != State.WORKING) resume();
		state = State.BEFORE_START;
		timer.cancel();

		long finalDuration = (System.currentTimeMillis() - startTime) / 1000;
		double finalSpeed = (distance*1000) / finalDuration;
		Bundle bun = new Bundle();
		//bun.putString("Mode", "");
		bun.putString(
				fromActivity.getString(R.string.KEY_SPEED), 
				String.format("%.2f m/s", finalSpeed));
		bun.putString(
				fromActivity.getString(R.string.KEY_DURATION), 
				String.format("%d:%02d:%02d", 
						(finalDuration / 60) / 60, 
						(finalDuration / 60) % 60, 
						finalDuration % 60));
		bun.putString(fromActivity.getString(R.string.KEY_DISTANCE), 
				unitHandler.presentDistanceWithUnit(distance));
		bun.putString(
				fromActivity.getString(R.string.KEY_CALORIE), 
				String.format("%.0f kcal", calories));
		
		// clear variables
		positions.clear();
		distance = 0.0;
		calories = 0.0;
		speedCalculator.clearRecord();
		
		return bun;
	}

	void pause() {
		state = State.PAUSE;
		pauseTime = System.currentTimeMillis();
		speedCalculator.clearRecord();
	}

	void resume() {
		state = State.WORKING;
		startTime += (System.currentTimeMillis() - pauseTime);
	}

	void updateDistance() {
		if(positions.size() < 2)
			return;
		
		GeoPoint gp1 = positions.get(positions.size() - 2);
		GeoPoint gp2 = positions.get(positions.size() - 1);
		distance += MathUtil.distanceBetween(gp1, gp2);
		fromActivity.updateDistanceDisplay(distance);
	}

	GeoPoint getLastPosition() {
		if (positions.size() > 0)
			return positions.get(positions.size() - 1);

		return null;
	}

	void addPosition(GeoPoint newgp) {
		if (state == State.BEFORE_START)
			positions.clear();
		positions.add(newgp);
		if (state == State.WORKING) {
			updateDistance();
			speedCalculator.addRecord(newgp);
			double speed = speedCalculator.getLastPeriodSpeed();
			double duration = speedCalculator.getLastPeriodTime();
			String mode = settingpref.getString(
					fromActivity.getString(R.string.KEY_MODE), 
					fromActivity.getString(R.string.VALUE_WALKING));
			double weight = Double.parseDouble(settingpref.getString(
					fromActivity.getString(R.string.KEY_WEIGHTVALUE), 
					fromActivity.getString(R.string.INIT_WEIGHTVALUE)));
			calories += MathUtil.calculateCalories(mode, speed, duration, weight);
			fromActivity.updateCaloriesDisplay(calories);
		}
	}
	
	public void refreshDistanceDisplay() {
		fromActivity.updateDistanceDisplay(distance);
	}

	boolean isStateBeforeStart() {
		if (state == State.BEFORE_START)
			return true;
		return false;
	}

	boolean isStateWorking() {
		if (state == State.WORKING)
			return true;
		return false;
	}

	boolean isStatePause() {
		if (state == State.PAUSE)
			return true;
		return false;
	}

	List<GeoPoint> getPositions() {
		return positions;
	}

	State getState() {
		return state;
	}
}
