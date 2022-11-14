package com.klnet.plism3.plsim3;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.telephony.TelephonyManager;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import static android.os.Build.VERSION_CODES.M;

public class MainActivity extends AppCompatActivity {

    final private int WAIT_TOKEN = 1;

    ImageButton bt_home, bt_prev, bt_next, bt_refresh;
    ImageButton bt_top;
    ObservableWebView WebView01;
    RelativeLayout rel_intro;
    RelativeLayout rel_main;
    RelativeLayout rel_footer;
    View.OnClickListener cListener;
    float yPos;
    float frameHeight;
    String status = "on";
    String sAuthKey = "";
    String sHash = "";

    private final Handler handler = new Handler();
    boolean isLoginPage = false;
    boolean isAutoLoginPage = false;
    boolean isMianPage = false;

    long LoginBackKeyClickTme;
    long AutoLoginBackKeyClickTme;
    long MainBackKeyClickTme;

    private Toast toast;
    private RequestQueue queue;
    //Security Check
    public static final String ROOT_PATH = Environment.getExternalStorageDirectory() + "";
    public static final String ROOTING_PATH_1 = "/system/bin/su";
    public static final String ROOTING_PATH_2 = "/system/xbin/su";
    public static final String ROOTING_PATH_3 = "/system/app/SuperUser.apk";
    public static final String ROOTING_PATH_4 = "/data/data/com.noshufou.android.su";
    public static final String ROOTING_PATH_5 = "/system/app/Superuser.apk";


    public String[] RootFilesPath = new String[]{
            ROOT_PATH + ROOTING_PATH_1 ,
            ROOT_PATH + ROOTING_PATH_2 ,
            ROOT_PATH + ROOTING_PATH_3 ,
            ROOT_PATH + ROOTING_PATH_4 ,
            ROOT_PATH + ROOTING_PATH_5
    };
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Context myApp = this;
        final AlertDialog.Builder alertDialogBuilderExit = new AlertDialog.Builder(this);



        if(!BuildConfig.DEBUG ) {
            queue = Volley.newRequestQueue(this);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, DataSet.connect_url + "/mbl/com/selectMobileHashKey.do?app_id=PLISM3&app_os=android&app_version=1", null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        sHash = response.getString("hash_code");
                        if (!sHash.trim().equals(getHashKey().trim())) {
                            alertDialogBuilderExit.setMessage("프로그램 무결성에 위배됩니다. \nPlayStore 내에서 \n 설치하시기 바랍니다.").setCancelable(false)
                                    .setPositiveButton("종료", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            MainActivity.this.finish();
                                        }
                                    });
                            AlertDialog dialog = alertDialogBuilderExit.create();
                            dialog.show();

                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("########",error.toString() + "12313");
                }
            });

        queue.add(jsonObjectRequest);
            //Rooting Check
            boolean isRootingFlag = false;
            try {
                Runtime.getRuntime().exec("su");
                isRootingFlag = true;
            } catch (Exception e) {
                // Exception 나면 루팅 false;
                isRootingFlag = false;
            }

            if (!isRootingFlag) {
                isRootingFlag = checkRootingFiles(createFiles(RootFilesPath));
            }

            Log.d("test", "isRootingFlag = " + isRootingFlag);

            alertDialogBuilderExit.setTitle("프로그램 종료");


            if (isRootingFlag == true) {
                alertDialogBuilderExit.setMessage("루팅된 단말기 입니다. \n개인정보 유출의 위험성이 있으므로\n 프로그램을 종료합니다.").setCancelable(false)
                        .setPositiveButton("종료", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                MainActivity.this.finish();
                            }
                        });
                AlertDialog dialog = alertDialogBuilderExit.create();
                dialog.show();
            }


            if (kernelBuildTagTest() == true) {
                alertDialogBuilderExit.setMessage("루팅된 단말기 입니다. \n개인정보 유출의 위험성이 있으므로\n 프로그램을 종료합니다.\n Error Code : 2").setCancelable(false)
                        .setPositiveButton("종료", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                MainActivity.this.finish();
                            }
                        });
                AlertDialog dialog = alertDialogBuilderExit.create();
                dialog.show();
            }
            if (shellComendExecuteCheck() == true) {
                alertDialogBuilderExit.setMessage("루팅된 단말기 입니다. \n개인정보 유출의 위험성이 있으므로\n 프로그램을 종료합니다.\n Error Code : 3").setCancelable(false)
                        .setPositiveButton("종료", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                MainActivity.this.finish();
                            }
                        });
                AlertDialog dialog = alertDialogBuilderExit.create();
                dialog.show();
            }


        }


        DataSet.getInstance().isrunning = "true";
        DataSet.getInstance().islogin = "false";
        DataSet.getInstance().userid = "";


        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        final String deviceId = DataSet.getDeviceID(this);

        Log.d("CHECK", "deviceId :" + deviceId);


        //앱이 종료된 상태에서 푸시를 보는 경우
        Intent intent = getIntent();
        if (intent != null) {
            if (intent.getStringExtra("push_id") != null) {
                //앱이 종료된 상태에서 푸시 클릭한 경우 처리 부분
                DataSet.getInstance().push_id = getIntent().getStringExtra("push_id");
                DataSet.getInstance().obj_id = getIntent().getStringExtra("obj_id");
                DataSet.getInstance().recv_id = getIntent().getStringExtra("recv_id");
                DataSet.getInstance(). type = getIntent().getStringExtra("type");
                DataSet.getInstance(). msg = getIntent().getStringExtra("msg");
                DataSet.getInstance(). badge_num = getIntent().getStringExtra("badge_num");

                Log.d("CHECK", "push value, push_id:" + DataSet.getInstance().push_id);
                Log.d("CHECK", "push value, obj_id:" + DataSet.getInstance().obj_id);
                Log.d("CHECK", "push value, recv_id:" + DataSet.getInstance().recv_id);
                Log.d("CHECK", "push value, type:" + DataSet.getInstance().type);
                Log.d("CHECK", "push value, msg:" + DataSet.getInstance().msg);
                Log.d("CHECK", "push value, badge_num:" + DataSet.getInstance().badge_num);

                //앱 실행 아이콘 개수 조절
                Intent badgeIntent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
                badgeIntent.putExtra("badge_count", Integer.parseInt("0"));
                badgeIntent.putExtra("badge_count_package_name", "com.klnet.plism3.plsim3");
                badgeIntent.putExtra("badge_count_class_name", "com.klnet.plism3.plsim3.MainActivity");
                sendBroadcast(badgeIntent);

                /*
                 * 푸시 처리 관련 앱 기능 추가
                 */

                NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                manager.cancel(DataSet.getInstance().type + ":" + DataSet.getInstance().obj_id, 0);
            }
        }


        WebView01 = (ObservableWebView) findViewById(R.id.webView);
        WebSettings webSettings = WebView01.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccess(true);
        WebView01.addJavascriptInterface(new AndroidBridge(), "AndroidInterface");
        WebView01.clearHistory();
        WebView01.clearCache(true);
        WebView01.clearView();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        Context context = WebView01.getContext();
        PackageManager packageManager = context.getPackageManager();
        String appName = "";
        String appVersion = "";
        String userAgent = webSettings.getUserAgentString();

        try {
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            appName = packageManager.getApplicationLabel(applicationInfo).toString();

            // App 버전 추출
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            appVersion = String.format("%s", "" + packageInfo.versionName);

            // User-Agent에 App 이름과 버전을 붙여서 보내줌
            userAgent = String.format("%s %s %s", userAgent, appName, appVersion);

            // 변경된 User-Agent 반영
            webSettings.setUserAgentString(userAgent);

        } catch (PackageManager.NameNotFoundException e) {
            // e.printStackTrace();
        }

        rel_footer = (RelativeLayout) findViewById(R.id.rel_footer);
        rel_intro = (RelativeLayout) findViewById(R.id.rel_intro);
        rel_main = (RelativeLayout) findViewById(R.id.rel_main);

        bt_home = (ImageButton) findViewById(R.id.bt_home);
        bt_prev = (ImageButton) findViewById(R.id.bt_prev);
        bt_next = (ImageButton) findViewById(R.id.bt_next);
        bt_refresh = (ImageButton) findViewById(R.id.bt_refresh);
        bt_top = (ImageButton) findViewById(R.id.bt_top);



        SharedPreferences prefs2 = getSharedPreferences("JPP_FCM_Property", Activity.MODE_PRIVATE);

        String sRegId = prefs2.getString("prefFCMRegsterID", null);

        if(sRegId == null || sRegId.equals("")) {
            String token = FirebaseInstanceId.getInstance().getToken();
            Log.i("CHECK", "received token : " + token);
            if (token == null || "".equals(token))
            {
                Log.d("CHECK", "NOT Token");
                Message msg = mHandler.obtainMessage();
                msg.arg1 = WAIT_TOKEN;
                msg.obj = null;
                mHandler.sendMessage(msg);
            }
        } else {
            SharedPreferences prefs = getSharedPreferences("AuthKeyInfo", Activity.MODE_PRIVATE);
            sAuthKey = prefs.getString("AuthKey", null);
            Log.i("CHECK", "sAuthKey :" + sAuthKey);

            if (sAuthKey == null) {
                Log.i("CHECK", "url :" + DataSet.connect_url + "/mbl/main/login_auth.jsp");
                WebView01.loadUrl(DataSet.connect_url + "/mbl/main/login_auth.jsp");
            } else {
                WebView01.loadUrl(DataSet.connect_url + "/mbl/main/auto_login.jsp");
            }
        }

        //WebView01.loadUrl(connect_url + "/test.html");

        cListener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                switch (v.getId()) {
                    case R.id.bt_home:
                        bt_homeClick();
                        break;
                    case R.id.bt_prev:
                        bt_prevClick();
                        break;
                    case R.id.bt_next:
                        bt_nextClick();
                        break;
                    case R.id.bt_refresh:
                        bt_refreshClick();
                        break;
                    case R.id.bt_top:
                        bt_topClick();
                        break;
                    default:
                        break;
                }
            }
        };
        bt_home.setOnClickListener(cListener);
        bt_prev.setOnClickListener(cListener);
        bt_next.setOnClickListener(cListener);
        bt_refresh.setOnClickListener(cListener);
        bt_top.setOnClickListener(cListener);

        WebView01.post(new Runnable() {
            @Override
            public void run() {
                frameHeight = WebView01.getHeight() / 5;
            }
        });

        WebView01.setDownloadListener(new DownloadListener() {
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimetype,
                                        long contentLength) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        WebView01.setWebViewClient(new WebViewClient() {


            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d("CHECK", url);
                if (view == null || url == null) {
                    return false;
                }


                if (url.contains("play.google.com")) {
                    // play.google.com 도메인이면서 App 링크인 경우에는 market:// 로 변경
                    String[] params = url.split("details");
                    if (params.length > 1) {
                        url = "market://details" + params[1];
                        view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                        return true;
                    }
                }

                /*
                if(url.toLowerCase().endsWith(".jpg") || url.toLowerCase().endsWith(".png") || url.toLowerCase().endsWith(".pdf")|| url.toLowerCase().endsWith(".ppt")
                        || url.toLowerCase().endsWith(".hwp")|| url.toLowerCase().endsWith(".doc") || url.toLowerCase().endsWith(".xls") || url.toLowerCase().endsWith(".xlsx")) {
                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                    request.allowScanningByMediaScanner();

                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

                    String filename[] = url.split(("/"));
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename[filename.length-1]);
                    DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                    dm.enqueue(request);
                }]*/


                if (url.startsWith("http:") || url.startsWith("https:")) {
                    // HTTP/HTTPS 요청은 내부에서 처리한다.
                    view.loadUrl(url);
                } else {
                    Intent intent;

                    try {
                        intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                    } catch (URISyntaxException e) {
                        // 처리하지 못함
                        return false;
                    }

                    try {
                        view.getContext().startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        // Intent Scheme인 경우, 앱이 설치되어 있지 않으면 Market으로 연결
                        if (url.startsWith("intent:") && intent.getPackage() != null) {
                            url = "market://details?id=" + intent.getPackage();
                            view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                            return true;
                        } else {
                            // 처리하지 못함
                            return false;
                        }
                    }
                }
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                if (url.contains("/mbl/main/auto_login.jsp")) {
                    isAutoLoginPage = true;
                    isLoginPage = false;
                    isMianPage = false;
                } else if (url.contains("/mbl/main/login_auth.jsp")) {
                    isAutoLoginPage = false;
                    isLoginPage = true;
                    isMianPage = false;
                } else if (url.contains("/mbl/main/main.jsp")) {
                    isAutoLoginPage = false;
                    isLoginPage = false;
                    isMianPage = true;
                } else {
                    isAutoLoginPage = false;
                    isLoginPage = false;
                    isMianPage = false;
                }
            }


            @Override
            public void onReceivedError(final WebView view, int errorCode, String description,
                                        final String failingUrl) {

                new AlertDialog.Builder(myApp)
                        .setTitle("확인")
                        .setMessage("접속 할 수 없습니다. 관리자에게 문의 바랍니다.")
                        .setPositiveButton(android.R.string.ok,
                                new AlertDialog.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        DataSet.getInstance().isrunning = "false";
                                        DataSet.getInstance().islogin = "false";
                                        DataSet.getInstance().userid = "";
                                        finish();
                                    }
                                })
                        .setCancelable(false)
                        .create()
                        .show();

                super.onReceivedError(view, errorCode, description, failingUrl);
            }



            @Override
            public void onPageFinished(WebView view, String url) {

                //최초 실행 여부 판단
                SharedPreferences pref = getSharedPreferences("isFirst", Activity.MODE_PRIVATE);
                boolean first = pref.getBoolean("isFirst", false);
                String trueOrFalse = String.valueOf(first);
                Log.d("CHECK","THE FIRST TIME :" + trueOrFalse);
                if(first==false){
                    Log.d("CHECK","THE FIRST TIME");
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("isFirst",true);
                    editor.commit();
                    DialogHtmlView();
                }




                WebView01.loadUrl("javascript:setJPPPushUrl('" + DataSet.push_url + "')");
                WebView01.loadUrl("javascript:setJPPMobileAppId('PLISM3')");
                WebView01.loadUrl("javascript:setJPPDeviceOs('fcm_and')");
                WebView01.loadUrl("javascript:setJPPDeviceOsVerion('"+Build.VERSION.RELEASE+"')");
                WebView01.loadUrl("javascript:setJPPDeviceId('"+deviceId+"')");
                SharedPreferences prefs2 = getSharedPreferences("JPP_FCM_Property", Activity.MODE_PRIVATE);
                String sRegId = prefs2.getString("prefFCMRegsterID", null);
                WebView01.loadUrl("javascript:setJPPToken('"+sRegId+"')");
                WebView01.loadUrl("javascript:setJPPUserId()");
                WebView01.loadUrl("javascript:pushList()");

                if(WebView01.canGoBack()) {
                    bt_prev.setClickable(true);
                    bt_prev.setEnabled(true);
                    bt_prev.setImageDrawable(getResources().getDrawable(R.drawable.m02));
                } else {
                    bt_prev.setClickable(false);
                    bt_prev.setEnabled(false);
                    bt_prev.setImageDrawable(getResources().getDrawable(R.drawable.m02_disabled));
                }

                if (url.contains("/mbl/main/main.jsp")) {
                    rel_intro.setVisibility(View.GONE);
                    rel_main.setVisibility(View.VISIBLE);
                    WindowManager.LayoutParams attrs = getWindow().getAttributes();
                    {
                        attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
                    }
                    getWindow().setAttributes(attrs);
                    bt_prev.setClickable(false);
                    bt_prev.setEnabled(false);
                    bt_prev.setImageDrawable(getResources().getDrawable(R.drawable.m02_disabled));
                    DataSet.getInstance().islogin = "true";

                    Log.d("CHECK", "push id : "+ DataSet.getInstance().push_id);
                    Log.d("CHECK", "userid : "+ DataSet.getInstance().userid);

                    if( DataSet.getInstance().push_id != null && ! DataSet.getInstance().push_id.equals("") && DataSet.getInstance().userid.equals( DataSet.getInstance().recv_id)) {
                        if (!DataSet.getInstance().isrunapppush.equals("true")) {
                            new AlertDialog.Builder(MainActivity.this)
                                    .setTitle("알림")
                                    .setMessage(DataSet.getInstance().msg)
                                    .setPositiveButton(android.R.string.ok,
                                            new AlertDialog.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    WebView01.loadUrl("javascript:openpushmenu()");
                                                    DataSet.getInstance().push_id = "";
                                                }
                                            })
                                    .setNegativeButton(android.R.string.cancel,
                                            new AlertDialog.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    DataSet.getInstance().push_id = "";
                                                }
                                            })
                                    .setCancelable(false)
                                    .create()
                                    .show();
                        } else {
                            WebView01.loadUrl("javascript:openpushmenu()");
                            DataSet.getInstance().push_id = "";
                            DataSet.getInstance().isrunapppush.equals("false");
                        }
                    }


                } else if(url.contains("/mbl/main/login_auth.jsp")) {
                    rel_intro.setVisibility(View.GONE);
                    rel_main.setVisibility(View.VISIBLE);
                    rel_footer.setVisibility(View.GONE);
                    WindowManager.LayoutParams attrs = getWindow().getAttributes();
                    {
                        attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
                    }
                    getWindow().setAttributes(attrs);
                    WebView01.loadUrl("javascript:fn_setDeviceId('" + deviceId + "')");
                } else if(url.contains("/mbl/main/auto_login.jsp")) {
                    rel_main.setVisibility(View.VISIBLE);
                    rel_footer.setVisibility(View.GONE);
                    WebView01.loadUrl("javascript:fn_setAuthKey('" + sAuthKey + "')");
                    WebView01.loadUrl("javascript:fn_setDeviceId('" + deviceId + "')");
                }  else {
                    rel_footer.setVisibility(View.VISIBLE);
                }

                if (WebView01.canGoForward()) {
                    bt_next.setClickable(true);
                    bt_next.setEnabled(true);
                    bt_next.setImageDrawable(getResources().getDrawable(R.drawable.m03));
                } else {
                    bt_next.setClickable(false);
                    bt_next.setEnabled(false);
                    bt_next.setImageDrawable(getResources().getDrawable(R.drawable.m03_disabled));
                }

                if (url.contains("/mbl/main/setting.jsp")) {
                    if (sAuthKey == null) {
                        Log.d("CHECK", " off");
                        WebView01.loadUrl("javascript:fn_initAutoLoginButton('off', '" + deviceId + "')");
                    } else {
                        Log.d("CHECK", " on");
                        WebView01.loadUrl("javascript:fn_initAutoLoginButton('on', '" + deviceId + "')");
                    }

                    WebView01.loadUrl("javascript:pageInitPushInfo()");
                    String versionName = "";
                    try {
                        PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
                        versionName = info.versionName;
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                    WebView01.loadUrl("javascript:setVersion('" + versionName + "')");
                }


                /*
                rel_intro.setVisibility(View.GONE);
                rel_main.setVisibility(View.VISIBLE);


                WebView01.loadUrl("javascript:setJPPPushUrl('" + DataSet.push_url + "')");
                WebView01.loadUrl("javascript:setJPPMobileAppId('PLISM3')");
                WebView01.loadUrl("javascript:setJPPDeviceOs('android')");
                WebView01.loadUrl("javascript:setJPPDeviceOsVerion('"+Build.VERSION.RELEASE+"')");
                WebView01.loadUrl("javascript:setJPPDeviceId('"+deviceId+"')");
                SharedPreferences prefs2 = getSharedPreferences("JPP_FCM_Property", Activity.MODE_PRIVATE);
                String sRegId = prefs2.getString("prefFCMRegsterID", null);
                WebView01.loadUrl("javascript:setJPPToken('"+sRegId+"')");
                WebView01.loadUrl("javascript:setJPPUserId('test01')");
                */


            }
        });


        WebView01.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
                new AlertDialog.Builder(myApp)
                        .setTitle("")
                        .setMessage(message)
                        .setPositiveButton(android.R.string.ok,
                                new AlertDialog.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        result.confirm();
                                    }
                                })
                        .setCancelable(false)
                        .create()
                        .show();

                return true;
            }

            @Override
            public boolean onJsConfirm(WebView view, String url, String message,
                                       final JsResult result) {
                // TODO Auto-generated method stub
                //return super.onJsConfirm(view, url, message, result);
                new AlertDialog.Builder(view.getContext())
                        .setTitle("확인")
                        .setMessage(message)
                        .setPositiveButton("확인",
                                new AlertDialog.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        result.confirm();
                                    }
                                })
                        .setNegativeButton("취소",
                                new AlertDialog.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        result.cancel();
                                    }
                                })
                        .setCancelable(false)
                        .create()
                        .show();
                return true;
            }
        });


        WebView01.setOnScrollChangedCallback(new ObservableWebView.OnScrollChangedCallback() {
            public void onScroll(int l, int t) {
                yPos = t;
                if (yPos <= frameHeight) {
                    bt_top.setVisibility(View.INVISIBLE);
                } else {
                    bt_top.setVisibility(View.VISIBLE);
                }
                Log.d("CHECK", "yPos : " + yPos);
            }
        });

/*
        WebView01.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                yPos = WebView01.getScrollY();

                Log.d("CHECK", "yPos = " + yPos);

                if (yPos <= frameHeight) {
                    bt_top.setVisibility(View.INVISIBLE);
                } else {
                    bt_top.setVisibility(View.VISIBLE);
                }
            }
        });


*/

    }


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.arg1 == WAIT_TOKEN) {
                TimerTask myTask1 = new TimerTask() {
                    @Override
                    public void run() {
                        String token = FirebaseInstanceId.getInstance().getToken();
                        if (token == null || "".equals(token))
                        {
                            Log.d("CHECK", "NOT Token2");
                            Message msg = mHandler.obtainMessage();
                            msg.arg1 = WAIT_TOKEN;
                            msg.obj = null;
                            mHandler.sendMessage(msg);
                        } else {
                            Log.d("CHECK", "Save Token2 :"+ token);
                            SharedPreferences prefs2 = getSharedPreferences("JPP_FCM_Property", Activity.MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefs2.edit();
                            editor.putString("prefFCMRegsterID", token);
                            editor.commit();

                            WebView01.post(new Runnable() {
                                public void run() {
                                    SharedPreferences prefs = getSharedPreferences("AuthKeyInfo", Activity.MODE_PRIVATE);
                                    sAuthKey = prefs.getString("AuthKey", null);
                                    Log.i("CHECK", "sAuthKey :" + sAuthKey);

                                    if (sAuthKey == null) {
                                        Log.i("CHECK", "url :" + DataSet.connect_url + "/mbl/main/login_auth.jsp");
                                        WebView01.loadUrl(DataSet.connect_url + "/mbl/main/login_auth.jsp");
                                    } else {
                                        WebView01.loadUrl(DataSet.connect_url + "/mbl/main/auto_login.jsp");
                                    }
                                }
                            });

                        }
                    }
                };
                Timer timer1 = new Timer();
                timer1.schedule(myTask1, 1000);
            }
        }
    };


    @Override
    protected void onResume() {
        super.onResume();
        if( DataSet.getInstance().push_id != null && ! DataSet.getInstance().push_id.equals("") && DataSet.getInstance().userid.equals( DataSet.getInstance().recv_id)) {
            if (!DataSet.getInstance().isrunapppush.equals("true")) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("알림")
                        .setMessage(DataSet.getInstance().msg)
                        .setPositiveButton(android.R.string.ok,
                                new AlertDialog.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        WebView01.loadUrl("javascript:openpushmenu()");
                                        DataSet.getInstance().push_id = "";
                                    }
                                })
                        .setNegativeButton(android.R.string.cancel,
                                new AlertDialog.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        DataSet.getInstance().push_id = "";
                                    }
                                })
                        .setCancelable(false)
                        .create()
                        .show();
            } else {
                WebView01.loadUrl("javascript:openpushmenu()");
                DataSet.getInstance().push_id = "";
                DataSet.getInstance().isrunapppush.equals("false");
            }
        }
        checkPermissionF();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // TODO Auto-generated method stub
        super.onNewIntent(intent);

        Log.d("CHECK", "push_id : " + intent.getStringExtra("push_id"));
        //앱이 실행된 상태에서 푸시를 보는 경우
        if (intent != null) {
            if(DataSet.getInstance().islogin.equals("true")) {
                if (intent.getStringExtra("push_id") != null) {
                    //앱이 실행된 상태에서 푸시 클릭한 경우 처리 부분

                    DataSet.getInstance().push_id = intent.getStringExtra("push_id");
                    DataSet.getInstance().obj_id = intent.getStringExtra("obj_id");
                    DataSet.getInstance().recv_id = intent.getStringExtra("recv_id");
                    DataSet.getInstance().type = intent.getStringExtra("type");
                    DataSet.getInstance().msg = intent.getStringExtra("msg");
                    DataSet.getInstance().badge_num = intent.getStringExtra("badge_num");

                    Log.d("CHECK", "push value1, push_id:" +  DataSet.getInstance().push_id);
                    Log.d("CHECK", "push value1, obj_id:" +  DataSet.getInstance().obj_id);
                    Log.d("CHECK", "push value1, recv_id:" +  DataSet.getInstance().recv_id);
                    Log.d("CHECK", "push value1, type:" +  DataSet.getInstance().type);
                    Log.d("CHECK", "push value1, msg:" +  DataSet.getInstance().msg);
                    Log.d("CHECK", "push value1, badge_num:" +  DataSet.getInstance().badge_num);

                    //앱 실행 아이콘 개수 조절
                    Intent badgeIntent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
                    badgeIntent.putExtra("badge_count", Integer.parseInt("0"));
                    badgeIntent.putExtra("badge_count_package_name", "com.klnet.plism3.plsim3");
                    badgeIntent.putExtra("badge_count_class_name", "com.klnet.plism3.plsim3.MainActivity");
                    sendBroadcast(badgeIntent);

                    /**
                     * 푸시 처리 관련 앱 기능 추가
                     * */

                    NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    manager.cancel( DataSet.getInstance().type + ":" +  DataSet.getInstance().obj_id, 0);

                    //
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("알림")
                            .setMessage( DataSet.getInstance().msg)
                            .setPositiveButton(android.R.string.ok,
                                    new AlertDialog.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            WebView01.loadUrl("javascript:openpushmenu()");
                                        }
                                    })
                            .setNegativeButton(android.R.string.cancel,
                                    new AlertDialog.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {


                                        }
                                    })
                            .setCancelable(false)
                            .create()
                            .show();
                }
            } else {
                if (intent.getStringExtra("push_id") != null) {
                    //앱이 종료된 상태에서 푸시 클릭한 경우 처리 부분
                    DataSet.getInstance().push_id = getIntent().getStringExtra("push_id");
                    DataSet.getInstance().obj_id = getIntent().getStringExtra("obj_id");
                    DataSet.getInstance().recv_id = getIntent().getStringExtra("recv_id");
                    DataSet.getInstance().type = getIntent().getStringExtra("type");
                    DataSet.getInstance().msg = getIntent().getStringExtra("msg");
                    DataSet.getInstance().badge_num = getIntent().getStringExtra("badge_num");

                    //앱 실행 아이콘 개수 조절
                    Intent badgeIntent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
                    badgeIntent.putExtra("badge_count", Integer.parseInt("0"));
                    badgeIntent.putExtra("badge_count_package_name", "com.klnet.plism3.plsim3");
                    badgeIntent.putExtra("badge_count_class_name", "com.klnet.plism3.plsim3.MainActivity");
                    sendBroadcast(badgeIntent);

                    /**
                     * 푸시 처리 관련 앱 기능 추가
                     * */

                    NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    manager.cancel( DataSet.getInstance().type + ":" +  DataSet.getInstance().obj_id, 0);
                }
            }
        }
    }
    private class AndroidBridge {
        @JavascriptInterface
        public void sendVersion(final String arg) {
            handler.post(new Runnable() {
                public void run() {
                    Log.d("CHECK", "sendVersion(" + arg + ")");

                    String versionName = "";
                    try {
                        PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
                        versionName = info.versionName;
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }

                    Log.d("CHECK", "versionName(" + versionName + ")");

                    if (arg != null && !arg.equals(versionName)) {
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("확인")
                                .setMessage("새로운버전("+arg+")이 나왔습니다. 업데이트 하시겠습니까?")
                                .setPositiveButton(android.R.string.ok,
                                        new AlertDialog.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.klnet.plism3.plsim3")));
                                            }
                                        })
                                .setNegativeButton(android.R.string.cancel,
                                        new AlertDialog.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        })
                                .setCancelable(false)
                                .create()
                                .show();
                    }
                }
            });
        }

        @JavascriptInterface
        public void sendAutoLoginStatus(final String arg) {
            handler.post(new Runnable() {
                public void run() {
                    Log.d("CHECK", "sendAutoLoginStatus(" + arg + ")");
                    status = arg;
                    SharedPreferences prefs = getSharedPreferences("AuthKeyInfo", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("AuthKey", null);
                    editor.commit();
                }
            });
        }

        @JavascriptInterface
        public void sendAuthKey(final String arg) {
            handler.post(new Runnable() {
                public void run() {
                    Log.i("CHECK", "sendAuthKey(" + arg + ")");
                    Log.i("CHECK", "status(" + status + ")");
                    if (status != null && status.equals("on")) {
                        sAuthKey = arg;
                        SharedPreferences prefs = getSharedPreferences("AuthKeyInfo", Activity.MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("AuthKey", arg);
                        editor.commit();
                        Log.d("CHECK", "insert success : " + sAuthKey);
                    } else if (status != null && status.equals("off") && arg.equals("")) {
                        SharedPreferences prefs = getSharedPreferences("AuthKeyInfo", Activity.MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("AuthKey", null);
                        editor.commit();
                        sAuthKey = null;
                        Log.d("CHECK", "delete success : ");
                    }
                }
            });
        }

        @JavascriptInterface
        public void sendUserId(final String arg) {
            handler.post(new Runnable() {
                public void run() {
                    Log.d("CHECK", "sendUserId(" + arg + ")");
                    DataSet.getInstance().userid = arg;
                }
            });
        }

        @JavascriptInterface
        public void sendIksOutLink(final String arg) {
            handler.post(new Runnable() {
                public void run() {
                    Log.d("CHECK", "sendIksOutLink(" + arg + ")");
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    Uri u = Uri.parse(DataSet.connect_url+arg);
                    i.setData(u);
                    startActivity(i);

                }
            });
        }

        @JavascriptInterface
        public void sendOksOutLink(final String arg) {
            handler.post(new Runnable() {
                public void run() {
                    Log.d("CHECK", "sendOksOutLink(" + arg + ")");
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    Uri u = Uri.parse(DataSet.connect_url+arg);
                    i.setData(u);
                    startActivity(i);
                }
            });
        }

        @JavascriptInterface
        public void sendCargoDetailtOutLink(final String arg) {
            handler.post(new Runnable() {
                public void run() {
                    Log.d("CHECK", "sendCargoDetailtOutLink(" + arg + ")");
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    Uri u = Uri.parse(DataSet.connect_url+arg);
                    i.setData(u);
                    startActivity(i);
                }
            });
        }

        @JavascriptInterface
        public void sendShipDetailOutLink(final String arg) {
            handler.post(new Runnable() {
                public void run() {
                    Log.d("CHECK", "sendShipDetailOutLink(" + arg + ")");
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    Uri u = Uri.parse(DataSet.connect_url+arg);
                    i.setData(u);
                    startActivity(i);
                }
            });
        }

        @JavascriptInterface
        public void sendCmsOutLink(final String arg) {
            handler.post(new Runnable() {
                public void run() {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    Uri u = Uri.parse(DataSet.connect_url + arg);
                    i.setData(u);
                    startActivity(i);
                }
            });
        }


        @JavascriptInterface
        public void sendAppName(final String arg) {
            handler.post(new Runnable() {
                public void run() {
                    Log.d("CHECK", "sendAppName(" + arg + ")");
                    if (arg != null && arg.equals("ciqapp")) {
                        PackageInfo pi;
                        PackageManager pm = getPackageManager();
                        try {
                            String strAppPackage = "mciq2.klnet.co.kr";
                            pi = pm.getPackageInfo(strAppPackage,  PackageManager.GET_ACTIVITIES);
                            Intent intent = getPackageManager().getLaunchIntentForPackage("mciq2.klnet.co.kr");
                            startActivity(intent);
                        }
                        catch (PackageManager.NameNotFoundException e) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=mciq2.klnet.co.kr")));
                        }
                    }
                }
            });
        }

        @JavascriptInterface
        public void sendLogout(final String arg) {
            handler.post(new Runnable() {
                public void run() {
                    Log.d("CHECK", "sendLogout(" + arg + ")");
                    if (arg != null && arg.equals("success")) {
                        if (status != null && status.equals("on")) {
                            sAuthKey = arg;
                            SharedPreferences prefs = getSharedPreferences("AuthKeyInfo", Activity.MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString("AuthKey", null);
                            editor.commit();
                        }
                    }
                    DataSet.getInstance().isrunning = "false";
                    DataSet.getInstance().islogin = "false";
                    DataSet.getInstance().userid = "";
                    WebView01.loadUrl(DataSet.connect_url + "/mbl/main/login_auth.jsp");
                    rel_footer.setVisibility(View.GONE);
                }
            });
        }
    }

    void bt_homeClick() {
        WebView01.loadUrl(DataSet.connect_url + "/mbl/main/main.jsp");
    }

    void bt_prevClick() {
        WebView01.goBack();
    }

    void bt_nextClick() {
        WebView01.goForward();
    }

    void bt_refreshClick() {
        WebView01.reload();
    }

    void bt_topClick() {
        WebView01.loadUrl("javascript:goTop()");
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        int duration = 2000;
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            if(isLoginPage) {
                if(System.currentTimeMillis() >  LoginBackKeyClickTme + 2000) {
                    LoginBackKeyClickTme = System.currentTimeMillis();
                    finishGuide();
                    return true;
                }

                if (System.currentTimeMillis() <= LoginBackKeyClickTme + 2000) {
                    toast.cancel();
                    DataSet.getInstance().isrunning = "false";
                    DataSet.getInstance().islogin = "false";
                    DataSet.getInstance().userid = "";
                    this.finish();
                    return true;
                }
            } else if(isAutoLoginPage) {
                if(System.currentTimeMillis() >  AutoLoginBackKeyClickTme + 2000) {
                    AutoLoginBackKeyClickTme = System.currentTimeMillis();
                    finishGuide();
                    return true;
                }

                if (System.currentTimeMillis() <= AutoLoginBackKeyClickTme + 2000) {
                    toast.cancel();
                    DataSet.getInstance().isrunning = "false";
                    DataSet.getInstance().islogin = "false";
                    DataSet.getInstance().userid = "";
                    this.finish();
                    return true;
                }
            } else if(isMianPage) {
                if(System.currentTimeMillis() >  MainBackKeyClickTme + 2000) {
                    MainBackKeyClickTme = System.currentTimeMillis();
                    finishGuide();
                    return true;
                }

                if (System.currentTimeMillis() <= MainBackKeyClickTme + 2000) {
                    toast.cancel();
                    DataSet.getInstance().isrunning = "false";
                    DataSet.getInstance().islogin = "false";
                    DataSet.getInstance().userid = "";
                    finish();
                    return true;
                }
            } else if( WebView01.canGoBack()){
                WebView01.goBack();
                return true;
            }
        }

        return super.onKeyDown(keyCode, event);
    }


    public void finishGuide() {
        toast = Toast.makeText(this, "\'뒤로\'버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT);
        toast.show();
    }

    /**
     * 루팅파일 의심 Path를 가진 파일들을 생성 한다.
     */
    private File[] createFiles(String[] sfiles){
        File[] rootingFiles = new File[sfiles.length];
        for(int i=0 ; i < sfiles.length; i++){
            rootingFiles[i] = new File(sfiles[i]);
        }
        return rootingFiles;
    }

    /**
     * 루팅파일 여부를 확인 한다.
     */
    private boolean checkRootingFiles(File... file){
        boolean result = false;
        for(File f : file){
            if(f != null && f.exists() && f.isFile()){
                result = true;
                break;
            }else{
                result = false;
            }
        }
        return result;
    }

    /**
     * 최초 실행 알림창
     */
    private void DialogHtmlView(){
        AlertDialog.Builder ab = new AlertDialog.Builder(this);
        ab.setMessage("[필수적 접근 권한] \n" +
                "*인터넷 : 인터넷을 이용한 PLISM 서비스 접근 \n" +
                "*저장공간 : 기기 사진, 미디어, 파일 액세스 권한으로 다운로드 파일 보관\n" +
                "[선택적 접근 권한] \n" +
                "*푸시알림 : PUSH 알림 서비스");
        ab.setPositiveButton("확인", null);
        AlertDialog title = ab.create();
        title.setTitle("앱 권한 이용 안내");
        title.show();
    }
    /* 커널 빌드 태그 검사 */
    public boolean kernelBuildTagTest() {

        String buildTags = Build.TAGS;

        if(buildTags != null && buildTags.contains("test-keys")) {
            return true;
        }else {
            return false;
        }
    }
    /* Shell 명령어 실행 가능 여부 */
    public boolean shellComendExecuteCheck() {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(new String[] { "/system/xbin/which", "su" });
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            if (in.readLine() != null) return true;
            return false;
        } catch (Throwable t) {
            return false;
        } finally {
            if (process != null) process.destroy();
        }
    }

    private void checkPermissionF() {

        if (android.os.Build.VERSION.SDK_INT >= M) {
            // only for LOLLIPOP and newer versions
            Log.d("CHECK","Hello Marshmallow (마시멜로우)");
            int permissionResult = getApplicationContext().checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if (permissionResult == PackageManager.PERMISSION_DENIED) {
                //요청한 권한( WRITE_EXTERNAL_STORAGE )이 없을 때..거부일때...
                /* 사용자가 WRITE_EXTERNAL_STORAGE 권한을 한번이라도 거부한 적이 있는 지 조사한다.
                 * 거부한 이력이 한번이라도 있다면, true를 리턴한다.
                 */

                if (shouldShowRequestPermissionRationale(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(new ContextThemeWrapper(this, android.R.style.Theme_DeviceDefault_Light));
                    dialog.setTitle("권한이 필요합니다.")
                            .setMessage("단말기의 파일쓰기 권한이 필요합니다.\n계속하시겠습니까?")
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    if (Build.VERSION.SDK_INT >= M) {

                                        Log.i("CHECK","감사합니다. 권한을 허락했네요 (마시멜로우)");
                                        requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE,android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                                    }

                                }
                            })
                            .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            })
                            .create()
                            .show();

                    //최초로 권한을 요청할 때.
                } else {
                    Log.i("CHECK","최초로 권한을 요청할 때. (마시멜로우)");
                    requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE,android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                }
            }else{
                //권한이 있을 때.
            }

        } else {
            Log.d("CHECK","(마시멜로우 이하 버전입니다.)");
            //   getThumbInfo();
        }

    }


    public String getHashKey(){
        String hashKey = "";
        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("####",e.toString());
            e.printStackTrace();
        }
        if (packageInfo == null)
            return null;

            for (Signature signature : packageInfo.signatures) {
                try {

                    MessageDigest md = MessageDigest.getInstance("SHA");
                    md.update(signature.toByteArray());
                    hashKey = Base64.encodeToString(md.digest(), Base64.DEFAULT);

                } catch (NoSuchAlgorithmException e) {
                    Log.e("KeyHash", "Unable to get MessageDigest.");
                    return null;
                }
        }
        return hashKey;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == 1) {
            /* 요청한 권한을 사용자가 "허용"했다면 인텐트를 띄워라
                내가 요청한 게 하나밖에 없기 때문에. 원래 같으면 for문을 돈다.*/
/*            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.READ_PHONE_STATE,Manifest.permission.READ_EXTERNAL_STORAGE}, 1);*/

            for(int i = 0 ; i < permissions.length ; i++) {
                if (grantResults.length > 0 && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        Log.i("CHECK","onRequestPermissionsResult WRITE_EXTERNAL_STORAGE ( 권한 성공 ) ");
                    }


                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        Log.i("CHECK","onRequestPermissionsResult READ_EXTERNAL_STORAGE ( 권한 성공 ) ");
                    }
                }


            }

        } else {
            Log.i("CHECK","onRequestPermissionsResult ( 권한 거부) ");
            Toast.makeText(getApplicationContext(), "요청 권한 거부", Toast.LENGTH_SHORT).show();
        }

    }

}
