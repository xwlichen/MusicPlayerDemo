package com.smart.musicplayer.listener;

import com.smart.musicplayer.widget.MusicSeekBar;
import com.smart.musicplayer.entity.SeekParams;


public interface SeekChangeListener {

    void onSeeking(SeekParams seekParams);

    void onStartTrackingTouch(MusicSeekBar seekBar);

    void onStopTrackingTouch(MusicSeekBar seekBar);


}