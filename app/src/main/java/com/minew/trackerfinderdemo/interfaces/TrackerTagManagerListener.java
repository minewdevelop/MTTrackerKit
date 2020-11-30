package com.minew.trackerfinderdemo.interfaces;

import com.minew.trackerfinderdemo.tag.TrackerTag;
import com.minewtech.mttrackit.enums.ConnectionState;

import java.util.ArrayList;

/**
 * @author boyce
 * @date 2017/11/2 14:14
 */

public interface TrackerTagManagerListener {
    void onUpdateBindTrackers(ArrayList<TrackerTag> trackerTags);

    void onUpdateConnectionState(TrackerTag trackerTag, ConnectionState connectionState);
}
