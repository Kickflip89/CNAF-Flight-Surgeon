package mil.navy.med.cnafflightsurgeon;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

import mil.navy.med.cnafflightsurgeon.Utilties.Chamber;
import mil.navy.med.cnafflightsurgeon.Utilties.ChamberAdapter;
import mil.navy.med.cnafflightsurgeon.Utilties.ChamberViewModel;
import mil.navy.med.cnafflightsurgeon.Utilties.DatabaseReader;

public class TreeActivity extends AppCompatActivity {

    ChamberViewModel mViewModel;
    RecyclerView mRecyclerView;
    ChamberAdapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tree);

        final Context context = this;

        String region;
        Intent intentThatStarted = getIntent();
        if (intentThatStarted.hasExtra(Intent.EXTRA_TEXT)) {
            region = intentThatStarted.getStringExtra(Intent.EXTRA_TEXT);
        } else region = "Northeast";

        // Initializing objects
        mViewModel = ViewModelProviders.of(this).get(ChamberViewModel.class);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // get data from ViewModel if available
        ArrayList<Chamber> chambers = mViewModel.getChambers();


        class ChamberLoader extends AsyncTask<String, Void, ArrayList<Chamber>> {
            @Override
            protected ArrayList<Chamber> doInBackground(String... argv) {
                ArrayList<Chamber> ans;
                String area = argv[0];
                DatabaseReader reader = DatabaseReader.getInstance(context);
                reader.open();
                ans = reader.getChambers(area);
                reader.close();
                return ans;
            }

            @Override
            protected void onPostExecute(ArrayList<Chamber> chamber_list) {
                super.onPostExecute(chamber_list);
                mViewModel.setChambers(chamber_list);
                mAdapter = new ChamberAdapter(chamber_list);
                mRecyclerView.setAdapter(mAdapter);
            }
        }


        if(chambers == null) {
            ChamberLoader loader = new ChamberLoader();
            loader.execute(region);
        }
        else {
            mAdapter = new ChamberAdapter(chambers);
            mRecyclerView.setAdapter(mAdapter);
        }
    }

}
