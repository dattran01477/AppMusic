package com.tranthanhdat.mucsicplayergroup2.view;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;

import com.tranthanhdat.mucsicplayergroup2.R;
import com.tranthanhdat.mucsicplayergroup2.service.PlayerService;

public class NotifycationMusic extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.musicnotification);
        registerReceiver(receiver, new IntentFilter(
                PlayerService.ACTION_NOTIFICATION_BUTTON_CLICK));
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override public void onReceive(Context context, Intent intent) {
            int id = intent.getIntExtra(PlayerService.EXTRA_BUTTON_CLICKED, -1);
            switch (id) {
                case R.id.btnPrevious:

                    break;
                case R.id.btnNext:

                    break;
            }
        }
    };


}
