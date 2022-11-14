package com.klnet.plism3.plsim3;

import java.io.File;
import java.io.InputStream;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.Html.ImageGetter;
import android.util.Log;

public class DataSet {
	public String isrunning = "false";
	public String islogin = "false";
	public String userid = "";
	public String isrunapppush = "false";

	public String push_id = "";	// 푸시ID
	public String obj_id;		// 푸시 연관 계시물 ID
	public String recv_id; 	// 수신자 ID
	public String type;		// 메세지 종류
	public String msg;    		// 알림 메세지
	public String badge_num;	// 배지로 표시할 푸시 개수
	public String addon;


//	public static String push_url = "https://testpush.plism.com";
	public static String push_url = "https://push.plism.com";
	public static String connect_url = "https://www.plism.com";
//	public static String connect_url = "https://test.plism.com";

	private static DataSet _instance;

	static {
		_instance = new DataSet();
	}

	private DataSet() {
		
	}

	public static DataSet getInstance() {
		return _instance;
	}

	public static String getDeviceID(Context context)
	{
		String serial = "";
		String androidId = "";
		try {
			serial = (String)Build.class.getField("SERIAL").get(null);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}

		androidId = "" + android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

		UUID deviceUuid = new UUID(androidId.hashCode(), serial.hashCode());
		return deviceUuid.toString();
	}

}
