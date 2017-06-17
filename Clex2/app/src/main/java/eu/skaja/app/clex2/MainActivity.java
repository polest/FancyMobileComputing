package eu.skaja.app.clex2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

public class MainActivity extends Activity {

    private Button btnCreate;
    private Button btnSelectImages;
    private Button btnPickMusic;
    private GalleryAdapter adapter;
    private GridView gridGallery;
    private Handler handler;
    private ImageLoader imageLoader;
    private ImageView imgSinglePick;
    private int PICK_IMAGE_MULTIPLE = 1;
    private List<String> imagesEncodedList;
    private String action;
    private String selectedImage;
    private String imageEncoded;
    private ViewSwitcher viewSwitcher;
	private Button btnSortUp;
	private Button btnSortDown;
	private Button btnDelete;
	private Button btnStartEditor;
	private String musicPath;

	public static int CAMERA_PREVIEW_RESULT = 1;


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

		handler = new Handler();
		gridGallery = (GridView) findViewById(R.id.gridGallery);
		gridGallery.setFastScrollEnabled(true);
		adapter = new GalleryAdapter(getApplicationContext(), imageLoader);
		adapter.setMultiplePick(false);

        AdapterView.OnItemClickListener mItemMulClickListener = new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> l, View v, int position, long id) {

                Drawable highlight = getResources().getDrawable( R.drawable.border );

                selectedImage = adapter.getItem(position).sdcardPath;

                Toast.makeText(MainActivity.this, adapter.getItem(position).sdcardPath, Toast.LENGTH_SHORT).show();
                //adapter.getItem(position).

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    v.setBackground(highlight);
                }

            }
        };

        //Leiste oben
		btnCreate = (Button) findViewById(R.id.btnCreate);
		btnSortUp = (Button)findViewById(R.id.btnSortUp);
		btnSortDown = (Button)findViewById(R.id.btnSortDown);
		btnDelete = (Button)findViewById(R.id.btnDelete);
		btnStartEditor = (Button)findViewById(R.id.btnStartEditor);

		btnSelectImages = (Button) findViewById(R.id.btnSelectImages);
		viewSwitcher = (ViewSwitcher) findViewById(R.id.viewSwitcher);
		btnPickMusic = (Button) findViewById(R.id.btnPickMusic);

		gridGallery.setOnItemClickListener(mItemMulClickListener);
        gridGallery.setAdapter(adapter);

		viewSwitcher.setDisplayedChild(1);


		imgSinglePick = (ImageView) findViewById(R.id.imgSinglePick);


		btnStartEditor.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, PhotoEditor.class);
				intent.putExtra("selectedImagePath", selectedImage);
                startActivity(intent);
			}
		});

		this.musicPath = "";
		btnPickMusic.setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(MainActivity.this, MusicPicker.class);
						intent.putExtra("selectedImagePath", selectedImage);
						startActivity(intent);
					}
				}
		);

		btnSelectImages.setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
                        Intent i = new Intent(Intent.ACTION_GET_CONTENT,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        i.setType("image/*");
                        i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                        startActivityForResult(i, PICK_IMAGE_MULTIPLE);
					}
				}
		);

	}


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            // When an Image is picked
            if (requestCode == PICK_IMAGE_MULTIPLE && resultCode == RESULT_OK
                    && null != data) {


                imagesEncodedList = new ArrayList<String>();
                if(data.getData()!=null){

                    Uri mImageUri=data.getData();

                    imageEncoded  = getRealPathFromURI_API19(getApplicationContext(), mImageUri);
                    imagesEncodedList.add(imageEncoded);

                }else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        if (data.getClipData() != null) {
                            ClipData mClipData = data.getClipData();

                            for (int i = 0; i < mClipData.getItemCount(); i++) {

                                ClipData.Item item = mClipData.getItemAt(i);
                                Uri uri = item.getUri();

                                imageEncoded  = getRealPathFromURI_API19(getApplicationContext(), uri);
                                imagesEncodedList.add(imageEncoded);

                            }
						}
                    }
                }

                ArrayList<CustomGallery> dataT = new ArrayList<CustomGallery>();
                for (String string : imagesEncodedList) {
                    CustomGallery item = new CustomGallery();
                    item.sdcardPath = string;
                    dataT.add(item);
                }

                viewSwitcher.setDisplayedChild(0);
                adapter.addAll(dataT);



            } else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


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

}
