package com.sensorsdata.android.push;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.sensorsdata.android.push.jpush.JiguangPushDemo;
import com.sensorsdata.android.push.yzk.JsonViewerAdapter;
import com.sensorsdata.android.push.yzk.ToolBox;
import com.umeng.message.UmengNotifyClickActivity;
import com.yuyh.jsonviewer.library.JsonRecyclerView;

import org.android.agoo.common.AgooConstants;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 使用一个 Activity 来做 push intent 的处理跳转
 * <p>
 * uri ---> yang://www.test.com/path?custom=aaa
 * <p>
 * intentUri ---> intent://www.test.com/path?custom=aaa#Intent;scheme=yang;launchFlags=0x10000000;component=com.sensorsdata.android.push/.HandlePushActivity;end
 * <p>
 * intent://www.test.com/path?custom=aaa#Intent;scheme=yang;launchFlags=0x10000000;component=com.sensorsdata.android.push/.HandlePushActivity;S.sf_data={"aa":"bb"};end
 */
public class HandlePushActivity extends UmengNotifyClickActivity {

    private static final String TAG = "HandlePushActivity";
    private TextView tvTitle;
    private TextView tvContent;
    private TextView tvCustom;
    private JsonRecyclerView rcSfData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        Log.i(TAG, "onCreate");
        //initActionBar();
        iniView();
        handlePushIntent();
    }

    private void iniView() {
        tvTitle = findViewById(R.id.push_title);
        tvContent = findViewById(R.id.push_content);
        tvCustom = findViewById(R.id.push_custom);
        rcSfData = findViewById(R.id.push_sf_data);
    }

    /**
     * 友盟厂商通道消息回调
     * <p>
     * 该 Activity 需继承自 UmengNotifyClickActivity，同时实现父类的 onMessage 方法，
     * 对该方法的 intent 参数进一步解析即可，该方法异步调用，不阻塞主线程。
     * 并设置 launchMode="singleTask" 和 exported="true"
     */
    @Override
    public void onMessage(Intent intent) {
        super.onMessage(intent);  //此方法必须调用，否则无法统计打开数
        if (intent != null) {
            // 解析 intent
            String body = intent.getStringExtra(AgooConstants.MESSAGE_BODY);
            if (!TextUtils.isEmpty(body)) {
                try {
                    JSONObject push = new JSONObject(body);
                    JSONObject extra = push.optJSONObject("extra");
                    String extraStr = null;
                    if (extra != null) {
                        extraStr = extra.toString();
                        // TODO 处理自定义消息
                        ToolBox.handleSFCustomMessage(extraStr);
                    }
                    // 推送标题
                    String title = push.optJSONObject("body").optString("title");
                    // 推送内容
                    String content = push.optJSONObject("body").optString("text");
                    // TODO 埋点 App 打开推送消息 事件
                    ToolBox.trackAppOpenNotification(extraStr, title, content);
                    Log.i("TODO", String.format("title： %s。content：%s。sf_data： %s。", title, content, extraStr));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Log.i(TAG, "厂商通道消息：" + body);
        }
    }


    /**
     * 处理推送消息的 Intent
     */
    private void handlePushIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            String intentUri = intent.toUri(Intent.URI_INTENT_SCHEME);
            //Log.i(TAG, "----handlePushIntent intentUri：---- " + intentUri);

            if (intent.getExtras() != null) {
                Log.i(TAG, "----intent.getExtras：----- " + intent.getExtras().toString());
            }

            intent.setData(Uri.parse("{\n" +
                    "    \"msg_id\":\"123456\",\n" +
                    "    \"n_content\":\"thisiscontent\",\n" +
                    "    \"n_extras\":{\n" +
                    "        \"sf_data\":\"{\\\"customized\\\":{\\\"path\\\":\\\"按按\\\",\\\"as\\\":\\\"是是是\\\"},\\\"sf_landing_type\\\":\\\"CUSTOMIZED\\\",\\\"sf_msg_id\\\":\\\"f880d822-32af-4da2-8944-972f7dcd683d\\\",\\\"sf_plan_id\\\":\\\"-1\\\",\\\"sf_audience_id\\\":\\\"-1\\\",\\\"sf_plan_strategy_id\\\":\\\"-1\\\"}\",\n" +
                    "        \"key2\":\"value2\"\n" +
                    "    },\n" +
                    "    \"n_title\":\"thisistitle\",\n" +
                    "    \"rom_type\":0\n" +
                    "}"));

            // 极光推送厂商消息
            // 小米,魅族,vivo 通道和极光通道是通过 intent.getExtras();
            // 华为: intent.getData();
            // oppo: intent.getExtras().getString("JMessageExtra")
            if (intent.getData() != null) {
                try {
                    Log.i(TAG, "----intent.getData().toString() ：----- " + intent.getData().toString());
                    JSONObject jsonObject =new JSONObject(intent.getData().toString());
                    String t= jsonObject.optString("n_title");
                    String c= jsonObject.optString("n_content");
                   String sss = jsonObject.optJSONObject("n_extras").optString("sf_data");
                    Log.i(TAG, "----intent.getData() sss ：----- " + sss);
                    HashMap<String,String> extra = new HashMap<> ();
                    extra.put("sf_data",sss);
                    JiguangPushDemo.handleSFPushMessage(extra);
                    // TODO 埋点 App 打开推送消息 事件
                    JiguangPushDemo.trackAppOpenNotification(extra, t, c);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // 推送 title content
            String title = intent.getStringExtra("title");
            String content = intent.getStringExtra("content");
            // TODO 拿到 sf_data 埋点打开推送消息事件
            String sf_data = intent.getStringExtra("sf_data");
            Log.i(TAG, String.format("-----title： %s。content：%s。sf_data： %s。", title, content, sf_data));
            updateUI(title, content, sf_data);
        }
    }

    /**
     * 更新数据
     */
    private void updateUI(String title, String content, String sf_data) {
        tvTitle.setText(Html.fromHtml("推送标题：" + "<font color=\"#3AB54A\"><b>" + title + "</b></font>"));
        tvContent.setText(Html.fromHtml("推送内容：" + "<font color=\"#3AB54A\"><b>" + content + "</b></font>"));
        if (!TextUtils.isEmpty(sf_data)) {
            try {
                JSONObject Json = new JSONObject(sf_data).optJSONObject("customized");
                if (Json != null) {
                    tvCustom.setText(Html.fromHtml("自定义字段：" + "<font color=\"#3AB54A\"><b>" + Json.toString() + "</b></font>"));
                }
                //rcSfData.bindJson(sf_data);
                rcSfData.setAdapter(new JsonViewerAdapter(sf_data));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
