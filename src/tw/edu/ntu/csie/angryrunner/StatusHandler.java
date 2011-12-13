package tw.edu.ntu.csie.angryrunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;

import com.google.android.maps.GeoPoint;

public class StatusHandler {
	public static enum State {
		BEFORE_START, WORKING, PAUSE
	}

	private State state;
	private Activity fromActivity;
	private List<GeoPoint> positions;
	private double distance; // km
	private long startTime; // millisecond
	private long pauseTime; // millisecond
	private Timer timer;

	public StatusHandler(final Activity activity) {
		positions = new ArrayList<GeoPoint>();

		state = State.BEFORE_START;
		distance = 0.0;

		fromActivity = activity;

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
				// TODO Auto-generated method stub
				long duration = (System.currentTimeMillis() - startTime) / 1000;
				if (state == State.WORKING) {
					((WorkoutActivity) fromActivity)
							.updateDurationDisplay(duration);
				}
			}
		}, 1000, 1000);
	}

	void stop() {
		// TODO
		state = State.BEFORE_START;
		timer.cancel();
	}

	void pause() {
		// TODO
		state = State.PAUSE;
		pauseTime = System.currentTimeMillis();
	}

	void resume() {
		// TODO
		state = State.WORKING;
		startTime += (System.currentTimeMillis() - pauseTime);
	}

	void updateDistance() {
		GeoPoint gp1, gp2;
		double tmplat1, tmplat2, tmplong1, tmplong2;

		if(positions.size() < 2)
			return;
		
		gp1 = positions.get(positions.size() - 2);
		tmplat1 = (Math.PI / 180) * (gp1.getLatitudeE6() / 1E6);
		tmplong1 = (Math.PI / 180) * (gp1.getLongitudeE6() / 1E6);
		gp2 = positions.get(positions.size() - 1);
		tmplat2 = (Math.PI / 180) * (gp2.getLatitudeE6() / 1E6);
		tmplong2 = (Math.PI / 180) * (gp2.getLongitudeE6() / 1E6);
		
		distance += (Math.acos(Math.sin(tmplat1) * Math.sin(tmplat2)
				+ Math.cos(tmplat1) * Math.cos(tmplat2)
				* Math.cos(tmplong2 - tmplong1)) * 6371.0);

		((WorkoutActivity) fromActivity).updateDistanceDisplay(distance);
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
		if (state == State.WORKING)
			updateDistance();
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

	void setState(State state) {
		this.state = state;
	}

}
