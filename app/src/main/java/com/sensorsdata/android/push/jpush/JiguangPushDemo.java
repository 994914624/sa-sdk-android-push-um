package com.sensorsdata.android.push.jpush;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.sensorsdata.analytics.android.sdk.SensorsDataAPI;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.NotificationMessage;
import cn.jpush.android.service.JPushMessageReceiver;

/**
 * Created by yzk on 2019-11-26
 *
 * 神策 SF 推送消息埋点示例及说明。标注 "TO DO" 的为开发者要做的或者要注意的。
 *
 * 具体说明（开发者要做的）：
 * 一、埋点 "App 打开推送消息"
 *   1.1 需要在 onNotifyMessageOpened 接口中，调用 trackAppOpenNotification(notificationMessage.notificationExtras, notificationMessage.notificationTitle, notificationMessage.notificationContent);
 *   1.2 如果使用了厂商通道需要在处理厂商通道的 Activity 的 onCreate，onNewIntent 中处理 intent，解析出相应的参数，调用  trackAppOpenNotification(extra, title, content);
 *
 * 二、上报 "推送 ID"
 *   2.1 在 onRegister 接口中，调用 SensorsDataAPI.sharedInstance().profilePushId("jiguang_id",s);
 *   2.2 在调用神策 SDK login 接口之后。调用 SensorsDataAPI.sharedInstance().profilePushId("jiguang_id",JPushInterface.getRegistrationID(this));
 *
 * 三、处理神策 SF 推送的 "打开 App 消息"、"打开 URL 消息"
 *   3.1 需要在 onNotifyMessageOpened 接口中，调用 handleSFPushMessage(notificationMessage.notificationExtras); 同时在 handleSFPushMessage 方法内加上处理动作。
 *   3.2 如果使用了厂商通道，需要在处理厂商通道的 Activity 的 onCreate，onNewIntent 中处理 intent，解析出相应的参数，调用  handleSFPushMessage(extra); 同时在 handleSFPushMessage 方法内加上处理动作。
 *
 * 具体接入过程中，如果有什么疑问，请及时在微信群里提出来！！！
 */

public class JiguangPushDemo {

    /**
     * 初始化极光推送 SDK
     */
    public static void init(Context context) {
        JPushInterface.init(context);
    }

    /**
     * 极光推送的 JPushMessageReceiver
     */
    public class MyJPushMessageReceiver extends JPushMessageReceiver {

        /**
         * 处理通知消息的点击
         */
        @Override
        public void onNotifyMessageOpened(Context context, NotificationMessage notificationMessage) {
            super.onNotifyMessageOpened(context, notificationMessage);
            // TODO 处理神策 SF 推送的 "打开 App"、"打开 URL 消息" 的动作
            handleSFPushMessage(notificationMessage.notificationExtras);
            // TODO 埋点 App 打开推送消息 事件
            trackAppOpenNotification(notificationMessage.notificationExtras, notificationMessage.notificationTitle, notificationMessage.notificationContent);
        }

        /**
         * 推送 ID 的处理
         */
        @Override
        public void onRegister(Context context, String s) {
            super.onRegister(context, s);
            // TODO 上报极光 "推送 ID"
            SensorsDataAPI.sharedInstance().profilePushId("jiguang_id",s);
            // TODO 注意在调用神策 SDK login 接口后，也需要调用 profilePushId 接口上报极光 "推送 ID"
            //SensorsDataAPI.sharedInstance().profilePushId("jiguang_id",JPushInterface.getRegistrationID(this));

        }
    }

    // ---------------------------------------- 下边是处理厂商通道消息示例代码 ----------------------------------------

    /**
     * 处理厂商通道消息的 Activity
     */
    public class HandlePushActivity extends Activity {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Intent intent = getIntent();
            if (intent != null) {
                // TODO 从推送的 intent 拿到具体的推送标题
                String title = "xxx";
                // TODO 从推送的 intent 拿到具体的推送内容
                String content = "xxx";
                // TODO 从推送的 intent 拿到具体的 sf_data 字段（神策 SF 发的推送消息 intent 中会有 sf_data 字段）
                String sf_data = "xxx";
                Log.i("TODO", String.format("-----title： %s。content：%s。sf_data： %s。", title, content, sf_data));
                HashMap<String,String> extra = new HashMap<> ();
                extra.put("sf_data",sf_data);
                // TODO 处理神策 SF 推送的 "打开 App"、"打开 URL" 动作
                handleSFPushMessage(extra);
                // TODO 埋点 App 打开推送消息 事件
                trackAppOpenNotification(extra, title, content);
            }
        }

        @Override
        protected void onNewIntent(Intent intent) {
            super.onNewIntent(intent);
            if (intent != null) {
                // TODO 从推送的 intent 拿到具体的推送标题
                String title = "xxx";
                // TODO 从推送的 intent 拿到具体的推送内容
                String content = "xxx";
                // TODO 从推送的 intent 拿到具体的 sf_data 字段（神策 SF 发的推送消息 intent 中会有 sf_data 字段）
                String sf_data = "xxx";
                Log.i("TODO", String.format("-----title： %s。content：%s。sf_data： %s。", title, content, sf_data));
                HashMap<String,String> extra = new HashMap<> ();
                extra.put("sf_data",sf_data);
                // TODO 处理神策 SF 推送的 "打开 App"、"打开 URL" 动作
                handleSFPushMessage(extra);
                // TODO 埋点 App 打开推送消息 事件
                trackAppOpenNotification(extra, title, content);
            }
        }
    }

    // ---------------------------------------- 下边是神策 SF 推送埋点示例代码 ----------------------------------------

    /**
     * 埋点 App 打开推送消息
     * <p>
     * 事件名：AppOpenNotification
     *
     * @param notificationExtras  推送消息的 extras（参数类型只能传 String 或 Map<String,String>）
     * @param notificationTitle   推送消息的标题
     * @param notificationContent 推送消息的内容
     */
    public static void trackAppOpenNotification(Object notificationExtras, String notificationTitle, String notificationContent) {
        try {
            JSONObject properties = new JSONObject();
            // 推送消息的标题
            properties.put("$sf_msg_title", notificationTitle);
            // 推送消息的内容
            properties.put("$sf_msg_content", notificationContent);
            try {
                String sfData = null;
                if (notificationExtras != null) {
                    if (notificationExtras instanceof String) {
                        sfData = new JSONObject((String) notificationExtras).optString("sf_data");
                    } else if (notificationExtras instanceof Map) {
                        sfData = new JSONObject((Map) notificationExtras).optString("sf_data");
                    }
                }
                if (!TextUtils.isEmpty(sfData)) {
                    JSONObject sfJson = new JSONObject(sfData);
                    // 推送消息中 SF 的内容
                    properties.put("$sf_msg_id", sfJson.optString("sf_msg_id", null));
                    properties.put("$sf_plan_id", sfJson.optString("sf_plan_id", null));
                    if (!"null".equals(sfJson.optString("sf_audience_id", null))) {
                        properties.put("$sf_audience_id", sfJson.optString("sf_audience_id", null));
                    }
                    properties.put("$sf_link_url", sfJson.optString("sf_link_url", null));
                    properties.put("$sf_plan_name", sfJson.optString("sf_plan_name", null));
                    properties.put("$sf_plan_strategy_id", sfJson.optString("sf_plan_strategy_id", null));
                    JSONObject customized = sfJson.optJSONObject("customized");
                    if (customized != null) {
                        Iterator<String> iterator = customized.keys();
                        while (iterator.hasNext()) {
                            String key = iterator.next();
                            properties.put(key, customized.opt(key));
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            // 埋点 "App 打开推送消息" 事件
            SensorsDataAPI.sharedInstance().track("AppOpenNotification", properties);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *  处理神策 SF 推送的 "打开 App"、"打开 URL 消息" 动作
     * TODO 此方法只是解析了神策 SF 推送消息，具体的业务逻辑需要开发者加上！！！
     *
     * @param notificationExtras 推送消息的 extra
     */
    public static void handleSFPushMessage(Object notificationExtras) {
        try {
            String sfData = null;
            if (notificationExtras != null) {
                if (notificationExtras instanceof String) {
                    sfData = new JSONObject((String) notificationExtras).optString("sf_data");
                } else if (notificationExtras instanceof Map) {
                    sfData = new JSONObject((Map) notificationExtras).optString("sf_data");
                }
            }
            if (!TextUtils.isEmpty(sfData)) {
                JSONObject sfJson = new JSONObject(sfData);
                if ("CUSTOMIZED".equals(sfJson.optString("sf_landing_type"))) {
                    JSONObject custom = sfJson.optJSONObject("customized");
                    if (custom != null) {
                        // 如果你们已经有根据自定义字段的跳转逻辑，此处无需处理（因为 SF 发的推送消息会兼容极光控制台的 "附加字段"）。
                    }
                } else if ("LINK".equals(sfJson.optString("sf_landing_type"))) {
                    String url = sfJson.optString("sf_link_url");
                    if(!TextUtils.isEmpty(url)){
                        // TODO 处理打开 URL 的消息
                        Log.i("TODO","-- 请处理打开 URL --: " + url);
                    }
                } else if ("OPEN_APP".equals(sfJson.optString("sf_landing_type"))) {
                    // TODO 打开 App
                    Log.i("TODO","-- 请启动 App --");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
