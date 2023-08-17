package com.gokulsundar4545.connectwithpeople.Adapter;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.List;

public class GalleryUtilsvedio {
    public static List<String> getAllVideos(Context context) {
        List<String> videoPaths = new ArrayList<>();
        String[] projection = {MediaStore.Video.Media.DATA};
        Cursor cursor = context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String videoPath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
                videoPaths.add(videoPath);
            }
            cursor.close();
        }
        return videoPaths;
    }
}
