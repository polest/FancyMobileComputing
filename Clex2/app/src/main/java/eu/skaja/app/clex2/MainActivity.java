package eu.skaja.app.clex2;

import java.util.ArrayList;
import java.util.List;
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

public class MainActivity extends Activity {

    // init variables
    private String selectedImagePath = null;
    private int selectedImagePos = -1;
    private Button btnCreate;
    private Button btnDelete;
    private Button btnDeleteAll;
    private Button btnPickMusic;
    private Button btnSelectImages;
    private Button btnSortLeft;
    private Button btnSortRight;
    private Button btnStartEditor;
    private GalleryAdapter adapter;
    private GridView gridGallery;
    private ImageLoader imageLoader;
    private int toggle;
    private List<String> imagesEncodedList;
    private String imageEncoded;
    private String musicPath = null;
    private ViewSwitcher viewSwitcher;
    private ArrayList<ImageObject> dataT = new ArrayList<>();
    private ArrayList<String> selectedImagesPathList;

    public static final int PICK_IMAGE_MULTIPLE = 1;
    public static final int MUSIC_PICKER = 2;
    public static final int VIDEO_SETTING = 3;
    private int EDITOR_RESULT = 4;
    private int VIDEO_SETTING_RESULT = 6;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        initImageLoader();
        init();
    }

    private void initImageLoader() {
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheOnDisc().imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                .bitmapConfig(Bitmap.Config.RGB_565).build();
        ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(
                this).defaultDisplayImageOptions(defaultOptions).memoryCache(
                new WeakMemoryCache());

        ImageLoaderConfiguration config = builder.build();
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(config);
    }

    private void init() {

        gridGallery = (GridView) findViewById(R.id.gridGallery);
        gridGallery.setFastScrollEnabled(true);
        adapter = new GalleryAdapter(getApplicationContext(), imageLoader);
        //adapter.setMultiplePick(false);

        // OnClick function for the GridView Items
        AdapterView.OnItemClickListener mItemMulClickListener = new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> l, View v, int position, long id) {

                // Selectes and unselectes the image
                if(selectedImagePath == dataT.get(position).sdcardPath && toggle == 1){

                    // unselect image
                    selectedImagePath = null;
                    selectedImagePos = -1;
                    toggle = 0;

                    adapter.setUnselectedPath();

                } else {
                    // select selected
                    selectedImagePath = adapter.getItem(position).sdcardPath;
                    selectedImagePos = position;
                    toggle = 1;

                    selectedImagePath = adapter.getItem(position).sdcardPath;
                    selectedImagePos = position;
                    adapter.setSelectedPath(dataT.get(position).sdcardPath);

                }
                // Refresh the gridView
                adapter.notifyDataSetChanged();
            }
        };

        //Leiste oben
        btnCreate = (Button) findViewById(R.id.btnCreate);
        btnDelete = (Button)findViewById(R.id.btnDelete);
        btnDeleteAll = (Button) findViewById(R.id.btnDeleteAll);
        btnSortRight = (Button)findViewById(R.id.btnSortDown);
        btnSortLeft = (Button)findViewById(R.id.btnSortUp);
        btnPickMusic = (Button) findViewById(R.id.btnPickMusic);
        btnStartEditor = (Button)findViewById(R.id.btnStartEditor);
        btnSelectImages = (Button) findViewById(R.id.btnSelectImages);
        viewSwitcher = (ViewSwitcher) findViewById(R.id.viewSwitcher);
        gridGallery.setOnItemClickListener(mItemMulClickListener);
        gridGallery.setAdapter(adapter);
        viewSwitcher.setDisplayedChild(1);

        // OnClick function from SDK button
        btnStartEditor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Dialog Listener
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                viewSwitcher.setDisplayedChild(0);
                                dataT.remove(selectedImagePos);
                                adapter.addAll(dataT);
                                selectedImagePos = -1;
                                selectedImagePath = null;

                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };

                // Checks if an image is selected. If not -> make dialog. If yes, start SDK
                if(selectedImagePath != null){
                    Intent intent = new Intent(MainActivity.this, PhotoEditor.class);
                    intent.putExtra("selectedImagePath", selectedImagePath);
                    intent.putExtra("selectedImagePos", selectedImagePos);
                    startActivityForResult(intent, EDITOR_RESULT);
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setMessage("Please select an image").setNegativeButton("Ok", dialogClickListener).show();
                }

            }
        });

        // OnClick function from music picker button
        btnPickMusic.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // This will start the music picker activity
                        Intent intent = new Intent(MainActivity.this, MusicPicker.class);
                        startActivityForResult(intent, MUSIC_PICKER);
                    }
                }
        );

        // OnClickfunction from native android galery
        btnSelectImages.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            // Befor open the native galery, the permission will be asked
                            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED) {
                                Intent i = new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                i.setType("image/*");
                                i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                                startActivityForResult(i, PICK_IMAGE_MULTIPLE);
                            } else {
                                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                            }
                        }
                    }
                }
        );

        // Invokes the processing video activity and deliver the image path list
        btnCreate.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectedImagesPathList = extractPathFromCustomGallery(dataT);
                        if(!selectedImagesPathList.isEmpty()) {
                            Intent intent = new Intent(MainActivity.this, VideoSettings.class);
                            intent.putStringArrayListExtra("selectedImagesPathList", selectedImagesPathList);
                            intent.putExtra("musicPath", musicPath);
                            startActivityForResult(intent, VIDEO_SETTING_RESULT);
                        }
                        else{
                            Toast.makeText(MainActivity.this, "No image is selected!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        // OnClick function for sort to left button
        btnSortLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Dialog Listener
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };

                // if no image is selected, create the dialog else sort the selected image one position to the left
                if(selectedImagePath != null){

                    if(selectedImagePos-1 >= 0) {

                        ImageObject leftImageTemp = dataT.get(selectedImagePos - 1);

                        dataT.set((selectedImagePos - 1), dataT.get(selectedImagePos));
                        dataT.set(selectedImagePos, leftImageTemp);

                        // Updates the gridView with the new values
                        adapter.addAll(dataT);

                        selectedImagePos = selectedImagePos - 1;
                        selectedImagePath = dataT.get(selectedImagePos).sdcardPath;

                    }

                } else {
                    // Create the Dialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setMessage("Please select an image").setNegativeButton("Ok", dialogClickListener).show();
                }
            }
        });


        // OnClick function for sort to right button
        btnSortRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Dialog Listener
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };

                // if no image is selected, create the dialog else sort the selected image one position to the right
                if(selectedImagePath != null){

                    if(selectedImagePos+1 <= dataT.size()-1) {

                        ImageObject rightImageTemp = dataT.get(selectedImagePos + 1);

                        dataT.set((selectedImagePos + 1), dataT.get(selectedImagePos));
                        dataT.set(selectedImagePos, rightImageTemp);

                        // Updates the gridView with the new values
                        adapter.addAll(dataT);

                        selectedImagePos = selectedImagePos + 1;
                        selectedImagePath = dataT.get(selectedImagePos).sdcardPath;
                    }

                } else {
                    // Creates the dialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setMessage("Please select an image").setNegativeButton("Ok", dialogClickListener).show();
                }
            }
        });

        // OnClick function for deleting an image
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Dialog Listener
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                // Deletes the selected image
                                viewSwitcher.setDisplayedChild(0);
                                dataT.remove(selectedImagePos);
                                adapter.addAll(dataT);
                                selectedImagePos = -1;
                                selectedImagePath = null;

                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };

                // if no image is selected, create the dialog else create a dialog for deleting the slected image
                if(selectedImagePos != -1){
                    // Creates a dialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setMessage("Do you really want to delete this image?").setPositiveButton("Yes", dialogClickListener)
                            .setNegativeButton("No", dialogClickListener).show();
                } else {
                    // Creates a dialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setMessage("Please select an image").setNegativeButton("Ok", dialogClickListener).show();
                }
            }
        });

        // OnClick function for deleting ALL images
        btnDeleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Dialog listener
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                // Deletes all images
                                dataT = new ArrayList<>();
                                adapter.addAll(dataT);
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };

                // Creates a dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setMessage("Do you really want to delete ALL images?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // Handles the result from the native android galery
            if (requestCode == PICK_IMAGE_MULTIPLE && resultCode == RESULT_OK && null != data) {
                // when a single image is picked
                imagesEncodedList = new ArrayList<String>();
                if(data.getData()!=null){
                    Uri mImageUri=data.getData();
                    // converts the URI to a real path
                    imageEncoded  = getRealPathFromURI_API19(getApplicationContext(), mImageUri);
                    imagesEncodedList.add(imageEncoded);
                }else {
                    // when multiple images are picked
                    if (data.getClipData() != null) {
                        ClipData mClipData = data.getClipData();
                        for (int i = 0; i < mClipData.getItemCount(); i++) {
                            ClipData.Item item = mClipData.getItemAt(i);
                            Uri uri = item.getUri();
                            // converts the URI to a real path
                            imageEncoded  = getRealPathFromURI_API19(getApplicationContext(), uri);
                            imagesEncodedList.add(imageEncoded);
                        }
                    }
                }

                // Insert all (no duplicates) images in the right order to the main gridView array
                int x = dataT.size();

                for (String string : imagesEncodedList) {
                    ImageObject item = new ImageObject();

                    boolean addItem = true;
                    for(int i = 0; i < dataT.size(); i++){
                        String path = dataT.get(i).sdcardPath;
                        if(string.equals(path)){
                            addItem = false;
                        }
                    }

                    if(addItem) {
                        item.sdcardPath = string;
                        item.position = x;
                        dataT.add(item);
                        x++;
                    }
                }
                viewSwitcher.setDisplayedChild(0);
                adapter.addAll(dataT);

            }
        } catch (Exception e) {
            // In case of an error
            Toast.makeText(this, "Something went wrong, please try again", Toast.LENGTH_LONG).show();
        }

        // Get the selected music for the video
        if(requestCode == MUSIC_PICKER && resultCode == RESULT_OK){
            if (data.getStringExtra("selectedMusicPath") != null) {
                // Get the music path from the intent
                this.musicPath = data.getStringExtra("selectedMusicPath");
            }
        }

        // Handles the result from the SDK activity
        if (requestCode == EDITOR_RESULT && resultCode == RESULT_OK) {
            // Switches the old image with the new generated image
            String newPath=data.getStringExtra("getNewPath");
            int imagePos = data.getIntExtra("getImagePos", -1);
            ImageObject cgNew = new ImageObject();
            cgNew.sdcardPath = newPath;
            cgNew.position = imagePos;
            dataT.set(imagePos, cgNew);
            adapter.addAll(dataT);
        }

        // Handles the result from the video setting activity
        if (requestCode == VIDEO_SETTING_RESULT && resultCode == RESULT_OK) {
            int deleteAll = data.getIntExtra("deleteAll", -1);

            // In case the video is created we get the number 1 as result und delete the gridView and the selected music
            if (deleteAll == 1) {
                this.musicPath = null;
                dataT = new ArrayList<>();
                adapter.addAll(dataT);
            }

        }

    }

    // get real path from URI
    public static String getRealPathFromURI_API19(Context context, Uri uri) {
        String filePath = "";
        if (uri.getHost().contains("com.android.providers.media")) {
            // Image pick from recent
            String wholeID = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                wholeID = DocumentsContract.getDocumentId(uri);
            }

            // Split at colon, use second item in the array
            String id = wholeID.split(":")[1];

            String[] column = {MediaStore.Images.Media.DATA};

            // where id is equal to
            String sel = MediaStore.Images.Media._ID + "=?";

            Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    column, sel, new String[]{id}, null);

            int columnIndex = cursor.getColumnIndex(column[0]);

            if (cursor.moveToFirst()) {
                filePath = cursor.getString(columnIndex);
            }
            cursor.close();
            return filePath;
        } else {
            // image pick from gallery
            return  getRealPathFromURI(context,uri);
        }

    }

    // get real path from URI
    public static String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri, proj, null,
                    null, null);
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    // Extract the path from custom gallery objects which is necessary for creating a video
    public ArrayList<String> extractPathFromCustomGallery(ArrayList<ImageObject> imageObject){
        ArrayList<String> selectedImagesPathList = new ArrayList<>();

        for (ImageObject cg : imageObject) {
            selectedImagesPathList.add(cg.getPath());
        }
        return selectedImagesPathList;
    }

}