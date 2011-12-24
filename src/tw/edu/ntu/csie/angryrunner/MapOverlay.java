package tw.edu.ntu.csie.angryrunner;

import java.util.List;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class MapOverlay extends Overlay {
	List<GeoPoint> pts = null;
	Paint paint;
	ViewPager vpfrom;
	
	public MapOverlay(ViewPager vp) {
		vpfrom = vp;
	}
	
	public MapOverlay(List<GeoPoint> pos, ViewPager vp) {
		super();
		pts = pos;
		paint = new Paint();
		paint.setColor(Color.BLUE);
		
		vpfrom = vp;
	}
	
	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		super.draw(canvas, mapView, shadow);
		
		float savx, savy;
		if(!shadow && pts != null && pts.size() > 0){
			Projection proj = mapView.getProjection();
			Point po = new Point();
			
			if(pts.size() == 1){
				proj.toPixels(pts.get(0), po);
				canvas.drawCircle(po.x, po.y, 5, paint);
				//canvas.drawPoint(po.x, po.y, paint);
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
				canvas.drawCircle(po.x, po.y, 5, paint);
			}
			
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent e, MapView mapView) {
		if(mapView.isEnabled()){
			vpfrom.requestDisallowInterceptTouchEvent(true);
			return super.onTouchEvent(e, mapView);
		}
		
		return false;
	}

}
