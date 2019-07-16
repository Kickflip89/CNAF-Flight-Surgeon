package mil.navy.med.cnafflightsurgeon.Utilties;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import mil.navy.med.cnafflightsurgeon.R;


public class ChamberAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{

    private static final int FOOTER_VIEW = 1;
    private static final int MONOPLACE = 0;
    private static final int MULTIPLACE = 1;
    private ArrayList<Chamber> data;

    public ChamberAdapter(ArrayList<Chamber> chambers)
    {
        data = chambers;
    }

    public class NormalViewHolder extends ViewHolder
    {
        public NormalViewHolder(View itemView)
        {
            super(itemView);

            //This isn't implemented but is here for growth
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }

    }

    public class FooterViewHolder extends ViewHolder
    {
        //define views
        TextView mTVFooter;

        public FooterViewHolder(View itemView)
        {
            super(itemView);
            //find Views
            mTVFooter = (TextView) itemView.findViewById(R.id.textViewFooter);

            //this isn't implemented but for future growth could be
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View v;
        if(viewType == FOOTER_VIEW){
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_footer, parent, false);
            FooterViewHolder vh = new FooterViewHolder(v);
            return vh;
        }

        v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_chamber, parent, false);
        NormalViewHolder vh = new NormalViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position)
    {
        try {
            if(holder instanceof NormalViewHolder) {
                NormalViewHolder vh = (NormalViewHolder) holder;
                vh.bindView(position);
            }
            else if(holder instanceof FooterViewHolder){
                FooterViewHolder vh = (FooterViewHolder) holder;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        if(data == null) return 0;
        if(data.size() == 0) return 1;

        return data.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if(position == data.size()) return FOOTER_VIEW;

        return super.getItemViewType(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        // define views
        TextView mTVState;
        TextView mTVLoc;
        TextView mTVPriNum;
        TextView mTVSecNum;
        TextView mTVName;
        TextView mTVOwner;
        TextView mTVDepth;
        TextView mTVHours;

        public ViewHolder(View itemView)
        {
            super(itemView);
            // findby IDs here
            mTVState = (TextView) itemView.findViewById(R.id.textViewState);
            mTVLoc = (TextView) itemView.findViewById(R.id.textViewLocation);
            mTVPriNum = (TextView) itemView.findViewById(R.id.textViewPriNum);
            mTVSecNum = (TextView) itemView.findViewById(R.id.textViewSecNum);
            mTVName = (TextView) itemView.findViewById(R.id.textViewName);
            mTVOwner = (TextView) itemView.findViewById(R.id.textViewOwner);
            mTVDepth = (TextView) itemView.findViewById(R.id.textViewDepth);
            mTVHours = (TextView) itemView.findViewById(R.id.textViewHours);
        }

        public void bindView(int position)
        {
            // implement from data ArrayList
            Chamber thisOne = data.get(position);
            String type = "";
            switch (thisOne.type){
                case MONOPLACE :
                    type = "Monoplace";
                    break;
                case MULTIPLACE :
                    type = "Multiplace";
                    break;
                default :
                    break;
            }

            mTVState.setText(thisOne.state);
            mTVLoc.setText(thisOne.location);
            mTVPriNum.setText(thisOne.priNum);
            if(thisOne.secNum.equals("")){
                mTVSecNum.setVisibility(View.GONE);
            }
            else mTVSecNum.setText(thisOne.secNum);
            mTVName.setText(thisOne.name);
            String t1 = thisOne.owner + " / " + type;
            mTVOwner.setText(t1);
            String t2 = "Depth: " + thisOne.depth;
            mTVDepth.setText(t2);
            String t3 = "Hours: " + thisOne.hours;
            mTVHours.setText(t3);
        }
    }
}
