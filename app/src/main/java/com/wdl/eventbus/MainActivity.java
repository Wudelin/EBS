package com.wdl.eventbus;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.wdl.ebs.EventBus;
import com.wdl.ebs.annotation.Subscriber;

public class MainActivity extends AppCompatActivity
{

    private TextView tvTest;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvTest = findViewById(R.id.tv_test);
        tvTest.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                EventBus.getInstance().post(new TestEvent());
            }
        });
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        EventBus.getInstance().unregister(this);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        EventBus.getInstance().register(this);
    }

    @Subscriber
    public void test(TestEvent event)
    {
        tvTest.setText("Subscriber --------------------------- Subscriber");
    }
}
