package com.example.yf.grabwallet;

import android.accessibilityservice.AccessibilityService;
import android.app.Notification;
import android.app.PendingIntent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

public class GrabWallet extends AccessibilityService {


    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

        int eventType = event.getEventType();
        switch (eventType){
            //第一步监听通知栏消息
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
                List<CharSequence> texts = event.getText();
                if (!texts.isEmpty()){
                    for (CharSequence text : texts){
                        String content = text.toString();
                        if (content.contains("[微信红包]")){
                            //模拟打开通知栏消息
                            if (event.getParcelableData() != null && event.getParcelableData() instanceof Notification){
                                Notification notification = (Notification) event.getParcelableData();
                                PendingIntent pendingIntent = notification.contentIntent;
                                try {
                                    pendingIntent.send();
                                }
                                catch (PendingIntent.CanceledException e){
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
                break;
            //第二部：监听是都进入微信红包消息界面
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                String className = event.getClassName().toString();
                if (className.equals("com.tencent.mm.ui.LauncherUI")){
                    //开始抢红包
                    getPacket();
                }
                else if (className.equals("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyReceiveUI")){
                    //开始打开红包
                    openPacket();
                }
                break;
        }

    }

    //打开红包
    private void openPacket() {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo != null){
            List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText("抢红包");
            for (AccessibilityNodeInfo n : list){
                n.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        }

    }

    //抢红包
    private void getPacket() {
        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        recycle(rootNode);
    }

    private void recycle(AccessibilityNodeInfo info) {
        if (info.getChildCount() == 0){
            if (info.getText() != null){
                if ("開".equals(info.getText().toString())){
                    info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    AccessibilityNodeInfo parent = info.getParent();
                    while (parent != null){
                        if (parent.isClickable()){
                            parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                            break;
                        }
                        parent = parent.getParent();
                    }
                }
            }
        }
        else {
            for (int i = 0 ; i < info.getChildCount() ; i++){
                if (info.getChild(i) != null){
                    recycle(info.getChild(i));
                }
            }
        }

    }

    @Override
    public void onInterrupt() {

    }
}
