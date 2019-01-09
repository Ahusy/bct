package cn.antke.bct.launcher.controller;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;

import java.util.Timer;
import java.util.TimerTask;

import cn.antke.bct.R;
import cn.antke.bct.base.BaseActivity;
import cn.antke.bct.db.CityDBManager;
import cn.antke.bct.main.controller.MainActivity;
import cn.antke.bct.utils.SafeSharePreferenceUtil;

/**
 * Created by liuzhichao on 2017/6/27.
 * 空白启动页，判断进入登录页还是主页
 */
public class SplashActivity extends BaseActivity {
    private static final long LAUNCH_MIN_TIME = 2000L;
    private static final int MSG_CITY_INIT_FINISH = 1;
    private long mLaunchTime;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_CITY_INIT_FINISH:
                    gotoActivity();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_splash);
        SafeSharePreferenceUtil.saveInt("statusBarHeight", getStatusHeight(this));
        mLaunchTime = SystemClock.elapsedRealtime();
        initCityDB();
    }

    public static int getStatusHeight(Activity activity){
        int statusHeight = 0;
        Rect localRect = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(localRect);
        statusHeight = localRect.top;
        if (0 == statusHeight){
            Class<?> localClass;
            try {
                localClass = Class.forName("com.android.internal.R$dimen");
                Object localObject = localClass.newInstance();
                int i5 = Integer.parseInt(localClass.getField("status_bar_height").get(localObject).toString());
                statusHeight = activity.getResources().getDimensionPixelSize(i5);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (NumberFormatException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
        return statusHeight;
    }

    private void initCityDB() {
        new Thread(() -> {
            CityDBManager.introducedCityDB(this);
            handler.sendEmptyMessageDelayed(MSG_CITY_INIT_FINISH, 100);
        }).start();
    }

    private void gotoActivity() {
        long elapsed = SystemClock.elapsedRealtime() - mLaunchTime;
        if (elapsed >= LAUNCH_MIN_TIME) {
            performGotoActivity();
            finish();
        } else {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    if (SplashActivity.this.isFinishing()) {
                        return;
                    }
                    cancel();
                    performGotoActivity();
                    finish();
                }
            }, LAUNCH_MIN_TIME - elapsed);
        }

    }

    private void performGotoActivity() {
//        if (UserCenter.isLogin(this)) {
        startActivity(new Intent(this, MainActivity.class));
//            NewActivityActivity.startNewActivityActivity(this, "", "http://onau582bt.bkt.clouddn.com/6505a939-75b0-4c84-b4fd-01ab58d040f2");
//        } else {
//            startActivity(new Intent(this, LoginActivity.class));
//        }
    }

}
