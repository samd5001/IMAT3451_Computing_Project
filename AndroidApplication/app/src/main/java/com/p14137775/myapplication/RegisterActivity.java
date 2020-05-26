package com.p14137775.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity
        implements RegisterBeginFragment.OnRegisterBegin, RegisterCompleteFragment.OnRegisterComplete {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        if (savedInstanceState != null) {
            return;
        }

        RegisterBeginFragment beginFragment = new RegisterBeginFragment();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.placeholder, beginFragment, "registerBegin").commit();
    }

    public void onRegisterComplete() {
        finish();
    }

    public void onRegisterBegin(String email, String password) {
        RegisterCompleteFragment completeFragment = new RegisterCompleteFragment();
        Bundle args = new Bundle();
        args.putString("email", email);
        args.putString("password", password);
        completeFragment.setArguments(args);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.placeholder, completeFragment, "registerComplete")
                .addToBackStack(null).commit();
    }
}
