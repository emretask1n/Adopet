package com.example.adopet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;


import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class registerActivity extends AppCompatActivity{

    private Button CreateAccountButton;
    private EditText InputName, InputMail, InputPassword;
    private ProgressDialog loadingBar;
    private Spinner roleSpinner;

    String [] array_spinner = new String[]{"Adopter","Pet Owner"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        CreateAccountButton = (Button) findViewById(R.id.register_btn);
        InputMail = (EditText) findViewById(R.id.register_email_input);
        InputPassword = (EditText) findViewById(R.id.register_password_input);
        InputName = (EditText) findViewById(R.id.register_username_input);
        loadingBar = new ProgressDialog(this);
        roleSpinner = (Spinner) findViewById(R.id.spinner);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,array_spinner);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roleSpinner.setAdapter(adapter);

        roleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id){
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent){

            }
            });
                
        CreateAccountButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view){

                CreateAccount();
            }

        });

    }

    private void CreateAccount() {

        String name = InputName.getText().toString();
        String mail = InputMail.getText().toString();
        String password = InputPassword.getText().toString();
        String role = roleSpinner.getSelectedItem().toString();


        if (TextUtils.isEmpty(name))
        {
            Toast.makeText(this," Please write your name ...", Toast.LENGTH_SHORT ).show();
        }
        else if (TextUtils.isEmpty(mail))
        {
            Toast.makeText(this," Please write your e-mail address ...", Toast.LENGTH_SHORT ).show();
        }
        else if (TextUtils.isEmpty(password))
        {
            Toast.makeText(this," Please write your password ...", Toast.LENGTH_SHORT ).show();
        }
        else{
            loadingBar.setTitle("Create Account");
            loadingBar.setMessage("Please wait, we are checking the credentials.");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();


            ValidateMail(name, mail, password,role);


        }

    }

    private void ValidateMail(final String name,final String mail,final String password,final String role)
    {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (!(dataSnapshot.child("Users").child(mail).exists())){

                    HashMap<String, Object> userdataMap = new HashMap<>();
                    userdataMap.put("name",name);
                    userdataMap.put("mail",mail);
                    userdataMap.put("password",password);
                    userdataMap.put("role",role);

                    RootRef.child("Users").child(mail).updateChildren(userdataMap).addOnCompleteListener(new OnCompleteListener<Void>(){
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            Toast.makeText(registerActivity.this, "Congratulations, your account has been created.", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();

                            Intent intent = new Intent(registerActivity.this, loginActivity.class);
                            startActivity(intent);
                        }
                        else
                        {
                            Toast.makeText(registerActivity.this, "Network Error: Please try again after some time...", Toast.LENGTH_SHORT).show();
                        }
                    }
                });



                }
                else {
                    Toast.makeText(registerActivity.this, "This" + mail + "already exist", Toast.LENGTH_LONG).show();
                    loadingBar.dismiss();
                    Toast.makeText(registerActivity.this, "Try using another e-Mail address", Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(registerActivity.this, registerActivity.class);
                    startActivity(intent);

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}