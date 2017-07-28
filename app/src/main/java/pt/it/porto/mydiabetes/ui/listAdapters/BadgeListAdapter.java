package pt.it.porto.mydiabetes.ui.listAdapters;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.data.BadgeBoard;
import pt.it.porto.mydiabetes.data.BadgeRec;
import pt.it.porto.mydiabetes.utils.LevelsPointsUtils;


public class BadgeListAdapter extends BaseExpandableListAdapter {

    private List<String> expandableListTitle;
    private HashMap<String, HashMap<String,HashMap<String,BadgeBoard.BadgeGlobalObjective.BadgeSingleObjective>>> allMedals;
    private Context con;
    private int playerLvl;
    String TAG = "cenas";


    public BadgeListAdapter(Context con, ArrayList<String> diff, HashMap<String, HashMap<String,HashMap<String,BadgeBoard.BadgeGlobalObjective.BadgeSingleObjective>>> allMedals,int lvl){
        this.allMedals = allMedals;
        this.expandableListTitle = diff;
        this.con = con;
        this.playerLvl = lvl;
    }


    @Override
    public Object getChild(int listPosition, int expandedListPosition) { //returns hashmap (Bronze/silver/gold - conditions)

        //Log.i(TAG, "getChild: TESTING:");
        //Log.i(TAG, "getChild: expandableList: POS: "+listPosition+" = "+this.expandableListTitle.get(listPosition));

        HashMap<String, HashMap<String, BadgeBoard.BadgeGlobalObjective.BadgeSingleObjective>> tempResult = this.allMedals.get(this.expandableListTitle.get(listPosition));
        List<String> s = new ArrayList<>(tempResult.keySet());


        //Log.i(TAG, "getChild: result_temp: "+this.allMedals.get(this.expandableListTitle.get(listPosition)).get(s.get(expandedListPosition)).keySet().toString());//.get(expandedListPosition));
        return this.allMedals.get(this.expandableListTitle.get(listPosition)).get(s.get(expandedListPosition)).keySet().toString();
    }

    @Override
    public long getChildId(int listPosition, int expandedListPosition) {
        Log.i(TAG, "getChildID: result: "+expandedListPosition);
        return expandedListPosition;
    }

    @Override
    public View getChildView(int listPosition, final int expandedListPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        Log.i(TAG, "getChildView:");
        //return Linear Layout with 3 medals
        final String expandedListText = (String) getChild(listPosition, expandedListPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.badge_display_row, null);
        }
        TextView expandedListTextView = (TextView) convertView.findViewById(R.id.expandedListItem);
        expandedListTextView.setText(expandedListText);
        return convertView;
    }

    @Override
    public int getChildrenCount(int listPosition) {

        Log.i(TAG, "getChildrenCount: Result: "+this.allMedals.get(this.expandableListTitle.get(listPosition)).size());
        return this.allMedals.get(this.expandableListTitle.get(listPosition)).size();
    }

    @Override
    public Object getGroup(int listPosition) {
        Log.i(TAG, "getGroup: result: "+this.expandableListTitle.get(listPosition));
        return this.expandableListTitle.get(listPosition);
    }

    @Override
    public int getGroupCount() {
        Log.i(TAG, "getGroupCount: result: "+this.expandableListTitle.size());
        return this.expandableListTitle.size();
    }

    @Override
    public long getGroupId(int listPosition) {

        Log.i(TAG, "getGroup: result: "+listPosition);
        return listPosition;
    }

    @Override
    public View getGroupView(int listPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        //titulos

        Log.i(TAG, "getGroupView: ListPosition: "+listPosition);

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.badge_display_row_title, null);
        }
        TextView listTitleTextView;
        listTitleTextView = (TextView) convertView.findViewById(R.id.listTitle);
        String text;
        if(isUnlocked(playerLvl, expandableListTitle.get(listPosition))){
            Log.i(TAG, "getGroupView: -> STRING1: "+expandableListTitle.get(listPosition));
            text = con.getString(con.getResources().getIdentifier(expandableListTitle.get(listPosition),"string", con.getPackageName()));
            listTitleTextView.setTypeface(null, Typeface.BOLD);
            listTitleTextView.setText(text);
        }else{
            Log.i(TAG, "getGroupView: -> STRING2: "+expandableListTitle.get(listPosition)+"_locked");
            text = con.getString(con.getResources().getIdentifier(expandableListTitle.get(listPosition)+"_locked","string", con.getPackageName()));
            listTitleTextView.setTypeface(null, Typeface.BOLD);
            listTitleTextView.setText(text);
        }
        return convertView;
    }

    private boolean isUnlocked(int playerLvl, String diff) {
        switch (diff){
            case "medium":
                return playerLvl > LevelsPointsUtils.BADGES_MEDIUM_UNLOCK_LEVEL;
            case "advanced":
                return playerLvl > LevelsPointsUtils.BADGES_ADVANCED_UNLOCK_LEVEL;
        }
        return true;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int listPosition, int expandedListPosition) {
        return false;
    }
}
