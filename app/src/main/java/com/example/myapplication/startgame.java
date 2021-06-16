package com.example.myapplication;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

//the start a new game dialog
public class startgame extends Dialog{

    protected Button okBtn;
    protected Button cancelBtn;
    public EditText date;
    public EditText time;
    public EditText max;

    public startgame(Context context) {
        super(context);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setCancelable(false);  // 是否可以撤销
        setContentView(R.layout.startgamedia);
        okBtn = (Button) findViewById(R.id.ok);
        cancelBtn = (Button) findViewById(R.id.cancel);

    }

    //ok button
    public void setRightButton(View.OnClickListener clickListener) {
        okBtn.setOnClickListener(clickListener);
    }

    //cancel button
    public void setLeftButton(View.OnClickListener clickListener) {
        cancelBtn.setOnClickListener(clickListener);
    }

    public String getDate(){
        date=findViewById(R.id.date);
        return date.getText().toString();
    }
    public String getTime(){
        time=findViewById(R.id.time);
        return time.getText().toString();
    }
    public String getMax(){
        max=findViewById(R.id.max);
        return max.getText().toString();
    }
}