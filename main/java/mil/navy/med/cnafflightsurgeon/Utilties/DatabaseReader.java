package mil.navy.med.cnafflightsurgeon.Utilties;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseReader {

    private SQLiteOpenHelper helper;
    private SQLiteDatabase database;
    private static DatabaseReader instance;

    private DatabaseReader(Context context)
    {
        this.helper = new MyDatabase(context);
    }

    public static DatabaseReader getInstance(Context context)
    {
        if(instance == null){
            instance = new DatabaseReader(context);
        }
        return instance;
    }

    public void open()
    {
        this.database = helper.getReadableDatabase();
    }

    public void close()
    {
        if(database != null) this.database.close();
    }

    public ArrayList<Chamber> getChambers(String area)
    {
        ArrayList<Chamber> chambers = new ArrayList<>();
        String query = "SELECT * from CHAMBERS where Region = " + "'" + area
                + "' ORDER BY State, Location";
        Cursor cursor = database.rawQuery(query, null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            Chamber thisOne = new Chamber();
            thisOne.region = cursor.getString(1);
            thisOne.state = cursor.getString(2);
            thisOne.priNum = cursor.getString(3);

            thisOne.secNum = cursor.getString(4);
            if(thisOne.secNum == null) {
                thisOne.secNum = "";
            }

            thisOne.name = cursor.getString(5);
            thisOne.owner = cursor.getString(6);
            thisOne.type = cursor.getInt(7);
            thisOne.depth = cursor.getString(8);
            thisOne.hours = cursor.getString(9);
            thisOne.location = cursor.getString(10);

            chambers.add(thisOne);
            cursor.moveToNext();
        }
        cursor.close();
        return chambers;
    }

    public ArrayList<CPGBranch> getBranches()
    {
        ArrayList<CPGBranch> branches = new ArrayList<>();
        String query = "SELECT * from BRANCHES ORDER BY IDNum";
        Cursor cursor = database.rawQuery(query, null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast())
        {
            String sub2 = "\u2082";
            String bodyTitle = cursor.getString(0);
            bodyTitle = bodyTitle.replace("u2082", sub2);
            String body = cursor.getString(1);
            body = body.replace("u2082", sub2);
            String leftButtonText = cursor.getString(2);
            String rightButtonText = cursor.getString(3);
            int idNum = cursor.getInt(4);
            int lid = cursor.getInt(5);
            int rid = cursor.getInt(6);

            CPGBranch thisOne = new CPGBranch(bodyTitle, body, leftButtonText, rightButtonText,
                    idNum, lid, rid);

            branches.add(thisOne);
            cursor.moveToNext();
        }
        cursor.close();
        return branches;
    }
}
