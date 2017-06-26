package eu.skaja.app.clex2;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;

import static eu.skaja.app.clex2.MainActivity.VIDEO_SETTING;

public class VideoSettings extends Activity {

    private SeekBar seekBar;
    private TextView txtCurrentDuration;
    private int duration = 3;
    private ArrayList<String> selectedImagesPathList;
    private String musicPath;
    private Button btnProcessVideo;
    private ProgressDialog waiting;
    private int PROCESS_RESULT = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_settings);

        //Dialog
        waiting = new ProgressDialog(this);
        waiting.setMessage("Video will be created");
        waiting.setIndeterminate(false);
        waiting.setCancelable(false);

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
                        waiting.show();
                        Intent intent = new Intent(VideoSettings.this, ProcessingDone.class);
                        intent.putStringArrayListExtra("selectedImagesPathList", selectedImagesPathList);
                        intent.putExtra("musicPath", musicPath);
                        intent.putExtra("duration", duration);
                        startActivityForResult(intent, PROCESS_RESULT);
                    }
                }
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == PROCESS_RESULT && resultCode == RESULT_OK){
            Intent returnIntent = new Intent();
            returnIntent.putExtra("deleteAll",1);
            setResult(Activity.RESULT_OK,returnIntent);
            finish();
        }



    }

}