package tw.edu.ntu.csie.angryrunner;

import java.util.ArrayList;

import com.google.android.maps.GeoPoint;

public class SpeedCalculator {
	private static final long PERIOD = 5000;	// milliseconds 
	private ArrayList<TimeLocationPair> record;
	
	public SpeedCalculator() {
		record = new ArrayList<TimeLocationPair>();
	}
	
	public void clearRecord() {
		record.clear();
	}
	
	public void addRecord(GeoPoint geoPoint) {
		record.add(new TimeLocationPair(geoPoint, System.currentTimeMillis()));
	}
	
	public double getSpeed() {
		if(record.size() < 2) return 0.0;
		long now = System.currentTimeMillis();
		while(record.size() > 2 && record.get(0).getTime() < now-PERIOD) {
			record.remove(0);
		}
		long dt = record.get(record.size()-1).getTime() - record.get(0).getTime();
		double dx = 0.0;
		for(int i = 1; i < record.size(); ++i)
			dx += MathUtil.distanceBetween(record.get(i-1).getLocation(), record.get(i).getLocation());
		return dx * 1000 * 1000 / dt;
	}
	
	public double getLastPeriodTime() {
		int n = record.size();
		if(n < 2) return 0;
		else return (record.get(n-1).getTime() - record.get(n-2).getTime()) / 1000.0;
	}
	
	public double getLastPeriodSpeed() {
		int n = record.size();
		if(n < 2) return 0.0;
		else {
			double dt = (record.get(n-1).getTime() - record.get(n-2).getTime()) / 1000.0;
			return MathUtil.distanceBetween(record.get(n-1).getLocation(), record.get(n-2).getLocation()) / dt;
		}
	}

	public static class TimeLocationPair {
		private GeoPoint location;
		private long time;
		public TimeLocationPair(GeoPoint geoPoint, long time) {
			this.location = geoPoint;
			this.time = time;
		}
		public GeoPoint getLocation() {
			return location;
		}
		public long getTime() {
			return time;
		}
	}
}
