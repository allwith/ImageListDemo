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


class ImageListAdapter extends BaseRecyclerViewAdapter<ImageListAdapter.ImageHolder, ImageInfo> {

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

        final ImageInfo imageInfo = mDataList.get(position);
        holder.mImageIv.setImageResource(R.color.defaultImageSource);
        if (imageInfo.getHeight() != mHeight) {
            if (imageInfo.getHeight() == 0) {
                imageInfo.setWidth(mHeight);
                imageInfo.setHeight(mHeight);
                imageInfo.setNeedResize(true);
            } else {
                int width = mHeight * imageInfo.getWidth() / imageInfo.getHeight();
                imageInfo.setWidth(Math.min(width, mMaxWidth));
                imageInfo.setHeight(mHeight);
            }
        }
        ViewGroup.LayoutParams layoutParams = holder.mImageIv.getLayoutParams();
        layoutParams.height = imageInfo.getHeight();
        layoutParams.width = imageInfo.getWidth();
        holder.mImageIv.setLayoutParams(layoutParams);
        holder.mImageIv.setTag(imageInfo.getUri().getPath());
        mRequestManager.loadFromMediaStore(imageInfo.getUri())
                .asBitmap()
                .centerCrop()
                .override(imageInfo.getWidth(), imageInfo.getHeight())
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        if (holder.mImageIv.getTag().equals(imageInfo.getUri().getPath())) {
                            if (imageInfo.isNeedResize()) {
                                imageInfo.setHeight(resource.getHeight());
                                imageInfo.setWidth(resource.getWidth());
                                resizeImageView(holder.mImageIv, imageInfo);
                                imageInfo.setNeedResize(false);
                            }
                            holder.mImageIv.setImageBitmap(resource);
                        }
                    }
                });
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
