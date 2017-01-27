package com.globalpaysolutions.realtimelocationtest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity
{
    Button btnSunny;
    Button btnFoggy;
    TextView tvCondition;

    DatabaseReference  mRootReference = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mConditionRef = mRootReference.child("condition");

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSunny = (Button) findViewById(R.id.btnSunny);
        btnFoggy = (Button) findViewById(R.id.btnFoggy);
        tvCondition = (TextView) findViewById(R.id.tvCondition);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        mConditionRef.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                String text = dataSnapshot.getValue(String.class);
                tvCondition.setText(text);
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });

        btnSunny.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mConditionRef.setValue("Soleado");
            }
        });

        btnFoggy.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mConditionRef.setValue("Neblina");
            }
        });
    }


}
