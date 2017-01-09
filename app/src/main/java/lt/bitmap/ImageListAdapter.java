package lt.bitmap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.HashMap;
import java.util.Map;


class ImageListAdapter extends BaseRecyclerViewAdapter<ImageListAdapter.ImageHolder, ImageInfo> {

    private static final String TAG = "ImageListAdapter";

    private Picasso mPicasso;
    private int mHeight = 0;
    private int mMaxWidth;
    private final Map<String, Target> mTargetMap;

    ImageListAdapter(Context context) {
        super(context);
        mPicasso = new Picasso.Builder(context.getApplicationContext())
                .listener(new Picasso.Listener() {
                    @Override
                    public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                        Log.e(TAG, uri.toString(), exception);
                    }
                }).build();
        mPicasso.setLoggingEnabled(true);
        mMaxWidth = Utils.dip2px(context, 300);
        mTargetMap = new HashMap<>();
    }


    void setHeight(int height) {
        Log.i(TAG, "height:" + height);
        mHeight = height;
        notifyDataSetChanged();
    }

    int getHeight() {
        return mHeight;
    }

    @Override
    public ImageListAdapter.ImageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ImageListAdapter.ImageHolder(mInflater.inflate(R.layout.listitem_image, parent, false));
    }

    @Override
    public void onBindViewHolder(final ImageListAdapter.ImageHolder holder, int position) {
        if (mHeight == 0) return;

        final ImageInfo imageInfo = mDataList.get(position);
        holder.mImageIv.setImageResource(R.color.defaultImageSource);
        if (imageInfo.getHeight() != mHeight) {
            if (imageInfo.getHeight() == 0) {
                imageInfo.setHeight(mHeight);
                imageInfo.setWidth(mHeight);
                imageInfo.setNeedResize(true);
            } else {
                int width = mHeight * imageInfo.getWidth() / imageInfo.getHeight();
                imageInfo.setWidth(Math.min(width, mMaxWidth));
                imageInfo.setHeight(mHeight);
            }
        }

        resizeImageView(holder.mImageIv, imageInfo);
        holder.mImageIv.setTag(imageInfo.getUri().getPath());

        Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                if (holder.mImageIv.getTag().equals(imageInfo.getUri().getPath())) {
                    if (imageInfo.isNeedResize()) {
                        imageInfo.setHeight(bitmap.getHeight());
                        imageInfo.setWidth(bitmap.getWidth());
                        resizeImageView(holder.mImageIv, imageInfo);
                        imageInfo.setNeedResize(false);
                    }
                    holder.mImageIv.setImageBitmap(bitmap);
                }
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                Log.i(TAG, "onPrepareLoad");
            }
        };
        mTargetMap.put(imageInfo.getUri().toString(), target);

        mPicasso.load(imageInfo.getUri())
                .resize(imageInfo.getWidth(), imageInfo.getHeight())
                .config(Bitmap.Config.RGB_565)
                .centerCrop()
                .into(mTargetMap.get(imageInfo.getUri().toString()));
    }

    private static void resizeImageView(ImageView imageView, ImageInfo imageInfo) {
        ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
        layoutParams.height = imageInfo.getHeight();
        layoutParams.width = imageInfo.getWidth();
        imageView.setLayoutParams(layoutParams);
    }

    class ImageHolder extends RecyclerView.ViewHolder {

        ImageView mImageIv;

        ImageHolder(View itemView) {
            super(itemView);
            mImageIv = (ImageView) itemView.findViewById(R.id.image_iv);
        }
    }
}
