package tw.edu.ntu.csie.angryrunner;

import java.util.*;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaPlayer;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class MusicPlaylistActivity extends Activity {
	
	
	TextView enterName;
	EditText playlistName;
	ListView musiclist;
	Button confirm_bt, cancel_bt;

	MediaPlayer mMediaPlayer;
	ArrayAdapter<String> adapter;
	ArrayAdapter<Song> songAdapter;
	SimpleAdapter musiclistItemAdapter;
	ArrayList<HashMap<String, Object>> musiclistItem;

	Cursor musiccursor;
	String[] allSongs;
	CheckBox cb;
	
	ArrayList<Song> allSongList;
	Boolean [] checkBoxState;

	int music_column_index;
	int count;

	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.musiclist);
		
		
		enterName = (TextView)findViewById(R.id.enterName);
		enterName.setText("Playlist Name");
		
		playlistName = (EditText)findViewById(R.id.playlistName);
		
		
		musiclist = (ListView)findViewById(R.id.PhoneMusicList);
		//adapter = new ArrayAdapter<String>(this, R.layout.musiclist_item, R.id.songName);
		//songAdapter = new ArrayAdapter<Song>(this, R.layout.musiclist_item, R.id.songName);
		
		String[] proj = { MediaStore.Audio.Media._ID,
				MediaStore.Audio.Media.DATA,
				MediaStore.Audio.Media.DISPLAY_NAME,
				MediaStore.Video.Media.SIZE };

		allSongList = new ArrayList<Song>();
		addSongsToList(proj);
		initCheckBoxState();
		
		//songAdapter = new InteractiveArrayAdapter(this, allSongList);
		songAdapter = new SongArrayAdapter(this, allSongList);
		musiclist.setAdapter(songAdapter);
		
		musiclist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View item, int position, long id) {
				Song song = songAdapter.getItem(position);
				song.toggleChecked();
				SongItemViewHolder viewHolder = (SongItemViewHolder) item.getTag();
				viewHolder.getCheckBox().setChecked(song.isChecked());
			}
		});
		
		
		confirm_bt = (Button)findViewById(R.id.confirmBT);
		confirm_bt.setTextSize(16);
		confirm_bt.setOnClickListener(new Button.OnClickListener() {
        	@Override
        	public void onClick(View v) {
        		String target = "";
        		int counter = 0;
        		for (int i = 0; i < checkBoxState.length; ++i) {
        			if (checkBoxState[i] == true) {
        				++counter;
        				target += new Integer(i).toString();
        				if (i != checkBoxState.length-1) {
        					target += ", ";
        				}
        			}
        		}
        		//setTitle(counter+" songs are checked.");
        		setTitle(counter+": song "+target+" are checked.");
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
		
		//init_phone_music_grid();
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
	
	private void addSongsToList(String[] proj) {
		try {
			// the uri of the table that we want to query
			Uri uri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
			// we now launch the query (be sure not to do this in the UI thread
			// should it take a while)
			musiccursor = getApplicationContext().getContentResolver().query(
					uri, proj, null, null, null);
			count = musiccursor.getCount();
			
			if (musiccursor != null) {
				
				int i = 0;
				allSongs = new String[count];

				musiccursor.moveToFirst();
				while (!musiccursor.isAfterLast()) {
					// get the 1st col in our returned data set
					// (AlbumColumns.ALBUM)
					//String kstr = musiccursor.getString(0);
					music_column_index = musiccursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
					String kstr = getFileName( musiccursor.getString(music_column_index) );

					allSongs[i++] = kstr;
					musiccursor.moveToNext();
				}
				//musiclistItemAdapter.notifyDataSetChanged();
				//musiclist.setAdapter(musiclistItemAdapter);
				//musiclist.setAdapter(adapter);
			}
			
			for (int i = 0; i < allSongs.length; ++i) {
				allSongList.add(new Song(allSongs[i]));
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
		private int pos = -1;
		
		public void setPosition(int position) {
			this.pos = position;
		}

		public SongArrayAdapter(Context context, List<Song> songlist) {
			super(context, R.layout.musiclist_item, R.id.songName, songlist);
			// Cache the LayoutInflate to avoid asking for a new one each time.
			inflater = LayoutInflater.from(context);
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			// Song to display
			Song planet = (Song) this.getItem(position);
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
				checkBox.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						CheckBox cb = (CheckBox) v;
						Song song = (Song) cb.getTag();
						song.setChecked(cb.isChecked());
						checkBoxState[position] = cb.isChecked();
					}
				});
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
			checkBox.setTag(planet);

			// Display planet data
			checkBox.setChecked(planet.isChecked());
			textView.setText(planet.getName());

			return convertView;
		}

	}
	

	private class SongItemViewHolder {

		private CheckBox checkBox;
		private TextView textView;

		public SongItemViewHolder() {
		}

		public SongItemViewHolder(TextView textView, CheckBox checkBox) {
			this.checkBox = checkBox;
			this.textView = textView;
		}

		public CheckBox getCheckBox() {
			return checkBox;
		}

		public void setCheckBox(CheckBox checkBox) {
			this.checkBox = checkBox;
		}

		public TextView getTextView() {
			return textView;
		}

		public void setTextView(TextView textView) {
			this.textView = textView;
		}

	}
	
}

