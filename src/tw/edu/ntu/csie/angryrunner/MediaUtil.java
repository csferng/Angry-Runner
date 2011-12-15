package tw.edu.ntu.csie.angryrunner;

import java.util.ArrayList;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

// Large parts of this file are cribbed from MusicUtil.java in the android music player.

public class MediaUtil {
	public static final String TAG = "MediaUtil";

	public static void deleteTrack(Context context, String localPath) {
		Uri uri = MediaStore.Audio.Media.getContentUriForPath(localPath);
		context.getContentResolver().delete(uri, null, null);
	}

	public static Cursor query(Context context, Uri uri, String[] projection,
			String selection, String[] selectionArgs, String sortOrder,
			int limit) {
		try {
			ContentResolver resolver = context.getContentResolver();
			if (resolver == null) {
				return null;
			}
			if (limit > 0) {
				uri = uri.buildUpon().appendQueryParameter("limit", "" + limit)
						.build();
			}
			return resolver.query(uri, projection, selection, selectionArgs,
					sortOrder);
		} catch (UnsupportedOperationException ex) {
			return null;
		}
	}

	public static Cursor query(Context context, Uri uri, String[] projection,
			String selection, String[] selectionArgs, String sortOrder) {
		return query(context, uri, projection, selection, selectionArgs,
				sortOrder, 0);
	}

	private static int intFromCursor(Cursor c) {
		int id = -1;
		if (c != null) {
			c.moveToFirst();
			if (!c.isAfterLast()) {
				id = c.getInt(0);
			}
		}
		c.close();
		return id;
	}

	public static int idForplaylist(Context context, String name) {
		Cursor c = query(context,
				MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
				new String[] { MediaStore.Audio.Playlists._ID },
				MediaStore.Audio.Playlists.NAME + "=?", new String[] { name },
				MediaStore.Audio.Playlists.NAME);
		return intFromCursor(c);
	}

	public static int idFortrack(Context context, String path) {
		Cursor c = query(context,
				MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
				new String[] { MediaStore.Audio.Media._ID },
				MediaStore.Audio.Media.DATA + "=?", new String[] { path },
				MediaStore.Audio.Media.DATA);
		return intFromCursor(c);
	}

	public static void writePlaylist(Context context, String playlistName,
			ArrayList<String> paths) {
		ContentResolver resolver = context.getContentResolver();
		int playlistId = idForplaylist(context, playlistName);
		Uri uri;
		if (playlistId == -1) {
			ContentValues values = new ContentValues(1);
			values.put(MediaStore.Audio.Playlists.NAME, playlistName);
			uri = resolver.insert(
					MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, values);
			playlistId = idForplaylist(context, playlistName);
		} else {
			uri = MediaStore.Audio.Playlists.Members.getContentUri("external",
					playlistId);
		}
		Log.d(TAG, String.format("Writing playlist %s", uri));

		// Delete everything from the old playlist
		context.getContentResolver().delete(uri, null, null);

		// Add all the new tracks to the playlist.
		int size = paths.size();
		ContentValues values[] = new ContentValues[size];
		for (int k = 0; k < size; ++k) {
			values[k] = new ContentValues();
			values[k].put(MediaStore.Audio.Playlists.Members.PLAY_ORDER, k);
			values[k].put(MediaStore.Audio.Playlists.Members.AUDIO_ID,
					idFortrack(context, paths.get(k)));
		}

		resolver.bulkInsert(uri, values);
	}
}