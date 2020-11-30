package com.minew.trackerfinderdemo;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import com.minew.trackerfinderdemo.interfaces.ScanCallback;
import com.minew.trackerfinderdemo.tag.TrackerTag;
import com.minew.trackerfinderdemo.tag.TrackerTagManager;
import com.minew.trackerfinderdemo.tool.Tools;
import com.minewtech.mttrackit.enums.ConnectionState;

import java.util.List;

public class ManagerService extends Service {


    private Handler mHandler = new Handler();

    private Runnable scanRunnable = new Runnable() {
        @Override
        public void run() {
            boolean hasDeviceDisconnect = false;
            for (TrackerTag trackerTag : TrackerTagManager.bindTags) {
                if (!(trackerTag.mMTTracker.getConnectionState() == ConnectionState.DeviceLinkStatus_Connected)) {
                    hasDeviceDisconnect = true;
                    break;
                }
            }
            if (hasDeviceDisconnect) {
                if (!Tools.isScan) {
                    TrackerTagManager.getInstance(ManagerService.this).startScan(new ScanCallback() {
                        @Override
                        public void onScannedTracker(List<TrackerTag> trackerTags) {

                        }
                    });
                }
            } else {
                if (!Tools.isScan) {
                    TrackerTagManager.getInstance(ManagerService.this).stopScan();
                }
            }
            mHandler.postDelayed(scanRunnable, 15 * 1000);
        }
    };

    public ManagerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mHandler.post(scanRunnable);
    }
}
