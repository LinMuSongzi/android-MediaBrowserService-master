/*
 * Copyright 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.mediasession.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.mediasession.R;
import com.example.android.mediasession.client.MediaBrowserAdapter;
import com.example.android.mediasession.service.contentcatalogs.MusicLibrary;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MediaBrowserAdapter.class.getSimpleName();
    private ImageView mAlbumArt;
    private TextView mTitleTextView;
    private TextView mArtistTextView;
    private ImageView mMediaControlsImage;
    private MediaSeekBar mSeekBarAudio;

    private MediaBrowserAdapter mMediaBrowserAdapter;

    private boolean mIsPlaying;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeUI();
        mMediaBrowserAdapter = new MediaBrowserAdapter(this);
        mMediaBrowserAdapter.addListener(new MediaBrowserListener());
        Log.i(TAG, "onCreate: ");
    }

    private void initializeUI() {
        Log.i(TAG, "initializeUI: ");
        mTitleTextView = (TextView) findViewById(R.id.song_title);
        mArtistTextView = (TextView) findViewById(R.id.song_artist);
        mAlbumArt = (ImageView) findViewById(R.id.album_art);
        mMediaControlsImage = (ImageView) findViewById(R.id.media_controls);
        mSeekBarAudio = (MediaSeekBar) findViewById(R.id.seekbar_audio);

        final Button buttonPrevious = (Button) findViewById(R.id.button_previous);
        buttonPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMediaBrowserAdapter.getTransportControls().skipToPrevious();
            }
        });

        final Button buttonPlay = (Button) findViewById(R.id.button_play);
        buttonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * 播放与停止
                 */
                if (mIsPlaying) {
                    mMediaBrowserAdapter.getTransportControls().pause();
                } else {
                    mMediaBrowserAdapter.getTransportControls().play();
                }
            }
        });

        final Button buttonNext = (Button) findViewById(R.id.button_next);
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMediaBrowserAdapter.getTransportControls().skipToNext();
            }
        });
    }

    /**
     * 管理音乐播放器activity声明周期
     */
    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "onStart: MainActivity");
        mMediaBrowserAdapter.onStart();
    }

    /**
     * 管理音乐播放器activity声明周期
     */
    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, "onStop: MainActivity");
        mSeekBarAudio.disconnectController();
        mMediaBrowserAdapter.onStop();
    }

    private class MediaBrowserListener extends MediaBrowserAdapter.MediaBrowserChangeListener {

        /**
         * 连接获取播放器connext
         *
         * @param mediaController
         */
        @Override
        public void onConnected(@Nullable MediaControllerCompat mediaController) {
            super.onConnected(mediaController);
            Log.i(TAG, "onConnected: MainActivity");
            mSeekBarAudio.setMediaController(mediaController);
        }

        /**
         * 音乐播放状态回调
         *
         * @param playbackState
         */
        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat playbackState) {
            Log.i(TAG, "onPlaybackStateChanged: MainActivity  playbackState = " + playbackState);
            mIsPlaying = playbackState != null &&
                    playbackState.getState() == PlaybackStateCompat.STATE_PLAYING;
            mMediaControlsImage.setPressed(mIsPlaying);
        }

        /**
         * 音乐播放切换
         *
         * @param mediaMetadata
         */
        @Override
        public void onMetadataChanged(MediaMetadataCompat mediaMetadata) {
            Log.i(TAG, "onMetadataChanged: MainActivity  mediaMetadata = " + mediaMetadata);
            if (mediaMetadata == null) {
                return;
            }
            mTitleTextView.setText(
                    mediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE));
            mArtistTextView.setText(
                    mediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_ARTIST));
            mAlbumArt.setImageBitmap(MusicLibrary.getAlbumBitmap(
                    MainActivity.this,
                    mediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID)));
        }
    }
}
