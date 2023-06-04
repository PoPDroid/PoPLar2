package com.yleg.testvictimbutton;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {

    private Button startButton, clipButton, resetButton;
    private  TextView hinttext, textnum;
    private int level,depth;
    private int maxlevel  = 6;
    MyDatabaseHelper db;
    private int count = 0;
    int LAUNCH_SECOND_ACTIVITY = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new MyDatabaseHelper(this);
        level = db.getCurrentLevel();
        depth = db.GetCurrentDepth();
        updateLogs();

        startButton = (Button) findViewById(R.id.button2);
        hinttext = (TextView) findViewById(R.id.textViewHint);
        textnum = findViewById(R.id.textViewNumber);
        clipButton = (Button) findViewById(R.id.buttonclip);
        resetButton = (Button) findViewById(R.id.buttonreset);

        if(level==0){
            startButton.setVisibility(View.VISIBLE);
            startButton.setText("Start Trial Run - PoPLar");
            hinttext.setText("Start Trial Run. This will help you familiarize yourself with PoPLar");
            clipButton.setVisibility(View.GONE);
            EditText etm = findViewById(R.id.editTextTextMultiLine);
            textnum.setText(String.valueOf(maxlevel));
            etm.setText("");
            db.resetPuzzleLogs();
            depth=2;
        }else if (level<=maxlevel){

            textnum.setText(String.valueOf(maxlevel-level+1));
            hinttext.setText("Start PoPL: " + (maxlevel-level+1)+" runs left." );
        }else{
            clipButton.setVisibility(View.VISIBLE);
            textnum.setText("0");
        }

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (preferences.getBoolean("confirmed",false)){
        }else {
            Intent myint = new Intent(this, Confirm.class);
            startActivity(myint);
        }

    }

    public void onClick(View v){

        Intent myint = new Intent(this, PoPPuzzleChallenge_trap_3.class);
        textnum = findViewById(R.id.textViewNumber);
        int num = 2;
        String txt = ("test");
        myint.putExtra("PoPDepth",num);
        myint.putExtra("PoPText",txt);
        count++;
        startActivityForResult(myint, LAUNCH_SECOND_ACTIVITY);
    }

    public void onClipboard(View v){
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("label", getLogs());
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this,"Copied",Toast.LENGTH_LONG).show();
    }

    public void onReset(View v){
        //do counter that shows only start at first, then start and reset, and finally only send resutls
        new AlertDialog.Builder(this)
                .setTitle("Reset")
                .setMessage("Do you really want to reset?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        db.resetPuzzleLogs();
                        startButton.setVisibility(View.VISIBLE);
                        startButton.setText("Start Trial Run PoPLar");
                        hinttext.setText("Start Trial Run. This will help you familiarize yourself with PoPL");
                        clipButton.setVisibility(View.GONE);
                        resetButton.setVisibility(View.GONE);
                        EditText etm = findViewById(R.id.editTextTextMultiLine);
                        etm.setText("");
                        level = 0;
                        depth = 2;
                        textnum.setText(String.valueOf(maxlevel-level+1));
                    }})
                .setNegativeButton(android.R.string.no, null).show();
    }

    private String getLogs(){
        Cursor cursor = db.GetAllPuzzleLogs();
        String myres = "";
        while (cursor.moveToNext()) {
            myres+= String.valueOf(cursor.getInt(1));
            myres += " - " + String.valueOf(cursor.getInt(2)) +"\n";

        }
        if (cursor != null) {
            cursor.close();
        }
        return  myres;
    }
    private void updateLogs(){
        EditText etm = findViewById(R.id.editTextTextMultiLine);
        String myres = getLogs();
        etm.setText("");
        etm.setText(myres);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Boolean result=data.getBooleanExtra("PoPPuzzle",false);

        if (requestCode == LAUNCH_SECOND_ACTIVITY) {
            if(resultCode == Activity.RESULT_OK){

                if(result){
                    long duration = data.getLongExtra("PoPPuzzleTime",0);
                    db.InsertPuzzleDetails(depth, duration);
                    updateLogs();
                    level+=1;

                    if(level>0 && level<=maxlevel){
                        depth = 2;
                        startButton.setVisibility(View.VISIBLE);
                        startButton.setText("Launch PoPLar");
                        hinttext.setText("Launch PoPLar: " + (maxlevel-level+1)+" runs left." );
                        clipButton.setVisibility(View.GONE);
                        resetButton.setVisibility(View.VISIBLE);
                    }else {
                        hinttext.setText("Tests complete. Please submit time data" );
                        clipButton.setVisibility(View.VISIBLE);
                        startButton.setVisibility(View.GONE);
                    }

                    textnum.setText(String.valueOf(maxlevel-level+1));
                    String time = "Presence verified!! (" + duration/1000 + " seconds)";
                    Toast.makeText(getApplicationContext(),time, Toast.LENGTH_LONG).show();


                }

            }
            if (resultCode == Activity.RESULT_CANCELED) {
//                TextView tv = (TextView) findViewById(R.id.tv);
//                if(tv!=null){
//                    tv.setText("Prove your presence!");
//                }

            }
        }
    }//onActivityResult
}
