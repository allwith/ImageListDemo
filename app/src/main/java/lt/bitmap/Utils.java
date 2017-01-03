package lt.bitmap;

import android.content.Context;


/**
 * util set
 * Created by ldd on 2016/11/7.
 */

public class Utils {

    public static <T> T checkNotNull(T reference) {
        if (reference == null) {
            throw new NullPointerException();
        }
        return reference;
    }

    public static <T> T checkNotNull(T reference, String cause) {
        if (reference == null) {
            throw new NullPointerException(cause);
        }
        return reference;
    }

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}
