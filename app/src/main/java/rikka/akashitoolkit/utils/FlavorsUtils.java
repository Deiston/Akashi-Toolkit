package rikka.akashitoolkit.utils;

import rikka.akashitoolkit.BuildConfig;
import rikka.akashitoolkit.support.Settings;

/**
 * Created by Rikka on 2016/9/26.
 */

public class FlavorsUtils {

    /**
     * 返回是否是 Google Play 版，用于比如是否显示立绘等
     *
     * @return 是否是 Google Play 版
     */
    public static boolean isPlay() {
        return BuildConfig.isGooglePlay && !BuildConfig.DEBUG /* && !Settings.instance().getBoolean(Settings.DEVELOPER, false)*/;
    }
}
