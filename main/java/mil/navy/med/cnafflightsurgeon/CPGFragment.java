package mil.navy.med.cnafflightsurgeon;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import mil.navy.med.cnafflightsurgeon.Utilties.CPGBranch;


public class CPGFragment extends Fragment implements View.OnClickListener{
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_CPG = "CPGData";
    private static final String ARG_BRANCH = "branch";
    private CPGBranch mCPGBranch;
    private String mBranch;

    View view;

    ImageView mIVTitleBar;
    TextView mTVTitle;
    TextView mTVBody;
    Button mBLeftButton;
    Button mBRightButton;
    TextView mTVBuffer;

    ButtonListener callback;

    public void setButtonImplementer(Activity activity)
    {
        callback = (ButtonListener) activity;
    }

    public interface ButtonListener
    {
        void takeAction(int key, String branch);
    }

    public CPGFragment() {
        // Required empty public constructor
    }

    public static CPGFragment newInstance(CPGBranch data, String branchNum) {
        CPGFragment fragment = new CPGFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_CPG, data);
        args.putString(ARG_BRANCH, branchNum);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCPGBranch = (CPGBranch) getArguments().getSerializable(ARG_CPG);
            mBranch = getArguments().getString(ARG_BRANCH);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_cpg, container, false);
        mIVTitleBar = (ImageView) view.findViewById(R.id.ImageViewTitleBar);
        mTVTitle = (TextView) view.findViewById(R.id.textViewTitle);
        mTVBody = (TextView) view.findViewById(R.id.textViewBody);
        mBLeftButton = (Button) view.findViewById(R.id.buttonLeft);
        mBRightButton = (Button) view.findViewById(R.id.buttonRight);
        mTVBuffer = (TextView) view.findViewById(R.id.textViewBuffer);
        mTVBody.setMovementMethod(LinkMovementMethod.getInstance());
        mBLeftButton.setOnClickListener(this);
        mBRightButton.setOnClickListener(this);
        if(mCPGBranch != null) populateData();
        return view;
    }

    private void populateData()
    {
        mTVTitle.setText(mCPGBranch.getBodyTitle());
        mTVBody.setText(mCPGBranch.getBody());
        mBLeftButton.setText(mCPGBranch.getLBT());
        mBLeftButton.setVisibility(View.VISIBLE);
        String rbt = mCPGBranch.getRBT();
        if(rbt != null) {
            mBRightButton.setText(rbt);
            mBRightButton.setVisibility(View.VISIBLE);

        }
    }

    @Override
    public void onClick(View v)
    {
        if(mCPGBranch == null) return;
        switch(v.getId())
        {
            case R.id.buttonRight:
                callback.takeAction(mCPGBranch.getRightID(), mBranch);
                break;
            case R.id.buttonLeft:
                callback.takeAction(mCPGBranch.getLeftID(), mBranch);
                break;
            default:
                break;
        }
    }

}
