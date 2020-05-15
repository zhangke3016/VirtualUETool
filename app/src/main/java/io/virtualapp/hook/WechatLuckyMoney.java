package io.virtualapp.hook;

import static de.robv.android.xposed.XposedBridge.invokeOriginalMethod;
import static de.robv.android.xposed.XposedBridge.log;
import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.callStaticMethod;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;
import static de.robv.android.xposed.XposedHelpers.getObjectField;
import static de.robv.android.xposed.XposedHelpers.newInstance;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodHook.MethodHookParam;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import java.io.StringReader;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

public class WechatLuckyMoney implements IXposedHookLoadPackage {

    private static final String WECHAT_PACKAGE_NAME = "com.tencent.mm";
    private static final String LUCKY_MONEY_RECEIVE_UI_CLASS_NAME = "com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyReceiveUI";
    private static final String NOTIFICATION_CLASS_NAME = "com.tencent.mm.booter.notification.b";

    static boolean open = true;
    static boolean delay = false;
    static long delayTime = 300;

    @Override
    public void handleLoadPackage(final LoadPackageParam lpparam) {
        if (lpparam.packageName.equals(WECHAT_PACKAGE_NAME)) {
            findAndHookMethod(NOTIFICATION_CLASS_NAME, lpparam.classLoader, "a", NOTIFICATION_CLASS_NAME, String.class, String.class, int.class, int.class, boolean.class, new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            if (!open) {
                                return;
                            }
                            String msgtype = "436207665";
                            if (param.args[3].toString().equals(msgtype)) {
                                String xmlmsg = param.args[2].toString();
                                String xl = xmlmsg.substring(xmlmsg.indexOf("<msg>"));
                                //nativeurl
                                String p = "nativeurl";
                                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                                factory.setNamespaceAware(true);
                                XmlPullParser pz = factory.newPullParser();
                                pz.setInput(new StringReader(xl));
                                int v = pz.getEventType();
                                String saveurl = "";
                                while (v != XmlPullParser.END_DOCUMENT) {
                                    if (v == XmlPullParser.START_TAG) {
                                        if (pz.getName().equals(p)) {
                                            pz.nextToken();
                                            saveurl = pz.getText();
                                            break;
                                        }
                                    }
                                    v = pz.next();
                                }
                                String nativeurl = saveurl;
                                Uri nativeUrl = Uri.parse(nativeurl);
                                int msgType = Integer.parseInt(nativeUrl.getQueryParameter("msgtype"));
                                int channelId = Integer.parseInt(nativeUrl.getQueryParameter("channelid"));
                                String sendId = nativeUrl.getQueryParameter("sendid");
                                String headImg = "";
                                String nickName = "";
                                String sessionUserName = param.args[1].toString();
                                String ver = "v1.0";
                                final Object ab = newInstance(findClass("com.tencent.mm.plugin.luckymoney.c.ab", lpparam.classLoader),
                                        msgType, channelId, sendId, nativeurl, headImg, nickName, sessionUserName, ver);

                                Context context = (Context) callStaticMethod(findClass("com.tencent.mm.sdk.platformtools.z", lpparam.classLoader), "getContext");
                                final Object i = newInstance(findClass("com.tencent.mm.plugin.luckymoney.c.i", lpparam.classLoader), context, null);

                                if (delay) {
                                    Thread.sleep(delayTime);
                                }
                                callMethod(i, "a", ab, false);
                            }
                        }
                    }

            );


            findAndHookMethod(LUCKY_MONEY_RECEIVE_UI_CLASS_NAME, lpparam.classLoader, "d", int.class, int.class, String.class, "com.tencent.mm.s.j", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    Class receiveUI = findClass(LUCKY_MONEY_RECEIVE_UI_CLASS_NAME, lpparam.classLoader);

                    Button button = (Button) callStaticMethod(receiveUI, "e", param.thisObject);
                    if (button.isShown() && button.isClickable()) {
                        button.performClick();
                        callMethod(param.thisObject, "finish");
                    } else {
                        callMethod(param.thisObject, "finish");
                    }
                }
            });


            findAndHookMethod("com.tencent.mm.pluginsdk.ui.chat.ChatFooter$2", lpparam.classLoader, "onClick", View.class, new XC_MethodReplacement() {
                @Override
                protected Object replaceHookedMethod(XC_MethodHook.MethodHookParam methodHookParam) throws Throwable {
                    Object chatFooter = getObjectField(methodHookParam.thisObject, "iWt");
                    EditText editText = (EditText) getObjectField(chatFooter, "fdR");
                    log(editText.getText().toString());
                    String command = editText.getEditableText().toString().trim();
                    Context context = (Context) callStaticMethod(findClass("com.tencent.mm.sdk.platformtools.z", lpparam.classLoader), "getContext");
                    if (command.equals("open")) {
                        open = true;
                        Toast.makeText(context, "红包机器人打开", Toast.LENGTH_SHORT).show();
                    } else if (command.equals("close")) {
                        open = false;
                        Toast.makeText(context, "红包机器人关闭", Toast.LENGTH_SHORT).show();
                    } else if (command.equals("delay")) {
                        delay = true;
                        Toast.makeText(context, "延时已经开启", Toast.LENGTH_SHORT).show();
                    } else if (command.equals("nodelay")) {
                        delay = false;
                        Toast.makeText(context, "延时已经关闭", Toast.LENGTH_SHORT).show();
                    } else if (command.matches("delay\\d{2,}")) {
                        String tmp = command.replace("delay", "");
                        delayTime = Long.valueOf(tmp);
                        Toast.makeText(context, "延时设置成功: " + tmp + "毫秒", Toast.LENGTH_SHORT).show();
                    } else if (command.equals("help")) {
                        Toast.makeText(context, "命令说明：\nopen打开红包机器人\nclose关闭红包机器人\ndelay开启延时\nnodelay关闭延时\ndelay后面跟毫秒数设置延时\nhelp 显示本帮助", Toast.LENGTH_LONG).show();
                    } else {
                        return invokeOriginalMethod(methodHookParam.method, methodHookParam.thisObject, methodHookParam.args);
                    }
                    editText.setText("");
                    return null;
                }
            });
        }


    }
}
