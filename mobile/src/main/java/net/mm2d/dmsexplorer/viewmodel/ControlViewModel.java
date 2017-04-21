/*
 * Copyright (c) 2017 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.dmsexplorer.viewmodel;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.media.MediaPlayer;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import net.mm2d.dmsexplorer.BR;
import net.mm2d.dmsexplorer.R;
import net.mm2d.dmsexplorer.domain.model.MediaPlayerModel;
import net.mm2d.dmsexplorer.domain.model.MediaPlayerModel.StatusListener;

import java.util.Locale;

/**
 * @author <a href="mailto:ryo@mm2d.net">大前良介 (OHMAE Ryosuke)</a>
 */
public class ControlViewModel extends BaseObservable implements StatusListener {
    interface OnCompletionListener {
        void onCompletion();
    }

    private String mProgressText = makeTimeText(0);
    private String mDurationText = makeTimeText(0);
    private boolean mPlaying;
    private boolean mPrepared;
    private int mDuration;
    private int mProgress;
    private boolean mSeekable;
    private int mPlayButtonResId = R.drawable.ic_play;

    private boolean mTracking;
    private final MediaPlayerModel mMediaPlayerModel;
    private OnCompletionListener mOnCompletionListener;

    public ControlViewModel(MediaPlayerModel playerModel) {
        mMediaPlayerModel = playerModel;
        mMediaPlayerModel.setStatusListener(this);
    }

    public void terminate() {
        mMediaPlayerModel.terminate();
    }

    public void restoreSaveProgress(final int position) {
        mMediaPlayerModel.restoreSaveProgress(position);
    }

    public void setOnCompletionListener(OnCompletionListener listener) {
        mOnCompletionListener = listener;
    }

    public final OnSeekBarChangeListener onSeekBarChangeListener = new OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                setProgressText(progress);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            mTracking = true;
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            mTracking = false;
            mMediaPlayerModel.seekTo(seekBar.getProgress());
        }
    };

    public void onClickPlay() {
        final boolean playing = mMediaPlayerModel.isPlaying();
        if (playing) {
            mMediaPlayerModel.pause();
        } else {
            mMediaPlayerModel.play();
        }
        setPlaying(!playing);
    }

    @Bindable
    public int getProgress() {
        return mProgress;
    }

    private void setProgress(final int progress) {
        if (mTracking) {
            return;
        }
        setProgressText(progress);
        mProgress = progress;
        notifyPropertyChanged(BR.progress);
    }

    @Bindable
    public int getDuration() {
        return mDuration;
    }

    private void setDuration(final int duration) {
        mDuration = duration;
        notifyPropertyChanged(BR.duration);
        if (duration > 0) {
            setSeekable(true);
        }
        setDurationText(duration);
        setPrepared(true);
    }

    @Bindable
    public String getProgressText() {
        return mProgressText;
    }

    private void setProgressText(final int progress) {
        mProgressText = makeTimeText(progress);
        notifyPropertyChanged(BR.progressText);
    }

    @Bindable
    public String getDurationText() {
        return mDurationText;
    }

    private void setDurationText(final int duration) {
        mDurationText = makeTimeText(duration);
        notifyPropertyChanged(BR.durationText);
    }

    private void setPlaying(final boolean playing) {
        if (mPlaying == playing) {
            return;
        }
        mPlaying = playing;
        setPlayButtonResId(playing ? R.drawable.ic_pause : R.drawable.ic_play);
    }

    @Bindable
    public int getPlayButtonResId() {
        return mPlayButtonResId;
    }

    private void setPlayButtonResId(final int playButtonResId) {
        mPlayButtonResId = playButtonResId;
        notifyPropertyChanged(BR.playButtonResId);
    }

    @Bindable
    public boolean isPrepared() {
        return mPrepared;
    }

    private void setPrepared(final boolean prepared) {
        mPrepared = prepared;
        notifyPropertyChanged(BR.prepared);
    }

    @Bindable
    public boolean isSeekable() {
        return mSeekable;
    }

    private void setSeekable(final boolean seekable) {
        mSeekable = seekable;
        notifyPropertyChanged(BR.seekable);
    }

    private static String makeTimeText(int millisecond) {
        final long second = (millisecond / 1000) % 60;
        final long minute = (millisecond / 60000) % 60;
        final long hour = millisecond / 3600000;
        return String.format(Locale.US, "%01d:%02d:%02d", hour, minute, second);
    }

    @Override
    public void notifyDuration(final int duration) {
        setDuration(duration);
    }

    @Override
    public void notifyProgress(final int progress) {
        setProgress(progress);
    }

    @Override
    public void notifyPlayingState(final boolean playing) {
        setPlaying(playing);
    }

    @Override
    public boolean onError(final MediaPlayer mp, final int what, final int extra) {
        return false;
    }

    @Override
    public boolean onInfo(final MediaPlayer mp, final int what, final int extra) {
        return false;
    }

    @Override
    public void onCompletion(final MediaPlayer mp) {
        if (mOnCompletionListener != null) {
            mOnCompletionListener.onCompletion();
        }
    }
}
