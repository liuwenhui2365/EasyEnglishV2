package com.liu.easyenglishupdate.entity;

/**
 * 用来存储只有两个成语变量的类
 * （单词和意思、类型和网址）
 * Created by Administrator on 2015/10/18.
 */
public class VariableMap {
    /**
     * 成员变量1
     */
    private String mVariable1;
    /**
     * 成员变量2
     */
    private String mVariable2;

    public VariableMap(){
    }

    public VariableMap(String variable1,String variable2){
        this.mVariable1 = variable1;
        this.mVariable2 = variable2;
    }


    public String getmVariable2() {
        return mVariable2;
    }

    public void setmVariable2(String mVariable2) {
        this.mVariable2 = mVariable2;
    }

    public String getmVariable1() {
        return mVariable1;
    }

    public void setmVariable1(String mVariable1) {
        this.mVariable1 = mVariable1;
    }
}
