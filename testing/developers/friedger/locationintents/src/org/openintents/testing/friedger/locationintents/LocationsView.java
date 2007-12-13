package org.openintents.testing.friedger.locationintents;

import java.net.URISyntaxException;

import org.openintents.R;
import org.openintents.provider.Location.Locations;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.ContentURI;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.Menu.Item;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.AdapterView.OnItemClickListener;

public class LocationsView extends Activity implements OnItemClickListener {

	private static final int MENU_ADD_CURRENT_LOCATION = 1;
	private static final int MENU_VIEW = 2;
	private static final int MENU_TAG = 3;

	/** tag for logging */
	private static final String TAG = "locationsView";
	private static final String TAG_ACTION = "TAG";

	private ListView mList;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.main);

		mList = (ListView) findViewById(R.id.locations);

		mList.setOnItemClickListener(this);
		fillData();

	}

	private void fillData() {

		// Get a cursor with all location
		Cursor c = getContentResolver().query(
				Locations.CONTENT_URI,
				new String[] { Locations._ID, Locations.LATITUDE,
						Locations.LONGITUDE }, null, null,
				Locations.DEFAULT_SORT_ORDER);
		startManagingCursor(c);

		ListAdapter adapter = new SimpleCursorAdapter(this,
		// Use a template that displays a text view
				R.layout.location_row,
				// Give the cursor to the list adapter
				c,
				// Map the NAME column in the people database to...
				new String[] { Locations.LATITUDE, Locations.LONGITUDE },
				// The "text1" view defined in the XML template
				new int[] { R.id.latitude, R.id.longitude });
		mList.setAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		menu.add(0, MENU_ADD_CURRENT_LOCATION, R.string.add_current_location);
		menu.add(0, MENU_VIEW, R.string.view_location);
		menu.add(0, MENU_TAG, R.string.tag_location);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(Item item) {
		super.onOptionsItemSelected(item);
		switch (item.getId()) {
		case MENU_ADD_CURRENT_LOCATION:
			addCurrentLocation();
			break;
		case MENU_VIEW:
			Cursor cursor = (Cursor) mList.getSelectedItem();
			if (cursor != null) {
				viewLocation(cursor);
			}
			break;
		case MENU_TAG:
			long id = mList.getSelectedItemId();
			if (id >= 0) {
				tagLocation(id);
			}
			break;
		}
		return true;
	}

	private void tagLocation(long id) {
		ContentURI uri = Locations.CONTENT_URI;
		uri.addId(id);
		Intent intent = new Intent(TAG_ACTION, uri);
		startActivity(intent);		
	}

	private void viewLocation(Cursor cursor) {
		String latitude = cursor.getString(cursor
				.getColumnIndex(Locations.LATITUDE));
		String longitude = cursor.getString(cursor
				.getColumnIndex(Locations.LONGITUDE));
		ContentURI uri;
		try {
			uri = new ContentURI("geo:" + latitude + "," + longitude);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			uri = null;
		}

		if (uri != null) {
			Intent intent = new Intent(Intent.VIEW_ACTION, uri);
			startActivity(intent);
		}

	}

	private void addCurrentLocation() {
		Location location = getCurrentLocation();

		ContentValues values = new ContentValues(2);
		values.put(Locations.LATITUDE, String.valueOf(location.getLatitude()));
		values
				.put(Locations.LONGITUDE, String.valueOf(location
						.getLongitude()));
		getContentResolver().insert(Locations.CONTENT_URI, values);

		fillData();

	}

	private Location getCurrentLocation() {
		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		LocationProvider locationProvider = locationManager.getProviders().get(
				0);
		Location location = locationManager.getCurrentLocation(locationProvider
				.getName());
		return location;
	}

	public void onItemClick(AdapterView l, View v, int position, long id) {
		// do nothing

	}

}