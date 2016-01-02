package onglao.blackstorm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class NotificationListener extends NotificationListenerService {
	private String TAG = this.getClass().getSimpleName();
	private String NETFLIX_FILTER = "com.netflix.mediaclient";

	private NotificationListenerReceiver nlReceiver;
	private CharSequence past_state = PlayStates.PAUSED;
	
	@Override
    public void onCreate() {
        super.onCreate();
        nlReceiver = new NotificationListenerReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("onglao.blackstorm.NOTIFICATION_LISTENER_SERVICE");
        registerReceiver(nlReceiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(nlReceiver);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {

        Log.i(TAG,"**********  onNotificationPosted");
        Log.i(TAG,"ID :" + sbn.getId() + "\t" + sbn.getNotification().tickerText + "\t" + sbn.getPackageName());
        
        if(sbn.getPackageName().equals(NETFLIX_FILTER))
        {
        	String current_state = getPlayState(sbn);
        	
        	if(current_state != past_state)
        	{
	            Intent i = new Intent("onglao.blackstorm.NOTIFICATION_LISTENER");
	            i.putExtra("notification_event", current_state);
	            sendBroadcast(i);
	            past_state = current_state;
        	}
        }

    }
    
    public String getPlayState(StatusBarNotification sbn) {

		int size = NotificationCompat.getActionCount(sbn.getNotification());
		
		for (int i = 0; i < size; i++) {
			CharSequence action_title = NotificationCompat.getAction(sbn.getNotification(), i).title;
			
			if(action_title.equals(PlayStates.PLAYING)) {
				return PlayStates.PLAYING;
			}
			else if(action_title.equals(PlayStates.PAUSED)) {
				return PlayStates.PAUSED;
			}
		}
		
		return PlayStates.STOPPED;

//    	Bundle extras = NotificationCompat.getExtras(sbn.getNotification());
//		CharSequence chars = extras.getCharSequence(Notification.EXTRA_TEXT);
		
//		if(!TextUtils.isEmpty(chars))
//		   return chars.toString();
//		else if(!TextUtils.isEmpty((chars = extras.getString(Notification.EXTRA_SUMMARY_TEXT))))
//		   return chars.toString();
//		else
//		   return "";
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.i(TAG,"********** onNotificationRemoved");
        Log.i(TAG,"ID :" + sbn.getId() + "\t" + sbn.getNotification().tickerText +"\t" + sbn.getPackageName());
        
        if(sbn.getPackageName().equals(NETFLIX_FILTER)) {
        	
        	if(past_state != PlayStates.PAUSED)
        	{
	        	Intent i = new  Intent("onglao.blackstorm.NOTIFICATION_LISTENER");
	            i.putExtra("notification_event", PlayStates.PAUSED);
	            sendBroadcast(i);	 

	        	past_state = PlayStates.PAUSED;
        	}
        }
//        
//        Intent i = new  Intent("onglao.blackstorm.NOTIFICATION_LISTENER");
//        i.putExtra("notification_event","onNotificationRemoved :" + sbn.getPackageName() + "\n");
//        sendBroadcast(i);
    }
	
	class NotificationListenerReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
        	
            if(intent.getStringExtra("command").equals("clearall")){
            	NotificationListener.this.cancelAllNotifications();
            }
            else if(intent.getStringExtra("command").equals("list")){
                Intent i1 = new Intent("onglao.blackstorm.NOTIFICATION_LISTENER");
                i1.putExtra("notification_event","=====================");
                sendBroadcast(i1);
                int i=1;
                for (StatusBarNotification sbn : NotificationListener.this.getActiveNotifications()) {
                    Intent i2 = new Intent("onglao.blackstorm.NOTIFICATION_LISTENER");
                    i2.putExtra("notification_event", i +" " + sbn.getPackageName() + "\n");
                    sendBroadcast(i2);
                    i++;
                }
                Intent i3 = new Intent("onglao.blackstorm.NOTIFICATION_LISTENER");
                i3.putExtra("notification_event","===== Notification List ====");
                sendBroadcast(i3);

            }

        }
    }
	
	public class PlayStates {
		public static final String PLAYING = "Play";
		public static final String PAUSED = "Pause";
		public static final String STOPPED = "Stop";
	}
}
