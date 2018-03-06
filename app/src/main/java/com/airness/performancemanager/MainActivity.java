package com.airness.performancemanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private MyItem last=null;
    private RadioButton lastButton=null;

    public static final int REQUEST_MEDIA_PROJECTION = 0x2893;
    public static Intent data=null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        //initToolbar(R.layout.tool_bar,R.id.toolBarTitle,R.string.app_name);

        initView();
        initService();




    }

    private void initView(){
        ListView listView= findViewById(R.id.list);
        LayoutInflater inflater=getLayoutInflater();

        MyItem item1=new MyItem(getStringXML(R.string.model_title1),getStringXML(R.string.model_info1));
        item1.setChecked(true);
        MyItem item2=new MyItem(getStringXML(R.string.model_title2),getStringXML(R.string.model_info2));
        MyItem item3=new MyItem(getStringXML(R.string.model_title3),getStringXML(R.string.model_info3));
        MyItem item4=new MyItem(getStringXML(R.string.model_title4),getStringXML(R.string.model_info4));

        final MyAdapter adapter=new MyAdapter(inflater);
        adapter.IRadioButtonfinder=new OnRadioButtonFound() {
            @Override
            public void getRadioButton(RadioButton radioButton) {
                lastButton=radioButton;
            }
        };
        adapter.add(item1);
        adapter.add(item2);
        adapter.add(item3);
        adapter.add(item4);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView title=view.findViewById(R.id.item_title);
                TextView text=view.findViewById(R.id.item_text);

                TextView mTitle=findViewById(R.id.modelName);
                TextView mText=findViewById(R.id.modelText);

                mTitle.setText(title.getText());
                mText.setText(text.getText());


                RadioButton radioButton=view.findViewById(R.id.radioButton);
                if(lastButton==radioButton){
                    return;
                }
                radioButton.setChecked(true);
                if(lastButton!=null){
                    lastButton.setChecked(false);
                }
                lastButton=radioButton;

                MyItem item=(MyItem)adapter.getItem(i);
                item.setChecked(true);
                if(last!=null){
                    last.setChecked(false);
                }
                last=item;
            }
        });

    }

    private void initService(){
        Intent intent=new Intent(this,BackgroundService.class);
        startService(intent);
    }

    public String getStringXML(int id){
        return this.getResources().getString(id);
    }










}
