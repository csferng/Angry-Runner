package tw.edu.ntu.csie.angryrunner;

import android.app.Activity;
import android.content.SharedPreferences;

public class UnitHandler {
	private Activity act;
	private SharedPreferences pref;
	
	public UnitHandler(Activity activity, SharedPreferences pref) {
		this.act = activity;
		this.pref = pref;
	}
	
	private String getPrefUnit() {
		return pref.getString(act.getString(R.string.KEY_UNIT), act.getString(R.string.VALUE_KILOMETER));
	}
	
	public String getDisplayUnit() {
		String s = getPrefUnit();
		if(s.equals(act.getString(R.string.VALUE_KILOMETER)))
			return act.getString(R.string.STR_KM);
		else return act.getString(R.string.STR_MILE);
	}
	
	public String getSpeakUnit() {
		String s = getPrefUnit();
		if(s.equals(act.getString(R.string.VALUE_KILOMETER)))
			return act.getString(R.string.STR_KILOMETER);
		else return act.getString(R.string.STR_MILE);
	}
	
	public double distanceFromUnit(double distance) {
		if(getPrefUnit().equals("Mile")) {
			return distance * 1.609;
		} else {
			return distance;
		}
	}
	
	public double distanceToUnit(double distance) {
		if(getPrefUnit().equals("Mile")) {
			return distance / 1.609;
		} else {
			return distance;
		}
	}
	
	public String presentDistance(double distance) {
		return String.format("%.2f", distanceToUnit(distance));
	}
	
	public String presentDistanceWithUnit(double distance) {
		return presentDistance(distance) + " " + getDisplayUnit();
	}
}
