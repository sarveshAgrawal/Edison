package com.example.sarveshagrawal.edison;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.renderscript.ScriptGroup;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sarveshagrawal.edison.db.SqlDatabase;
import com.example.sarveshagrawal.edison.services.GPSTracker;


import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.cordinates)
    TextView cordinates;
    SqlDatabase helpler;
    GPSTracker tracker;
    Double lat;
    Double lng;
    String permission[] = {Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE};
    public static Timer mTimer = null;
    public static Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        helpler = new SqlDatabase(getApplicationContext());
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = new Timer();
        } else {
            // recreate new
            mTimer = new Timer();
        }
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, permission, 0);
        }
    }

    @OnClick({R.id.start_btn, R.id.cordinates})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.start_btn:
                helpler.isClicked = true;
                tracker = new GPSTracker(MainActivity.this);
                lat = tracker.getLocation().getLatitude();
                lng = tracker.getLocation().getLongitude();
                helpler.getInstance(MainActivity.this).insertCodinates(lat, lng);
                getInternetCoonnection();

                break;
            case R.id.cordinates:
                helpler.isClicked = false;
                String res = helpler.getInstance(MainActivity.this).getCordinates();
                String arr[] = res.split(",");

                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                if (arr.length > 0) {
                    intent.putExtra("lat", Double.valueOf(arr[0]));
                    intent.putExtra("lng", Double.valueOf(arr[1]));
                }
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (helpler.isPause && helpler.isClicked){
            getInternetCoonnection();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        helpler.isPause = true;
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = new Timer();
        }
    }

    public void getInternetCoonnection() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = new Timer();
        } else {
            // recreate new
            mTimer = new Timer();
        }
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                mHandler.post(new Runnable() {
                                  @Override
                                  public void run() {
                                      if (getNetworkConnected()) {
                                          String res = helpler.getInstance(MainActivity.this).getCordinates();
                                          String arr[] = res.split(",");
                                          if (arr.length > 0) {
                                              cordinates.setText("Cordinates:\n Lat:  "+arr[0]+"\n Lng:  "+arr[1]);
                                          }
                                      }
                                  }
                              }
                );
            }
        }, 0, 5 * 1000);
    }


    private boolean getNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }
}