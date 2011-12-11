package tw.edu.ntu.csie.angryrunner;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.pm.ResolveInfo;
import android.content.pm.PackageInfo;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.widget.Toast;

public class MusicHandler {

	Activity ac;

	String musicPackageName;
	AudioManager audioManager;
	
	final String appNameSuffix = "android.music";
	
	public MusicHandler() {
		getIntent();
	}
	
	public Intent getIntent(){
		
		audioManager = (AudioManager) ac.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);

		try {
			List<PackageInfo> appListInfo = ac.getPackageManager().getInstalledPackages(0);
			JSONArray ja = new JSONArray();
			for (PackageInfo p : appListInfo) {
				if (p.applicationInfo.uid > 10000) {
					JSONObject jo = new JSONObject();
					jo.put("label", p.applicationInfo.name);
					jo.put("packageName", p.applicationInfo.packageName);
					ja.put(jo);
					if (p.applicationInfo.packageName.endsWith( appNameSuffix )) {
						musicPackageName = p.applicationInfo.packageName;
						return startApplication(musicPackageName);
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		return null;
	}

	private Intent startApplication(String application_name){
	    try{
	        Intent intent = new Intent("android.intent.action.MAIN");
	        intent.addCategory("android.intent.category.LAUNCHER");

	        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
	        List<ResolveInfo> resolveinfo_list = ac.getPackageManager().queryIntentActivities(intent, 0);

	        for(ResolveInfo info:resolveinfo_list){
	            if(info.activityInfo.packageName.equalsIgnoreCase(application_name)){
	                intent.setComponent(new ComponentName(info.activityInfo.packageName, info.activityInfo.name));
	        	    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	        	    return intent;
	            }
	        }
	    }
	    catch (ActivityNotFoundException e) {
	        Toast.makeText(ac.getApplicationContext(), "There was a problem loading the application: "+application_name,Toast.LENGTH_SHORT).show();
	    }
	    return null;
	}

}
