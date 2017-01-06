package lt.bitmap;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;


class ImageListAdapter extends BaseRecyclerViewAdapter<ImageListAdapter.ImageHolder, Image> {

    private static final String TAG = "ImageListAdapter";

    private RequestManager mRequestManager;
    private int mHeight = 0;
    private int mMaxWidth;

    ImageListAdapter(Context context) {
        super(context);
        mRequestManager = Glide.with(mContext.getApplicationContext());
        mMaxWidth = Utils.dip2px(context, 300);
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
    public ImageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ImageHolder(mInflater.inflate(R.layout.listitem_image, parent, false));
    }

    @Override
    public void onBindViewHolder(final ImageHolder holder, int position) {
        if (mHeight == 0) return;

        final Image image = mDataList.get(position);
        holder.mImageIv.setImageResource(R.color.defaultImageSource);
        if (image.getHeight() != mHeight) {
            if (image.getHeight() == 0) {
                image.setWidth(mHeight);
                image.setHeight(mHeight);
                image.setNeedResize(true);
            } else {
                int width = mHeight * image.getWidth() / image.getHeight();
                image.setWidth(Math.min(width, mMaxWidth));
                image.setHeight(mHeight);
            }
        }
        ViewGroup.LayoutParams layoutParams = holder.mImageIv.getLayoutParams();
        layoutParams.height = image.getHeight();
        layoutParams.width = image.getWidth();
        holder.mImageIv.setLayoutParams(layoutParams);
        holder.mImageIv.setTag(image.getUri().getPath());
        mRequestManager.loadFromMediaStore(image.getUri())
                .asBitmap()
                .centerCrop()
                .override(image.getWidth(), image.getHeight())
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        if (holder.mImageIv.getTag().equals(image.getUri().getPath())) {
                            if (image.isNeedResize()) {
                                image.setHeight(resource.getHeight());
                                image.setWidth(resource.getWidth());
                                resizeImageView(holder.mImageIv, image);
                                image.setNeedResize(false);
                            }
                            holder.mImageIv.setImageBitmap(resource);
                        }
                    }
                });
    }

    private static void resizeImageView(ImageView imageView, Image image) {
        ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
        layoutParams.height = image.getHeight();
        layoutParams.width = image.getWidth();
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
