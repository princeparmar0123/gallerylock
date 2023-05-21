package com.calculator.vault.lock.hide.photo.video.common.utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;

import com.calculator.vault.lock.hide.photo.video.R;
import com.calculator.vault.lock.hide.photo.video.service.CamerSelfiService;


public class Constant {
    public static String[] recovery_question = new String[]{"Where's my birthplace?", "What is your birth month?", "Who is your best friend?", "What was your first car?", "Who is your favourite Cricketer?", "Which is your  favorite movie?", "Which is your favorite song?", "In what town was your first job?"};
    public static String[] home_module_name = new String[]{"Videos", "Photos", "Camera", "Secret Note", "", "Contact", "Intruder Selfie", "Recycle Bin", "Password", "Cloud Backup"};
//    public static int[] home_module_icon = new int[]{R.drawable.iv_home_video, R.drawable.iv_home_image,
//            R.drawable.iv_home_camera, R.drawable.iv_home_note,R.drawable.iv_home_note, R.drawable.iv_home_contact,
//            R.drawable.iv_home_selfie, R.drawable.iv_home_recycle, R.drawable.iv_home_password,
//            R.drawable.iv_home_cloud};

    public static String[] password_name = new String[]{"Debit/Credit Card", "Bank", "Social", "Email"};
    public static int[] password_icon = new int[]{R.drawable.card_icon, R.drawable.bank_icon, R.drawable.iv_social, R.drawable.gmail};

    public static String[] social = new String[]{"Facebook", "Instagram", "Snapchat", "Linkedin", "Hotstar", "Netflix", "Other"};

    public static String photo_Folder ="Photos";
    public static String video_Folder ="Videos";
    public static String Intruder_Folder ="Intruder";
    public static String Recyclebin_Folder ="Recyclebin";

    public static String home_path = (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/.Calculator Lock");
    public static String photos_path = (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/.Calculator Lock/" + photo_Folder);
    public static String videos_path = (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/.Calculator Lock/"+video_Folder);
    public static String intruder_path = (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/.Calculator Lock/"+ Intruder_Folder);
    public static String intruder_path_new = (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/.Calculator Lock");
    public static String recycle_path = (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/.Calculator Lock/"+Recyclebin_Folder);

    public static boolean isFirstTime = true;

    public static boolean USE_FAKE_SERVER = false;
    public static final String SKU_TEST = "android.test.purchased";

    public static String year = "Yearly at $199.99";
    public static String month = "Monthly at $39.99";
    public static String week = "Weekly with a 3-day free trial at $ 7.99";


    //    public static final String SKU_SUBSCRIPTION = TEST_BUILD ? SKU_TEST : "com.subscription.dailyquotes";
    public static final boolean TEST_BUILD = true;

    public static final int REQUEST_BUY_SUBSCRIPTION = 10;
    public static final String BASIC_SKU = "basic_subscription";
    public static final String PREMIUM_SKU = "premium_subscription";

    public static void StartCameraService(Activity activity) {
        if (activity == null) {
            return;
        }

        if (!activity.isFinishing()) {
            Intent front_translucent = new Intent(activity, CamerSelfiService.class);
            front_translucent.putExtra("Front_Request", true);
            front_translucent.putExtra("Quality_Mode", 0);
            activity.startService(front_translucent);
        } else {
            Log.e("CameraService", "No Start");
        }
    }

    public static String PREF_OPEN_AD_ID = "open_ad_id";
    public static String APP_OPEN = "app_open_id";

    public static boolean OPEN_ADS = true;
    public static boolean SHOW_OPEN_ADS = true;


    public static void showOpenAd() {
        SHOW_OPEN_ADS = true;
    }

    public static void hideOpenAd() {
        SHOW_OPEN_ADS = false;
    }

    final public static String SHARED_PREFS = "Gallery";
    final public static String SHARED_PREFS_INSTALL = "gallery_install";
    final public static String SHARED_PREFS_IMAGE_SHOW = "gallery_image_show";
    final public static String SHARED_PREFS_DATE = "gallery_date";
    final public static String SHARED_PREFS_AD_ON = "gallery_ad_on";
    final public static String SHARED_PREFS_APP_ID = "gallery_app_id";
    final public static String SHARED_PREFS_BANNER = "gallery_banner";
    final public static String SHARED_PREFS_INTERSTITIAL = "gallery_interstitial";
    final public static String SHARED_PREFS_NATIVE = "gallery_native";
    final public static String SHARED_PREFS_REWARD = "gallery_reward";
    final public static String SHARED_PREFS_OPEN = "gallery_open";
    final public static String SHARED_PREFS_AD_TYPE = "gallery_ad_type";
    final public static String SHARED_PREFS_AD_COUNT = "gallery_ad_count";
    final public static String SHARED_PREFS_QUREKA_ADS = "gallery_qureka_app";
    final public static String SHARED_PREFS_KD_ACCOUNT = "gallery_kd_account";
    final public static String SHARED_PREFS_GAME_ACCOUNT = "gallery_game_account";

}
