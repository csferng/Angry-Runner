package tw.edu.ntu.csie.angryrunner;

import java.util.*;

import android.app.Activity;
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

public class PlaylistActivity extends Activity {

	ListView mlv;
	SimpleAdapter songListItemAdapter;
	ArrayList<HashMap<String, Object>> songListItem;

	Button use_bt, back_bt;
	int count, songlist_column_index;
	
	String playlistName, playlistId;
	int listview_pos;
	
	SharedPreferences settingPref;
	SharedPreferences.Editor settingPrefEdt;
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.aplaylist);
		
		settingPref = getSharedPreferences(
        		getString(R.string.NAME_SHAREDPREFERENCE), 
        		MODE_PRIVATE);
        settingPrefEdt = settingPref.edit();
		
		//String playlistName = savedInstanceState.getString("playlistName");
		//String playlistId = savedInstanceState.getString("playlistId");
		Bundle b = getIntent().getExtras();
		playlistName = b.getString("playlistName");
		playlistId = b.getString("playlistId");
		listview_pos = b.getInt("pos");
		
		mlv = (ListView)findViewById(R.id.theSongList);
		songListItem = new ArrayList<HashMap<String, Object>>();
		songListItemAdapter = new SimpleAdapter(PlaylistActivity.this, songListItem, 
							R.layout.asong, 
							new String[]{"songName"}, 
							new int[]{R.id.songName});
		mlv.setAdapter(songListItemAdapter);
		
		getSongsInPlaylist( getPlaylist(playlistId) );
		
		mlv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View item, int position, long id) {
				// 可以支援撥放，按了 confirm 之後通通關掉
			}
		});

		use_bt = (Button)findViewById(R.id.useBT);
		use_bt.setTextSize(16);
		use_bt.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				// 設定此清單為播放清單
				settingPrefEdt.putString(getString(R.string.KEY_PLAYLISTID), playlistId).commit();
				
				Intent it = new Intent();
				Bundle bun = new Bundle();
				bun.putString("state", "true");
				bun.putInt("pos", listview_pos);
				it.putExtras(bun);
				setResult(RESULT_OK, it);
				Log.i("p-pos", new Integer(listview_pos).toString());
				
				finish();
			}
		});
		
		back_bt = (Button)findViewById(R.id.backBT);
		back_bt.setTextSize(16);
		back_bt.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent it = new Intent();
				Bundle bun = new Bundle();
				bun.putString("state", "false");
				bun.putInt("pos", listview_pos);
				it.putExtras(bun);
				setResult(RESULT_OK, it);
				finish();
			}
		});
	}

	public void getSongsInPlaylist(Cursor songlistCursor) {
		try {
			count = songlistCursor.getCount();
			
			if (songlistCursor != null) {
				
				String kstr = "";

				songlistCursor.moveToFirst();
				while (!songlistCursor.isAfterLast()) {
					// get the 1st col in our returned data set
					// (AlbumColumns.ALBUM)
					HashMap<String, Object> playlist = new HashMap<String, Object>();
					
					songlist_column_index = songlistCursor.getColumnIndexOrThrow(MediaStore.Audio.Playlists.Members.TITLE);
					kstr = songlistCursor.getString(songlist_column_index);
		    		playlist.put("songName", kstr);
		    		
		    		songlist_column_index = songlistCursor.getColumnIndexOrThrow(MediaStore.Audio.Playlists.Members.AUDIO_ID);
					kstr = songlistCursor.getString(songlist_column_index);
		    		playlist.put("songId", kstr);
		    		
		    		songListItem.add(playlist);
					songlistCursor.moveToNext();
				}
	    		songListItemAdapter.notifyDataSetChanged();
			}

		} finally {
			if (songlistCursor != null) {
				songlistCursor.close();
			}
		}
	}

	private Cursor getPlaylist(String playlistId) {
		Cursor cursor = null;

		String[] proj = { MediaStore.Audio.Playlists._ID,
				MediaStore.Audio.Playlists.NAME };

		cursor = this.managedQuery(
				MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, proj,
				MediaStore.Audio.Playlists._ID + " = " + playlistId + "", null,
				null);
		startManagingCursor(cursor);
		cursor.moveToFirst();
		
		String playlist_id = cursor.getString(0);
		long playlist_id2 = cursor.getLong(0);

		if (playlist_id2 > 0) {
			String[] projection = {
					MediaStore.Audio.Playlists.Members.AUDIO_ID,
					MediaStore.Audio.Playlists.Members.ARTIST,
					MediaStore.Audio.Playlists.Members.TITLE,
					MediaStore.Audio.Playlists.Members._ID

			};
			cursor = null;
			cursor = this.managedQuery(MediaStore.Audio.Playlists.Members
					.getContentUri("external", playlist_id2), projection,
					MediaStore.Audio.Media.IS_MUSIC + " != 0 ", null, null);

		}
		//cManager(cursor, 2, 1);
		return cursor;
	}

}
