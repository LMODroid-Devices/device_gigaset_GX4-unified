/*
 * Copyright (C) 2022 Paranoid Android
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.volla.spotlight.Services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.android.internal.util.ArrayUtils;

import com.volla.spotlight.Constants.Constants;
import com.volla.spotlight.Manager.AnimationManager;
import com.volla.spotlight.Manager.SettingsManager;
import com.volla.spotlight.Manager.StatusManager;
import com.volla.spotlight.Utils.FileUtils;

import java.util.ArrayList;

public class NotificationService extends NotificationListenerService {

    private static final String TAG = "SpotlightNotification";
    private static final boolean DEBUG = true;
    private ArrayList<Integer> mNotifications = new ArrayList<>();
    private AnimationManager mAnimationManager;

    @Override
    public void onCreate() {
        if (DEBUG) Log.d(TAG, "Creating service");

        mAnimationManager = new AnimationManager(this);
                
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (DEBUG) Log.d(TAG, "Starting service");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        if (DEBUG) Log.d(TAG, "Destroying service");
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn){
        String packageName = sbn.getPackageName();
        String packageChannelID = sbn.getNotification().getChannelId();
        int packageImportance = -1;
        try {
            Context packageContext = createPackageContext(packageName, 0);
            NotificationManager packageNotificationManager = (NotificationManager) packageContext.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel packageChannel = packageNotificationManager.getNotificationChannel(packageChannelID);
            if (packageChannel != null) {
                packageImportance = packageChannel.getImportance();
            }
        } catch (PackageManager.NameNotFoundException e) {};
        if (DEBUG) Log.d(TAG, "onNotificationPosted: package:" + packageName + " | channel id: " + packageChannelID + " | importance: " + packageImportance);
        if (SettingsManager.isSpotlightNotifsAppEnabled(this, packageName)
                        && !sbn.isOngoing()
                        && !ArrayUtils.contains(Constants.APPSTOIGNORE, packageName)
                        && !ArrayUtils.contains(Constants.NOTIFSTOIGNORE, packageName + ":" + packageChannelID)
                        && (packageImportance >= NotificationManager.IMPORTANCE_DEFAULT || packageImportance == -1)) {
            mNotifications.add(sbn.getId());
            mAnimationManager.playNotifications();
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn){
        if (DEBUG) Log.d(TAG, "onNotificationRemoved: package:" + sbn.getPackageName() + " | channel id: " + sbn.getNotification().getChannelId() + " | id: " + sbn.getId());
        if (mNotifications.contains(sbn.getId())) {
            mNotifications.remove((Integer) sbn.getId());
        }
        if (mNotifications.isEmpty()) {
            mAnimationManager.stopNotifications();
        }
    }
}
