package com.example.dailyshoppinglist;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dailyshoppinglist.Model.Data;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Date;


public class HomeActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private FloatingActionButton fab_btn;

    private DatabaseReference mDatabase;

    private FirebaseAuth mAuth;

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser mUser = mAuth.getCurrentUser();

        String uId = mUser.getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Shopping List").child(uId);

        mDatabase.keepSynced(true);

        mToolbar = findViewById(R.id.home_toolbar);
        mToolbar = findViewById(R.id.home_toolbar);

        setSupportActionBar(mToolbar);

        getSupportActionBar().setTitle("Daily Shopping List");

        recyclerView = findViewById(R.id.recycler_home);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        layoutManager.setStackFromEnd(true);

        layoutManager.setReverseLayout(true);

        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(layoutManager);

        fab_btn = findViewById(R.id.fab);

        fab_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customDialog();
            }
        });
    }

    private void customDialog() {

        AlertDialog.Builder myDialog = new AlertDialog.Builder(HomeActivity.this);

        LayoutInflater inflater = LayoutInflater.from(HomeActivity.this);

        View myView = inflater.inflate(R.layout.input_layout, null);

        final AlertDialog dialog = myDialog.create();

        dialog.setView(myView);

        final EditText mType = myView.findViewById(R.id.et_type);
        final EditText mAmount = myView.findViewById(R.id.et_amount);
        final EditText mNote = myView.findViewById(R.id.et_note);

        Button mSaveBtn = myView.findViewById(R.id.btn_save);

        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String type = mType.getText().toString().trim();
                String amount = mAmount.getText().toString().trim();
                String note = mNote.getText().toString().trim();
                int numAmount = Integer.parseInt(amount);

                if (TextUtils.isEmpty(type)) {
                    mType.setError("Required field ...");
                    return;
                }
                if (TextUtils.isEmpty(amount)) {
                    mAmount.setError("Required field ...");
                    return;
                }
                if (TextUtils.isEmpty(note)) {
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

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Data> options =
                new FirebaseRecyclerOptions.Builder<Data>()
                        .setQuery(mDatabase, new SnapshotParser<Data>() {
                            @NonNull
                            @Override
                            public Data parseSnapshot(@NonNull DataSnapshot snapshot) {
                                return new Data(snapshot.child("type").getValue().toString(),
                                        Integer.parseInt(snapshot.child("amount").getValue().toString()),
                                        snapshot.child("note").getValue().toString(),
                                        snapshot.child("date").getValue().toString(),
                                        snapshot.child("id").getValue().toString());
                            }
                        })
                        .setLifecycleOwner(this)
                        .build();

        final FirebaseRecyclerAdapter<Data, MyViewHolder> adapter = new FirebaseRecyclerAdapter<Data, MyViewHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder viewHolder, int i, @NonNull Data data) {

                viewHolder.setDate(data.getDate());
                viewHolder.setType(data.getType());
                viewHolder.setNote(data.getNote());
                viewHolder.setAmount(data.getAmount());
            }

            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_data, parent, false);
                return new MyViewHolder(view);
            }

        };

        recyclerView.setAdapter(adapter);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        View myView;

        public MyViewHolder(View itemView) {
            super(itemView);
            myView = itemView;
        }

        public void setType(String type) {
            TextView mType = myView.findViewById(R.id.type);
            mType.setText(type);
        }

        public void setNote(String note) {
            TextView mNote = myView.findViewById(R.id.note);
            mNote.setText(note);
        }

        public void setDate(String date) {
            TextView mDate = myView.findViewById(R.id.date);
            mDate.setText(date);
        }

        public void setAmount(int amount) {
            TextView mAmount = myView.findViewById(R.id.amount);
            String strAmount = String.valueOf(amount);
            mAmount.setText(strAmount);
        }

    }

}
