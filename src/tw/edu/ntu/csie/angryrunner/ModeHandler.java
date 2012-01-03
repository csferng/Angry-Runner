package tw.edu.ntu.csie.angryrunner;
import android.app.Activity;
import tw.edu.ntu.csie.angryrunner.R;


public class ModeHandler {
	private Activity act;
	
	public ModeHandler(Activity activity) {
		act = activity;
	}
	
	String getModeDisplay(String mode){
		if(mode.equals(act.getString(R.string.VALUE_WALKING))){
			return act.getString(R.string.STR_WALKING);
		}else if(mode.equals(act.getString(R.string.VALUE_RUNNING))){
			return act.getString(R.string.STR_RUNNING);
		}else if(mode.equals(act.getString(R.string.VALUE_CYCLING))){
			return act.getString(R.string.STR_CYCLING);
		}
		
		return act.getString(R.string.INIT_MODE);
	}
	
	String getMode(String mode){
		if(mode.equals(act.getString(R.string.STR_WALKING))){
			return act.getString(R.string.VALUE_WALKING);
		}else if(mode.equals(act.getString(R.string.STR_RUNNING))){
			return act.getString(R.string.VALUE_RUNNING);
		}else if(mode.equals(act.getString(R.string.STR_CYCLING))){
			return act.getString(R.string.VALUE_CYCLING);
		}
		
		return act.getString(R.string.VALUE_WALKING);
	}
	
}
