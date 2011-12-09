package tw.edu.ntu.csie.angryrunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.android.maps.GeoPoint;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class GpsHandler {
	LocationManager lm;
	List<Double> savlong;
	List<Double> savlat;
	Activity savact;
	
	LocationListener ll = new LocationListener(){

		@Override
		public void onLocationChanged(Location location) {
			savlong.add(location.getLongitude());
			savlat.add(location.getLatitude());
			if(savlong.size() == 5 && savlat.size() == 5){
				Double[] tmplong = savlong.toArray(new Double[5]);
				Double[] tmplat = savlat.toArray(new Double[5]);
				Arrays.sort(tmplong);
				Arrays.sort(tmplat);
				
				((WorkoutActivity) savact).gps2gmap(new GeoPoint(
				(int) ((tmplat[1]+tmplat[2]+tmplat[3]) / 3.0 * 1E6), 
				(int) ((tmplong[1]+tmplong[2]+tmplong[3]) / 3.0 * 1E6)));
				
				savlong.remove(0);
				savlat.remove(0);
			}
		}

		@Override
		public void onProviderDisabled(String provider) {}

		@Override
		public void onProviderEnabled(String provider) {}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {}
		
	};
	
	public GpsHandler(Activity activity) {
		super();
		savlong = new ArrayList<Double>();
		savlat = new ArrayList<Double>();
		lm = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 1, ll);
		savact = activity;
	}

}
