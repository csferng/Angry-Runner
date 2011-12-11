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
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MusicActivity extends Activity {
	
	String musicPackageName;
	AudioManager audioManager;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		//setContentView(R.layout.music);
		
		audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);

		try {
			List<PackageInfo> appListInfo = this.getPackageManager().getInstalledPackages(0);
			JSONArray ja = new JSONArray();
			for (PackageInfo p : appListInfo) {
				if (p.applicationInfo.uid > 10000) {
					JSONObject jo = new JSONObject();
					jo.put("label", p.applicationInfo.name);
					jo.put("packageName", p.applicationInfo.packageName);
					ja.put(jo);
					if (p.applicationInfo.packageName.endsWith("android.music")) {
						musicPackageName = p.applicationInfo.packageName;
						break;
					}
				}
			}
			startApplication(musicPackageName);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
	}
	
	public void startApplication(String application_name){
	    try{
	        Intent intent = new Intent("android.intent.action.MAIN");
	        intent.addCategory("android.intent.category.LAUNCHER");

	        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
	        List<ResolveInfo> resolveinfo_list = this.getPackageManager().queryIntentActivities(intent, 0);

	        for(ResolveInfo info:resolveinfo_list){
	            if(info.activityInfo.packageName.equalsIgnoreCase(application_name)){
	                launchComponent(info.activityInfo.packageName, info.activityInfo.name);
	                break;
	            }
	        }
	    }
	    catch (ActivityNotFoundException e) {
	        Toast.makeText(this.getApplicationContext(), "There was a problem loading the application: "+application_name,Toast.LENGTH_SHORT).show();
	    }
	}
	
	private void launchComponent(String packageName, String name){
	    Intent launch_intent = new Intent("android.intent.action.MAIN");
	    launch_intent.addCategory("android.intent.category.LAUNCHER");
	    launch_intent.setComponent(new ComponentName(packageName, name));
	    launch_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    
	    this.startActivity(launch_intent);
	}
	
}
