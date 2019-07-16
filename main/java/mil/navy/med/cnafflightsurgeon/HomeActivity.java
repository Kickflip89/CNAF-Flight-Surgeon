package mil.navy.med.cnafflightsurgeon;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import mil.navy.med.cnafflightsurgeon.Utilties.CPGBranch;
import mil.navy.med.cnafflightsurgeon.Utilties.CPGViewModel;
import mil.navy.med.cnafflightsurgeon.Utilties.DatabaseReader;


/*
The naming convention of the activates is a little weird since I went through some iterative
processes to refactor without renaming the activities.  Here is a list of the functionality
Main Activity - Main screen with start button
Home Activity - This contains the CPG Logic and the options menu
Conclude Activity - This shows either the CPG or the Neuro Exam
Feedback Activity - This either shows the conclusion text or the feedback text
Legal Activity - This shows the legal disclaimer.
 */


public class HomeActivity extends AppCompatActivity implements CPGFragment.ButtonListener {
    ScrollView mScreen;
    CPGViewModel mViewModel;

    // This is used to pass the region for the SQL query if the tree_activity is launched
    String SELECTED_CHAMBER;

    // handles launching the conclusion or the feedback in FeedbackActivity
    static final String ISCONCLUDE = "conclude";
    // handles launching ConcludeActivity to either show the CPG or the Neuro exam
    private static final String CPG_KEY = "cpg";

    //preamble views
    ImageView mIVTier2TitleBar;
    TextView mTVTier2Title;
    TextView mTVInitial;
    ImageView mIVTier3TitleBar;
    TextView mTVTests;
    Button mBRadGood;
    Button mBRadBad;
    TextView mTVRadDecision;

    //Chamber views
    ImageView mChamberBar;
    TextView mTVChamberTitle;
    TextView mTVChamberBody;
    ImageView mMap;
    Button mBCallNAMI;
    TextView mTVPrompt;
    Spinner mSpinnerRegions;
    TextView mTVResults;
    Button mBLaunchChamber;
    TextView mTVChamberBuffer;

    /*This initializes the constants and ArrayList to handle the CPG Branch logic
    the title corresponds with the order they are added to the branches ArrayList
    so get(RAD_NEGATIVE) for instance will return the CPGNode for RAD_NEGATIVE.

    THESE NEED TO MATCH THE IDNUMS IN CHAMBERS.DB IN ASSETS */
    ArrayList<CPGBranch> branches;
    private static final int RAD_NEGATIVE = 0;
    private static final int RAD_POS = 1;
    private static final int RESOLVED = 2;
    private static final int CHAMBER = 3;
    private static final int NO_CHAMBER = 4;
    private static final int LAUNCH_CHAMBER = 5;
    private static final int LAUNCH_CONCLUSION = 6;
    private static final int NOT_VIS = 99;

    //This is for the savedInstanceState to start the CPG Protocol
    private static final String BEGUN_KEY = "Begun";
    boolean begun = false;
    private static final String SYMPTOMS_KEY = "Symptoms";
    boolean symptoms = false;

    //These are for the savedInstanceStates to populate each branch with data
    private static final String BRANCH1_KEY = "Branch1";
    int branch1 = NOT_VIS;
    private static final String BRANCH2_KEY = "Branch2";
    int branch2 = NOT_VIS;
    private static final String BRANCH3_KEY = "Branch3";
    int branch3 = NOT_VIS;
    private static final String BRANCH4_KEY = "Branch4";
    int branch4 = NOT_VIS;
    private static final String CHAMBER_KEY = "Chamber";
    boolean chamber = false;

    //For chamber info views.  These need to be the same as the "Region" field in the
    //Database in order for the SQL Query to retrieve the chambers from a given region
    private static final String NW = "Northwest";
    private static final String ML = "Mid Atlantic";
    private static final String MW = "Midwest";
    private static final String SE = "Southeast";
    private static final String SW = "Southwest";
    private static final String NE = "Northeast";
    private static final String PA = "Pacific";

    @Override
    public void onAttachFragment(Fragment fragment){
        if(fragment instanceof CPGFragment)
        {
            CPGFragment cpgfragment = (CPGFragment) fragment;
            cpgfragment.setButtonImplementer(this);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mScreen = (ScrollView) findViewById(R.id.scroll);

        final Context context = this;

        //Populates the initial text
        mTVInitial = (TextView) findViewById(R.id.textViewInitial);
        mTVInitial.setMovementMethod(LinkMovementMethod.getInstance());
        mTVTests = findViewById(R.id.textViewTests);
        mTVTests.setText(getString(R.string.RadResults));

        //preamble views
        mIVTier2TitleBar = (ImageView) findViewById(R.id.Tier2TitleBar);
        mTVTier2Title = (TextView) findViewById(R.id.textViewTier2Title);
        mIVTier3TitleBar = (ImageView) findViewById(R.id.Tier3TitleBar);
        mTVTests = (TextView) findViewById(R.id.textViewTests);
        mBRadGood = (Button) findViewById(R.id.buttonRadGood);
        mBRadBad = (Button) findViewById(R.id.buttonRadBad);
        mTVRadDecision = (TextView) findViewById(R.id.textViewRadDecision);


        //Chamber views
        mChamberBar = (ImageView) findViewById(R.id.ChamberTitleBar);
        mTVChamberTitle = (TextView) findViewById(R.id.textViewChamberTitle);
        mTVChamberBody = (TextView) findViewById(R.id.textViewChamberBody);
        mMap = (ImageView) findViewById(R.id.map);
        mBCallNAMI = (Button) findViewById(R.id.buttonCallNAMI);
        mTVPrompt = (TextView) findViewById(R.id.textViewPrompt);
        mSpinnerRegions = (Spinner) findViewById(R.id.regions);
        mTVResults = (TextView) findViewById(R.id.textViewRegionSelected);
        mBLaunchChamber = (Button) findViewById(R.id.buttonLaunchChamber);
        mTVChamberBuffer = (TextView) findViewById(R.id.textViewChamberBuffer);

        //Handles the logic for populating the ViewModel
        mViewModel = ViewModelProviders.of(this).get(CPGViewModel.class);
        branches = mViewModel.getBranches();

        //SQL Queries off of the main thread
        class BranchLoader extends AsyncTask<String, Void, ArrayList<CPGBranch>>
        {
            @Override
            protected ArrayList<CPGBranch> doInBackground(String... argv)
            {
                ArrayList<CPGBranch> ans;
                DatabaseReader reader = DatabaseReader.getInstance(context);
                reader.open();
                ans = reader.getBranches();
                reader.close();
                return ans;
            }

            @Override
            protected void onPostExecute(ArrayList<CPGBranch> branchList)
            {
                super.onPostExecute(branchList);
                mViewModel.setBranches(branchList);
                branches = branchList;
            }
        }

        if(branches == null)
        {
            BranchLoader loader = new BranchLoader();
            loader.execute("");
        }

        //Populates the spinner for the chamber views
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.regions, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.
                simple_spinner_dropdown_item);
        mSpinnerRegions.setAdapter(adapter);
        mSpinnerRegions.setOnItemSelectedListener(new SpinnerActivity());

        //Handles populating the correct views for state changes
        if(savedInstanceState != null){
            if(savedInstanceState.containsKey(BEGUN_KEY)){
                if(savedInstanceState.getBoolean(BEGUN_KEY)) makeInfoVisible();
                if(savedInstanceState.getBoolean(SYMPTOMS_KEY)) showSymptoms();
            }
            branch1 = savedInstanceState.getInt(BRANCH1_KEY);
            branch2 = savedInstanceState.getInt(BRANCH2_KEY);
            branch3 = savedInstanceState.getInt(BRANCH3_KEY);
            branch4 = savedInstanceState.getInt(BRANCH4_KEY);
            chamber = savedInstanceState.getBoolean(CHAMBER_KEY);
            if (branch1 != NOT_VIS) populate(branch1, BRANCH1_KEY);
            if (branch2 != NOT_VIS) populate(branch2, BRANCH2_KEY);
            if (branch3 != NOT_VIS) populate(branch3, BRANCH3_KEY);
            if (branch4 != NOT_VIS) populate(branch4, BRANCH4_KEY);
            if (chamber) showChamber();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SYMPTOMS_KEY, symptoms);
        outState.putBoolean(BEGUN_KEY, begun);
        outState.putInt(BRANCH1_KEY, branch1);
        outState.putInt(BRANCH2_KEY, branch2);
        outState.putInt(BRANCH3_KEY, branch3);
        outState.putInt(BRANCH4_KEY, branch4);
        outState.putBoolean(CHAMBER_KEY, chamber);
    }

    // This class handles populating the spinner for the chamber menu
    public class SpinnerActivity extends Activity implements AdapterView.OnItemSelectedListener
    {
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
        {
            SELECTED_CHAMBER = parent.getItemAtPosition(pos).toString();
            String text = "";
            Drawable image = HomeActivity.this.getDrawable(R.drawable.em_small);
            switch(SELECTED_CHAMBER)
            {
                case NW :
                    text = HomeActivity.this.getString(R.string.Northwest);
                    image = HomeActivity.this.getDrawable(R.drawable.nw_small);
                    break;
                case ML :
                    text = HomeActivity.this.getString(R.string.lant);
                    image = HomeActivity.this.getDrawable(R.drawable.ma_small);
                    break;
                case SE :
                    text = HomeActivity.this.getString(R.string.Southeast);
                    image = HomeActivity.this.getDrawable(R.drawable.se_small);
                    break;
                case MW :
                    text = HomeActivity.this.getString(R.string.Midwest);
                    image = HomeActivity.this.getDrawable(R.drawable.mw_small);
                    break;
                case SW :
                    text = HomeActivity.this.getString(R.string.Southwest);
                    image = HomeActivity.this.getDrawable(R.drawable.sw_small);
                    break;
                case NE :
                    text = HomeActivity.this.getString(R.string.Northeast);
                    image = HomeActivity.this.getDrawable(R.drawable.ne_small);
                    break;
                case PA :
                    text = HomeActivity.this.getString(R.string.Pacific);
                    break;
                default :
                    break;
            }
            mTVResults.setText(text);
            mMap.setBackground(image);
            mTVResults.setVisibility(View.VISIBLE);
        }

        public void onNothingSelected(AdapterView<?> parent)
        {
            mTVResults.setVisibility(View.INVISIBLE);
        }
    }


    public void onClickSymptomsButton(View v)
    {
        showSymptoms();
    }

    public void onClickBeginButton(View v)
    {
        makeInfoVisible();
    }

    public void onClickRadBadButton(View v)
    {
        eraseBranches(BRANCH2_KEY);
        eraseChamber();
        populate(RAD_NEGATIVE, BRANCH1_KEY);
    }

    public void onClickRadGoodButton(View v)
    {
        eraseBranches(BRANCH2_KEY);
        eraseChamber();
        populate(RAD_POS, BRANCH1_KEY);
    }

    public void onClickCallNAMI(View v)
    {
        String number = "8504494629";
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + number));
        startActivity(intent);
    }

    public void onClickLaunchChamber(View v)
    {
        Context context = HomeActivity.this;
        Class destinationActivity = TreeActivity.class;
        Intent startTreeActivityIntent = new Intent(context, destinationActivity);
        startTreeActivityIntent.putExtra(Intent.EXTRA_TEXT, SELECTED_CHAMBER);
        startActivity(startTreeActivityIntent);

    }

    //This handles the visibility and content of the first batch of displays
    public void showSymptoms()
    {
        mTVTier2Title.setText(getString(R.string.symptomsTitle));
        mTVInitial.setText(getString(R.string.symptomsBody));
        mIVTier2TitleBar.setVisibility(View.VISIBLE);
        mTVTier2Title.setVisibility(View.VISIBLE);
        mTVInitial.setVisibility(View.VISIBLE);
        mTVTests.setVisibility(View.GONE);
        mTVRadDecision.setVisibility(View.GONE);
        mBRadBad.setVisibility(View.GONE);
        mBRadGood.setVisibility(View.GONE);

        if(begun){
            mIVTier3TitleBar.setVisibility(View.INVISIBLE);
            eraseBranches(BRANCH1_KEY);  //erase branches is defined below
            eraseChamber();
        }
        symptoms = true;
        begun = false;
        scrollDown();
    }

    //This does the same for beginning the CPG vice just the symptoms
    public void makeInfoVisible()
    {
        if(symptoms){
            mTVTier2Title.setText(getString(R.string.Initial));
            mTVInitial.setText(getString(R.string.InitialAction));
        }
        mIVTier2TitleBar.setVisibility(View.VISIBLE);
        mTVTier2Title.setVisibility(View.VISIBLE);
        mTVInitial.setVisibility(View.VISIBLE);
        mIVTier3TitleBar.setVisibility(View.VISIBLE);
        mTVTests.setVisibility(View.VISIBLE);
        mTVRadDecision.setVisibility(View.VISIBLE);
        mBRadBad.setVisibility(View.VISIBLE);
        mBRadGood.setVisibility(View.VISIBLE);
        if(begun) {
            eraseBranches(BRANCH1_KEY);
            eraseChamber();
        }

        begun = true;
        symptoms = false;
        scrollDown();

    }


    //This function does the right action based on which button was clicked
    // the key corresponds to the key of the title of the CPGNode
    // branch is the branch that called takeAction.
    public void takeAction(int key, String branch)
    {
        if(key == LAUNCH_CHAMBER){
            showChamber();
        }
        else if(key == LAUNCH_CONCLUSION){
            Context context = HomeActivity.this;
            Class dest = FeedbackActivity.class;
            Intent intent = new Intent(context, dest);
            intent.putExtra(ISCONCLUDE, true);
            startActivity(intent);
        }
        else {
            populate(key, nextBranch(branch));
            if(chamber) eraseChamber();
        }
    }

    public String nextBranch(String branch)
    {
        String ans = "";
        switch(branch)
        {
            case BRANCH1_KEY:
                ans = BRANCH2_KEY;
                break;
            case BRANCH2_KEY:
                ans = BRANCH3_KEY;
                break;
            case BRANCH3_KEY:
                ans = BRANCH4_KEY;
                break;
            default:
                ans = "ERROR";
                break;
        }
        return ans;
    }

    public void populate(int key_val, String branchKey) {
        if (key_val == NOT_VIS) return;
        eraseBranches(nextBranch(branchKey));

        int fragID = 0;

        switch (branchKey) {
            case BRANCH1_KEY:
                fragID = R.id.fragmentBranch1;
                branch1 = key_val;
                break;
            case BRANCH2_KEY:
                fragID = R.id.fragmentBranch2;
                branch2 = key_val;
                break;
            case BRANCH3_KEY:
                fragID = R.id.fragmentBranch3;
                branch3 = key_val;
                break;
            case BRANCH4_KEY:
                fragID = R.id.fragmentBranch4;
                branch4 = key_val;
                break;
            default:
                return;
        }

        CPGBranch branch = branches.get(key_val);
        CPGFragment newFrag = CPGFragment.newInstance(branch, branchKey);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(fragID, newFrag);
        ft.show(newFrag);
        ft.commitNowAllowingStateLoss();
        scrollDown();
    }

    public void showChamber()
    {
        mChamberBar.setVisibility(View.VISIBLE);
        mTVChamberTitle.setVisibility(View.VISIBLE);
        mTVChamberBody.setVisibility(View.VISIBLE);
        mBCallNAMI.setVisibility(View.VISIBLE);
        mMap.setVisibility(View.VISIBLE);
        mTVPrompt.setVisibility(View.VISIBLE);
        mSpinnerRegions.setVisibility(View.VISIBLE);
        mTVResults.setVisibility(View.VISIBLE);
        mBLaunchChamber.setVisibility(View.VISIBLE);
        mTVChamberBuffer.setVisibility(View.VISIBLE);
        chamber = true;

        scrollDown();
    }

    // The following functions set the visibility of various branches to GONE
    // This is only called by Tier 2 and above.
    public void eraseBranches(String key)
    {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        switch(key){
            case BRANCH1_KEY :
                if(branch1 != NOT_VIS) {
                    CPGFragment blank = new CPGFragment();
                    ft.replace(R.id.fragmentBranch1, blank);
                    ft.hide(blank);
                    branch1 = NOT_VIS;
                }
            case BRANCH2_KEY :
                if(branch2 != NOT_VIS) {
                    CPGFragment blank = new CPGFragment();
                    ft.replace(R.id.fragmentBranch2, blank);
                    ft.hide(blank);
                    branch2 = NOT_VIS;
                }
            case BRANCH3_KEY :
                if(branch3 != NOT_VIS) {
                    CPGFragment blank = new CPGFragment();
                    ft.replace(R.id.fragmentBranch3, blank);
                    ft.hide(blank);
                    branch3 = NOT_VIS;
                }
            case BRANCH4_KEY :
                if(branch4 != NOT_VIS) {
                    CPGFragment blank = new CPGFragment();
                    ft.replace(R.id.fragmentBranch4, blank);
                    ft.hide(blank);
                    branch4 = NOT_VIS;
                }
            default :
                break;
        }
        ft.commit();
    }


    public void eraseChamber()
    {
        mChamberBar.setVisibility(View.GONE);
        mTVChamberTitle.setVisibility(View.GONE);
        mTVChamberBody.setVisibility(View.GONE);
        mBCallNAMI.setVisibility(View.GONE);
        mMap.setVisibility(View.GONE);
        mTVPrompt.setVisibility(View.GONE);
        mSpinnerRegions.setVisibility(View.GONE);
        mTVResults.setVisibility(View.GONE);
        mTVChamberBuffer.setVisibility(View.GONE);
        mBLaunchChamber.setVisibility(View.GONE);
        chamber = false;

    }

    public void scrollDown()
    {
        mScreen.post(new Runnable() {
            @Override public void run(){
                mScreen.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }
    //Options Menu info

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Context context = HomeActivity.this;
        Class destinationActivity;
        Intent intent;

        switch(item.getItemId()){
            case R.id.menuCPG :
                destinationActivity = ConcludeActivity.class;
                intent = new Intent(context, destinationActivity);
                intent.putExtra(CPG_KEY, true);
                startActivity(intent);
                return true;
            case R.id.menuLegal :
                destinationActivity = LegalActivity.class;
                intent = new Intent(context, destinationActivity);
                startActivity(intent);
                return true;
            case R.id.menuFeedback :
                destinationActivity = FeedbackActivity.class;
                intent = new Intent(context, destinationActivity);
                intent.putExtra(ISCONCLUDE, false);
                startActivity(intent);
                return true;
            case R.id.menuNeuro :
                destinationActivity = ConcludeActivity.class;
                intent = new Intent(context, destinationActivity);
                intent.putExtra(CPG_KEY, false);
                startActivity(intent);
                return true;
        }
        return false;
    }
}
