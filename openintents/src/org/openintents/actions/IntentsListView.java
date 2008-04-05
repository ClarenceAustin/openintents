package org.openintents.actions;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.openintents.OpenIntents;
import org.openintents.R;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class IntentsListView extends Activity {

	private static final String LOG_TAG = "intentsListView";

    LayoutParams params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT);

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.intents_list);
		LinearLayout view = (LinearLayout) findViewById(R.id.intents_list_container);
		int flags = PackageManager.GET_RESOLVED_FILTER;

		Intent intent = new Intent();
		intent.setDataAndType(null, "*/*");

		ArrayList<String> list = new ArrayList<String>();
		HashMap<String, ArrayList<IntentFilter>> map = new HashMap<String, ArrayList<IntentFilter>>();
		for (Field f : Intent.class.getFields()) {

			if (f.getName().endsWith("ACTION")) {
				String action = null;
				try {
					action = (String) f.get(Intent.class);
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
				
				if (action != null) {
					intent.setAction(action);
					List<ResolveInfo> activities = this.getPackageManager()
							.queryIntentActivities(intent, flags);
					//Log.i(LOG_TAG, intent + ":" + activities.size());

					resolveListToMap(intent, activities, map);
				}
			}
		}

		intent.setAction(OpenIntents.TAG_ACTION);
		List<ResolveInfo> activities = this.getPackageManager()
				.queryIntentActivities(intent, flags);
		//Log.i(LOG_TAG, intent + ":" + activities.size());

		resolveListToMap(intent, activities, map);

		
		for (Entry<String, ArrayList<IntentFilter>> e : map.entrySet()) {
			StringBuffer sb = new StringBuffer();
			HashSet<String> actions = new HashSet<String>();
			for (IntentFilter i : e.getValue()) {
				if (i.actionsIterator() != null) {
					for (Iterator<String> i2 = i.actionsIterator(); i2
							.hasNext();) {
						String action = i2.next();
						if (!actions.contains(action)) {
							sb.append(action + " ");
							actions.add(action);
						}
					}
				} else {
					Log.w(LOG_TAG, " no actions for " + i);
				}
			}
			
			
	        TextView t1 = new TextView(this);
	        Typeface tf = Typeface.defaultFromStyle(Typeface.BOLD);
			t1.setTypeface(tf );
	        t1.setText(e.getKey());
	        view.addView(t1, params); 
	        
	        t1 = new TextView(this);
	        t1.setText(sb.toString());
	        view.addView(t1, params); 
		}		
	}

	private void resolveListToMap(Intent intent, List<ResolveInfo> activities,
			HashMap<String, ArrayList<IntentFilter>> map) {

		for (ResolveInfo ri : activities) {
			StringBuffer fi = new StringBuffer();
			if (ri.filter != null) {
				Iterator<String> i = ri.filter.typesIterator();
				if (i != null) {
					while (i.hasNext()) {
						String type = i.next();
						fi.append(type);
						ArrayList<IntentFilter> set = map.get(type);
						if (set == null) {
							set = new ArrayList<IntentFilter>();
							map.put(type, set);
						}						
						set.add(ri.filter);
						//Log.i("test", type + ": " + intent);

					}
				}
			}			
		}
	}

}
