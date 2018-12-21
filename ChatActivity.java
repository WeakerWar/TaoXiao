package com.example.taoxiao;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.taoxiao.ActivityCollector.BaseActivity;
import com.example.taoxiao.Adapter.MsgAdapter;
import com.example.taoxiao.Http.HttpUtil;
import com.example.taoxiao.bin.Msg;
import com.example.taoxiao.bin.MsgRecord;
import com.example.taoxiao.bin.WebSocketUtils;
import com.example.taoxiao.bin.user;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class ChatActivity extends BaseActivity {
    public static final int UPDATE_MSG = 1;
    private WebSocketClient mWebSocketClient;
    //8585为端口号，根据情况修改
    private final String address = "ws://" + HttpUtil.url + ":8080/ws/asset";

    private WebSocketUtils socketConnect = new WebSocketUtils();
    private List<Msg> msgList = new ArrayList<>();
    private EditText inputText;
    private Button send;
    private RecyclerView msgRecyclerView;
    private MsgAdapter adapter;
    private String id = null;
    private String message,sendid;
    private Msg msg;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        getSupportActionBar().hide();


        /*获取聊天对象id*/
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        Log.d("ChatActivity","聊天对象ID is " + id);

        /*对标题进行赋值*/
        TextView title = (TextView)findViewById(R.id.title_text);
        title.setText(id);
        Button bt_edit = (Button)findViewById(R.id.title_edit);
        bt_edit.setVisibility(View.INVISIBLE);
        ImageView bt_back = (ImageView) findViewById(R.id.title_back);


        inputText = (EditText) findViewById(R.id.input_text);
        send = (Button)findViewById(R.id.send);
        msgRecyclerView = (RecyclerView)findViewById(R.id.msg_recycle_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        msgRecyclerView.setLayoutManager(layoutManager);

        /*获取聊天记录*/
        List<MsgRecord> msgrecordList = DataSupport.select("message","status")
                                        .where("sendid = ?",id)
                                        .find(MsgRecord.class);
        int i=0;
        for(MsgRecord msgRecord: msgrecordList){
            i++;
            Msg msg = new Msg(msgRecord.getMessage(),msgRecord.getStatus());
            msgList.add(msg);
            if(i>9)break;
        }
        adapter = new MsgAdapter(msgList);
        msgRecyclerView.setAdapter(adapter);

        bt_back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
        send.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){



                String function = "chat";
                //account为发送者，修改成用户
                String account = user.username;
                String receiveaccount = id;
                //type为发送数据类型，目前只有文字
                String type = "text";
                String message = inputText.getText().toString();
                if("".equals(message)){
                    return;
                } else{
                    msg = new Msg(message, Msg.TPYE_SENT);
                    msgList.add(msg);
                    adapter.notifyItemInserted(msgList.size()-1);
                    msgRecyclerView.scrollToPosition(msgList.size()-1);
                    inputText.setText("");

                    inserttable(user.username,receiveaccount,message,Msg.TPYE_SENT);

                }
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");
                Date mtime = new Date(System.currentTimeMillis());
                String str = formatter.format(mtime);
                sendmessage2(function,account,receiveaccount,type,message,str);

            }
        });
        try {
            initSocketClient();
        }catch (URISyntaxException e){
            e.printStackTrace();
        }
        initSoctet();
    }


    /**存入表中*/
    public void inserttable(String userid,String sendid,String message,int TYPE){
        MsgRecord msgRecord = new MsgRecord();
        msgRecord.setUserid(user.username);
        msgRecord.setSendid(sendid);
        msgRecord.setMessage(message);
        msgRecord.setStatus(TYPE);
        msgRecord.save();
    }

    public void initSocketClient() throws URISyntaxException {
        if(mWebSocketClient == null) {
            mWebSocketClient = new WebSocketClient(new URI(address)) {
                @Override
                public void onOpen(ServerHandshake serverHandshake) {
                    String function = "online";
                    //account为发送者
                    String account=user.username;
                    sendmessage1(function,account);
                    Log.e("aaaa", "链接成功");
                }
                @Override
                public void onMessage(String s) {
                    try {
                        JSONObject jsonObject = new JSONObject(s);//收到服务器发送的消息
                        //发送给该用户的账号
//                        String sendid = jsonObject.getString("sendaccount");
//                        Msg msg = new Msg(sendid, Msg.TPYE_RECEIVED);
//                        msgList.add(msg);
//                        adapter.notifyItemInserted(msgList.size()-1);
//                        msgRecyclerView.scrollToPosition(msgList.size()-1);

                        /*收到的消息，和 发送者*/
                        message = jsonObject.getString("message");
                        sendid = jsonObject.getString("receiveaccount");
                        inserttable(user.username,sendid,message,Msg.TPYE_RECEIVED);

                        //更新界面
                        msg = new Msg(message, Msg.TPYE_RECEIVED);
                        msgList.add(msg);
                        adapter.notifyItemInserted(msgList.size()-1);
                        msgRecyclerView.scrollToPosition(msgList.size()-1);
                        Log.e("aaaa", "message" + message);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    //服务端消息
                    Log.e("aaaa", "服务器返回了" + s);
                }

                @Override
                public void onClose(int i, String s, boolean remote) {
                    //连接断开，remote判定是客户端断开还是服务端断开
                    if (remote) {
                        Log.e("aaaa", "服务器断开链接了");
                    } else {
                        Log.e("aaaa", "客户端断开链接了");
                    }
                }
                @Override
                public void onError(Exception e) {
                    Log.e("aaaa", "错误"+e.getMessage());
                }

            };
        }
    }


    //连接
    public void connect() {
        new Thread(){
            @Override
            public void run() {
                mWebSocketClient.connect();
            }
        }.start();
    }


    //关闭连接
    public void closeConnect() {
        try {
            mWebSocketClient.close();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        finally {
            mWebSocketClient = null;
        }
    }
    //登陆
    public void sendmessage1(String function, String account){
        String all="{\"function\":\""+function+"\",\"account\":\""+account+"\"}";
        mWebSocketClient.send(all);
    }
    /**
     *发送消息
     */
    public void sendmessage2(String function, String account, String receiveaccount,
                             String type, String message, String mtime){
        String all="{\"function\":\""+function+"\",\"sendaccount\":\""+account+"\",\"receiveaccount\":\""+receiveaccount
                +"\",\"type\":\""+type+"\",\"message\":\""+message+"\",\"mtime\":\""+mtime+"\"}";
        mWebSocketClient.send(all);
    }
    //断线了要重新执行这个函数
    public void initSoctet() {
        try {
            initSocketClient();
            //开始长连接
            connect();


        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private Handler handler = new Handler(){
        public void handleMessage(Message msg){
            switch (msg.what){
                case UPDATE_MSG:
                    //更新界面
                    Msg msg_rec = new Msg(message, Msg.TPYE_RECEIVED);
                    msgList.add(msg_rec);
                    adapter.notifyItemInserted(msgList.size()-1);
                    msgRecyclerView.scrollToPosition(msgList.size()-1);
                    adapter.notifyDataSetChanged();
                    Log.e("aaaa", "message" + message);
                    break;
                default:break;
            }
        }
    };

/*
            Log.d("aaaa","Message:"+ WebSocketUtils.message);
            if(id.equals(WebSocketUtils.sendid)){
        Msg msg = new Msg(WebSocketUtils.message, Msg.TPYE_RECEIVED);
        msgList.add(msg);
        adapter.notifyItemInserted(msgList.size()-1);
        msgRecyclerView.scrollToPosition(msgList.size()-1);
    }
 */
}





