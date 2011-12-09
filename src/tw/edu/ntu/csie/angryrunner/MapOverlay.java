package tw.edu.ntu.csie.angryrunner;

import java.util.List;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class MapOverlay extends Overlay {
	List<GeoPoint> pts;
	Paint paint;
	
	public MapOverlay(List<GeoPoint> pos) {
		super();
		pts = pos;
		paint = new Paint();
		paint.setColor(Color.BLUE);
	}
	
	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		super.draw(canvas, mapView, shadow);
		
		float savx, savy;
		if(!shadow){
			Projection proj = mapView.getProjection();
			Point po = new Point();
			if(pts.size() == 1){
				proj.toPixels(pts.get(0), po);
				canvas.drawPoint(po.x, po.y, paint);
			}else{
				proj.toPixels(pts.get(0), po);
				savx = po.x;
				savy = po.y;
				for(int i = 1; i < pts.size(); ++i){
					proj.toPixels(pts.get(i), po);
					canvas.drawLine(savx, savy, po.x, po.y, paint);
					if(Math.abs(savx-po.x) < Math.abs(savy-po.y)){
						canvas.drawLine(savx +1, savy, po.x +1, po.y, paint);
						canvas.drawLine(savx -1, savy, po.x -1, po.y, paint);
					}else{
						canvas.drawLine(savx, savy +1, po.x, po.y +1, paint);
						canvas.drawLine(savx, savy -1, po.x, po.y -1, paint);
					}
					
					savx = po.x;
					savy = po.y;
				}
			}
			
		}
	}

}
