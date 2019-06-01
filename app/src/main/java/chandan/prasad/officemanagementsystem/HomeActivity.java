package chandan.prasad.officemanagementsystem;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Date;
import java.util.Objects;

import chandan.prasad.officemanagementsystem.Model.Data;

public class HomeActivity extends AppCompatActivity {
    Toolbar toolbar;
    static String test = "Your Task App";
    DatabaseReference databaseReference;
    FirebaseAuth efirebaseAuth;

    RecyclerView recyclerView;

    //Update Field.....
    EditText editTextTitleUpdate, editTextNotesUpdate;
    Button buttonUpdate, buttonDelete;


    //Variable for the update and delete operation
    String u_title, u_note, post_key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        toolbar = findViewById(R.id.toolbar_home);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(test);
        getSupportActionBar().setIcon(R.drawable.ic_person);
        //Firebase Authentication
        efirebaseAuth = FirebaseAuth.getInstance();
        //Firebase User
        FirebaseUser firebaseUser = efirebaseAuth.getCurrentUser();
        String uId = null;
        if (firebaseUser != null) {
            uId = firebaseUser.getUid();
            //user is loged in
        }

        //Firebase Database
        if (uId != null) {
            databaseReference = FirebaseDatabase.getInstance().getReference().child("TaskNote").child(uId);
        }
        databaseReference.keepSynced(true);
        // Recycler View
        recyclerView = findViewById(R.id.recycler);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);


        FloatingActionButton floatingActionButton = findViewById(R.id.fab_btn);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //before inflating the custom alert dialog layout, we will get the current activity viewgroup
                ViewGroup viewGroup = findViewById(android.R.id.content);

                //then we will inflate the custom alert dialog xml that we created
                View dialogView = LayoutInflater.from(HomeActivity.this).inflate(R.layout.custom_input_field, viewGroup, false);


                //Now we need an AlertDialog.Builder object
                AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);


                //setting the view of the builder to our custom view that we already inflated
                builder.setView(dialogView);

                //finally creating the alert dialog and displaying it
                final AlertDialog alertDialog = builder.create();
                alertDialog.show();

                final EditText e_title = dialogView.findViewById(R.id.edit_Title);
                final EditText e_notes = dialogView.findViewById(R.id.edit_Notes);
                Button btn_save = dialogView.findViewById(R.id.btn_Save);
                btn_save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String s_title = e_title.getText().toString().trim();
                        String s_notes = e_notes.getText().toString().trim();
                        if (TextUtils.isEmpty(s_title)) {
                            e_title.setError("Required Field.. ");
                            return;
                        }
                        if (TextUtils.isEmpty(s_notes)) {
                            e_notes.setError("Required Field.. ");
                            return;
                        }

                        String s_id = databaseReference.push().getKey();
                        String s_date = DateFormat.getDateInstance().format(new Date());
                        Data data = new Data(s_title, s_notes, s_date, s_id);
                        if (s_id != null) {
                            databaseReference.child(s_id).setValue(data);
                        }
                        Toast.makeText(HomeActivity.this, "Data Inserted ", Toast.LENGTH_SHORT).show();
                        alertDialog.dismiss();
                    }
                });


            }
        });
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        View myview;

        public MyViewHolder(View itemView) {
            super(itemView);
            myview = itemView;
        }

        public void setTitle(String title) {
            TextView title_textView = myview.findViewById(R.id.rtitle);
            title_textView.setText(title);
        }

        public void setNote(String note) {
            TextView note_textView = myview.findViewById(R.id.note);
            note_textView.setText(note);
        }

        public void setdate(String date) {
            TextView date_textView = myview.findViewById(R.id.date);
            date_textView.setText(date);
        }

    }

    public void updatedata() {
        //before inflating the custom alert dialog layout, we will get the current activity viewgroup
        ViewGroup uviewGroup = findViewById(android.R.id.content);

        //then we will inflate the custom alert dialog xml that we created
        View udialogView = LayoutInflater.from(HomeActivity.this).inflate(R.layout.update_input_field, uviewGroup, false);


        //Now we need an AlertDialog.Builder object
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);


        //setting the view of the builder to our custom view that we already inflated
        builder.setView(udialogView);

        //finally creating the alert dialog and displaying it
        final AlertDialog ualertDialog = builder.create();

        //Update
        editTextTitleUpdate = udialogView.findViewById(R.id.edit_Title_update);
        editTextNotesUpdate = udialogView.findViewById(R.id.edit_Notes_update);
        editTextTitleUpdate.setText(u_title);
        editTextTitleUpdate.setSelection(u_title.length());

        editTextNotesUpdate.setText(u_note);
        editTextNotesUpdate.setSelection(u_title.length());

        buttonDelete = udialogView.findViewById(R.id.btn_del);
        buttonUpdate = udialogView.findViewById(R.id.btn_Update);

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                u_title = editTextTitleUpdate.getText().toString().trim();
                u_note = editTextNotesUpdate.getText().toString().trim();

                String uDate = DateFormat.getDateInstance().format(new Date());
                Data udata = new Data(u_title, u_note, uDate, post_key);
                databaseReference.child(post_key).setValue(udata);
                ualertDialog.dismiss();
            }
        });
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.child(post_key).removeValue();
                ualertDialog.dismiss();
            }
        });

        ualertDialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Data, MyViewHolder> adapter = new FirebaseRecyclerAdapter<Data, MyViewHolder>(Data.class, R.layout.item_data, MyViewHolder.class, databaseReference) {
            @Override
            protected void populateViewHolder(MyViewHolder viewHolder, final Data model, final int position) {
                viewHolder.setTitle(model.getTitle());
                viewHolder.setNote(model.getNote());
                viewHolder.setdate(model.getDate());
                viewHolder.myview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //for getting the key at  position of the task
                        post_key = getRef(position).getKey();
                        u_title = model.getTitle();
                        u_note = model.getNote();
                        updatedata();
                    }
                });
            }
        };
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        efirebaseAuth.signOut();
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                efirebaseAuth.signOut();
                startActivity(new Intent(HomeActivity.this, MainActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
