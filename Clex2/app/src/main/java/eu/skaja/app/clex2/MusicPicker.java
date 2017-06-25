
package eu.skaja.app.clex2;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;


import java.net.URI;

import java.util.ArrayList;
import java.util.HashMap;
import static android.R.id.list;
import static com.nostra13.universalimageloader.core.ImageLoader.TAG;

public class MusicPicker extends Activity {

    private ListView musicList;
    private MediaPlayer mPlayer = null;
    private Button btnConfirmMusic;
    String musicPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_picker);

        btnConfirmMusic = (Button)findViewById(R.id.btnConfirmMusic);
        btnConfirmMusic.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(MusicPicker.this, musicPath, Toast.LENGTH_LONG).show();
                        mPlayer.reset();
                        Intent intent = new Intent(MusicPicker.this, MainActivity.class);
                        intent.putExtra("selectedMusicPath", musicPath);
                        setResult(Activity.RESULT_OK, intent);
                        finish();
                    }
                }
        );

        musicList = (ListView)findViewById(R.id.musicList);

        String[] values = new String[] {
                "Africa",
                "Hip Hop Japanese",
                "Honorable Battle",
                "Inspirational",
                "Japanese Jingle",
                "Jazz Trio",
                "Not What It Seems",
                "Sadly",
                "Shoulder Angel (no vocals)",
                "Shoulder Angel (vocals)",
                "The Build Up",
                "Traditional Japanese",
                "Traditional Japanese 2",
                "Ufo Slowing",
                "Ufo Speeding",
                "World War 3",
                "Yegods",
                "Zombie Farm"
        };
        final Integer[] mp3s = new Integer[] {
                R.raw.africa,
                R.raw.hip_hop_japanese,
                R.raw.honorable_battle,
                R.raw.inspirational,
                R.raw.japanese_jingle,
                R.raw.jazz_trio,
                R.raw.not_what_it_seems,
                R.raw.sadly,
                R.raw.shoulder_angel_no_vocals,
                R.raw.shoulder_angel_with_vocals,
                R.raw.the_build_up,
                R.raw.traditional_japanese,
                R.raw.traditional_japanese_2,
                R.raw.ufo_slowing,
                R.raw.ufo_speeding,
                R.raw.world_war_3,
                R.raw.yegods,
                R.raw.zombie_farm
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.list_item, R.id.nameOfTitle, values);

        // Assign adapter to ListView
        musicList.setAdapter(adapter);

        musicList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if(mPlayer != null)
                    mPlayer.reset();

                // ListView Clicked item index
                int itemPosition = position;

                // ListView Clicked item value
                musicPath = "android.resource://"+getPackageName()+"/raw/" + (String) musicList.getItemAtPosition(position);

                mPlayer = MediaPlayer.create(MusicPicker.this, mp3s[position]);
                mPlayer.start();
            }

        });



    }
}
