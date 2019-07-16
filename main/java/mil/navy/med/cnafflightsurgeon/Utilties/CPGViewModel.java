package mil.navy.med.cnafflightsurgeon.Utilties;

import android.arch.lifecycle.ViewModel;

import java.util.ArrayList;

public class CPGViewModel extends ViewModel {

    private ArrayList<CPGBranch> branches;

    public ArrayList<CPGBranch> getBranches()
    {
        return branches;
    }

    public void setBranches(ArrayList<CPGBranch> list)
    {
        branches = list;
    }
}
