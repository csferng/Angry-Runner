package tw.edu.ntu.csie.angryrunner;

//import kankan.wheel.R;
import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class WeightActivity extends Activity {

	TextView dec_tv;
	WheelView cen, dec, unit;
	Button confirm_bt, cancel_bt;
	
	int curCen, curDec, curUnit;
	String [] digits;
	
	
	String [] initArray(int size, int start) {
		String [] as = new String[size];
		for (int i=0; i < size; ++i) {
			as[i] = new Integer(start+i).toString();
		}
		return as;
	}
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weight);
        
        setTitle("Weight");
        
        dec_tv = (TextView)findViewById(R.id.minuteText);
        dec_tv.setTypeface(Typeface.DEFAULT_BOLD);
        dec_tv.setTextColor(Color.YELLOW);
        dec_tv.setTextSize(22);
        
        
        confirm_bt = (Button)findViewById(R.id.confirmBT);
        confirm_bt.setTypeface(Typeface.DEFAULT_BOLD);
        confirm_bt.setTextSize(16);
        confirm_bt.setOnClickListener(new Button.OnClickListener(){
        	@Override
        	public void onClick(View v) {
        	}
        });
        
        cancel_bt = (Button)findViewById(R.id.cancelBT);
        cancel_bt.setTypeface(Typeface.DEFAULT_BOLD);
        cancel_bt.setTextSize(16);
        cancel_bt.setOnClickListener(new Button.OnClickListener(){
        	@Override
        	public void onClick(View v) {
        		finish();
        	}
        });
        
        
        digits = initArray(9, 0);
        
        cen = (WheelView)findViewById(R.id.centesimal);
        dec = (WheelView)findViewById(R.id.decimal);
        unit = (WheelView)findViewById(R.id.unit);
        
        OnWheelChangedListener listener = new OnWheelChangedListener() {
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                //updateDays(year, month, day);
            	curCen = cen.getCurrentItem();
            	cen.setViewAdapter(new DateArrayAdapter(WeightActivity.this, digits, curCen));
            	curDec = dec.getCurrentItem();
            	dec.setViewAdapter(new DateArrayAdapter(WeightActivity.this, digits, curDec));
            	curUnit = unit.getCurrentItem();
            	unit.setViewAdapter(new DateArrayAdapter(WeightActivity.this, digits, curUnit));
            	//tv.setText(day.getCurrentItem()+"_"+month.getCurrentItem()+"_"+year.getCurrentItem());
            }
        };
        
        curCen = 0;
        cen.setViewAdapter(new DateArrayAdapter(this, digits, curCen));
        cen.setCurrentItem(curCen);
        cen.addChangingListener(listener);
        
        curDec = 0;
        dec.setViewAdapter(new DateArrayAdapter(this, digits, curDec));
        dec.setCurrentItem(curDec);
        dec.addChangingListener(listener);
        
        curUnit = 0;
        unit.setViewAdapter(new DateArrayAdapter(this, digits, curUnit));
        unit.setCurrentItem(curUnit);
        unit.addChangingListener(listener);
        
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
            setTextSize(22);
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