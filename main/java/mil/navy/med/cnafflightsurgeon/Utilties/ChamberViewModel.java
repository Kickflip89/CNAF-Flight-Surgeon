package mil.navy.med.cnafflightsurgeon.Utilties;

import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.os.AsyncTask;

import java.util.ArrayList;

import mil.navy.med.cnafflightsurgeon.TreeActivity;

public class ChamberViewModel extends ViewModel
{
    private ArrayList<Chamber> chambers;

    public ArrayList<Chamber> getChambers()
    {
        return chambers;
    }

    public void setChambers(ArrayList<Chamber> list)
    {
        chambers = list;
    }

}
