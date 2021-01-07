package com.rockchipme.app.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.rockchipme.app.R;
import com.rockchipme.app.helpers.Constants;

public class RestIdPopUpActivity extends AppCompatActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(this);
        View mView = layoutInflaterAndroid.inflate(R.layout.user_input_dialog, null);
        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(this);
        alertDialogBuilderUserInput.setView(mView);

        final EditText userInputDialogEditText =  mView.findViewById(R.id.userInputDialog);
        if (Constants.REST_KEY!=null){
            userInputDialogEditText.setText(Constants.REST_KEY);
            userInputDialogEditText.setSelection(Constants.REST_KEY.length());
        }
        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {
                        // ToDo get user input here
                        String REST_KEY = userInputDialogEditText.getText().toString().trim();
                        hideKeyBoard();
                        if (REST_KEY.length()<1){
                            Toast.makeText(RestIdPopUpActivity.this, "Enter restaurant key", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Constants.REST_KEY = REST_KEY;
                        Intent intent = new Intent(getApplicationContext(), SplashScreenActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                })

                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                dialogBox.cancel();
                                finish();
                            }
                        });

        AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
        alertDialogAndroid.show();
    }

    public void hideKeyBoard(){
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}
