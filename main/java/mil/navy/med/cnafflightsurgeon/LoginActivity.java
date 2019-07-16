package mil.navy.med.cnafflightsurgeon;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    EditText mETUser;
    EditText mETPword;
    Spinner mSpinnerCMD;

    String username;
    String CMD;
    String password;
    String[] input;
    String[] output;
    HashMap<String, String> decoder;

    //I would like to load this from the String resources but couldn't figure out a good
    //way to do it since I can't reference them until onCreate...Just keep in mind you need to
    //update these if you update the commands in the Strings.xml file
    static final String CNAP = "CNAP";
    static final String CNAL = "CNAL";
    static final String CNAFR = "CNAFR";
    static final String CNATRA = "CNATRA";
    static final String NAVAIR = "NAVAIR";
    static final String NAMI = "NAMI";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mETUser = (EditText) findViewById(R.id.editTextUsername);
        mETPword = (EditText) findViewById(R.id.editTextPassword);
        mSpinnerCMD = (Spinner) findViewById(R.id.spinnerActivities);
        input = getResources().getStringArray(R.array.pwInput);
        output = getResources().getStringArray(R.array.pwOutput);
        decoder = new HashMap<>();
        username = "";
        password = "";
        for(int i =0; i<input.length; i++){
            decoder.put(input[i], output[i]);
        }

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.commands, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.
                simple_spinner_dropdown_item);
        mSpinnerCMD.setAdapter(adapter);
        mSpinnerCMD.setOnItemSelectedListener(new ActivitySpinner());


    }

    public void onClickLogin(View v)
    {
        username = mETUser.getText().toString();
        password = mETPword.getText().toString();
        if(username.isEmpty() || password.isEmpty()){
            Toast.makeText(this, getString(R.string.error_command_required), Toast.LENGTH_SHORT).show();
            return;
        }
        else if(username.length() < 2)
        {
            Toast.makeText(this, "Invalid Username", Toast.LENGTH_SHORT).show();
        }
        else if(checkPassword()){
            launchHome();
        }
        else {
            Toast.makeText(this, getString(R.string.error_incorrect_password), Toast.LENGTH_SHORT).show();
        }

    }

    public boolean checkPassword()
    {
        String A = decoder.get(username.substring(
                username.length()-2, username.length()-1));
        String C = decoder.get(username.substring(1,2));
        String E = decoder.get(username.substring(username.length()-1));

        String B;
        String D;

        switch(CMD){
            case CNAP :
                B = "cLd";
                D = "5";
                break;
            case CNAL :
                B = "S2";
                D = "6e";
                break;
            case CNAFR :
                B = "5tf";
                D = "7";
                break;
            case CNATRA  :
                B = "8p";
                D = "8g";
                break;
            case NAVAIR :
                B = "pzh";
                D = "9";
                break;
            case NAMI :
                B = "y6j";
                D = "4";
                break;
            default :
                B = "!@#$QWEWR";
                D = "1234qwer";
                break;
        }

        String ans = A+B+C+D+E;

        if (password.equals(ans)) return true;

        //Toast.makeText(this, ans, Toast.LENGTH_SHORT).show();
        return false;
    }

    public void launchHome()
    {
        Context context = LoginActivity.this;
        SharedPreferences sharedPref = this.getSharedPreferences(
                getString(R.string.mil_navy_med_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(getString(R.string.mil_navy_med_login_key), true);
        editor.apply();

        Class destinationActivity = HomeActivity.class;
        Intent intent = new Intent(context, destinationActivity);
        startActivity(intent);
    }

    public class ActivitySpinner extends Activity implements AdapterView.OnItemSelectedListener
    {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            CMD = parent.getItemAtPosition(pos).toString();
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }
}
