package mil.navy.med.cnafflightsurgeon;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class FeedbackActivity extends AppCompatActivity {

    TextView mTVFeedbackBody;
    TextView mTVFeedbackEmail;
    Button mBFeedback;

    static final String ISCONCLUDE = "conclude";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        mTVFeedbackBody = (TextView) findViewById(R.id.textViewFeedbackBody);
        mTVFeedbackEmail = (TextView) findViewById(R.id.textViewAddress);
        mBFeedback = (Button) findViewById(R.id.button);

        Intent intentThatLaunched = getIntent();
        if(intentThatLaunched.hasExtra(ISCONCLUDE)){
            if(intentThatLaunched.getBooleanExtra(ISCONCLUDE, false)){
                showConclusion();
            }
        }
    }

    public void showConclusion()
    {
        mTVFeedbackBody.setText(R.string.ConclusionBody);
        mTVFeedbackEmail.setVisibility(View.GONE);
        mBFeedback.setVisibility(View.GONE);
    }

    public void onClickEmail(View v)
    {
        String address = getResources().getString(R.string.emailaddy);
        String[] addresses = {address};
        String subj = getResources().getString(R.string.emailsubj);
        String body = getResources().getString(R.string.emailbody);
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        intent.putExtra(Intent.EXTRA_SUBJECT, subj);
        intent.putExtra(Intent.EXTRA_TEXT, body);
        if(intent.resolveActivity(getPackageManager()) != null){
            startActivity(intent);
        }
    }
}
