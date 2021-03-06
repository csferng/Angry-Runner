package tw.edu.ntu.csie.angryrunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class MusicActivity extends Activity {
	
	private String selectedId = "-1";
	private int selectedPosAfterSort = -1;
	
	private Button add_bt, unset_bt;
	private SimpleAdapter playListItemAdapter;
	private ArrayList<HashMap<String, String>> playListItem;
	private SharedPreferences settingPref;
	
	private void ShowMagicDialog(String Msg, final int position) {
		Builder MyAlertDialog = new AlertDialog.Builder(this);
		MyAlertDialog.setTitle(getString(R.string.STR_DPLTITLE));
		MyAlertDialog.setMessage(Msg);
		DialogInterface.OnClickListener confirmClick = new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				if (selectedPosAfterSort == position) {
					String val = playListItem.get(selectedPosAfterSort).get("playlistNameHL");
        			if(!val.equals("")) {
        				playListItem.get(selectedPosAfterSort).put("playlistName", val);
        				playListItem.get(selectedPosAfterSort).put("playlistNameHL", "");
        				playListItemAdapter.notifyDataSetChanged();
            		}
        			selectedId = "-1";
        			selectedPosAfterSort = -1;
            		SharedPreferences.Editor settingPrefEdt = settingPref.edit();
            		settingPrefEdt.putString(getString(R.string.KEY_PLAYLISTID), "-1").commit();
				}
        		HashMap<String, String> clickMap = playListItem.get(position);
        		MediaUtil.deletePlaylist(getApplicationContext(), clickMap.get("playlistName").toString());
        		playListItem.remove(position);
        		playListItemAdapter.notifyDataSetChanged();
			}
		};
		DialogInterface.OnClickListener cancelClick = new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
			}
		};
		MyAlertDialog.setPositiveButton(getString(R.string.BT_CONFIRM), confirmClick);
		MyAlertDialog.setNegativeButton(getString(R.string.BT_CANCEL), cancelClick);
		MyAlertDialog.show();
	}
	
    /** Called when the activity is first created. */
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music);
        
        settingPref = getSharedPreferences(
        		getString(R.string.NAME_SHAREDPREFERENCE), 
        		MODE_PRIVATE);
        selectedId = settingPref.getString(getString(R.string.KEY_PLAYLISTID), "-1");
        
        setTitle("Playlists");
        
        add_bt = (Button)findViewById(R.id.addPlaylist);
        add_bt.setTextSize(18);
        add_bt.setOnClickListener(new Button.OnClickListener() {
        	@Override
        	public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MusicActivity.this, MusicPlaylistActivity.class);
                startActivity(intent);
        	}
        });

        unset_bt = (Button)findViewById(R.id.unsetPlaylist);
        unset_bt.setTextSize(18);
        unset_bt.setOnClickListener(new Button.OnClickListener() {
        	@Override
        	public void onClick(View v) {
        		if(0 <= selectedPosAfterSort && selectedPosAfterSort < playListItem.size()) {
        			String val = playListItem.get(selectedPosAfterSort).get("playlistNameHL");
        			if(!val.equals("")) {
        				playListItem.get(selectedPosAfterSort).put("playlistName", val);
        				playListItem.get(selectedPosAfterSort).put("playlistNameHL", "");
        				playListItemAdapter.notifyDataSetChanged();
            		}
        		}
        		selectedId = "-1";
        		selectedPosAfterSort = -1;
        		SharedPreferences.Editor settingPrefEdt = settingPref.edit();
        		settingPrefEdt.putString(getString(R.string.KEY_PLAYLISTID), "-1").commit();
        	}
        });
        
        ListView playlist_list = (ListView)findViewById(R.id.playlist_list);
        playlist_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        	@Override
        	public void onItemClick(AdapterView<?> parent, View item, int position, long id) {
        		HashMap<String, String> clickMap = playListItem.get(position);
        		Intent intent = new Intent(MusicActivity.this, PlaylistActivity.class);
        		Bundle b = new Bundle();
        		
        		String name = clickMap.get("playlistName").toString();
        		String pid = clickMap.get("playlistId").toString();
        		
        		b.putString("playlistName", name);
        		b.putString("playlistId", pid);
        		b.putInt("pos", position);
        		intent.putExtras(b);
        		startActivityForResult(intent, 0);
        		
        		Log.i(new Integer(position).toString(), name);
        	}
		});
        playlist_list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
        	@Override
        	public boolean onItemLongClick(AdapterView<?> parent, View item, int position, long id) {
        		ShowMagicDialog(getString(R.string.MSG_DPL), position);
        		return true;
        	}
        	// The return of onItemLongClick should be true, 否則長按會是執行 setOnItemClickListener
		});
		playListItem = new ArrayList<HashMap<String, String>>();
		playListItemAdapter = new SimpleAdapter(MusicActivity.this, playListItem,
				R.layout.playlist_item, new String[] { "playlistName", "playlistNameHL" },
				new int[] { R.id.playlistName, R.id.playlistNameHL });
		playlist_list.setAdapter(playListItemAdapter);
    }
    
	@Override
	public void onResume() {
		super.onResume();
		getPlaylists();
	}
    
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == 0 && resultCode == RESULT_OK){
			if (data.getExtras().getString("state").equals("true")) {
				selectedId = data.getExtras().getString("playlistId");
				selectedPosAfterSort = data.getExtras().getInt("pos");
        		Log.i("selectedId", selectedId);
        		Log.i("selectedPosAfterSort", new Integer(selectedPosAfterSort).toString());
			}
    	}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
    @Override
    public void onBackPressed() {
    	((TabActivity) this.getParent()).getTabHost().setCurrentTab(0);
    }
    
    private void sortPlaylist(ArrayList<String> Name, ArrayList<String> Id) {
    	HashMap<String, Integer> order = new HashMap<String, Integer>();
    	
    	for (int i = 0; i < Name.size(); ++i) {
    		String tmp = Name.get(i);
    		order.put(tmp, i);	// Assume that each Name is unique
    		Log.i(new Integer(i).toString(), tmp);
    	}
    	
    	playListItem.clear();
    	Collections.sort(Name);
    	
    	for (int i = 0; i < Name.size(); ++i) {
    		String tmp = Name.get(i);
    		int index = order.get(tmp);
    		String tid = Id.get(index);
    		
    		HashMap<String, String> playlist = new HashMap<String, String>();
    		playlist.put("playlistId", tid);
    		
    		if (!tid.equals(selectedId)) {
    			playlist.put("playlistName", tmp);
    			playlist.put("playlistNameHL", "");
    		}else {
    			playlist.put("playlistName", "");
    			playlist.put("playlistNameHL", tmp);
    			selectedPosAfterSort = i;
    		}
    		
    		playListItem.add(playlist);
    		Log.i(new Integer(i).toString(), tmp);
    	}
    }
   
    private void getPlaylists() {
    	
		String[] proj = { 
				MediaStore.Audio.Playlists._ID,
				MediaStore.Audio.Playlists.NAME };
    	playListItem.clear();
    	Cursor playlistCursor = null;
    	
    	try {
			// the uri of the table that we want to query
			Uri uri = android.provider.MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
			// we now launch the query (be sure not to do this in the UI thread
			// should it take a while)
			playlistCursor = getApplicationContext().getContentResolver().query(
					uri, proj, null, null, null);
			playlistCursor.getCount();
			
			if (playlistCursor != null) {
				
				ArrayList<String> Name = new ArrayList<String>();
				ArrayList<String> Id = new ArrayList<String>();
				
				playlistCursor.moveToFirst();
				while (!playlistCursor.isAfterLast()) {
					
					HashMap<String, String> playlist = new HashMap<String, String>();
					
					int playlist_column_index = playlistCursor.getColumnIndexOrThrow(MediaStore.Audio.Playlists.NAME);
					String nstr = playlistCursor.getString(playlist_column_index);
					if (nstr.replaceAll("\\s", "").equals("")){
						playlistCursor.moveToNext();
						continue;
					}
		    		
		    		playlist_column_index = playlistCursor.getColumnIndexOrThrow(MediaStore.Audio.Playlists._ID);
					String idstr = playlistCursor.getString(playlist_column_index);
		    		playlist.put("playlistId", idstr);
		    		Id.add(idstr);
					
					if(!selectedId.equals(idstr)) {
						playlist.put("playlistName", nstr);
						playlist.put("playlistNameHL", "");
					} else {
						playlist.put("playlistNameHL", nstr);
						playlist.put("playlistName", "");
					}
					Name.add(nstr);
		    		
		    		//playListItem.add(playlist);
					playlistCursor.moveToNext();
					
				}
				sortPlaylist(Name, Id);
	    		playListItemAdapter.notifyDataSetChanged();
			}

		} finally {
			if (playlistCursor != null) {
				playlistCursor.close();
			}
		}
    }
}
