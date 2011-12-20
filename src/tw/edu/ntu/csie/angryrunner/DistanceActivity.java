package tw.edu.ntu.csie.angryrunner;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class DistanceActivity extends Activity {

	TextView dec_tv, speed_tv;
	WheelView cen, dec, unit;
	Button confirm_bt, cancel_bt;
	
	int curCenItemIndex, curDecItemIndex, curUnitItemIndex;
	int curCen, curDec;
	double curUnit;
	String [] digits, dpoints;

	String Unit, Distance;
	SharedPreferences settingPref;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.distance);
        setTitle(getString(R.string.KEY_DISTANCE) + " " + getString(R.string.TITLE));

        settingPref = getSharedPreferences(
        		getString(R.string.NAME_SHAREDPREFERENCE), 
        		MODE_PRIVATE);
        
        Unit = getUnit();
        Distance = getDistance(this.getIntent().getExtras());
        Log.i("Distance", Distance);        
        
        dec_tv = (TextView)findViewById(R.id.minuteText);
        dec_tv.setTypeface(Typeface.DEFAULT_BOLD);
        dec_tv.setTextColor(Color.YELLOW);
        dec_tv.setTextSize(24);
        dec_tv.setText(Unit);
        
        
        confirm_bt = (Button)findViewById(R.id.confirmBT);
        confirm_bt.setTextSize(16);
        confirm_bt.setOnClickListener(new Button.OnClickListener(){
        	@Override
        	public void onClick(View v) {
        		
        		Distance= calculateDistance();
        		Log.i("Distance", Distance);
        		
        		Intent it = new Intent();
				Bundle bun = new Bundle();
				Log.i("d-value", Distance);
				bun.putString(
						getString(R.string.KEY_DISTANCEGOAL), 
						Distance);
				Log.i("d-display", Distance+" "+Unit);
				bun.putString("display", Distance+" "+Unit);
				it.putExtras(bun);
				
				setResult(RESULT_OK, it);
				finish();
        	}
        });
        
        
        cancel_bt = (Button)findViewById(R.id.cancelBT);
        cancel_bt.setTextSize(16);
        cancel_bt.setOnClickListener(new Button.OnClickListener(){
        	@Override
        	public void onClick(View v) {
        		finish();
        	}
        });
        
        
        digits = initArray(10, 0);
        dpoints = initArray(20, 0, 0.05);
        
        cen = (WheelView)findViewById(R.id.centesimal);
        dec = (WheelView)findViewById(R.id.decimal);
        unit = (WheelView)findViewById(R.id.unit);
        
        
        initWheelValueIndex();
        
        
        OnWheelChangedListener cenListener = new OnWheelChangedListener() {
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
            	curCenItemIndex = cen.getCurrentItem();
            	curCen = Integer.parseInt( digits[curCenItemIndex] );
            	cen.setViewAdapter(new DateArrayAdapter(DistanceActivity.this, digits, curCenItemIndex));
            	cen.setCurrentItem( curCenItemIndex );
            	Distance = calculateDistance();
            }
        };
        
    	//curCenItemIndex = 0;
    	curCen = Integer.parseInt( digits[curCenItemIndex] );
    	cen.setViewAdapter(new DateArrayAdapter(DistanceActivity.this, digits, curCenItemIndex));
        cen.setCurrentItem( curCenItemIndex );
        cen.addChangingListener(cenListener);

        
        OnWheelChangedListener decListener = new OnWheelChangedListener() {
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
            	curDecItemIndex = dec.getCurrentItem();
            	curDec = Integer.parseInt( digits[curDecItemIndex] );
            	dec.setViewAdapter(new DateArrayAdapter(DistanceActivity.this, digits, curDecItemIndex));
            	dec.setCurrentItem( curDecItemIndex );
            	Distance = calculateDistance();
            }
        };
        
        //curDecItemIndex = 0;
    	curDec = Integer.parseInt( digits[curDecItemIndex] );
    	dec.setViewAdapter(new DateArrayAdapter(DistanceActivity.this, digits, curDecItemIndex));
    	dec.setCurrentItem( curDecItemIndex );
        dec.addChangingListener(decListener);
        
        
        OnWheelChangedListener unitListener = new OnWheelChangedListener() {
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
            	curUnitItemIndex = unit.getCurrentItem();
            	curUnit = Double.parseDouble( dpoints[curUnitItemIndex] );
            	unit.setViewAdapter(new DateArrayAdapter(DistanceActivity.this, dpoints, curUnitItemIndex));
            	unit.setCurrentItem( curUnitItemIndex );
            	Distance = calculateDistance();
            	Log.i("on change", String.format("idx %d val %.2f str %s", curUnitItemIndex, curUnit, Distance));
            }
        };
        
        //curUnitItemIndex = 0;
    	curUnit = Double.parseDouble( dpoints[curUnitItemIndex] );
    	unit.setViewAdapter(new DateArrayAdapter(DistanceActivity.this, dpoints, curUnitItemIndex));
    	unit.setCurrentItem( curUnitItemIndex );
        unit.addChangingListener(unitListener);
        
	}	

	
	String [] initArray(int size, int start) {
		String [] as = new String[size];
		for (int i=0; i < size; ++i) {
			as[i] = new Integer(start+i).toString();
		}
		return as;
	}

	String [] initArray(int size, double start, double gap) {
		String [] as = new String[size];
		String s = "";
		int pos = -1;
		for (int i=0; i < size; ++i) {
			s = String.format("%.2f", start);
			pos = s.indexOf(".");
			as[i] = s.substring(pos);
			start += gap;
		}
		return as;
	}
	String getUnit(){
        String str = settingPref.getString(
        		getString(R.string.KEY_UNIT), 
        		getString(R.string.INIT_UNIT));
        if (str.equals("Kilometer")) {
        	return "Km";
        }else if (str.equals("Mile")) {
        	return "Mile";
        }else {
        	return "Km";
        }
	}
	
	String getDistance(Bundle bun){
		String str = bun.getString(
				getString(R.string.KEY_DISTANCEGOAL));
		if (str.equals("")) {
			return getString(R.string.INIT_UNIT);
		}
        Log.i("getDistance()", str);
        
        int pos = str.indexOf(" ");
        if (pos != -1) {
        	return str.substring(0, pos);
        }
        return str;
	}
	
	String calculateDistance() {
		double sum = new Double(curCen*10) + new Double(curDec) + curUnit;
	    return doubleStringFormation( String.format("%.2f", sum) );
	}

	String doubleStringFormation(String target) {
		while(target.charAt(target.length()-1)=='0')
			target = target.substring(0, target.length()-1);
		return target.charAt(target.length()-1)=='.' ? target.substring(0, target.length()-1) : target;
	}
	
	void initWheelValueIndex() {
		Log.i("wheel-d", Distance);
		Double value = Double.parseDouble(Distance);
		int distance = value.intValue();
		double dec = value.doubleValue() - distance;
		curUnitItemIndex = (int)(dec*20+0.5);
		curDecItemIndex = distance%10;
		curCenItemIndex = distance/10;
	}
	
	
	public class DateArrayAdapter extends ArrayWheelAdapter<String> {
        // Index of current item
        int currentItem;
        // Index of item to be highlighted
        int currentValue;
        
        /**
         * Constructor
         */
        public DateArrayAdapter(Context context, String[] items, int current) {
            super(context, items);
            this.currentValue = current;
            setTextSize(32);
        }
        
        @Override
        protected void configureTextView(TextView view) {
            super.configureTextView(view);
            if (currentItem == currentValue) {
                view.setTextColor(0xFF0000F0);
            }
            view.setTypeface(Typeface.SANS_SERIF);
        }
        
        @Override
        public View getItem(int index, View cachedView, ViewGroup parent) {
            currentItem = index;
            return super.getItem(index, cachedView, parent);
        }
    }
	
}
