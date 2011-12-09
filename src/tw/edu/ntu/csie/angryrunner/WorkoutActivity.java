package tw.edu.ntu.csie.angryrunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class WorkoutActivity extends MapActivity {
	private Button btStart, btStop, btWorkout;
	private SpeedChartHandler speedChart;
	private StatusItemLayout statDuration, statCalorie, statDistance;
	ArrayList<View> pageViews;
	ViewPager vpWorkout;
	WorkoutPagerAdapter vpAdapter;
	List<GeoPoint> positions;
	GmapHandler gMapH;
	GpsHandler gpsH;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_view);
        
        vpWorkout = (ViewPager) findViewById(R.id.vp1);
        
        pageViews = new ArrayList<View>();
        LayoutInflater infla = getLayoutInflater();
        pageViews.add(infla.inflate(R.layout.workout, null));
        pageViews.add(infla.inflate(R.layout.page2, null));
        
        vpAdapter = new WorkoutPagerAdapter(pageViews);
        vpWorkout.setAdapter(vpAdapter);
        
        btStart = (Button) pageViews.get(0).findViewById(R.id.btStart);
        btStop = (Button) pageViews.get(0).findViewById(R.id.btStop);
        btWorkout = (Button) pageViews.get(0).findViewById(R.id.btWorkout);
        
        speedChart = new SpeedChartHandler(this, (ViewGroup) pageViews.get(0).findViewById(R.id.frDialChart));
        btStart.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				speedChart.setCurrentValue((new Random()).nextDouble()*10);
			}
		});
        initStatus(pageViews.get(0));
        btWorkout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				statDuration.setNumber(Integer.toString((new Random()).nextInt(60)));
			}
		});
        btStop.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				statCalorie.setNumber(Integer.toString((new Random()).nextInt(1000)));
			}
		});
        
        positions = new ArrayList<GeoPoint>();
        gMapH = new GmapHandler(pageViews.get(1), this);
        gpsH = new GpsHandler(this);
    }

	private void initStatus(View v) {
		statDuration = (StatusItemLayout) v.findViewById(R.id.statDuration);
        statCalorie = (StatusItemLayout) v.findViewById(R.id.statCalorie);
        statDistance = (StatusItemLayout) v.findViewById(R.id.statDistance);
		statDuration.setType("Duration");
		statDuration.setUnit("sec");
		statCalorie.setType("Calories");
		statCalorie.setUnit("kcal");
		statDistance.setType("Distance");
		statDistance.setUnit("km");
		statDistance.setNumber("0.00");
	}
    
    @Override
    public void onDestroy() {
    	speedChart.cleanUp();
    	super.onDestroy();
    }

    void gps2gmap(GeoPoint newgp){
		positions.add(newgp);
		gMapH.updatePosition(positions);
	}
	
	GeoPoint getLastPosition(){
		if(positions.size() > 0)
			return positions.get(positions.size()-1);
		
		return null;
	}
    
	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
}
