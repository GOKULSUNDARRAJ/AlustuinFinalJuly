package com.gokulsundar4545.connectwithpeople;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.ChangeBounds;
import androidx.transition.TransitionManager;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ja.burhanrashid52.photoeditor.OnPhotoEditorListener;
import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;
import ja.burhanrashid52.photoeditor.PhotoFilter;
import ja.burhanrashid52.photoeditor.ViewType;

public class EditImageActivitystory extends BaseActivity implements OnPhotoEditorListener, View.OnClickListener,
        PropertiesBSFragment.Properties, EmojiBSFragment.EmojiListener, StickerBSFragment.StickerListener,
        EditingToolsAdapter.OnItemSelected, FilterListener {

    private ScaleGestureDetector mScaleGestureDetector;
    private float mScaleFactor = 1.f;

    private PhotoEditor mPhotoEditor;
    private PhotoEditorView mPhotoEditorView;
    private PropertiesBSFragment mPropertiesBSFragment;
    private EmojiBSFragment mEmojiBSFragment;
    private StickerBSFragment mStickerBSFragment;
    private TextView mTxtCurrentTool;
    private Typeface mWonderFont;
    private RecyclerView mRvTools;
    private RecyclerView mRvFilters;
    private EditingToolsAdapter mEditingToolsAdapter;
    private FilterViewAdapter mFilterViewAdapter;
    private ConstraintLayout mRootView;
    private ConstraintSet mConstraintSet;
    private boolean mIsFilterVisible = false;

    private static final String TAG = EditImageActivitystory.class.getSimpleName();
    public static final String EXTRA_IMAGE_PATHS = "extra_image_pathsstory";
    private static final int CAMERA_REQUEST = 52;
    private static final int PICK_REQUEST = 53;
    private static final int REQUEST_STORAGE_PERMISSION = 101;
    private SharedPreferences sharedPreferences;
    private SharedPreferences sharedPreferences2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        makeFullScreen();
        setContentView(R.layout.activity_edit_image);
        initViews();
        sharedPreferences = getSharedPreferences("ModifiedImagePrefsstory", Context.MODE_PRIVATE);
        sharedPreferences2 = getSharedPreferences("ModifiedImagePrefsfinalstory", Context.MODE_PRIVATE);
        String modifiedImageUriString = sharedPreferences.getString("modified_image_uristory", null);

        if (modifiedImageUriString != null) {
            Uri modifiedImageUri = Uri.parse(modifiedImageUriString);
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), modifiedImageUri);
                mPhotoEditorView.getSource().setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
                // Handle error loading image
            }
        } else {
            // Handle case where modifiedImageUriString is null
        }


        mWonderFont = Typeface.createFromAsset(getAssets(), "beyond_wonderland.ttf");

        mPropertiesBSFragment = new PropertiesBSFragment();
        mEmojiBSFragment = new EmojiBSFragment();
        mStickerBSFragment = new StickerBSFragment();
        mStickerBSFragment.setStickerListener(this);
        mEmojiBSFragment.setEmojiListener(this);
        mPropertiesBSFragment.setPropertiesChangeListener(this);
        mEditingToolsAdapter = new EditingToolsAdapter(this);
        mFilterViewAdapter = new FilterViewAdapter(this);
        LinearLayoutManager llmTools = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRvTools.setLayoutManager(llmTools);
        mRvTools.setAdapter(mEditingToolsAdapter);
        LinearLayoutManager llmFilters = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRvFilters.setLayoutManager(llmFilters);
        mRvFilters.setAdapter(mFilterViewAdapter);

        mPhotoEditor = new PhotoEditor.Builder(this, mPhotoEditorView)
                .setPinchTextScalable(true)
                .build();

        mPhotoEditor.setOnPhotoEditorListener(this);
    }

    private void initViews() {
        ImageView imgUndo;
        ImageView imgRedo;
        ImageView imgCamera;
        ImageView imgGallery;
        TextView imgSave;
        ImageView imgClose;

        mPhotoEditorView = findViewById(R.id.photoEditorView);
        mTxtCurrentTool = findViewById(R.id.txtCurrentTool);
        mRvTools = findViewById(R.id.rvConstraintTools);
        mRvFilters = findViewById(R.id.rvFilterView);
        mRootView = findViewById(R.id.rootView);

        imgUndo = findViewById(R.id.imgUndo);
        imgUndo.setOnClickListener(this);
        imgRedo = findViewById(R.id.imgRedo);
        imgRedo.setOnClickListener(this);
        imgCamera = findViewById(R.id.imgCamera);
        imgCamera.setOnClickListener(this);
        imgGallery = findViewById(R.id.imgGallery);
        imgGallery.setOnClickListener(this);
        imgSave = findViewById(R.id.imgSave);
        imgSave.setOnClickListener(this);
        imgClose = findViewById(R.id.imgClose);
        imgClose.setOnClickListener(this);

        mScaleGestureDetector = new ScaleGestureDetector(this, new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                mScaleFactor *= detector.getScaleFactor();
                mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 5.0f)); // Limit scale factor
                mPhotoEditorView.setScaleX(mScaleFactor);
                mPhotoEditorView.setScaleY(mScaleFactor);
                return true;
            }
        });

        mPhotoEditorView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mScaleGestureDetector.onTouchEvent(event);
                return true;
            }
        });
    }

    @Override
    public void onEditTextChangeListener(View rootView, String text, int colorCode) {
        TextEditorDialogFragment textEditorDialogFragment = TextEditorDialogFragment.show(this, text, colorCode);
        textEditorDialogFragment.setOnTextEditorListener(new TextEditorDialogFragment.TextEditor() {
            @Override
            public void onDone(String inputText, int colorCode) {
                mPhotoEditor.editText(rootView, inputText, colorCode);
                mTxtCurrentTool.setText(R.string.label_text);
            }
        });
    }

    @Override
    public void onAddViewListener(ViewType viewType, int numberOfAddedViews) {
        Log.d(TAG, "onAddViewListener() called with: viewType = [" + viewType + "], numberOfAddedViews = [" + numberOfAddedViews + "]");
    }

    @Override
    public void onRemoveViewListener(ViewType viewType, int numberOfAddedViews) {
        Log.d(TAG, "onRemoveViewListener() called with: viewType = [" + viewType + "], numberOfAddedViews = [" + numberOfAddedViews + "]");
    }

    @Override
    public void onStartViewChangeListener(ViewType viewType) {
        Log.d(TAG, "onStartViewChangeListener() called with: viewType = [" + viewType + "]");
    }

    @Override
    public void onStopViewChangeListener(ViewType viewType) {
        Log.d(TAG, "onStopViewChangeListener() called with: viewType = [" + viewType + "]");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgUndo:
                mPhotoEditor.undo();
                break;
            case R.id.imgRedo:
                mPhotoEditor.redo();
                break;
            case R.id.imgSave:
                if (isStoragePermissionGranted()) {
                    saveImage();
                }
                break;
            case R.id.imgClose:
                onBackPressed();
                break;
            case R.id.imgCamera:
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
                break;
            case R.id.imgGallery:
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_REQUEST);
                break;
        }
    }

    @SuppressLint("MissingPermission")
    private boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            // Permissions are granted at install time on older SDK versions
            return true;
        }

        // Check if permission is granted
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            // Request permission
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE_PERMISSION);
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with your logic
                saveImage();
            } else {
                // Permission denied, handle accordingly (e.g., show a message)
                Toast.makeText(this, "Storage permission is required to save images.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveImage() {
        // Generate a filename with current timestamp
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String fileName = "edited_image_" + timeStamp + ".png";

        // Get the content resolver instance
        ContentResolver resolver = getContentResolver();

        // Create a ContentValues object to store the image metadata
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/YourAppName");

        // Use the content resolver to insert the image metadata into the MediaStore
        Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        if (imageUri != null) {
            try {
                // Open an output stream using the image URI
                OutputStream outputStream = resolver.openOutputStream(imageUri);

                // Get the scale factors
                float scaleX = mPhotoEditorView.getScaleX();
                float scaleY = mPhotoEditorView.getScaleY();

                // Create a bitmap from the PhotoEditorView
                Bitmap originalBitmap = Bitmap.createBitmap(
                        (int) (mPhotoEditorView.getWidth() * scaleX),
                        (int) (mPhotoEditorView.getHeight() * scaleY),
                        Bitmap.Config.ARGB_8888
                );
                Canvas canvas = new Canvas(originalBitmap);
                canvas.scale(scaleX, scaleY);
                mPhotoEditorView.draw(canvas);

                // Compress and write the bitmap to the output stream
                originalBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);

                // Close the output stream
                outputStream.close();

                // Image saved successfully
                Toast.makeText(this, "Image saved: " + imageUri.toString(), Toast.LENGTH_SHORT).show();

                // Save the URI of the modified image for further use
                SharedPreferences.Editor editor = sharedPreferences2.edit();
                editor.putString("modified_image_uri_finalstory", imageUri.toString());
                editor.apply();

                // Proceed to a new activity and pass the URI of the modified image
                Intent intent = new Intent(this, FinalPostEditActivitystory.class);
                startActivity(intent);

            } catch (IOException e) {
                // Handle IOException
                e.printStackTrace();
                Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Image insert failed
            Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show();
        }
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CAMERA_REQUEST:
                    mPhotoEditor.clearAllViews();
                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                    mPhotoEditorView.getSource().setImageBitmap(photo);
                    break;
                case PICK_REQUEST:
                    try {
                        mPhotoEditor.clearAllViews();
                        Uri uri = data.getData();
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                        mPhotoEditorView.getSource().setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }

    @Override
    public void onColorChanged(int colorCode) {
        mPhotoEditor.setBrushColor(colorCode);
        mTxtCurrentTool.setText(R.string.label_brush);
    }

    @Override
    public void onOpacityChanged(int opacity) {
        mPhotoEditor.setOpacity(opacity);
        mTxtCurrentTool.setText(R.string.label_brush);
    }

    @Override
    public void onBrushSizeChanged(int brushSize) {
        mPhotoEditor.setBrushSize(brushSize);
        mTxtCurrentTool.setText(R.string.label_brush);
    }

    @Override
    public void onEmojiClick(String emojiUnicode) {
        mPhotoEditor.addEmoji(emojiUnicode);
        mTxtCurrentTool.setText(R.string.label_emoji);
    }

    @Override
    public void onStickerClick(Bitmap bitmap) {
        mPhotoEditor.addImage(bitmap);
        mTxtCurrentTool.setText(R.string.label_sticker);
    }

    @Override
    public void isPermissionGranted(boolean isGranted, String permission) {
        if (isGranted) {
            saveImage();
        }
    }

    private void showSaveDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to exit without saving the image?");
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveImage();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setNeutralButton("Discard", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.create().show();
    }

    @Override
    public void onFilterSelected(PhotoFilter photoFilter) {
        mPhotoEditor.setFilterEffect(photoFilter);
    }

    @Override
    public void onToolSelected(ToolType toolType) {
        switch (toolType) {
            case BRUSH:
                mPhotoEditor.setBrushDrawingMode(true);
                mTxtCurrentTool.setText(R.string.label_brush);
                mPropertiesBSFragment.show(getSupportFragmentManager(), mPropertiesBSFragment.getTag());
                break;
            case TEXT:
                TextEditorDialogFragment textEditorDialogFragment = TextEditorDialogFragment.show(this);
                textEditorDialogFragment.setOnTextEditorListener(new TextEditorDialogFragment.TextEditor() {
                    @Override
                    public void onDone(String inputText, int colorCode) {
                        mPhotoEditor.addText(inputText, colorCode);
                        mTxtCurrentTool.setText(R.string.label_text);
                    }
                });
                break;
            case ERASER:
                mPhotoEditor.brushEraser();
                mTxtCurrentTool.setText(R.string.label_eraser);
                break;
            case FILTER:
                mTxtCurrentTool.setText(R.string.label_filter);
                showFilter(true);
                break;
            case EMOJI:
                mEmojiBSFragment.show(getSupportFragmentManager(), mEmojiBSFragment.getTag());
                break;
            case STICKER:
                mStickerBSFragment.show(getSupportFragmentManager(), mStickerBSFragment.getTag());
                break;
        }
    }

    private void showFilter(boolean isVisible) {
        mIsFilterVisible = isVisible;
        if (mConstraintSet == null) {
            mConstraintSet = new ConstraintSet();
        }
        mConstraintSet.clone(mRootView);
        if (isVisible) {
            mConstraintSet.clear(mRvFilters.getId(), ConstraintSet.END);
            mConstraintSet.connect(mRvFilters.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
            mConstraintSet.connect(mRvFilters.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
        } else {
            mConstraintSet.clear(mRvFilters.getId(), ConstraintSet.START);
            mConstraintSet.connect(mRvFilters.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
        }
        ChangeBounds changeBounds = new ChangeBounds();
        changeBounds.setDuration(350);
        changeBounds.setInterpolator(new AnticipateOvershootInterpolator(1.0f));
        TransitionManager.beginDelayedTransition(mRootView, changeBounds);
        mConstraintSet.applyTo(mRootView);
    }

    @Override
    public void onBackPressed() {
        if (mIsFilterVisible) {
            showFilter(false);
            mTxtCurrentTool.setText(R.string.app_name);
        } else if (!mPhotoEditor.isCacheEmpty()) {
            showSaveDialog();
        } else {
            super.onBackPressed();
        }
    }
}
