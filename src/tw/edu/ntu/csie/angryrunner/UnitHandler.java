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
		return pref.getString(act.getString(R.string.KEY_UNIT), act.getString(R.string.INIT_UNIT));
	}
	
	public String getDisplayUnit() {
		// TODO Use act.getString() for different languages
		String s = getPrefUnit();
		if(s.equals("Kilometer")) return "Km";
		else return "Mile";
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
