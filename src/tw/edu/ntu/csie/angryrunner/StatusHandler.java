package tw.edu.ntu.csie.angryrunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.google.android.maps.GeoPoint;

public class StatusHandler {
	public static enum State {
		BEFORE_START, WORKING, PAUSE
	}

	private State state;
	private WorkoutActivity fromActivity;
	private List<GeoPoint> positions;
	private double distance; // km
	private long startTime; // millisecond
	private long pauseTime; // millisecond
	private Timer timer;
	private SpeedCalculator speedCalculator;

	public StatusHandler(final WorkoutActivity activity) {
		fromActivity = activity;

		state = State.BEFORE_START;
		distance = 0.0;
		positions = new ArrayList<GeoPoint>();
		speedCalculator = new SpeedCalculator();
	}

	void start() {
		// TODO
		state = State.WORKING;
		distance = 0.0;
		startTime = System.currentTimeMillis();
		timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				long duration = (System.currentTimeMillis() - startTime) / 1000;
				if (state == State.WORKING) {
					fromActivity.updateDurationDisplay(duration);
				}
			}
		}, 1000, 1000);
	}

	void stop() {
		// TODO
		state = State.BEFORE_START;
		timer.cancel();
		speedCalculator.clearRecord();
	}

	void pause() {
		// TODO
		state = State.PAUSE;
		pauseTime = System.currentTimeMillis();
		speedCalculator.clearRecord();
	}

	void resume() {
		// TODO
		state = State.WORKING;
		startTime += (System.currentTimeMillis() - pauseTime);
	}

	void updateDistance() {
		if(positions.size() < 2)
			return;
		
		GeoPoint gp1 = positions.get(positions.size() - 2);
		GeoPoint gp2 = positions.get(positions.size() - 1);
		distance += SpeedCalculator.distanceBetween(gp1, gp2);
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
			fromActivity.updateSpeedDisplay(speedCalculator.getSpeed());
		}
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
