package com.avidly.roy.applovinmaxsample;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxReward;
import com.applovin.mediation.MaxRewardedAdListener;
import com.applovin.mediation.ads.MaxInterstitialAd;
import com.applovin.mediation.ads.MaxRewardedAd;
import com.applovin.sdk.AppLovinSdk;
import com.applovin.sdk.AppLovinSdkConfiguration;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private MaxInterstitialAd interAd;
    private MaxRewardedAd rwdAd;
    private String RWD_UNIT_ID = "4b215c42834cc37b";
    private String INTER_UNIT_ID = "64c15cb487bc6c06";
    private int interAdRetryTime;
    private int rwdAdRetryTime;
    private final String TAG = "roy_max";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createInterAd();
        createRwdAd();
        AppLovinSdk.getInstance(this).setMediationProvider("max");
        AppLovinSdk.initializeSdk(this, new AppLovinSdk.SdkInitializationListener() {
            @Override
            public void onSdkInitialized(final AppLovinSdkConfiguration configuration) {
                // AppLovin SDK is initialized, start loading ads
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "init finish", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void createRwdAd() {
        rwdAd = MaxRewardedAd.getInstance(RWD_UNIT_ID, this);
        rwdAd.setListener(new MyMaxRewardedAdListener());

    }

    private void createInterAd() {
        interAd = new MaxInterstitialAd(INTER_UNIT_ID, this);
        interAd.setListener(new MyMaxAdListener());
    }

    public void loadInter(View view) {
        interAd.loadAd();
    }

    public void loadRwd(View view) {
        rwdAd.loadAd();
    }

    public void showInter(View view) {
        if (interAd.isReady()) {
            interAd.showAd();
        } else {
            Toast.makeText(this, "interAd is not ready", Toast.LENGTH_SHORT).show();
        }
    }

    public void showRwd(View view) {
        if (rwdAd.isReady()) {
            rwdAd.showAd();
        } else {
            Toast.makeText(this, "rwdAd is not ready", Toast.LENGTH_SHORT).show();
        }
    }

    public void showDebugView(View view) {
        AppLovinSdk.getInstance( this ).showMediationDebugger();
    }

    class MyMaxAdListener implements MaxAdListener {

        @Override
        public void onAdLoaded(MaxAd ad) {
            interAdRetryTime = 0;
            showLogWithAction("onAdLoaded", ad);

        }

        @Override
        public void onAdLoadFailed(String adUnitId, int errorCode) {

            showLogWithAction("onAdLoadFailed", adUnitId,errorCode+"");
            // 计算延迟的时间，2的倍数，最小是64s
            long delayTime = TimeUnit.SECONDS.toMillis((long) Math.pow(2, Math.min(6, interAdRetryTime++)));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    interAd.loadAd();
                }
            }, delayTime);

        }

        @Override
        public void onAdDisplayed(MaxAd ad) {
            showLogWithAction("onAdDisplayed", ad);
        }

        @Override
        public void onAdHidden(MaxAd ad) {
            showLogWithAction("onAdHidden", ad);
            interAd.loadAd();
        }

        @Override
        public void onAdClicked(MaxAd ad) {
            showLogWithAction("onAdClicked", ad);
        }

        @Override
        public void onAdDisplayFailed(MaxAd ad, int errorCode) {
            showLogWithAction("onAdDisplayFailed", ad,errorCode+"");
        }
    }

    class MyMaxRewardedAdListener implements MaxRewardedAdListener {

        @Override
        public void onRewardedVideoStarted(MaxAd ad) {
            showLogWithAction("onRewardedVideoStarted", ad);
        }

        @Override
        public void onRewardedVideoCompleted(MaxAd ad) {
            showLogWithAction("onRewardedVideoCompleted", ad);

        }

        @Override
        public void onUserRewarded(MaxAd ad, MaxReward reward) {
            showLogWithAction("onUserRewarded", ad,reward.getLabel());
        }

        @Override
        public void onAdLoaded(MaxAd ad) {
            rwdAdRetryTime=0;
            showLogWithAction("onAdLoaded", ad);

        }

        @Override
        public void onAdLoadFailed(String adUnitId, int errorCode) {
            showLogWithAction("onAdLoadFailed", adUnitId,errorCode+"");

            // 计算延迟的时间，2的倍数，最小是64s
            long delayTime = TimeUnit.SECONDS.toMillis((long) Math.pow(2, Math.min(6, interAdRetryTime++)));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    rwdAd.loadAd();
                }
            }, delayTime);
        }

        @Override
        public void onAdDisplayed(MaxAd ad) {
            showLogWithAction("onAdDisplayed", ad);

        }

        @Override
        public void onAdHidden(MaxAd ad) {
            showLogWithAction("onAdHidden", ad);
            rwdAd.loadAd();
        }

        @Override
        public void onAdClicked(MaxAd ad) {
            showLogWithAction("onAdClicked", ad);
        }

        @Override
        public void onAdDisplayFailed(MaxAd ad, int errorCode) {
            showLogWithAction("onAdDisplayFailed", ad, errorCode+"");
        }
    }

    private void showLogWithAction(String actionName, MaxAd ad) {
        Log.i(TAG, actionName +"---" + ad.getAdUnitId() + "--network---" + ad.getNetworkName());
    }

    private void showLogWithAction(String actionName, MaxAd ad, String msg ) {
        Log.i(TAG, actionName+"---"  + ad.getAdUnitId() + "--network---" + ad.getNetworkName() + "---extMsg---" + msg);
    }
    private void showLogWithAction(String actionName, String adunitId, String msg ) {
        Log.i(TAG, actionName+"---" + adunitId + "---extMsg---" + msg);
    }
}
