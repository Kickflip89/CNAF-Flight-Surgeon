package mil.navy.med.cnafflightsurgeon.Utilties;

import java.io.Serializable;

public class CPGBranch implements Serializable {

    private String bodyTitle;
    private String body;
    private String leftButtonText;
    private String rightButtonText;
    private int idNum;
    private int leftID;
    private int rightID;

    public CPGBranch(String bt, String by, String lbt, String rbt, int id, int li, int ri)
    {
        bodyTitle = bt;
        body = by;
        leftButtonText = lbt;
        rightButtonText = rbt;
        idNum = id;
        leftID = li;
        rightID = ri;
    }

    public String getBodyTitle()
    {
        return bodyTitle;
    }

    public String getBody()
    {
        return body;
    }

    public String getLBT()
    {
        return leftButtonText;
    }

    public String getRBT()
    {
        return rightButtonText;
    }

    public int getIDNum()
    {
        return idNum;
    }

    public int getLeftID()
    {
        return leftID;
    }

    public int getRightID()
    {
        return rightID;
    }
}
