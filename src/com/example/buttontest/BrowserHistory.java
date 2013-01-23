package com.example.buttontest;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import android.text.format.Time;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.Browser;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;



public class BrowserHistory extends Activity {
	ProgressDialog progressDialogInbox;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		progressDialogInbox = new ProgressDialog(this);
		new FetchBrowserHistory().execute();
		setContentView(R.layout.activity_browser_history);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_browser_history, menu);
		return true;
	}
	
    public abstract class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "browserhist_entry";
        public static final String COLUMN_NAME_ENTRY_ID = "entryid";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_URL = "url";
        
        // Prevents the FeedReaderContract class from being instantiated.
        private FeedEntry() {}	
    }

    
    public class FeedReaderDbHelper extends SQLiteOpenHelper {
        // If you change the database schema, you must increment the database version.
    	
    	private static final String TEXT_TYPE = " TEXT";
    	private static final String COMMA_SEP = ",";
    	private static final String SQL_CREATE_ENTRIES =
    	    "CREATE TABLE " + FeedEntry.TABLE_NAME + " (" +
    	    FeedEntry.COLUMN_NAME_ENTRY_ID + " INTEGER PRIMARY KEY autoincrement," +
    	    FeedEntry.COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
    	    FeedEntry.COLUMN_NAME_DATE + TEXT_TYPE + COMMA_SEP +
    	    FeedEntry.COLUMN_NAME_URL + TEXT_TYPE +
    	    " );";

    	private static final String SQL_DELETE_ENTRIES =
    	    "DROP TABLE IF EXISTS " + FeedEntry.TABLE_NAME;
    	
        public static final int DATABASE_VERSION = 2;
        public static final String DATABASE_NAME = "FeedReader.db";

        public FeedReaderDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        public void onCreate(SQLiteDatabase db) {
        	db.execSQL(SQL_DELETE_ENTRIES);
            db.execSQL(SQL_CREATE_ENTRIES);
        }
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // This database is only a cache for online data, so its upgrade policy is
            // to simply to discard the data and start over
            db.execSQL(SQL_DELETE_ENTRIES);
            onCreate(db);
        }
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }
    } 
    
    public HashMap<String, String> fetchBrowHist()
    {     
        HashMap<String, String> bh = new HashMap<String, String>();
    	
    	//Uri uribh = Uri.parse(Browser.BOOKMARKS_URI);
    	Cursor cursor = getContentResolver().query(Browser.BOOKMARKS_URI, Browser.HISTORY_PROJECTION,null,null,null); 

    	cursor.moveToFirst();
        if (cursor.moveToFirst() && cursor.getCount() > 0) {
        	
        	FeedReaderDbHelper mDbHelper = new FeedReaderDbHelper(getBaseContext());

        	// Gets the data repository in write mode
        	SQLiteDatabase db = mDbHelper.getWritableDatabase();
        	
        	Log.e("db.delete db.delete db.delete db.delete", "11111111111111111111111");
        	db.delete(FeedEntry.TABLE_NAME, null, null);  
        	Log.e("db.delete db.delete db.delete db.delete", "22222222222222222222222");
        	
        	int index = 1;
        	
            while (cursor.isAfterLast() == false) { 
                Log.d("visit_index", cursor.getString(Browser.HISTORY_PROJECTION_VISITS_INDEX));
                if(Integer.parseInt(cursor.getString(Browser.HISTORY_PROJECTION_VISITS_INDEX)) != 0){
                	
	            	//bh.add("idIdx: " + cursor.getString(Browser.HISTORY_PROJECTION_ID_INDEX)+
	            			//"\n visitsIdx: "+cursor.getString(Browser.HISTORY_PROJECTION_VISITS_INDEX)+
	            			//"\n dateIdx: "+cursor.getString(Browser.HISTORY_PROJECTION_DATE_INDEX)+
	            			//"\n titleIdx: "+cursor.getString(Browser.HISTORY_PROJECTION_TITLE_INDEX)+
	            			//"\n urlIdx: "+cursor.getString(Browser.HISTORY_PROJECTION_URL_INDEX));
	            	 
	            	// Create a new map of values, where column names are the keys
	            	ContentValues values = new ContentValues();
	            	values.put(FeedEntry.COLUMN_NAME_ENTRY_ID, index);
	            	values.put(FeedEntry.COLUMN_NAME_TITLE, cursor.getString(Browser.HISTORY_PROJECTION_TITLE_INDEX));
	            	
	            	Time t = new Time();
	            	t.set(Long.parseLong(cursor.getString(Browser.HISTORY_PROJECTION_DATE_INDEX), 10));
	            	Log.e("BrowserHist Time", t.toString());
	            	
	    	    	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
	    	    	Calendar calendar = Calendar.getInstance();
	    	    	Long date_ms = Long.parseLong(cursor.getString(Browser.HISTORY_PROJECTION_DATE_INDEX), 10);
	    	    	calendar.setTimeInMillis(date_ms);
	            	
	            	
	            	values.put(FeedEntry.COLUMN_NAME_DATE, formatter.format(calendar.getTime()));
	            	values.put(FeedEntry.COLUMN_NAME_URL, cursor.getString(Browser.HISTORY_PROJECTION_URL_INDEX));
            	
            	
	            	// Insert the new row, returning the primary key value of the new row
	            	long newRowId;
	            	newRowId = db.insert(
	            	         FeedEntry.TABLE_NAME,
	            	         null,
	            	         values);
	            	
	     		    Log.d("newRowId", String.valueOf(newRowId));
	     		    index++;
            	}
            	cursor.moveToNext();
           }
        }
        
        cursor.close();
        
        FeedReaderDbHelper mDbHelper = new FeedReaderDbHelper(getBaseContext());
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

	     // Define a projection that specifies which columns from the database
	     // you will actually use after this query.
	     String[] projection = {
	         FeedEntry.COLUMN_NAME_ENTRY_ID,
	         FeedEntry.COLUMN_NAME_TITLE,
	         FeedEntry.COLUMN_NAME_DATE,
	         FeedEntry.COLUMN_NAME_URL
	         };
	
	     Cursor c = db.query(
	         FeedEntry.TABLE_NAME,  // The table to query
	         projection,                               // The columns to return
	         null,                                // The columns for the WHERE clause
	         null,                            // The values for the WHERE clause
	         null,                                     // don't group the rows
	         null,                                     // don't filter by row groups
	         null                                 // The sort order
	         );
	     
	     c.moveToFirst();
	     while (!c.isAfterLast()) {
         	bh.put("ID: "+c.getLong(0),
        			"Title: "+c.getString(1)+
        			"\nDate: "+c.getString(2)+
        			"\nURL: "+c.getString(3));
         	
	        c.moveToNext();
	     }
	     // Make sure to close the cursor
	     c.close();
	     
    	 return bh;  	
    }
    
    public static abstract class Details {
        public static ArrayList<String> DETAILS = new ArrayList<String>();
        
        // Prevents the FeedReaderContract class from being instantiated.
        private Details() {}
    }

	private class FetchBrowserHistory extends AsyncTask<Void, Void, HashMap<String, String>> {
		@Override
		protected HashMap<String, String> doInBackground(Void... params) {
			// TODO Auto-generated method stub
			return fetchBrowHist();
		}
		
		protected void onPreExecute() {
			// update the UI immediately after the task is executed
			super.onPreExecute();
			
	        // Create a progress bar to display while the list loads   
	        progressDialogInbox.setMessage("Fetching...");
			progressDialogInbox.setIndeterminate(true);
			progressDialogInbox.setCancelable(true);
			progressDialogInbox.show();
		}
		
		@Override
        protected void onPostExecute(HashMap<String, String> result) {
			super.onPostExecute(result);
			
        	progressDialogInbox.dismiss();
        	
        	TitlesFragment tf = (TitlesFragment) getFragmentManager().findFragmentById(R.id.titles);
        	
        	ArrayList<String> titles = new ArrayList<String>();
        	
        	titles.addAll(result.keySet());
        	
        	Collections.sort(titles, new Comparator<String>() {
        	    public int compare(String a, String b) {
        	        return (fixString(a) - fixString(b));
        	    }
        	    private int fixString(String in) {
        	        return Integer.parseInt(in.substring(in.indexOf(' ')+1, in.length()));
        	    }
        	});  
        	
        	for (String key : titles){
        		Details.DETAILS.add(result.get(key));
        	}
        	
            // Populate list with our static array of titles.
        	tf.setListAdapter(new ArrayAdapter<String>(tf.getActivity(),
                    android.R.layout.simple_list_item_activated_1, titles));

            // Check to see if we have a frame in which to embed the details
            // fragment directly in the containing UI.
            View detailsFrame = tf.getActivity().findViewById(R.id.details);
            tf.mDualPane = detailsFrame != null && detailsFrame.getVisibility() == View.VISIBLE;
            
            if (tf.mDualPane) {
                // In dual-pane mode, the list view highlights the selected item.
            	tf.getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                // Make sure our UI is in the correct state.
            	tf.showDetails(tf.mCurCheckPosition);
            }
        }
    }

    
    /**
     * This is a secondary activity, to show what the user has selected
     * when the screen is not large enough to show it all in one activity.
     */

    public static class DetailsActivity extends Activity {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            if (getResources().getConfiguration().orientation
                    == Configuration.ORIENTATION_LANDSCAPE) {
                // If the screen is now in landscape mode, we can show the
                // dialog in-line with the list so we don't need this activity.
                finish();
                return;
            }

            if (savedInstanceState == null) {
                // During initial setup, plug in the details fragment.
                DetailsFragment details = new DetailsFragment();
                details.setArguments(getIntent().getExtras());
                getFragmentManager().beginTransaction().add(android.R.id.content, details).commit();
            }
        }
    }


    /**
     * This is the "top-level" fragment, showing a list of items that the
     * user can pick.  Upon picking an item, it takes care of displaying the
     * data to the user as appropriate based on the currrent UI layout.
     */

    public static class TitlesFragment extends ListFragment {
        boolean mDualPane;
        int mCurCheckPosition = 0;

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            
            if (savedInstanceState != null) {
                // Restore last state for checked position.
                mCurCheckPosition = savedInstanceState.getInt("curChoice", 0);
            }
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            outState.putInt("curChoice", mCurCheckPosition);
        }

        @Override
        public void onListItemClick(ListView l, View v, int position, long id) {
            showDetails(position);
        }

        /**
         * Helper function to show the details of a selected item, either by
         * displaying a fragment in-place in the current UI, or starting a
         * whole new activity in which it is displayed.
         */
        void showDetails(int index) {
            mCurCheckPosition = index;

            if (mDualPane) {
                // We can display everything in-place with fragments, so update
                // the list to highlight the selected item and show the data.
                getListView().setItemChecked(index, true);

                // Check what fragment is currently shown, replace if needed.
                DetailsFragment details = (DetailsFragment)
                        getFragmentManager().findFragmentById(R.id.details);
                if (details == null || details.getShownIndex() != index) {
                    // Make new fragment to show this selection.
                    details = DetailsFragment.newInstance(index);

                    // Execute a transaction, replacing any existing fragment
                    // with this one inside the frame.
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.details, details);
                    
                    /*
                    if (index == 0) {
                        ft.replace(R.id.details, details);
                    } else {
                        ft.replace(R.id.a_item, details);
                    }
                    */
                    
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    ft.commit();
                }

            } else {
                // Otherwise we need to launch a new activity to display
                // the dialog fragment with selected text.
                Intent intent = new Intent();
                intent.setClass(getActivity(), DetailsActivity.class);
                intent.putExtra("index", index);
                startActivity(intent);
            }
        }
    }


    /**
     * This is the secondary fragment, displaying the details of a particular
     * item.
     */

    public static class DetailsFragment extends Fragment {
        /**
         * Create a new instance of DetailsFragment, initialized to
         * show the text at 'index'.
         */
        public static DetailsFragment newInstance(int index) {
            DetailsFragment f = new DetailsFragment();

            // Supply index input as an argument.
            Bundle args = new Bundle();
            args.putInt("index", index);
            f.setArguments(args);

            return f;
        }

        public int getShownIndex() {
            return getArguments().getInt("index", 0);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            if (container == null) {
                // We have different layouts, and in one of them this
                // fragment's containing frame doesn't exist.  The fragment
                // may still be created from its saved state, but there is
                // no reason to try to create its view hierarchy because it
                // won't be displayed.  Note this is not needed -- we could
                // just run the code below, where we would create and return
                // the view hierarchy; it would just never be used.
                return null;
            }

            ScrollView scroller = new ScrollView(getActivity());
            TextView text = new TextView(getActivity());
            int padding = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    4, getActivity().getResources().getDisplayMetrics());
            text.setPadding(padding, padding, padding, padding);
            scroller.addView(text);
            //text.setText(Shakespeare.DIALOGUE[getShownIndex()]);
            text.setText(Details.DETAILS.get(getShownIndex()));
            return scroller;
        }
    }

}
