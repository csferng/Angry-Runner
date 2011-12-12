package tw.edu.ntu.csie.angryrunner;

import java.util.List;

import android.app.Activity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class GmapHandler{
	MapView mv;
	MapController mc;
	Button center, isEnable, back;
	ViewPager vpfrom;
	
	public GmapHandler(View v, final Activity activity, ViewPager vp) {
		super();
		mv = (MapView) v.findViewById(R.id.mv1);
		center = (Button) v.findViewById(R.id.btCenter);
		isEnable = (Button) v.findViewById(R.id.btEnable);
		back = (Button) v.findViewById(R.id.btBack);
		vpfrom = vp;
		
		mc = mv.getController();
		mv.setBuiltInZoomControls(true);
		mv.setEnabled(false);
		
		center.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View arg0) {
				GeoPoint gp = ((WorkoutActivity) activity).getLastPosition();
				if(gp != null)
					mc.animateTo(gp);
			}
		});
		
		isEnable.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View arg0) {
				if(mv.isEnabled()){
					mv.setEnabled(false);
					isEnable.setText("Enable ");
				}else{
					mv.setEnabled(true);
					isEnable.setText("Disable");
				}
			}
		});
		
		List<Overlay> ol = mv.getOverlays();
		ol.clear();
		ol.add(new MapOverlay(vpfrom));
		
		back.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View v) {
				vpfrom.setCurrentItem(0);
			}
		});
		
	}
	
	void updatePosition(List<GeoPoint> positions){
		mc.animateTo(positions.get(positions.size()-1));
		List<Overlay> ol = mv.getOverlays();
		ol.clear();
		ol.add(new MapOverlay(positions, vpfrom));
	}

}
