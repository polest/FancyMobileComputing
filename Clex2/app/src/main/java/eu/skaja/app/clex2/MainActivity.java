package eu.skaja.app.clex2;

import java.io.IOException;
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
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
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

    private String selectedImagePath = null;
    private int selectedImagePos = -1;
	private boolean imagesPicked = false;
	private boolean musicPicked = false;
	private Button btnCreate;
	private Button btnDelete;
	private Button btnPickMusic;
	private Button btnSelectImages;
	private Button btnSortLeft;
	private Button btnSortRight;
	private Button btnStartEditor;
	private GalleryAdapter adapter;
	private GridView gridGallery;
	private Handler handler;
	private ImageLoader imageLoader;
	private ImageView imgSinglePick;
	private int PICK_IMAGE_MULTIPLE = 1;
    private int EDITOR_RESULT = 2;
	private List<String> imagesEncodedList;
	private String action;
	private String imageEncoded;
	private String musicPath;
	private String selectedImage;
    private int toggle;
	private ViewSwitcher viewSwitcher;
	private AdapterView<?> adapterView;
	private CreateVideo video;
    ArrayList<CustomGallery> dataT = new ArrayList<CustomGallery>();

	public static final int CAMERA_PREVIEW_RESULT = 3;
	public static final int MUSIC_PICKER = 4;


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
            //adapter.notifyDataSetChanged();
            }
        };

        //Leiste oben
		btnCreate = (Button) findViewById(R.id.btnCreate);
		btnDelete = (Button)findViewById(R.id.btnDelete);
		btnSortRight = (Button)findViewById(R.id.btnSortDown);
		btnSortLeft = (Button)findViewById(R.id.btnSortUp);
		btnPickMusic = (Button) findViewById(R.id.btnPickMusic);
		btnStartEditor = (Button)findViewById(R.id.btnStartEditor);
		btnSelectImages = (Button) findViewById(R.id.btnSelectImages);
		imgSinglePick = (ImageView) findViewById(R.id.imgSinglePick);
		viewSwitcher = (ViewSwitcher) findViewById(R.id.viewSwitcher);

		gridGallery.setOnItemClickListener(mItemMulClickListener);
		gridGallery.setAdapter(adapter);

		viewSwitcher.setDisplayedChild(1);

		btnStartEditor.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

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

		this.musicPath = "";
		btnPickMusic.setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(MainActivity.this, MusicPicker.class);
						startActivityForResult(intent, MUSIC_PICKER);
					}
				}
		);

		btnSelectImages.setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
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

		btnCreate.setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						try {
							// Create video with given image paths and music path
							Toast.makeText(getApplicationContext(),"Video ist created",Toast.LENGTH_SHORT).show();
							video = new CreateVideo(imagesEncodedList, musicPath);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
		);


		btnSortLeft.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {


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


                if(selectedImagePath != null){

                    if(selectedImagePos-1 >= 0) {

                        CustomGallery leftImageTemp = dataT.get(selectedImagePos - 1);

                        dataT.set((selectedImagePos - 1), dataT.get(selectedImagePos));
                        dataT.set(selectedImagePos, leftImageTemp);

                        adapter.addAll(dataT);

                        selectedImagePos = selectedImagePos - 1;
                        selectedImagePath = dataT.get(selectedImagePos).sdcardPath;
                        //adapter.setUnselectedPath();

                    }

                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setMessage("Please select an image").setNegativeButton("Ok", dialogClickListener).show();
                }






			}
		});


        btnSortRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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


                if(selectedImagePath != null){

                    if(selectedImagePos+1 <= dataT.size()-1) {

                        CustomGallery rightImageTemp = dataT.get(selectedImagePos + 1);

                        dataT.set((selectedImagePos + 1), dataT.get(selectedImagePos));
                        dataT.set(selectedImagePos, rightImageTemp);

                        adapter.addAll(dataT);

                        selectedImagePos = selectedImagePos + 1;
                        selectedImagePath = dataT.get(selectedImagePos).sdcardPath;
                        //adapter.setUnselectedPath();

                    }

                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setMessage("Please select an image").setNegativeButton("Ok", dialogClickListener).show();
                }








            }
        });


        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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


        if(selectedImagePos != -1){
            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
            builder.setMessage("Do you really want to delete this image?").setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
            builder.setMessage("Please select an image").setNegativeButton("Ok", dialogClickListener).show();
        }



            }
        });






	}

	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
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

				int x = 0;

				for (String string : imagesEncodedList) {
					CustomGallery item = new CustomGallery();
					item.sdcardPath = string;
                    item.position = x;
					dataT.add(item);
                    x++;
				}

				viewSwitcher.setDisplayedChild(0);
				adapter.addAll(dataT);
				this.imagesPicked = true;

            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong, please try again", Toast.LENGTH_LONG).show();
        }

		// Get the selected music for the video
		if(requestCode == MUSIC_PICKER && resultCode == RESULT_OK){
			// Get the music path from the intent
			this.musicPath = data.getStringExtra("selectedMusicPath");
			// Set boolean for picked music on true to give the hint
			// that the music for the video is selected
			this.musicPicked = true;
		}

        if (requestCode == EDITOR_RESULT && resultCode == RESULT_OK) {

                String newPath=data.getStringExtra("getNewPath");
				int imagePos = data.getIntExtra("getImagePos", -1);

                CustomGallery cgNew = new CustomGallery();
                cgNew.sdcardPath = newPath;
                cgNew.position = imagePos;

				dataT.set(imagePos, cgNew);
                adapter.addAll(dataT);

        }


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

    // Check if video can be created
	public void checkCreation() {
		if(this.imagesEncodedList.isEmpty() || this.musicPath.isEmpty()){
			btnCreate.setClickable(false);
		}else{
			btnCreate.setClickable(true);
		}
	}




}
