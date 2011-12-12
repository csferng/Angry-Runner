package tw.edu.ntu.csie.angryrunner;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class StatusItemLayout extends RelativeLayout {
	TextView tvType, tvUnit, tvNumber;

	public StatusItemLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.status_item, this);
		tvType = (TextView)findViewById(R.id.tvType);
		tvUnit = (TextView)findViewById(R.id.tvUnit);
		tvNumber = (TextView)findViewById(R.id.tvNumber);
		if(tvNumber != null) tvNumber.setTextSize(tvNumber.getTextSize()*1.5f);
	}
	
	public void setType(String type) {
		tvType.setText(type);
	}

	public void setUnit(String unit) {
		tvUnit.setText(unit);
	}

	public void setNumber(String number) {
		tvNumber.setText(number);
	}
}
