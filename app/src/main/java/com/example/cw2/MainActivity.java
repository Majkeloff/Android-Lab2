package com.example.cw2;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    BroadcastReceiver batteryInfoReceiver;
    TextView tv_batteryLevel;
    TextView tv_batteryStatus;
    TextView tv_batterySource;
    TextView tv_batteryLevelMax;
    TextView tv_batteryTech;
    TextView tv_batteryTemp;
    TextView tv_batteryVoltage;
    TextView tv_batteryHealth;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Lab2 - BatteryInfo");
        }

        tv_batteryLevel = findViewById(R.id.batteryLevel);
        tv_batteryStatus = findViewById(R.id.batteryStatus);
        tv_batterySource = findViewById(R.id.batterySource);
        tv_batteryLevelMax = findViewById(R.id.batteryLevelMax);
        tv_batteryTech = findViewById(R.id.batteryTech);
        tv_batteryTemp = findViewById(R.id.batteryTemp);
        tv_batteryVoltage = findViewById(R.id.batteryVoltage);
        tv_batteryHealth = findViewById(R.id.batteryHealth);
        progressBar = findViewById(R.id.progressBar);
    }

    @Override
    public void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);

        batteryInfoReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                updateBatteryData(intent);
            }
        };

        registerReceiver(batteryInfoReceiver, intentFilter);
        Log.i(MainActivity.class.getName(),"BroadcastReceiver registered.");
    }

    @Override
    public void onStop() {
        super.onStop();
        unregisterReceiver(batteryInfoReceiver);
        Log.i(MainActivity.class.getName(),"BroadcastReceiver unregistered.");
    }

    @SuppressLint("SetTextI18n")
    private void updateBatteryData(Intent intent) {
        boolean batteryPresent = intent.getBooleanExtra(BatteryManager.EXTRA_PRESENT, false);
        if (batteryPresent) {
            // BATTERY LEVEL
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            int batteryLevel = level * 100 / scale;
            tv_batteryLevel.setText("Battery level: " + batteryLevel + " %");
            progressBar.setProgress(batteryLevel);

            // BATTERY STATUS
            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            String statusLbl;

            switch (status) {
                case BatteryManager.BATTERY_STATUS_CHARGING:
                    statusLbl = "Charging";
                    break;
                case BatteryManager.BATTERY_STATUS_DISCHARGING:
                    statusLbl = "Discharging";
                    break;
                case BatteryManager.BATTERY_STATUS_FULL:
                    statusLbl = "Full";
                    break;
                case BatteryManager.BATTERY_STATUS_UNKNOWN:
                    statusLbl = "Unknown";
                    break;
                case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                default:
                    statusLbl = "Not charging";
                    break;
            }
            if (status != -1){
                tv_batteryStatus.setText("Battery status : " + statusLbl);
            }

            // BATTERY POWER SOURCE
            int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0);
            String pluggedLbl;

            switch (plugged) {
                case BatteryManager.BATTERY_PLUGGED_AC:
                    pluggedLbl = "AC";
                    break;
                case BatteryManager.BATTERY_PLUGGED_USB:
                    pluggedLbl = "USB";
                    break;
                case BatteryManager.BATTERY_PLUGGED_WIRELESS:
                    pluggedLbl = "Wireless";
                    break;
                default:
                    pluggedLbl = "None";
                    break;
            }
            tv_batterySource.setText("Power source : " + pluggedLbl);

            // BATTERY CAPACITY
            long capacity = getBatteryCapacity(this);

            if (capacity > 0) {
                tv_batteryLevelMax.setText("Capacity : " + capacity + " mAh");
            }

            // BATTERY TECHNOLOGY
            if (intent.getExtras() != null) {
                String technology = intent.getExtras().getString(BatteryManager.EXTRA_TECHNOLOGY);

                if (!"".equals(technology)) {
                    tv_batteryTech.setText("Technology : " + technology);
                }
            }

            // BATTERY TEMPERATURE
            int temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0);

            if (temperature > 0) {
                float temp = ((float) temperature) / 10f;
                tv_batteryTemp.setText("Temperature : " + temp + "°C");
            }

            // BATTERY VOLTAGE
            int voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0);

            if (voltage > 0) {
                tv_batteryVoltage.setText("Voltage : " + voltage + " mV");
            }

            // BATTERY HEALTH
            int health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, 0);
            String healthLbl;

            switch(health){
                case BatteryManager.BATTERY_HEALTH_COLD:
                    healthLbl = "Cold";
                    break;
                case BatteryManager.BATTERY_HEALTH_DEAD:
                    healthLbl = "Dead";
                    break;
                case BatteryManager.BATTERY_HEALTH_GOOD:
                    healthLbl = "Good";
                    break;
                case BatteryManager.BATTERY_HEALTH_OVERHEAT:
                    healthLbl = "Overheat";
                    break;
                case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
                    healthLbl = "Over voltage";
                    break;
                case BatteryManager.BATTERY_HEALTH_UNKNOWN:
                    healthLbl = "Unknown";
                    break;
                case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
                    healthLbl = "Unspecified failure";
                    break;
                default:
                    healthLbl = "None";
                    break;
            }
            tv_batteryHealth.setText("Health : " + healthLbl);
        } else {
            Toast.makeText(this, "No battery present", Toast.LENGTH_SHORT).show();
        }
    }
    public long getBatteryCapacity(Context context) {
        BatteryManager mBatteryManager = (BatteryManager) context.getSystemService(Context.BATTERY_SERVICE);
        long chargeCounter = mBatteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER);
        long capacity = mBatteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);

        return (long) (((float) chargeCounter / (float) capacity) * 100f);
    }
}