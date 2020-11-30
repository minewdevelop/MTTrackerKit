package com.minew.trackerfinderdemo;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.minew.trackerfinderdemo.interfaces.ScanCallback;
import com.minew.trackerfinderdemo.tag.BindDevice;
import com.minew.trackerfinderdemo.tag.TrackerTag;
import com.minew.trackerfinderdemo.tag.TrackerTagManager;
import com.minew.trackerfinderdemo.tool.Tools;
import com.minewtech.mttrackit.TrackerException;
import com.minewtech.mttrackit.interfaces.ConnectionStateCallback;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ScanActivity extends AppCompatActivity {

    @BindView(R.id.scan_toolbar)
    Toolbar      mScanToolbar;
    @BindView(R.id.scan_recyeler)
    RecyclerView mScanRecyeler;
    private RecyclerView      mRecycle;
    private ScanListAdapter   mAdapter;
    private TrackerTagManager mTrackerTagManager;
    private ProgressDialog    progressDialog;
    private TrackerTag        mTrackerTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        ButterKnife.bind(this);

        initView();
        initManager();
        initData();
        initListener();
    }

    private void initListener() {
        mAdapter.setOnItemClickLitener(new ScanListAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, final int position) {
                mTrackerTagManager.stopScan();
                AlertDialog.Builder builder = new AlertDialog.Builder(ScanActivity.this);
                builder.setTitle("BindDevice")
                        .setMessage("Did you want to bind device?")
                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mTrackerTagManager.startScan(scanCallback);
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                progressDialog = new ProgressDialog(ScanActivity.this);

                                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                progressDialog.setCanceledOnTouchOutside(false);
                                progressDialog.setMessage("Connecting...");
                                progressDialog.show();

                                mTrackerTag = mAdapter.getData(position);
                                mTrackerTagManager.validate(mTrackerTag, connectionCallback);

                            }
                        });
                builder.show();
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
    }

    private ConnectionStateCallback connectionCallback = new ConnectionStateCallback() {
        @Override
        public void onUpdateConnectionState(final boolean success, final TrackerException trackerException) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }
                    if (success) {

                        Toast.makeText(ScanActivity.this, "success", Toast.LENGTH_SHORT).show();
                        mTrackerTagManager.bindTrackerTag(mTrackerTag);
                        //保存到本地
                        BindDevice bindDevice = new BindDevice();
                        bindDevice.setMacAddress(mTrackerTag.mMTTracker.getMacAddress());
                        bindDevice.setTrackerModel(mTrackerTag.mMTTracker.getName().getValue());
                        Tools.saveBindDevice(ScanActivity.this, bindDevice);
                        finish();
                    } else {
                        Toast.makeText(ScanActivity.this, trackerException.getMsg(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    };


    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.scan_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRecycle = (RecyclerView) findViewById(R.id.scan_recyeler);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecycle.setLayoutManager(layoutManager);
        mAdapter = new ScanListAdapter();
        mRecycle.setAdapter(mAdapter);
        mRecycle.addItemDecoration(new RecycleViewDivider(this, LinearLayoutManager
                .HORIZONTAL));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initManager() {
        mTrackerTagManager = TrackerTagManager.getInstance(this);
    }

    private void initData() {
        Tools.isScan = true;
        mTrackerTagManager.startScan(scanCallback);
    }

    private ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScannedTracker(List<TrackerTag> trackerTags) {
            mAdapter.setData(trackerTags);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTrackerTagManager.stopScan();
        Tools.isScan = false;
    }
}
