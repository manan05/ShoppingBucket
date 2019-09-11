package com.example.dailyshoppinglist;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.w3c.dom.Text;

public class HomeActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    public FloatingActionButton fab_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mToolbar = findViewById(R.id.home_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Daily Shopping List");

        fab_btn =  findViewById(R.id.fab);

        fab_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customDialog();
            }
        });

    }

    private  void customDialog(){

        AlertDialog.Builder myDialog = new AlertDialog.Builder(HomeActivity.this);

        LayoutInflater inflater = LayoutInflater.from(HomeActivity.this);

        View myView = inflater.inflate(R.layout.input_layout,null);

        final AlertDialog dialog = myDialog.create();

        dialog.setView(myView);

        final EditText mType = myView.findViewById(R.id.et_type);
        final EditText mAmount = myView.findViewById(R.id.et_amount);
        final EditText mNote =  myView.findViewById(R.id.et_note);

        Button mSaveBtn = myView.findViewById(R.id.btn_save);

        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String type = mType.getText().toString().trim();
                String amount = mAmount.getText().toString().trim();
                String note = mNote.getText().toString().trim();

                if(TextUtils.isEmpty(type)){
                    mType.setError("Required field ...");
                    return;
                }
                if(TextUtils.isEmpty(amount)){
                    mAmount.setError("Required field ...");
                    return;
                }
                if(TextUtils.isEmpty(note)){
                    mNote.setError("Required field ...");
                    return;
                }
                dialog.dismiss();
            }
        });

        dialog.show();
    }

}
