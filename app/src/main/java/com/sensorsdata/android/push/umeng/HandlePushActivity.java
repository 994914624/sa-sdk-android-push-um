package com.sensorsdata.android.push.umeng;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.sensorsdata.android.push.yzk.ToolBox;
import com.umeng.message.UmengNotifyClickActivity;

import org.android.agoo.common.AgooConstants;
import org.json.JSONObject;

/**
 * Created by yzk on 2019-11-25
 */

public class HandlePushActivity extends UmengNotifyClickActivity {


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
            Log.i("TODO", "厂商通道消息：" + body);
        }
    }
}
