package eu.skaja.app.clex2;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Movie;
import android.os.Environment;
import android.provider.ContactsContract;
import android.widget.Toast;
import org.jcodec.api.android.SequenceEncoder;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

//Wird als Objekt genutzt, welches in JEncode geladen wird.
public class CreateVideo{

    private ArrayList<String> imagePathList;
    private int fps;
    private String musicPath;
    private String filename;
    private String videoPath;
    private final String folder = "Clex";

    public CreateVideo(ArrayList<String> imagePathList, String musicPath, int fps) throws IOException {
        this.imagePathList = imagePathList;
        this.musicPath = musicPath;
        this.fps = fps;
        this.filename = getVideoTitle();

        try {
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + File.separator + folder, this.filename);

            SequenceEncoder encoder = new SequenceEncoder(file);
            // GOP size will be supported in 0.2
            //encoder.getEncoder().setKeyInterval(25);

            for (String path : this.imagePathList) {
                for(int i = 0; i < fps; i++){
                    Bitmap image = BitmapFactory.decodeFile(path);
                    encoder.encodeImage(image);
                }
            }
            encoder.finish();
            //Toast.makeText(,"Video has been created!", Toast.LENGTH_LONG);
        }catch (IOException ex){
            ex.printStackTrace();
        }
}

    // Combine video with music track
        String videofilepath = videoPath;
        String audiofilepath = musicPath;
        File file = new File(videofilepath);

        H264TrackImpl h264Track = new H264TrackImpl(new FileDataSourceImpl(videofilepath));
        AACTrackImpl aacTrack = new AACTrackImpl(new FileDataSourceImpl(audiofilepath));

        Movie movie = new Movie();
        movie.addTrack(h264Track);
        movie.addTrack(aacTrack);


        Container mp4file = new DefaultMp4Builder().build(movie);

        FileChannel fc = new FileOutputStream(new File(Environment.getExternalStorageDirectory().toString() + "/video.mp4")).getChannel();
        mp4file.writeContainer(fc);
        fc.close();
    }
    public String getVideoTitle() {
        Date date = new Date(); // your date
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
