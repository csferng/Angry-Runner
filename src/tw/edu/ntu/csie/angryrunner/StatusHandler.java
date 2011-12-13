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
	private double distance;	// km
	private long startTime;		// millisecond
	private Timer timer;
	//private TimerTask timerTask;

	public StatusHandler(final Activity activity) {
		positions = new ArrayList<GeoPoint>();
		
		state = State.BEFORE_START;
		distance = 0.0;
		
		fromActivity = activity;

	}
	
	void start(){
		// TODO
		state = State.WORKING;
		
		startTime = System.currentTimeMillis();
		timer = new Timer();
		timer.schedule(new TimerTask(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				long duration = (System.currentTimeMillis() - startTime) / 1000;
				((WorkoutActivity) fromActivity).updateDurationDisplay(duration);
			}
		}, 1000, 1000);
	}
	
	void stop(){
		// TODO
		state = State.BEFORE_START;
		timer.cancel();
	}
	
	void pause(){
		//TODO
		state = State.PAUSE;
	}
	
	void resume(){
		//TODO
		state = State.WORKING;
	}

	void updateDistance() {
		GeoPoint gp1, gp2;

		gp1 = positions.get(positions.size() - 2);
		gp2 = positions.get(positions.size() - 1);
		distance += (Math.acos(Math.sin(gp1.getLatitudeE6() / 1E6)
				* Math.sin(gp2.getLatitudeE6() / 1E6)
				+ Math.cos(gp1.getLatitudeE6() / 1E6)
				* Math.cos(gp2.getLatitudeE6() / 1E6)
				* Math.cos((gp2.getLongitudeE6() / 1E6)
						- (gp1.getLongitudeE6() / 1E6))) * 6371.0);
	}

	GeoPoint getLastPosition() {
		if (positions.size() > 0)
			return positions.get(positions.size() - 1);

		return null;
	}

	void addPosition(GeoPoint newgp) {
		positions.add(newgp);
	}

	void clearPositions() {
		positions.clear();
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
