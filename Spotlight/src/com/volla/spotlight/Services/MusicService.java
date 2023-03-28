/*
 * Copyright (C) 2015 The CyanogenMod Project
 *               2017-2018 The LineageOS Project
 *               2020-2022 Paranoid Android
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

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.AudioPlaybackConfiguration;
import android.media.session.MediaController;
import android.os.BatteryManager;
import android.os.IBinder;
import android.util.Log;

import com.volla.spotlight.Constants.Constants;
import com.volla.spotlight.Manager.AnimationManager;
import com.volla.spotlight.Utils.FileUtils;

import java.util.List;

public class MusicService extends Service {

    private static final String TAG = "SpotlightMusicService";
    private static final boolean DEBUG = true;
    private AnimationManager mAnimationManager;
    private AudioManager mAudioManager;

    @Override
    public void onCreate() {
        if (DEBUG) Log.d(TAG, "Creating service");

        mAnimationManager = new AnimationManager(this);

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        mAudioManager.registerAudioPlaybackCallback(mAudioPlaybackCallback, null);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (DEBUG) Log.d(TAG, "Starting service");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (DEBUG) Log.d(TAG, "Destroying service");

        onMusicStopped();

        super.onDestroy();
        mAudioManager.unregisterAudioPlaybackCallback(mAudioPlaybackCallback);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    AudioManager.AudioPlaybackCallback mAudioPlaybackCallback = new AudioManager.AudioPlaybackCallback() {
        @Override
        public void onPlaybackConfigChanged(List<AudioPlaybackConfiguration> configs) {
            if (DEBUG) Log.d(TAG, "onPlaybackConfigChanged");
            if (mAudioManager.isMusicActive()) {
                onMusicPlaying();
            } else {
                onMusicStopped();
            }

            super.onPlaybackConfigChanged(configs);
        }
    };


    private void onMusicPlaying() {
        if (DEBUG) Log.d(TAG, "Playing music");
        mAnimationManager.playMusic();
    }

    private void onMusicStopped() {
        if (DEBUG) Log.d(TAG, "Not playing music");
        mAnimationManager.stopMusic();
    }
}
