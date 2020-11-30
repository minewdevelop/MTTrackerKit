package com.minew.trackerfinderdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.minew.trackerfinderdemo.tag.TrackerTag;
import com.minew.trackerfinderdemo.tag.TrackerTagManager;
import com.minew.trackerfinderdemo.tool.Tools;
import com.minewtech.mttrackit.TrackerException;
import com.minewtech.mttrackit.interfaces.OperationCallback;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.detil_toolbar)
    Toolbar mDetilToolbar;
    @BindView(R.id.ring)
    Button  mRing;
    @BindView(R.id.stop)
    Button  mStop;
    private TrackerTagManager mTrackerTagManager;
    private TrackerTag        mTrackerTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        initView();
        initManager();
        initData();

        initListener();
    }

    private void initListener() {
        mRing.setOnClickListener(this);
        mStop.setOnClickListener(this);
    }

    private void initView() {
        setSupportActionBar(mDetilToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
        String macAddress = getIntent().getStringExtra(Tools.MAC);
        for (TrackerTag trackerTag : TrackerTagManager.bindTags) {
            if (trackerTag.mMTTracker.getMacAddress().equals(macAddress)) {
                mTrackerTag = trackerTag;
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ring:
                mTrackerTag.mMTTracker.switchBellStatus(true, new OperationCallback() {
                    @Override
                    public void onOperation(boolean success, TrackerException mtException) {

                    }
                });
                break;
            case R.id.stop:
                mTrackerTag.mMTTracker.switchBellStatus(false, new OperationCallback() {
                    @Override
                    public void onOperation(boolean success, TrackerException mtException) {

                    }
                });
                break;
        }
    }
}
