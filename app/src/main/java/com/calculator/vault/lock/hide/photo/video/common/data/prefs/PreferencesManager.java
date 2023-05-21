package com.calculator.vault.lock.hide.photo.video.common.data.prefs;

import android.content.Context;
import android.content.SharedPreferences;

import com.calculator.vault.lock.hide.photo.video.BuildConfig;
import com.calculator.vault.lock.hide.photo.video.R;
import com.calculator.vault.lock.hide.photo.video.common.utils.Constant;
import com.calculator.vault.lock.hide.photo.video.model.AdsData;


public class PreferencesManager {
    public static void saveImageShowADs(Context context, Boolean value) {
        SharedPreferences preferences = context.getSharedPreferences(
                Constant.SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(Constant.SHARED_PREFS_IMAGE_SHOW, value);
        editor.apply();
    }

    public static Boolean getImageShowADs(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(
                Constant.SHARED_PREFS, Context.MODE_PRIVATE);
        return preferences.getBoolean(Constant.SHARED_PREFS_IMAGE_SHOW, false);
    }

    public static void saveInstall(Context context, Boolean value) {
        SharedPreferences preferences = context.getSharedPreferences(
                Constant.SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(Constant.SHARED_PREFS_INSTALL, value);
        editor.apply();
    }

    public static Boolean getInstall(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(
                Constant.SHARED_PREFS, Context.MODE_PRIVATE);
        return preferences.getBoolean(Constant.SHARED_PREFS_INSTALL, false);
    }

    public static void saveDate(Context context, String value) {
        SharedPreferences preferences = context.getSharedPreferences(
                Constant.SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(Constant.SHARED_PREFS_DATE, value);
        editor.apply();
    }

    public static String getDate(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(
                Constant.SHARED_PREFS, Context.MODE_PRIVATE);
        return preferences.getString(Constant.SHARED_PREFS_DATE, "");
    }

    public static void saveAdIds(Context context, AdsData adsData) {
        SharedPreferences preferences = context.getSharedPreferences(
                Constant.SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        try {
            if (adsData.getData().getGoogleAppId() != null)
                editor.putString(Constant.SHARED_PREFS_APP_ID, adsData.getData().getGoogleAppId());
            if (adsData.getData().getGoogleBanner() != null)
                editor.putString(Constant.SHARED_PREFS_BANNER, adsData.getData().getGoogleBanner());
            if (adsData.getData().getGoogleInterstitial() != null)
                editor.putString(Constant.SHARED_PREFS_INTERSTITIAL, adsData.getData().getGoogleInterstitial());
            if (adsData.getData().getGoogleNative() != null)
                editor.putString(Constant.SHARED_PREFS_NATIVE, adsData.getData().getGoogleNative());
            if (adsData.getData().getGoogleRewarded() != null)
                editor.putString(Constant.SHARED_PREFS_REWARD, adsData.getData().getGoogleRewarded());
            if (adsData.getData().getGoogleOpen() != null)
                editor.putString(Constant.SHARED_PREFS_OPEN, adsData.getData().getGoogleOpen());
            if (adsData.getData().getAdsSequence() != null)
                editor.putString(Constant.SHARED_PREFS_AD_TYPE, adsData.getData().getAdsSequence());
            if (adsData.getData().getOtherId1() != null)
                editor.putString(Constant.SHARED_PREFS_AD_COUNT, adsData.getData().getOtherId1());
            if (adsData.getData().getAppAdsStatus() != null)
                editor.putBoolean(Constant.SHARED_PREFS_AD_ON, adsData.getData().getAppAdsStatus().equals("1"));
            if (!adsData.getData().getOtherId3().isEmpty()) {
                editor.putString(Constant.SHARED_PREFS_QUREKA_ADS, adsData.getData().getOtherId3());
            }
            if (!adsData.getData().getOtherId2().isEmpty()) {
                editor.putString(Constant.SHARED_PREFS_KD_ACCOUNT, adsData.getData().getOtherId2());
            }
            if (!adsData.getData().getOtherId4().isEmpty()) {
                editor.putString(Constant.SHARED_PREFS_GAME_ACCOUNT, adsData.getData().getOtherId4());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        editor.apply();
    }

    public static Boolean getAdsOn(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(
                Constant.SHARED_PREFS, Context.MODE_PRIVATE);
        return preferences.getBoolean(Constant.SHARED_PREFS_AD_ON, true);
    }

    public static String getQurekaLink(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(
                Constant.SHARED_PREFS, Context.MODE_PRIVATE);
        return preferences.getString(Constant.SHARED_PREFS_QUREKA_ADS, "");
    }

    public static String getKdAppLink(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(Constant.SHARED_PREFS, Context.MODE_PRIVATE);
        return preferences.getString(Constant.SHARED_PREFS_KD_ACCOUNT, "KD Inc.");
    }

    public static String getMoreAppLink(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(Constant.SHARED_PREFS, Context.MODE_PRIVATE);
        return preferences.getString(Constant.SHARED_PREFS_GAME_ACCOUNT, "KD Inc.");
    }

    public static String getAppId(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(
                Constant.SHARED_PREFS, Context.MODE_PRIVATE);
        return BuildConfig.DEBUG ? context.getResources().getString(R.string.admob_app_id) : preferences.getString(Constant.SHARED_PREFS_APP_ID, "");
    }

    public static String getBannerId(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(
                Constant.SHARED_PREFS, Context.MODE_PRIVATE);
        return BuildConfig.DEBUG ? context.getResources().getString(R.string.admob_banner) : preferences.getString(Constant.SHARED_PREFS_BANNER, "");
    }

    public static String getInterstitialId(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(
                Constant.SHARED_PREFS, Context.MODE_PRIVATE);
        return BuildConfig.DEBUG ? context.getResources().getString(R.string.admob_interstitial) : preferences.getString(Constant.SHARED_PREFS_INTERSTITIAL, "");
    }

    public static String getRewardId(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(
                Constant.SHARED_PREFS, Context.MODE_PRIVATE);
        return BuildConfig.DEBUG ? context.getResources().getString(R.string.admob_reward) : preferences.getString(Constant.SHARED_PREFS_REWARD, "");
    }

    public static String getNativeId(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(
                Constant.SHARED_PREFS, Context.MODE_PRIVATE);
        return BuildConfig.DEBUG ? context.getResources().getString(R.string.admob_native) : preferences.getString(Constant.SHARED_PREFS_NATIVE, "");
    }

    public static String getOpenId(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(
                Constant.SHARED_PREFS, Context.MODE_PRIVATE);
        return BuildConfig.DEBUG ? context.getResources().getString(R.string.admob_open) : preferences.getString(Constant.SHARED_PREFS_OPEN, "");
    }

    public static String getAdType(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(
                Constant.SHARED_PREFS, Context.MODE_PRIVATE);
        return preferences.getString(Constant.SHARED_PREFS_AD_TYPE, "1");
    }

    public static String getAdClickCount(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(
                Constant.SHARED_PREFS, Context.MODE_PRIVATE);
        return preferences.getString(Constant.SHARED_PREFS_AD_COUNT, "4");
    }

}
