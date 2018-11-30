package com.smart.musicplayerdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.smart.musicplayer.base.MusicPlayer;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        MusicPlayer musicPlayer=findViewById(R.id.musicplayer);

        String source1 = "http://9890.vod.myqcloud.com/9890_4e292f9a3dd011e6b4078980237cc3d3.f20.mp4";
        musicPlayer.setUp(source1, true, "测试视频");
    }
}
