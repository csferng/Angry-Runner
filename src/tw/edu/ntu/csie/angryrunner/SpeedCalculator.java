package tw.edu.ntu.csie.angryrunner;

import java.util.ArrayList;

import com.google.android.maps.GeoPoint;

public class SpeedCalculator {
	private static final long PERIOD = 5000;	// milliseconds 
	private ArrayList<TimeLocationPair> record;
	
	public SpeedCalculator() {
		record = new ArrayList<TimeLocationPair>();
	}
	
	/**
	 * Calculate distance between two GeoPoints.
	 * @return distance in kilometers
	 */
	static double distanceBetween(GeoPoint gp1, GeoPoint gp2) {
		double tmplat1 = (Math.PI / 180) * (gp1.getLatitudeE6() / 1E6);
		double tmplong1 = (Math.PI / 180) * (gp1.getLongitudeE6() / 1E6);
		double tmplat2 = (Math.PI / 180) * (gp2.getLatitudeE6() / 1E6);
		double tmplong2 = (Math.PI / 180) * (gp2.getLongitudeE6() / 1E6);
		
		double d = (Math.acos(Math.sin(tmplat1) * Math.sin(tmplat2)
				+ Math.cos(tmplat1) * Math.cos(tmplat2)
				* Math.cos(tmplong2 - tmplong1)) * 6371.0);
		return d;
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
			dx += distanceBetween(record.get(i-1).getLocation(), record.get(i).getLocation());
		return dx * 1000 * 1000 / dt;
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
