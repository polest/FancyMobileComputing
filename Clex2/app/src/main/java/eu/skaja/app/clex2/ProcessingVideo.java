package eu.skaja.app.clex2;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

import static android.R.attr.data;
import static eu.skaja.app.clex2.MainActivity.VIDEO_SETTING;

public class ProcessingVideo extends Activity {

    private SeekBar seekBar;
    private TextView txtCurrentDuration;
    private int duration = 0;
    private ArrayList<String> selectedImagesPathList;
    private String musicPath;
    private Button btnProcessVideo;
    private CreateVideo video;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_processing_video);

        txtCurrentDuration = (TextView) findViewById(R.id.txtCurrentDuration);


        seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        duration = (seekBar.getProgress() + 1);
                        txtCurrentDuration.setText("Current duration: " + duration + " seconds");


                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                }
        );


        btnProcessVideo = (Button) findViewById(R.id.btnProcessVideo);
        Bundle bundle;
        bundle = getIntent().getExtras();
        selectedImagesPathList = bundle.getStringArrayList("selectedImagesPathList");
        musicPath = bundle.getString("musicPath");

        btnProcessVideo.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            // Create video with given image paths and music path
                            video = new CreateVideo(selectedImagesPathList, musicPath, 50);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == VIDEO_SETTING && resultCode == RESULT_OK){
            this.selectedImagesPathList = data.getStringArrayListExtra("selectedImagesPathList");
            this.musicPath = data.getStringExtra("musicPath");
        }
    }
}