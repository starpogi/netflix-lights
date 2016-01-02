package onglao.blackstorm;

import java.io.IOException;

import onglao.blackstorm.NotificationListener.PlayStates;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity implements
		NavigationDrawerFragment.NavigationDrawerCallbacks {

	/*
	 * Notification Listeners
	 */
	private TextView txtView;
    private NotificationReceiver nReceiver;
    private LightReceiver lReceiver;
    private String TAG = this.getClass().getSimpleName();
    private final static String LIGHT_INTENT_STRING = "onglao.blackstorm.LIGHT_API_RECIEVER";
    
	public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

	private OkHttpClient client = new OkHttpClient();
    
	/**
	 * Fragment managing the behaviors, interactions and presentation of the
	 * navigation drawer.
	 */
	private NavigationDrawerFragment mNavigationDrawerFragment;

	/**
	 * Used to store the last screen title. For use in
	 * {@link #restoreActionBar()}.
	 */
	private CharSequence mTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
        txtView = (TextView) findViewById(R.id.textView1);
        nReceiver = new NotificationReceiver();
        IntentFilter nfilter = new IntentFilter();
        nfilter.addAction("onglao.blackstorm.NOTIFICATION_LISTENER");
        registerReceiver(nReceiver, nfilter);
//        
//        lReceiver = new LightReceiver();
//        IntentFilter lfilter = new IntentFilter();
//        lfilter.addAction(LIGHT_INTENT_STRING);
//        registerReceiver(lReceiver, lfilter);

		mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager()
				.findFragmentById(R.id.navigation_drawer);
		mTitle = getTitle();

		// Set up the drawer.
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout));
	}
	
	@Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(nReceiver);
    }

	@Override
	public void onNavigationDrawerItemSelected(int position) {
		// update the main content by replacing fragments
		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager
				.beginTransaction()
				.replace(R.id.container,
						PlaceholderFragment.newInstance(position + 1)).commit();
		Log.i(TAG, " " + position);
	}

	public void onSectionAttached(int number) {
		switch (number) {
		case 1:
			mTitle = getString(R.string.title_section1);
			break;
		case 2:
			mTitle = getString(R.string.title_section2);
			break;
		case 3:
			mTitle = getString(R.string.title_section3);
			break;
		}
	}

	public void restoreActionBar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mTitle);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!mNavigationDrawerFragment.isDrawerOpen()) {
			// Only show items in the action bar relevant to this screen
			// if the drawer is not showing. Otherwise, let the drawer
			// decide what to show in the action bar.
			getMenuInflater().inflate(R.menu.main, menu);
			restoreActionBar();
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		if (item.getItemId() == R.id.action_example) {
			
//			item.setEnabled(false);
			
			try {
				get_url("http://192.168.1.74:5000/off");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void get_url(String url) throws Exception {
	    Request request = new Request.Builder()
	        .url(url)
	        .build();
	    
	    client.newCall(request).enqueue(new Callback() {
	      @Override 
	      public void onFailure(Request request, IOException throwable) {
	    	  Intent intent = new Intent(LIGHT_INTENT_STRING);
	    	  intent.putExtra("success", false);
	    	  intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    	  sendBroadcast(intent);
	      }

	      @Override 
	      public void onResponse(Response response) throws IOException {
	    	  Intent intent = new Intent(LIGHT_INTENT_STRING);
	    	  
	    	  if (!response.isSuccessful()) {
	    		  intent.putExtra("success", false);
	    	  }

	    	  Headers responseHeaders = response.headers();

	    	  Log.i(TAG, "************* RESPONSE: " + response.body().string());
	    	  
	    	  intent.putExtra("success", true);
	    	  intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    	  sendBroadcast(intent);
	      }
	    });
	  }

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";

		/**
		 * Returns a new instance of this fragment for the given section number.
		 */
		public static PlaceholderFragment newInstance(int sectionNumber) {
			PlaceholderFragment fragment = new PlaceholderFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}

		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			((MainActivity) activity).onSectionAttached(getArguments().getInt(
					ARG_SECTION_NUMBER));
		}
	}

    private class NotificationReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String temp = intent.getStringExtra("notification_event") + "\n" + txtView.getText();
            txtView.setText(temp);
            
            String event = intent.getStringExtra("notification_event");
            
            Log.i(TAG, "*********************** EVENT: " + event);
            
            try {
            	if(event.equals(PlayStates.PAUSED))
            		get_url("http://192.168.1.74:5000/on");
            	else 
            		get_url("http://192.168.1.74:5000/off");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    }
    
    private class LightReceiver extends BroadcastReceiver{
    	
        @Override
        public void onReceive(Context context, Intent intent) {
        	Boolean is_success = intent.getBooleanExtra("success", false);
        	
        	if(is_success) {				
//				invalidateOptionsMenu();
        	}
        }
    }

}
