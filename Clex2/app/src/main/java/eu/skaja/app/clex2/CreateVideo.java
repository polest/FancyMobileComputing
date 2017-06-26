package eu.skaja.app.clex2;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import org.jcodec.api.android.SequenceEncoder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class CreateVideo{

    private ArrayList<String> imagePathList;
    private int fps;
    private String musicPath;
    private String filename;
    private String videoPath;

    public CreateVideo(ArrayList<String> imagePathList, String musicPath, int fps) throws IOException {
        this.imagePathList = imagePathList;
        this.musicPath = musicPath;
        this.fps = fps;
        this.filename = getVideoTitle();

        try {
            // Sets the output directory for the video
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES), this.filename);

            // Sets the Sequence Encoder
            SequenceEncoder encoder = new SequenceEncoder(file);

            // Gets all images from the GridView
            for (String path : this.imagePathList) {
                for(int i = 0; i < fps - 1; i++){
                    Bitmap image = BitmapFactory.decodeFile(path);
                    encoder.encodeImage(image);
                }
            }

            // Closes the encoder
            encoder.finish();

            // Saves the absolute video path
            videoPath = file.getAbsolutePath();

        }catch (IOException ex){
            ex.printStackTrace();
        }
    }

    // returns the defined video details
    public String getVideoTitle() {
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        Integer year = cal.get(Calendar.YEAR);
        Integer month = cal.get(Calendar.MONTH) + 1;
        Integer day = cal.get(Calendar.DAY_OF_MONTH);
        Integer hour = cal.get(Calendar.HOUR_OF_DAY);
        Integer minutes = cal.get(Calendar.MINUTE);
        Integer seconds = cal.get(Calendar.SECOND);
        String timestamp = year.toString() + month.toString() + day.toString() + hour.toString() + minutes.toString() + seconds.toString();
        String filename = "clex_" + timestamp + ".mp4";

        return filename;
    }
}
