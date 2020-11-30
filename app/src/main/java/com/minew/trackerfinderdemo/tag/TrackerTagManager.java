package com.minew.trackerfinderdemo.tag;

import android.content.Context;

import com.minew.trackerfinderdemo.interfaces.ScanCallback;
import com.minew.trackerfinderdemo.interfaces.TrackerTagManagerListener;
import com.minewtech.mttrackit.MTTracker;
import com.minewtech.mttrackit.MTTrackerManager;
import com.minewtech.mttrackit.enums.BluetoothState;
import com.minewtech.mttrackit.enums.ConnectionState;
import com.minewtech.mttrackit.enums.TrackerModel;
import com.minewtech.mttrackit.interfaces.ConnectionStateCallback;
import com.minewtech.mttrackit.interfaces.OperationCallback;
import com.minewtech.mttrackit.interfaces.ScanTrackerCallback;
import com.minewtech.mttrackit.interfaces.TrackerManagerListener;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static com.minewtech.mttrackit.enums.ConnectionState.DeviceLinkStatus_Disconnect;

/**
 * @author boyce
 * @date 2018/5/14 9:31
 */
public class TrackerTagManager {

    private static TrackerTagManager         single;
    private static Context                   mContext;
    public static  ArrayList<TrackerTag>     bindTags;
    private        ScanCallback              mScanCallback;
    private        TrackerTagManagerListener listener;

    private TrackerTagManager() {

    }

    public static TrackerTagManager getInstance(Context context) {
        if (single == null) {
            synchronized ((TrackerTagManager.class)) {
                if (single == null) {
                    single = new TrackerTagManager();
                    mContext = context;
                    bindTags = new ArrayList<>();
                }
            }
        }
        return single;
    }


    public void startScan(ScanCallback scanCallback) {
        mScanCallback = scanCallback;
        MTTrackerManager.getInstance(mContext).startScan(scanTrackerCallback);
    }

    public void stopScan() {
        MTTrackerManager.getInstance(mContext).stopScan();
    }

    public void validate(TrackerTag trackerTag, ConnectionStateCallback connectionStateCallback) {
        MTTrackerManager.getInstance(mContext).bindingVerify(trackerTag.mMTTracker, connectionStateCallback);
    }

    public void setPassword(String password) {
        MTTrackerManager.getInstance(mContext).setPassword(password);
    }

    public void bindTrackerTag(TrackerTag trackerTag) {
        String mac = trackerTag.mMTTracker.getMacAddress();
        MTTrackerManager.getInstance(mContext).bindMTTracker(mac);
        bindTags.add(trackerTag);
    }

    public void bindTrackerTags(List<BindDevice> bindDevices) {
        for (int i = 0; i < bindDevices.size(); i++) {
            BindDevice bindDevice = bindDevices.get(i);
            String mac = bindDevice.getMacAddress();
            MTTracker mtTracker = MTTrackerManager.getInstance(mContext).bindMTTracker(mac);
            TrackerTag trackerTag = new TrackerTag(mtTracker);
            int trackerModel = bindDevice.getTrackerModel();
            switch (trackerModel) {
                case 0:
                    mtTracker.setName(TrackerModel.MODEL_Finder);
                    break;
                case 1:
                    mtTracker.setName(TrackerModel.MODEL_F4S);
                    break;
            }
            bindTags.add(trackerTag);
        }
    }

    public void setTrackerTagManagerListener(TrackerTagManagerListener trackerTagManagerListener) {
        listener = trackerTagManagerListener;
        MTTrackerManager.getInstance(mContext).setTrackerManangerListener(trackerManagerListener);
    }

    private TrackerManagerListener trackerManagerListener = new TrackerManagerListener() {
        @Override
        public void onUpdateBindTrackers(List<MTTracker> mtTrackers) {
            if (listener != null) {
                listener.onUpdateBindTrackers(bindTags);
            }
        }

        @Override
        public void onUpdateConnectionState(MTTracker mtTracker, ConnectionState connectionState) {
            if (listener != null) {
                String macAddress = mtTracker.getMacAddress();
                for (TrackerTag trackerTag : bindTags) {
                    if (trackerTag.mMTTracker.getMacAddress().equals(macAddress)) {
                        listener.onUpdateConnectionState(trackerTag, connectionState);
                    }
                }
                listener.onUpdateBindTrackers(bindTags);
            }
        }
    };

    /**
     * 移除绑定设备
     *
     * @param
     */
    public void unBindTrackertag(TrackerTag trackerTag, OperationCallback operationCallback) {
        for (int i = 0; i < bindTags.size(); i++) {
            TrackerTag tag = bindTags.get(i);
            if (tag.mMTTracker.getMacAddress().
                    equals(trackerTag.mMTTracker.getMacAddress())) {
                MTTrackerManager.getInstance(mContext).unBindMTTracker(tag.mMTTracker.getMacAddress(), operationCallback);
            }
        }
    }

    public BluetoothState checkBluetoothState() {
        BluetoothState bluetoothState = MTTrackerManager.getInstance(mContext).checkBluetoothState();
        return bluetoothState;
    }

    private ScanTrackerCallback scanTrackerCallback = new ScanTrackerCallback() {
        @Override
        public void onScannedTracker(LinkedList<MTTracker> mtTrackers) {
            if (mScanCallback != null) {
                List<TrackerTag> trackerTags = new ArrayList<>();
                for (MTTracker mtTracker : mtTrackers) {
                    TrackerTag minewTracker = new TrackerTag(mtTracker);
                    trackerTags.add(minewTracker);
                }
                mScanCallback.onScannedTracker(trackerTags);
            }
        }
    };
}
