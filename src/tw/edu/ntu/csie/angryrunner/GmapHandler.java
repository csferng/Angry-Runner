package tw.edu.ntu.csie.angryrunner;

import java.util.List;
import android.app.Activity;
import android.view.View;
import android.widget.Button;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class GmapHandler{
	MapView mv;
	MapController mc;
	Button center;
	
	public GmapHandler(View v, final Activity activity) {
		super();
		mv = (MapView) v.findViewById(R.id.mv1);
		center = (Button) v.findViewById(R.id.button1);
		
		mc = mv.getController();
		mv.setBuiltInZoomControls(true);
		
		center.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View arg0) {
				GeoPoint gp = ((WorkoutActivity) activity).getLastPosition();
				if(gp != null)
					mc.animateTo(gp);
			}
		});
	}
	
	void updatePosition(List<GeoPoint> positions){
		mc.animateTo(positions.get(positions.size()-1));
		List<Overlay> ol = mv.getOverlays();
		ol.clear();
		ol.add(new myOverlay(positions));
	}

}
