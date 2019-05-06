package com.smart.musicplayerdemo;

import android.content.Intent;
import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.smart.musicplayer.base.MusicPlayer;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        MusicPlayer musicPlayer = findViewById(R.id.musicplayer);

//        String source1 = "http://9890.vod.myqcloud.com/9890_4e292f9a3dd011e6b4078980237cc3d3.f20.mp4";
        String source1 = "https://newst.bailemi.com/20190429/TMKQHTcFGfkJ8nQZAn2yCDHGSi7tYaHD.mp3";
//        String source1=Environment.getExternalStorageDirectory().getPath()+"/七里香.flac";
        musicPlayer.setUp(source1, true, "测试视频");
        musicPlayer.startPlayLogic();

        Button tvRoate = findViewById(R.id.tvRoate);
        ImageView ivRoate = findViewById(R.id.ivRoate);

        tvRoate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, TestActivity.class));
            }
        });
        initVisualizer();

    }


    private Visualizer visualizer;

    public void initVisualizer() {

        visualizer = new Visualizer(0);
        //采样的最大值
        int captureSize = Visualizer.getCaptureSizeRange()[1];
        //采样的频率
        int captureRate = Visualizer.getMaxCaptureRate() * 2 / 3;
        visualizer.setCaptureSize(captureSize);
        visualizer.setDataCaptureListener(dataCaptureListener, captureRate, true, true);
        visualizer.setScalingMode(Visualizer.SCALING_MODE_NORMALIZED);
        visualizer.setMeasurementMode(Visualizer.MEASUREMENT_MODE_PEAK_RMS);

        visualizer.setEnabled(true);
    }

    private Visualizer.OnDataCaptureListener dataCaptureListener = new Visualizer.OnDataCaptureListener() {
        @Override
        public void onWaveFormDataCapture(Visualizer visualizer, final byte[] waveform, int samplingRate) {
            Log.e("xw", "waveform:" + waveform[0]);
        }

        @Override
        public void onFftDataCapture(Visualizer visualizer, final byte[] fft, int samplingRate) {
            Log.e("xw", "fft:" + fft[0]);
        }
    };
}
