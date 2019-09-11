package com.example.dailyshoppinglist;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.dailyshoppinglist.Model.Data;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Date;


public class HomeActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private FloatingActionButton fab_btn;

    private DatabaseReference mDatabase;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mToolbar = findViewById(R.id.home_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Daily Shopping List");

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser mUser = mAuth.getCurrentUser();

        String uId = mUser.getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Shopping List").child(uId);


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
                int numAmount = Integer.parseInt(amount);

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

                String id = mDatabase.push().getKey();

                String date = DateFormat.getDateInstance().format(new Date());

                Data data = new Data(type, numAmount, note, date, id);

                mDatabase.child(id).setValue(data);

                Toast.makeText(getApplicationContext(), "Data Added", Toast.LENGTH_SHORT).show();

                dialog.dismiss();
            }
        });

        dialog.show();
    }

}
