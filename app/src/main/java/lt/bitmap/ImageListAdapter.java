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


class ImageListAdapter extends BaseRecyclerViewAdapter<ImageListAdapter.ImageHolder, Image> {

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

        final Image image = mDataList.get(position);
        holder.mImageIv.setImageResource(R.color.defaultImageSource);
        if (image.getHeight() != mHeight) {
            int width = mHeight * image.getWidth() / image.getHeight();
            image.setWidth(Math.min(width, mMaxWidth));
            image.setHeight(mHeight);
        }
        ViewGroup.LayoutParams layoutParams = holder.mImageIv.getLayoutParams();
        layoutParams.height = image.getHeight();
        layoutParams.width = image.getWidth();
        holder.mImageIv.setLayoutParams(layoutParams);
        holder.mImageIv.setTag(image.getUri().getPath());

        Log.i(TAG, "start load:" + image.getUri().toString());
        Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                if (holder.mImageIv.getTag().equals(image.getUri().getPath())) {
                    Log.i(TAG, "load success:" + image.getUri().toString());
                    Log.i(TAG, "from:" + from);
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
        mTargetMap.put(image.getUri().toString(), target);

        mPicasso.load(image.getUri())
                .resize(image.getWidth(), image.getHeight())
                .config(Bitmap.Config.RGB_565)
                .centerInside()
                .into(mTargetMap.get(image.getUri().toString()));
    }

    class ImageHolder extends RecyclerView.ViewHolder {

        ImageView mImageIv;

        ImageHolder(View itemView) {
            super(itemView);
            mImageIv = (ImageView) itemView.findViewById(R.id.image_iv);
        }
    }
}
