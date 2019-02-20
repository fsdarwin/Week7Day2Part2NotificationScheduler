package com.example.week7day2part2notificationscheduler;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    RadioGroup networkOptions;
    int selectedNetworkID;
    int selectedNetworkOption;
    private JobScheduler jobScheduler;
    private static final int JOB_ID = 0;
    JobInfo jobInfo;
    boolean constraintSet;
    private Switch swDeviceIdleSwitch;
    private Switch swDeviceChargingSwitch;
    SeekBar sbSeekBar;
    int seekBarInteger;
    boolean seekBarSet;
    TextView seekBarProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        swDeviceIdleSwitch = findViewById(R.id.idleSwitch);
        swDeviceChargingSwitch = findViewById(R.id.chargingSwitch);
        sbSeekBar = findViewById(R.id.seekBar);
        seekBarProgress = findViewById(R.id.seekBarProgress);

        sbSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress > 0) {
                    seekBarProgress.setText(progress + " s");
                } else {
                    seekBarProgress.setText("Not Set");
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {


            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void scheduleJob(View view) {
        networkOptions = findViewById(R.id.networkOptions);
        selectedNetworkID = networkOptions.getCheckedRadioButtonId();
        selectedNetworkOption = JobInfo.NETWORK_TYPE_NONE;
        jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        seekBarInteger = sbSeekBar.getProgress();
        seekBarSet = seekBarInteger > 0;

        switch (selectedNetworkID) {
            case R.id.noNetwork:
                selectedNetworkOption = JobInfo.NETWORK_TYPE_NONE;
                break;
            case R.id.anyNetwork:
                selectedNetworkOption = JobInfo.NETWORK_TYPE_ANY;
                break;
            case R.id.wifiNetwork:
                selectedNetworkOption = JobInfo.NETWORK_TYPE_UNMETERED;
                break;
        }
        ComponentName serviceName = new ComponentName(getPackageName(),
                NotificationJobService.class.getName());
        JobInfo.Builder builder = new JobInfo.Builder(JOB_ID, serviceName);
        builder.setRequiredNetworkType(selectedNetworkOption);
        builder.setRequiresDeviceIdle(swDeviceIdleSwitch.isChecked());
        builder.setRequiresCharging(swDeviceChargingSwitch.isChecked());
        if (seekBarSet) {
            builder.setOverrideDeadline(seekBarInteger * 1000);
        }
        constraintSet = selectedNetworkOption != JobInfo.NETWORK_TYPE_NONE
                || swDeviceChargingSwitch.isChecked() || swDeviceIdleSwitch.isChecked()
                || seekBarSet;
        if (constraintSet) {
            jobInfo = builder.build();
            jobScheduler.schedule(jobInfo);
            Toast.makeText(this, "Job Scheduled, job will run when " +
                    "the constraints are met.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Please set at least one constraint",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void cancelJobs(View view) {
        if (jobScheduler != null) {
            jobScheduler.cancelAll();
            jobScheduler = null;
            Toast.makeText(this, "Jobs cancelled", Toast.LENGTH_SHORT).show();
        }
    }
}
