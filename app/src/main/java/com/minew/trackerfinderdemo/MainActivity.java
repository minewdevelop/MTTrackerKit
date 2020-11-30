package com.minew.trackerfinderdemo;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.minew.trackerfinderdemo.interfaces.TrackerTagManagerListener;
import com.minew.trackerfinderdemo.tag.BindDevice;
import com.minew.trackerfinderdemo.tag.TrackerTag;
import com.minew.trackerfinderdemo.tag.TrackerTagManager;
import com.minew.trackerfinderdemo.tool.Tools;
import com.minewtech.mttrackit.TrackerException;
import com.minewtech.mttrackit.enums.BluetoothState;
import com.minewtech.mttrackit.enums.ConnectionState;
import com.minewtech.mttrackit.interfaces.OperationCallback;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.add)
    ImageView    mAdd;
    @BindView(R.id.toolbar)
    Toolbar      mToolbar;
    @BindView(R.id.recyeler)
    RecyclerView mRecyeler;
    private TrackerTagManager mTrackerTagManager;

    private static final int REQUEST_ENABLE_BT = 4;
    private MainListAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initView();
        initManager();
        initListener();
        initData();
        checkBluetooth();
        getRequiredPermissions();
        Intent intent = new Intent(this, ManagerService.class);
        startService(intent);
    }

    private void initData() {
        if (TrackerTagManager.bindTags.size() == 0) {
            List<BindDevice> bindDevices = Tools.getBindDevices(this);
            if (bindDevices != null && bindDevices.size() > 0) {
                mTrackerTagManager.bindTrackerTags(bindDevices);
            }
        }
        mAdapter.setData(TrackerTagManager.bindTags);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAdapter.setData(TrackerTagManager.bindTags);
    }

    private void initManager() {
        mTrackerTagManager = TrackerTagManager.getInstance(this);
        /**
         * must set a 8 length password ,or app will crash.
         *
         */
        mTrackerTagManager.setPassword("minew123");
    }

    private void initListener() {
        mAdd.setOnClickListener(this);
        mTrackerTagManager.setTrackerTagManagerListener(trackerTagManagerListener);


        mAdapter.setOnItemClickLitener(new MainListAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                TrackerTag trackerTag = mAdapter.getData(position);
                intent.putExtra(Tools.MAC, trackerTag.mMTTracker.getMacAddress());
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {
                final TrackerTag trackerTag = mAdapter.getData(position);
                TrackerTagManager.getInstance(MainActivity.this).unBindTrackertag(trackerTag, new OperationCallback() {
                    @Override
                    public void onOperation(boolean b, TrackerException e) {
                        if (b){//unbinding success
                            TrackerTagManager.bindTags.remove(trackerTag);

                            BindDevice bindDevice = new BindDevice();
                            bindDevice.setMacAddress(trackerTag.mMTTracker.getMacAddress());
                            bindDevice.setTrackerModel(trackerTag.mMTTracker.getName().getValue());

                            Tools.removeBindDevice(MainActivity.this,bindDevice);
                            mAdapter.setData(TrackerTagManager.bindTags);

                            Toast.makeText(MainActivity.this,getString(R.string.unbind_success),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private TrackerTagManagerListener trackerTagManagerListener = new TrackerTagManagerListener() {
        @Override
        public void onUpdateBindTrackers(final ArrayList<TrackerTag> trackerTags) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAdapter.setData(trackerTags);
                }
            });
        }

        @Override
        public void onUpdateConnectionState(final TrackerTag trackerTag, ConnectionState connectionState) {
            switch (connectionState) {
                case DeviceLinkStatus_Connected:
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, trackerTag.mMTTracker.getMacAddress() + " Connected", Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;
                case DeviceLinkStatus_ConnectFailed:
                case DeviceLinkStatus_Disconnect:
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, trackerTag.mMTTracker.getMacAddress() + "Disconnected", Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;
            }
        }
    };

    private void initView() {
        setSupportActionBar(mToolbar);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyeler.setLayoutManager(layoutManager);
        mAdapter = new MainListAdapter();
        mRecyeler.setAdapter(mAdapter);
        mRecyeler.addItemDecoration(new RecycleViewDivider(this, LinearLayoutManager
                .HORIZONTAL));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add:
                Intent intent = new Intent(this, ScanActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void checkBluetooth() {
        BluetoothState bluetoothState = mTrackerTagManager.checkBluetoothState();
        switch (bluetoothState) {
            case BluetoothStateNotSupported:
                Toast.makeText(this, "Not Support BLE", Toast.LENGTH_SHORT).show();
                finish();
                break;
            case BluetoothStatePowerOff:
                showBLEDialog();
                break;
            case BluetoothStatePowerOn:
                break;
        }
    }

    private void showBLEDialog() {
        Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
    }

    private void getRequiredPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 5);
        } else {
        }
    }
}
