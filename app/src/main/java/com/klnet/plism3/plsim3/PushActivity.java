package com.klnet.plism3.plsim3;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class PushActivity extends AppCompatActivity {

    TextView tv_title, tv_msg;
    Button btn_ok, btn_cancel;
    View.OnClickListener cListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_push);

        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_msg = (TextView) findViewById(R.id.tv_msg);
        btn_ok = (Button) findViewById(R.id.btn_ok);
        btn_cancel = (Button) findViewById(R.id.btn_cancel);

        tv_msg.setText(DataSet.getInstance().addon);

        SharedPreferences prefs = getSharedPreferences("AuthKeyInfo", Activity.MODE_PRIVATE);
        String sAuthKey = prefs.getString("AuthKey", null);
        //tv_msg.setText("제출번호:16KMTCSC361-KMTC-0001   \n오류내용:선사부호를 입력하시오 외 1건 ");

        cListener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                switch (v.getId()) {
                    case R.id.btn_ok:
                        btn_okClick();
                        break;
                    case R.id.btn_cancel:
                        btn_cancel();
                        break;
                    default:
                        break;
                }
            }
        };

        Intent intent = getIntent();

        if (intent != null) {
            if (getIntent().getExtras().getString("msg") != null) {

                String msg = getIntent().getExtras().getString("msg");
                JSONObject data = null;
                try {
                    data = new JSONObject(msg);
                    String push_id = data.getString("id");       // 푸시ID
                    String obj_id = data.getString("obj_id");    // 푸시 연관 계시물 ID
                    String recv_id = data.getString("recv_id");  // 수신자 ID
                    String type = data.getString("type");        // 메세지 종류
                    String badge_num = data.getString("badge");	// 배지로 표시할 푸시 개수
                    String addon = data.getString("addon");	    // 추가 메세지 정보


                    DataSet.getInstance().push_id = push_id;
                    DataSet.getInstance().obj_id = obj_id;
                    DataSet.getInstance().recv_id = recv_id;
                    DataSet.getInstance().type = type;
                    DataSet.getInstance().msg = getIntent().getStringExtra("alert");
                    DataSet.getInstance().badge_num = badge_num;
                    DataSet.getInstance().addon = addon.replace("\\n", "\n");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                while(true)
                {
                    int i = DataSet.getInstance().addon.indexOf("\\n");
                    if (i < 0)
                        break;

                    DataSet.getInstance().addon = DataSet.getInstance().addon.replace("\\n", "\n");
                }
                tv_title.setText(DataSet.getInstance().msg);
                tv_msg.setText(DataSet.getInstance().addon);

                btn_ok.setOnClickListener(cListener);

                if(DataSet.getInstance().isrunning.equals("true")) {
                    finish();
                }

            }
        }
    }

    void btn_okClick() {
        Intent badgeIntent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
        badgeIntent.putExtra("badge_count", Integer.parseInt("0"));
        badgeIntent.putExtra("badge_count_package_name", "com.klnet.plism3.plsim3");
        badgeIntent.putExtra("badge_count_class_name", "com.klnet.plism3.plsim3.MainActivity");
        sendBroadcast(badgeIntent);

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.cancel(DataSet.getInstance().type + ":" + DataSet.getInstance().obj_id, 0);

        DataSet.getInstance().isrunapppush = "true";
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        this.startActivity(intent);
        finish();
    }

    void btn_cancel() {
        Intent badgeIntent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
        badgeIntent.putExtra("badge_count", Integer.parseInt("0"));
        badgeIntent.putExtra("badge_count_package_name", "com.klnet.plism3.plsim3");
        badgeIntent.putExtra("badge_count_class_name", "com.klnet.plism3.plsim3.MainActivity");
        sendBroadcast(badgeIntent);

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.cancel(DataSet.getInstance().type + ":" + DataSet.getInstance().obj_id, 0);

        finish();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);


    }
}
