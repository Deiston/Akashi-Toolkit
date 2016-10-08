package rikka.akashitoolkit.res;

import android.content.Context;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.AnimatedStateListDrawable;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatDrawableManager;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;

/**
 * Created by Rikka on 2016/10/8.
 */

public class AppCompatStateListDrawableResource {

    private static final String LOG_TAG = "ASLDrawableManager";

    private AppCompatStateListDrawableResource() {
    }

    /**
     * Returns the {@link AnimatedStateListDrawable} (API 21+) or {@link StateListDrawable} from the given resource.
     *
     * @param context context to inflate against
     * @param resId   the resource identifier of the Drawable to retrieve
     */
    public static Drawable getStateListDrawable(@NonNull Context context, @DrawableRes int resId) {
        if (Build.VERSION.SDK_INT >= 21) {
            // On M+ we can use the framework
            return context.getDrawable(resId);
        }

        Drawable dr = inflate(context, resId);
        if (dr != null) {
            return dr;
        }

        return AppCompatDrawableManager.get().getDrawable(context, resId);
    }

    /**
     * Inflates a {@link Drawable} from resources.
     */
    @Nullable
    private static Drawable inflate(Context context, int resId) {
        final Resources r = context.getResources();
        final XmlPullParser xml = r.getXml(resId);
        try {
            return AppCompatStateListDrawableInflater.createFromXml(r, xml, context.getTheme());
        } catch (Exception e) {
            Log.e(LOG_TAG, "Failed to inflate StateListDrawable, leaving it to the framework", e);
        }
        return null;
    }
}
