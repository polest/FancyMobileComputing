package eu.skaja.app.clex2;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class ProcessingVideo extends Activity {

    private Button btnProcessVideo;
    private SeekBar seekBar;
    private TextView txtCurrentDuration;
    private int duration = 0;

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

        btnProcessVideo.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //HALLO DENNIS
                    }
                }
        );


    }
}
