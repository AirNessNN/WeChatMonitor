package com.airness.performancemanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Dell on 2018/2/18.
 */

public class MyAdapter extends BaseAdapter {

    private ArrayList<MyItem> list=null;
    private LayoutInflater mInflater=null;
    public OnRadioButtonFound IRadioButtonfinder=null;

    public MyAdapter(LayoutInflater inflater){
        mInflater=inflater;
        list=new ArrayList<>();
    }

    public void add(MyItem item){
        list.add(item);
    }


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }


    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View mView=mInflater.inflate(R.layout.item,null);

        MyItem item=list.get(i);
        TextView title=mView.findViewById(R.id.item_title);
        TextView text=mView.findViewById(R.id.item_text);
        RadioButton radioButton=mView.findViewById(R.id.radioButton);
        radioButton.setChecked(item.isChecked());
        if(IRadioButtonfinder!=null&&item.isChecked()){
            IRadioButtonfinder.getRadioButton(radioButton);
            item.setChecked(false);
        }
        title.setText(item.getTitle());
        text.setText(item.getText());

        return mView;
    }
}
