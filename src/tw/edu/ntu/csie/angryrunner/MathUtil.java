package tw.edu.ntu.csie.angryrunner;

import com.google.android.maps.GeoPoint;

public class MathUtil {
	
	// format: MET1, speed1, MET2, speed2, ...
	// METi for speed in [speed(i-1), speed(i)], speed0 = 0 
	// MET unit: kcal/kg/hr; speed unit: m/s
	private static final float MET_JOG[] =		// for walking and running 
			new float[]{2.3f, 0.75f, 2.9f, 1.111f, 3.3f, 1.333f, 3.6f, 1.528f, 5f, 1.778f,
				6f, 2.0513f, 8f, 2.222f, 10f, 2.6667f, 13.5f, 3.5508f, 16f, 4.4444f};
	private static final float MET_CYCLE[] =	// for cycling  
			new float[]{4f, 4.4444f, 10f, 7.1111f};

	/**
	 * Calculate distance between two GeoPoints.
	 * @return distance in kilometers
	 */
	public static double distanceBetween(GeoPoint gp1, GeoPoint gp2) {
		double tmplat1 = (Math.PI / 180) * (gp1.getLatitudeE6() / 1E6);
		double tmplong1 = (Math.PI / 180) * (gp1.getLongitudeE6() / 1E6);
		double tmplat2 = (Math.PI / 180) * (gp2.getLatitudeE6() / 1E6);
		double tmplong2 = (Math.PI / 180) * (gp2.getLongitudeE6() / 1E6);
		
		double d = (Math.acos(Math.sin(tmplat1) * Math.sin(tmplat2)
				+ Math.cos(tmplat1) * Math.cos(tmplat2)
				* Math.cos(tmplong2 - tmplong1)) * 6371.0);
		return (Double.isNaN(d) || Double.isInfinite(d)) ? 0 : d;
	}

	public static double calculateCalories(String mode, double speed, double seconds, double weight) {
		if(mode.equals("Cycling")) return calculateCalories(MET_CYCLE, speed, seconds, weight);
		else return calculateCalories(MET_JOG, speed, seconds, weight);
	}

	private static double calculateCalories(float[] mets, double speed, double seconds, double weight) {
		double sp0 = 0f, sp1 = 0f, met0 = 0f, met1 = 0f, met;
		for(int i = 1; i < mets.length; i += 2) {
			sp0 = sp1;
			sp1 = mets[i];
			met0 = met1;
			met1 = mets[i-1];
			if(speed <= sp1) break;
		}
		if(speed <= sp1) {
			met = (met0*(sp1-speed)+met1*(speed-sp0)) / (sp1-sp0);
		} else {
			met = met1 + (met1-met0)*(speed-sp1)/(sp1-sp0);
		}
		return met*weight*seconds/60.0/60.0;
	}
	
	public static float getMaxSpeedForMode(String mode) {
		if(mode.equals("Walking")) return 5f;
		else if(mode.equals("Running")) return 10f;
		else if(mode.equals("Cycling")) return 25f;
		else return 10f;
	}
}
