package eu.skaja.app.clex2;

import android.app.Activity;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Movie;
import android.os.Environment;
import android.provider.ContactsContract;
import android.widget.Toast;

import com.coremedia.iso.boxes.Container;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;

import org.jcodec.api.android.SequenceEncoder;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
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
                for(int i = 0; i < fps - 1; i++){
                    Bitmap image = BitmapFactory.decodeFile(path);
                    encoder.encodeImage(image);
                }
            }
            /*for(int j = 0; j < imagePathList.size()-1; j++){
                for(int i = 0; i < fps; i++){
                    Bitmap image = BitmapFactory.decodeFile(imagePathList.get(j));
                    encoder.encodeImage(image);
                }
            }*/
            encoder.finish();
            //Toast.makeText(,"Video has been created!", Toast.LENGTH_LONG);
        }catch (IOException ex){
            ex.printStackTrace();
        }
}

        // Combine video with music track
      /*public void combine(String videoPath, String musicPath){
          MediaMuxer muxer = new MediaMuxer("temp.mp4", OutputFormat.MUXER_OUTPUT_MPEG_4);
          // More often, the MediaFormat will be retrieved from MediaCodec.getOutputFormat()
          // or MediaExtractor.getTrackFormat().
          MediaFormat audioFormat = new MediaFormat();
          MediaFormat videoFormat = new MediaFormat();
          int audioTrackIndex = muxer.addTrack(audioFormat);
          int videoTrackIndex = muxer.addTrack(videoFormat);
          ByteBuffer inputBuffer = ByteBuffer.allocate(bufferSize);
          boolean finished = false;
          MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();

          muxer.start();
          while(!finished) {
              // getInputBuffer() will fill the inputBuffer with one frame of encoded
              // sample from either MediaCodec or MediaExtractor, set isAudioSample to
              // true when the sample is audio data, set up all the fields of bufferInfo,
              // and return true if there are no more samples.
              finished = getInputBuffer(inputBuffer, isAudioSample, bufferInfo);
              if (!finished) {
                  int currentTrackIndex = isAudioSample ? audioTrackIndex : videoTrackIndex;
                  muxer.writeSampleData(currentTrackIndex, inputBuffer, bufferInfo);
              }
          };
          muxer.stop();
          muxer.release();
    }*/

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
