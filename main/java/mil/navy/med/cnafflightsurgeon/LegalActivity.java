package mil.navy.med.cnafflightsurgeon;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

public class LegalActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_legal);

        TextView mTVLegal = (TextView) findViewById(R.id.textViewPrivacy);
        mTVLegal.setMovementMethod(LinkMovementMethod.getInstance());

    }
}
