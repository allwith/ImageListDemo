package lt.bitmap;

import android.net.Uri;

public class Image {
    private final Uri mUri;
    private int width;
    private int height;

    public Image(Uri uri, int width, int height) {
        mUri = uri;
        this.width = width;
        this.height = height;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public Uri getUri() {
        return mUri;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

}
