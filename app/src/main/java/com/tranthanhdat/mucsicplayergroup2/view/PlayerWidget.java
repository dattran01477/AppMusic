package com.tranthanhdat.mucsicplayergroup2.view;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.IdRes;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.tranthanhdat.mucsicplayergroup2.R;
import com.tranthanhdat.mucsicplayergroup2.service.PlayerService;

/**
 * Implementation of App Widget functionality.
 */
public class PlayerWidget extends AppWidgetProvider {

    private static final String TAG = "Music Widget";
    public static final String ACTION_PLAY_PAUSE = "com.smartpocket.musicwidget.play_pause";
    public static final String UPDATE_WIDGET = "UPDATE_WIDGET";
    public static final String ACTION_STOP = "com.smartpocket.musicwidget.stop";
    public static final String ACTION_NEXT = "com.smartpocket.musicwidget.next";
    public static final String ACTION_PREVIOUS = "com.smartpocket.musicwidget.previous";
    public static final String ACTION_SHUFFLE = "com.smartpocket.musicwidget.shuffle";
    public static final String ACTION_JUMP_TO = "com.smartpocket.musicwidget.jump_to";

    private RemoteViews remoteViews;

    private BroadcastReceiver receiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(PlayerService.GUI_UPDATE_ACTION)) {
                if (intent.hasExtra(PlayerService.TITLE_EXTRA)) {
                    String titleTmp = intent.getStringExtra(PlayerService.TITLE_EXTRA);
                    remoteViews.setTextViewText(R.id.title_namesong_widget,titleTmp);
                }
            }
        }
    };




    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        associateIntents(context);

        Log.d(TAG, "Widget's onUpdate()");
    }

    public static RemoteViews getRemoteViews(Context context){
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.player_widget);

        // For Play/Pause button
        PendingIntent pendingIntentStart = getPendingIntent(context, PlayerWidget.ACTION_PLAY_PAUSE);
        remoteViews.setOnClickPendingIntent(R.id.play_pause_btn_widget, pendingIntentStart);

    /*    // For Stop button
        PendingIntent pendingIntentStop = getPendingIntent(context, PlayerWidget.ACTION_STOP);
        remoteViews.setOnClickPendingIntent(R.id.play_pause_btn_widget,pendingIntentStop);*/

        // For Previous button
        PendingIntent pendingIntentPrevious = getPendingIntent(context, PlayerWidget.ACTION_PREVIOUS);
        remoteViews.setOnClickPendingIntent(R.id.previous_btn_widget,pendingIntentPrevious);

        // For Next button
        PendingIntent pendingIntentNext = getPendingIntent(context, PlayerWidget.ACTION_NEXT);
        remoteViews.setOnClickPendingIntent(R.id.next_btn_widget,pendingIntentNext);


        // For Song List activity
        Intent intent= new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendIntentSongList = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.layout_header, pendIntentSongList);

        return remoteViews;
    }

    public static PendingIntent getPendingIntent(Context context, String action) {
        Intent intent = new Intent(context, PlayerWidget.class);
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    private void associateIntents(Context context) {

        try {
            remoteViews = getRemoteViews(context);

            // Push update for this widget to the home screen
            ComponentName thisWidget = new ComponentName(context, PlayerWidget.class);
            AppWidgetManager manager = AppWidgetManager.getInstance(context);
            manager.updateAppWidget(thisWidget, remoteViews);
        }
        catch (Exception e)
        {}


    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        final String action = intent.getAction();
        Log.d(TAG, "Widget received action: " + action);

        if ((action.equals(ACTION_PLAY_PAUSE)
                || action.equals(ACTION_NEXT)
                || action.equals(ACTION_STOP)
                || action.equals(ACTION_PREVIOUS)
                || action.equals(ACTION_SHUFFLE)))
        {
            Intent serviceIntent = new Intent(context, PlayerService.class);
            serviceIntent.setAction(action);
            context.startService(serviceIntent);
        }
        /*else if(action.equals("SEPARATE_APP_ACTION")){
            remoteViews.setTextViewText(R.id.title_namesong_widget,intent.getStringExtra("Name"));
        }*/
        else
        {
            super.onReceive(context, intent);
        }
    }

}

