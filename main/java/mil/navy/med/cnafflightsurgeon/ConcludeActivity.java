package mil.navy.med.cnafflightsurgeon;

import android.content.Intent;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ConcludeActivity extends AppCompatActivity {

    TouchImageView img;
    ImageView mIVFwd;
    ImageView mIVBck;
    TextView mTVTitle;

    private static final String CPG_KEY = "cpg";
    boolean show_cpg;

    public static int page;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conclude);

        Intent intentThatStarted = getIntent();
        if(intentThatStarted.hasExtra(CPG_KEY)){
            show_cpg = intentThatStarted.getBooleanExtra(CPG_KEY, false);
        }
        if(savedInstanceState != null){
            show_cpg = savedInstanceState.getBoolean(CPG_KEY);
        }

        img = (TouchImageView) findViewById(R.id.TIVImage);
        mIVFwd = (ImageView) findViewById(R.id.imageViewFwd);
        mIVBck = (ImageView) findViewById(R.id.imageViewBck);
        mTVTitle = (TextView) findViewById(R.id.textViewTouchTitle);
        img.setImageResource(R.drawable.neuro_pg1);
        mTVTitle.setText(R.string.neuroDescrip);

        page = 0;

        mIVFwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int curr = ConcludeActivity.page;
                if(curr < 1){
                    img.setImageResource(R.drawable.neuro_pg2);
                    ConcludeActivity.page++;
                }
                else {
                    Toast.makeText(ConcludeActivity.this, "Final Page", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mIVBck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int curr = ConcludeActivity.page;
                if(page > 0){
                    img.setImageResource(R.drawable.neuro_pg1);
                    ConcludeActivity.page--;
                }
                else {
                    Toast.makeText(ConcludeActivity.this, "First Page", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if(show_cpg) showCPG();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(CPG_KEY, show_cpg);
    }

    protected void showCPG()
    {
        img.setImageResource(R.drawable.cpg);
        mIVBck.setVisibility(View.GONE);
        mIVFwd.setVisibility(View.GONE);
        mTVTitle.setText(R.string.CPGDescrip);
    }
}
