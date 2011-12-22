package tw.edu.ntu.csie.angryrunner;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class MusicPlaylistActivity extends Activity {
	private TextView enterName;
	private EditText playlistName;
	private ListView musiclist;
	private Button confirm_bt, cancel_bt;

	private ArrayAdapter<Song> songAdapter;
	private Cursor musiccursor;
	private String[] allSongs;
	private int[] allSongId;
	private ArrayList<Song> allSongList;
	private Boolean[] checkBoxState;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.musiclist);
		
		enterName = (TextView)findViewById(R.id.enterName);
		enterName.setText("Playlist Name");
		playlistName = (EditText)findViewById(R.id.playlistName);
		musiclist = (ListView)findViewById(R.id.PhoneMusicList);
		
		allSongList = new ArrayList<Song>();
		addSongsToList();
		initCheckBoxState();
		
		songAdapter = new SongArrayAdapter(this, allSongList);
		musiclist.setAdapter(songAdapter);
		
		musiclist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View item, int position, long id) {
				Song song = songAdapter.getItem(position);
				song.toggleChecked();
				SongItemViewHolder viewHolder = (SongItemViewHolder) item.getTag();
				viewHolder.getCheckBox().setChecked(song.isChecked());
				checkBoxState[position] = song.isChecked();
				if (song.isChecked() == true) {
    				setTitle( position+": "+getFileName( allSongs[position] ) );
    			}
			}
		});
		
		confirm_bt = (Button)findViewById(R.id.confirmBT);
		confirm_bt.setOnClickListener(new Button.OnClickListener() {
        	@Override
        	public void onClick(View v) {
				MediaUtil.writePlaylist(getApplicationContext(), playlistName.getText().toString(), getSelectedIds());
        		
        		//Intent it = new Intent();
				//Bundle bun = new Bundle();
				//bun.putString("value", playlistName.getText().toString());
				//it.putExtras(bun);
				//setResult(RESULT_OK, it);
				
				finish();
        	}
        });
		
		cancel_bt = (Button)findViewById(R.id.cancelBT);
		cancel_bt.setTextSize(16);
		cancel_bt.setOnClickListener(new Button.OnClickListener() {
        	@Override
        	public void onClick(View v) {
        		finish();
        	}
        });
	}
	
	private ArrayList<Integer> getSelectedIds() {
		ArrayList<Integer> ids = new ArrayList<Integer>();
		for(int i = 0; i < checkBoxState.length; ++i) {
			if(checkBoxState[i])
				ids.add(allSongId[i]);
		}
		return ids;
	}
	
	private void initCheckBoxState() {
		checkBoxState = new Boolean[ allSongList.size() ];
		for (int i = 0; i < checkBoxState.length; ++i) {
			checkBoxState[i] = false;
		}
	}
	
	private String getFileName(String target) {
		String [] t1 = target.split("/");
		String tmp = t1[t1.length-1];
		int index = tmp.lastIndexOf(".");
		return tmp.substring(0, index);
	}
	
	private void addSongsToList() {
		String[] proj = { MediaStore.Audio.Media._ID,
				MediaStore.Audio.Media.DATA,
				MediaStore.Audio.Media.DISPLAY_NAME,
				MediaStore.Audio.Media.SIZE };

		try {
			// the uri of the table that we want to query
			Uri uri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
			// we now launch the query (be sure not to do this in the UI thread
			// should it take a while)
			String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
			musiccursor = getApplicationContext().getContentResolver().query(
					uri, proj, selection, null, null);
			
			if (musiccursor != null) {
				
				int count = musiccursor.getCount();
				int i = 0;
				allSongs = new String[count];
				allSongId = new int[count];

				musiccursor.moveToFirst();
				while (!musiccursor.isAfterLast()) {
					int music_column_index = musiccursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
					String kstr = musiccursor.getString(music_column_index);
					music_column_index = musiccursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID);

					allSongId[i] = musiccursor.getInt(music_column_index);
					allSongs[i++] = kstr;
					musiccursor.moveToNext();
				}
			}
			
			for (int i = 0; i < allSongs.length; ++i) {
				Song s = new Song(allSongs[i]);
				s.setIndex(i);
				s.setName(getFileName( allSongs[i] ));
				s.setFilePath(allSongs[i]);
				allSongList.add(s);
			}

		} finally {
			if (musiccursor != null) {
				musiccursor.close();
			}
		}
	}
	
	
	/** Custom adapter for displaying an array of Song objects. */
	private class SongArrayAdapter extends ArrayAdapter<Song> {

		private LayoutInflater inflater;

		public SongArrayAdapter(Context context, List<Song> songlist) {
			super(context, R.layout.musiclist_item, R.id.songName, songlist);
			// Cache the LayoutInflate to avoid asking for a new one each time.
			inflater = LayoutInflater.from(context);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// Song to display
			final Song theSong = (Song) this.getItem(position);
			//setPosition(position);

			// The child views in each row.
			CheckBox checkBox;
			TextView textView;

			// Create a new row view
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.musiclist_item, null);

				// Find the child views.
				textView = (TextView) convertView.findViewById(R.id.songName);
				checkBox = (CheckBox) convertView.findViewById(R.id.checkBox);

				// Optimization: Tag the row with it's child views, so we don't
				// have to
				// call findViewById() later when we reuse the row.
				convertView.setTag(new SongItemViewHolder(textView, checkBox));

				// If CheckBox is toggled, update the planet it is tagged with.
				/*
				checkBox.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						CheckBox cb = (CheckBox) v;
						Song song = (Song) cb.getTag();
						song.setChecked(cb.isChecked());
						//checkBoxState[theSong.index] = cb.isChecked();
					}
				});
				*/
			}
			// Reuse existing row view
			else {
				// Because we use a ViewHolder, we avoid having to call
				// findViewById().
				SongItemViewHolder viewHolder = (SongItemViewHolder) convertView.getTag();
				checkBox = viewHolder.getCheckBox();
				textView = viewHolder.getTextView();
			}

			// Tag the CheckBox with the Song it is displaying, so that we can
			// access the planet in onClick() when the CheckBox is toggled.
			checkBox.setTag(theSong);
			checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
	            @Override
	            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
	                theSong.setChecked(isChecked);
	                songAdapter.notifyDataSetChanged();
	    			checkBoxState[theSong.getIndex()] = isChecked;
	    			if (isChecked == true) {
	    				setTitle( theSong.getName() );
	    			}
	            }
	        });

	        checkBox.setChecked(theSong.isChecked());

			// Display planet data
			checkBox.setChecked(theSong.isChecked());
			textView.setText(theSong.getName());

			return convertView;
		}

	}
	

	private class SongItemViewHolder {

		private CheckBox checkBox;
		private TextView textView;

		public SongItemViewHolder(TextView textView, CheckBox checkBox) {
			this.checkBox = checkBox;
			this.textView = textView;
		}

		public CheckBox getCheckBox() {
			return checkBox;
		}

		public TextView getTextView() {
			return textView;
		}

	}
	
}
