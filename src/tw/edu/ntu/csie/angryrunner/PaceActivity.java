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

public class PaceActivity extends Activity {

	private TextView min_tv, sec_tv, pace_tv, speed_tv;
	private Button unset_bt, confirm_bt, cancel_bt;
	private WheelView min, sec;
	
	private String unit;
	private int pace;
	private SharedPreferences settingPref;
	private int curMinItemIndex, curSecItemIndex;
	private int curMin, curSec;
	private String [] ms;
	private UnitHandler unitHandler;
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pace);
        setTitle(getString(R.string.DISPLAY_PACE)
        		+ " " + getString(R.string.TITLE));

        settingPref = getSharedPreferences(
        		getString(R.string.NAME_SHAREDPREFERENCE), 
        		MODE_PRIVATE);
        unitHandler = new UnitHandler(this, settingPref);
        
        unit = unitHandler.getDisplayUnit();
        pace = getPace(this.getIntent().getExtras());
        
        min_tv = (TextView)findViewById(R.id.minuteText);
        min_tv.setTypeface(Typeface.DEFAULT_BOLD);
        min_tv.setTextColor(Color.YELLOW);
        min_tv.setTextSize(18);
        sec_tv = (TextView)findViewById(R.id.secondText);
        sec_tv.setTypeface(Typeface.DEFAULT_BOLD);
        sec_tv.setTextColor(Color.YELLOW);
        sec_tv.setTextSize(18);
        
        pace_tv = (TextView)findViewById(R.id.paceText);
        pace_tv.setTextSize(32);
        pace_tv.setText("/ "+unit);
        
        speed_tv = (TextView)findViewById(R.id.speedText);
        speed_tv.setTypeface(Typeface.DEFAULT_BOLD);
        speed_tv.setTextColor(Color.CYAN);
        speed_tv.setTextSize(24);
        updateSpeedText();
        
        unset_bt = (Button)findViewById(R.id.unsetBT);
        unset_bt.setTextSize(16);
        unset_bt.setOnClickListener(new Button.OnClickListener(){
        	@Override
        	public void onClick(View v) {
            	curMinItemIndex = 0;
            	curMin = Integer.parseInt( ms[curMinItemIndex] );
            	min.setViewAdapter(new DateArrayAdapter(PaceActivity.this, ms, curMinItemIndex));
            	min.setCurrentItem( curMinItemIndex );
                
            	curSecItemIndex = 0;
            	curSec = Integer.parseInt( ms[curSecItemIndex] );
            	sec.setViewAdapter(new DateArrayAdapter(PaceActivity.this, ms, curSecItemIndex));
            	sec.setCurrentItem( curSecItemIndex );
                
            	pace = calculatePace();
            	updateSpeedText();
        	}
        });
        
        confirm_bt = (Button)findViewById(R.id.confirmBT);
        confirm_bt.setTextSize(16);
        confirm_bt.setOnClickListener(new Button.OnClickListener(){
        	@Override
        	public void onClick(View v) {
        		
        		pace = calculatePace();
        		double secPerKm = pace * unitHandler.distanceToUnit(1.0);
        		double speed = calcSpeed();

        		Intent it = new Intent();
				Bundle bun = new Bundle();
				bun.putString(getString(R.string.KEY_PACEGOAL), 
						Double.toString(secPerKm));
				bun.putString(getString(R.string.KEY_SPEEDGOAL), 
						Double.toString(speed));
				bun.putString("display", String.format("%.2f m/s & %d s/%s", speed, pace, unit));
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
        
        
        ms = initArray(60, 0);
        
        min = (WheelView)findViewById(R.id.minute);
        sec = (WheelView)findViewById(R.id.second);
        
        initWheelValueIndex();
        
        OnWheelChangedListener minListener = new OnWheelChangedListener() {
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
            	curMinItemIndex = min.getCurrentItem();
            	curMin = Integer.parseInt( ms[curMinItemIndex] );
            	min.setViewAdapter(new DateArrayAdapter(PaceActivity.this, ms, curMinItemIndex));
            	min.setCurrentItem( curMinItemIndex );
                
            	pace = calculatePace();
            	updateSpeedText();
            }
        };
        
        curMin = Integer.parseInt( ms[curMinItemIndex] );
        min.setViewAdapter(new DateArrayAdapter(this, ms, curMinItemIndex));
        min.setCurrentItem( curMinItemIndex );
        min.addChangingListener(minListener);

        OnWheelChangedListener secListener = new OnWheelChangedListener() {
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
            	curSecItemIndex = sec.getCurrentItem();
            	curSec = Integer.parseInt( ms[curSecItemIndex] );
            	sec.setViewAdapter(new DateArrayAdapter(PaceActivity.this, ms, curSecItemIndex));
            	sec.setCurrentItem( curSecItemIndex );
                
            	pace = calculatePace();
            	updateSpeedText();
            }
        };
        
        curSec = Integer.parseInt( ms[curSecItemIndex] );
        sec.setViewAdapter(new DateArrayAdapter(this, ms, curSecItemIndex));
        sec.setCurrentItem( curSecItemIndex );
        sec.addChangingListener(secListener);
        
        pace = calculatePace();
        updateSpeedText();
	}
	
	private String [] initArray(int size, int start) {
		String [] as = new String[size];
		for (int i=0; i < size; ++i) {
			as[i] = new Integer(start+i).toString();
		}
		return as;
	}

	private void updateSpeedText() {
		double speed = calcSpeed();
		speed_tv.setText(String.format("%s:    %07.2f %s", getString(R.string.DISPLAY_SPEED), speed, getString(R.string.STR_UNIT_M_S)));
	}

	private double calcSpeed() {
		return (pace==0) ? 0 : (unitHandler.distanceFromUnit(1.0)*1000.0/pace);
	}
	
	private int getPace(Bundle bun) {
		String str = bun.getString(getString(R.string.KEY_PACEGOAL));
		if (str == null || str.equals("")) {
			str = getString(R.string.INIT_GOALVALUES);
		}
        Log.i("getPace()", str);
        double v = Double.parseDouble(str) / unitHandler.distanceToUnit(1.0);
        return (int)(v+0.5);
	}
	
	private int calculatePace() {
		return curMin*60 + curSec;
	}
	
	private void initWheelValueIndex() {
		curSecItemIndex = pace%60;
		curMinItemIndex = pace/60;
		if(curMinItemIndex > ms.length) {
			curMinItemIndex = ms.length - 1;
			curSecItemIndex = ms.length - 1;
		}
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
