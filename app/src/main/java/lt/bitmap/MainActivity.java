package lt.bitmap;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int READ_EXTERNAL_STORAGE = 100;
    private static final int MAX_IMAGE = 100;

    private List<ImageInfo> mImageInfoList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private ImageListAdapter mImageListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.image_rv);
        mImageListAdapter = new ImageListAdapter(this);
        mRecyclerView.setAdapter(mImageListAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                int height = mRecyclerView.getHeight();
                if (mImageListAdapter.getHeight() != height) {
                    mImageListAdapter.setHeight(height);
                }
            }
        });

        if (checkPermission()) {
            loadImage();
        }
    }

    private boolean checkPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE);
            return false;
        } else {
            return true;
        }
    }

    private void loadImage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Cursor mCursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        new String[]{MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA,
                                MediaStore.Images.Media.WIDTH, MediaStore.Images.Media.HEIGHT},
                        MediaStore.Images.Media.MIME_TYPE + "=? OR " + MediaStore.Images.Media.MIME_TYPE + "=?",
                        new String[] { "image/jpeg", "image/png" }, MediaStore.Images.Media._ID + " DESC");

                if (mCursor == null) return;
                // Take 100 images
                while (mCursor.moveToNext() && mImageInfoList.size() < MAX_IMAGE) {
                    long id = mCursor.getLong(mCursor.getColumnIndex(MediaStore.Images.Media._ID));

                    String path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA));
                    int height = mCursor.getInt(mCursor.getColumnIndex(MediaStore.Images.Media.HEIGHT));
                    int width = mCursor.getInt(mCursor.getColumnIndex(MediaStore.Images.Media.WIDTH));
                    Uri uri = Uri.fromFile(new File(path));
                    ImageInfo imageInfo = new ImageInfo(uri, width, height);
                    Log.i(TAG, imageInfo.toString());
                    mImageInfoList.add(imageInfo);
                }
                mCursor.close();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mImageListAdapter.addAllData(mImageInfoList);
                        mImageListAdapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case READ_EXTERNAL_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    loadImage();
                } else {
                    Toast.makeText(this, "不给权限就不给用", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }

    }
}
