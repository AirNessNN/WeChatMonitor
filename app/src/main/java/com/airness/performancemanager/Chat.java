package com.airness.performancemanager;

import java.io.Serializable;

/**
 * Created by ${AirNess} on 2018/2/23.
 */

public class Chat implements Serializable {
    private String name;
    private String text;

    public Chat(String name,String text){
        this.name=name;
        this.text=text;
    }


    public String getName(){
        return name;
    }

    public String getText(){
        return text;
    }

    @Override
    public boolean equals(Object obj) {
        Chat c=(Chat)obj;

        return c.getText().equals(this.getText());
    }

}
