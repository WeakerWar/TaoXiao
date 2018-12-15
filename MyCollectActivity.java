package com.example.taoxiao;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.taoxiao.Adapter.CollectDataAdapter;
import com.example.taoxiao.bin.CollectData;

import java.util.ArrayList;
import java.util.List;

public class MyCollectActivity extends AppCompatActivity implements View.OnClickListener {

    private List<CollectData> collectDataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_collect);

        getSupportActionBar().hide();

        /*对标题进行赋值*/
        TextView title = (TextView)findViewById(R.id.title_text);
        title.setText("我的收藏");
        Button bt_edit = (Button)findViewById(R.id.title_edit);
        bt_edit.setVisibility(View.INVISIBLE);
        Button bt_back = (Button)findViewById(R.id.title_back);
        bt_back.setOnClickListener(this);
        init();

        CollectDataAdapter adapter = new CollectDataAdapter(MyCollectActivity.this,R.layout.collect_item,collectDataList);
        ListView listView = (ListView)findViewById(R.id.collect_ls);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?>parent, View view, int position, long id){
                CollectData collectData = collectDataList.get(position);

                /*Log.d("ChatActivity","ID is " + data);*/

                Intent intent = new Intent(MyCollectActivity.this,PublishDetailActivity.class);   //跳转到文章界面
                /*将id传给下个发送聊天信息的界面*/
                intent.putExtra("title", collectData.getTitle());
                intent.putExtra("price", collectData.getPrice());
                intent.putExtra("description", collectData.getDescription());
                startActivity(intent);
            }
        });

    }

    @Override
    public void onClick(View v) {
        finish();
    }
    public void init(){
        CollectData collectData = new CollectData("充电宝","90","用了挺久的充电宝，很耐用，能虫3次苹果6.");
        collectDataList.add(collectData);
    }
}
