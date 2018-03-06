package com.airness.performancemanager;

/**
 * Created by Dell on 2018/2/18.
 */

public class MyItem {
    private String title;
    private String text;
    private boolean isChecked;

    public MyItem(String title,String text){
        this.title=title;
        this.text=text;
    }

    public boolean isChecked(){
        return isChecked;
    }

    public void setChecked(boolean b){
        isChecked=b;
    }

    public String getTitle(){
        return title;
    }
    public String getText(){
        return text;
    }

}
