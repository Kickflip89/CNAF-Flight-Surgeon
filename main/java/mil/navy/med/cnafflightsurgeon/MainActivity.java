package mil.navy.med.cnafflightsurgeon;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    TextView mTVDisclaimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTVDisclaimer = (TextView) findViewById(R.id.textViewDisclaimer);

        mTVDisclaimer.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public void onClickStartButton(View v)
    {
        //Toast.makeText(this, "Clicked on Button", Toast.LENGTH_LONG).show();
        Context context = MainActivity.this;

        SharedPreferences prefs = getSharedPreferences(getString(R.string.mil_navy_med_file_key),
                Context.MODE_PRIVATE);
        Class destinationActivity;
        if(prefs.getBoolean(getString(R.string.mil_navy_med_login_key), false)) {
            destinationActivity = HomeActivity.class;
        }
        else destinationActivity = LoginActivity.class;

        Intent startNexttIntent = new Intent(context, destinationActivity);

        startActivity(startNexttIntent);
    }

}
