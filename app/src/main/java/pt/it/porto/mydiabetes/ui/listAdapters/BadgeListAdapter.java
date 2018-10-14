package pt.it.porto.mydiabetes.ui.listAdapters;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.ui.fragments.badges.BadgeBoard;
import pt.it.porto.mydiabetes.utils.LevelsPointsUtils;


public class BadgeListAdapter extends BaseExpandableListAdapter {

    private List<String> expandableListTitle;
    private List<String> expandableListBadgeNames;
    private HashMap<String, HashMap<String,HashMap<String,BadgeBoard.BadgeGlobalObjective.BadgeSingleObjective>>> allMedals;
    private Context con;
    private int playerLvl;
    String TAG = "cenas";


    public BadgeListAdapter(Context con, ArrayList<String> diff,ArrayList<String> names, HashMap<String, HashMap<String,HashMap<String,BadgeBoard.BadgeGlobalObjective.BadgeSingleObjective>>> allMedals,int lvl){
        this.allMedals = allMedals;
        this.expandableListTitle = diff;
        this.con = con;
        this.playerLvl = lvl;
        this.expandableListBadgeNames = names;
    }


    @Override
    public Object getChild(int listPosition, int expandedListPosition) { //returns hashmap (Bronze/silver/gold - conditions)

        if(this.expandableListTitle.get(listPosition).equals("daily")){
            HashMap<String, HashMap<String, BadgeBoard.BadgeGlobalObjective.BadgeSingleObjective>> tempResult = this.allMedals.get(this.expandableListTitle.get(listPosition));
            List<String> s = new ArrayList<>(tempResult.keySet());
            //Log.i(TAG, "getChild: "+this.allMedals.get(this.expandableListTitle.get(listPosition)).get(s.get(expandedListPosition)));
            return this.allMedals.get(this.expandableListTitle.get(listPosition)).get(s.get(expandedListPosition));
        }else{
            if(this.expandableListTitle.get(listPosition).equals("beginner")){
                //HashMap<String, HashMap<String, BadgeBoard.BadgeGlobalObjective.BadgeSingleObjective>> tempResult = this.allMedals.get(this.expandableListTitle.get(listPosition));
//                Log.i(TAG, "getChild: -> -> "+allMedals);
//                Log.i(TAG, "getChild: -> -> "+allMedals.get(this.expandableListTitle.get(listPosition)));
//                Log.i(TAG, "getChild: -> -> "+expandableListBadgeNames.get(expandedListPosition));
                return this.allMedals.get(this.expandableListTitle.get(listPosition)).get(expandableListBadgeNames.get(expandedListPosition));
            }else{
                HashMap<String, HashMap<String, BadgeBoard.BadgeGlobalObjective.BadgeSingleObjective>> diffMedals = this.allMedals.get(this.expandableListTitle.get(listPosition));
//                Log.i(TAG, "selectedChild:Pos: "+expandedListPosition);
//                Log.i(TAG, "selectedChild:Name: "+Types2ArrayAdv().get(expandedListPosition));
                HashMap<String, BadgeBoard.BadgeGlobalObjective.BadgeSingleObjective> listChildMedals = diffMedals.get(Types2ArrayAdv().get(expandedListPosition));
                return listChildMedals;
            }
        }
    }

    public ArrayList<String> Types2ArrayAdv() {
        ArrayList<String> types = new ArrayList<>();
        for(int index = 2; index< BadgeBoard.BadgeName.values().length; index++){
            types.add(BadgeBoard.BadgeName.values()[index].toString());
        }
        return types;
    }

    @Override
    public long getChildId(int listPosition, int expandedListPosition) {
        //Log.i(TAG, "getChildID: result: "+expandedListPosition);
        return expandedListPosition;
    }

    @Override
    public View getChildView(int listPosition, final int expandedListPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        //Log.i(TAG, "getChildView:");
        //return Linear Layout with 3 medals
        @SuppressWarnings("unchecked")
        HashMap<String, BadgeBoard.BadgeGlobalObjective.BadgeSingleObjective> badgeRow = (HashMap<String, BadgeBoard.BadgeGlobalObjective.BadgeSingleObjective>) getChild(listPosition, expandedListPosition);
        LayoutInflater layoutInflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (listPosition>=2) {
            convertView = layoutInflater.inflate(R.layout.badge_display_row_advanced, null);
        }else{
            convertView = layoutInflater.inflate(R.layout.badge_display_row, null);
        }

//        Log.i(TAG, "CLICKED ON: POS: "+listPosition+" inside: " +expandedListPosition);

        //Log.i(TAG, "-----------------------------getChildView: "+badgeRow.keySet());

        for(BadgeBoard.BadgeGlobalObjective.BadgeSingleObjective obj: badgeRow.values()){
            TextView badgeTextHolder = null;
            ImageView badgeBackGroundHolder = null;
            ImageView badgeIcon = null;
//            Log.i(TAG, "getChildView: -----------------------------> "+obj.getMark());
            switch (obj.getMark()){
                case bronze:
                    badgeTextHolder = (TextView) convertView.findViewById(R.id.badge_text1);
                    badgeBackGroundHolder = (ImageView) convertView.findViewById(R.id.badge_background1);
                    badgeIcon = (ImageView) convertView.findViewById(R.id.badge_symbol1);
                    break;
                case silver:
                    badgeTextHolder = (TextView) convertView.findViewById(R.id.badge_text2);
                    badgeBackGroundHolder = (ImageView) convertView.findViewById(R.id.badge_background2);
                    badgeIcon = (ImageView) convertView.findViewById(R.id.badge_symbol2);
                    break;
                case gold:
                    badgeTextHolder = (TextView) convertView.findViewById(R.id.badge_text3);
                    badgeBackGroundHolder = (ImageView) convertView.findViewById(R.id.badge_background3);
                    badgeIcon = (ImageView) convertView.findViewById(R.id.badge_symbol3);
                    break;
                case single:
                    badgeTextHolder = (TextView) convertView.findViewById(R.id.badge_text1);
                    badgeBackGroundHolder = (ImageView) convertView.findViewById(R.id.badge_background1);
                    badgeIcon = (ImageView) convertView.findViewById(R.id.badge_symbol1);

                    TextView badgeTextHolder1 = (TextView) convertView.findViewById(R.id.badge_text2);
                    ImageView badgeBackGroundHolder1 = (ImageView) convertView.findViewById(R.id.badge_background2);
                    ImageView badgeIcon1 = (ImageView) convertView.findViewById(R.id.badge_symbol2);

                    TextView badgeTextHolder2 = (TextView) convertView.findViewById(R.id.badge_text3);
                    ImageView badgeBackGroundHolder2 = (ImageView) convertView.findViewById(R.id.badge_background3);
                    ImageView badgeIcon2 = (ImageView) convertView.findViewById(R.id.badge_symbol3);

                    badgeBackGroundHolder1.setVisibility(View.INVISIBLE);
                    badgeIcon1.setVisibility(View.INVISIBLE);
                    badgeTextHolder1.setVisibility(View.INVISIBLE);

                    badgeBackGroundHolder2.setVisibility(View.INVISIBLE);
                    badgeIcon2.setVisibility(View.INVISIBLE);
                    badgeTextHolder2.setVisibility(View.INVISIBLE);
                    break;
            }

            String test = obj.getMyStringPath();
//            Log.i(TAG, "test: "+test);

            badgeTextHolder.setText(con.getResources().getIdentifier(obj.getMyStringPath(),"string", con.getPackageName()));
            if(!obj.isLocked()){
                badgeBackGroundHolder.setImageResource(con.getResources().getIdentifier(obj.getMyBackgroundPath(),"drawable", con.getPackageName()));
                badgeIcon.setImageResource(con.getResources().getIdentifier(obj.getMyIconPath(),"drawable", con.getPackageName()));
            }
        }
        return convertView;
    }

    @Override
    public int getChildrenCount(int listPosition) {
//        Log.i(TAG, "CHILD OF LIGHT: "+this.expandableListTitle.get(listPosition)+" : "+this.allMedals.get(this.expandableListTitle.get(listPosition)));
        return this.allMedals.get(this.expandableListTitle.get(listPosition)).size();
    }

    @Override
    public Object getGroup(int listPosition) {
        //Log.i(TAG, "getGroup: result: "+this.expandableListTitle.get(listPosition));
        return this.expandableListTitle.get(listPosition);
    }

    @Override
    public int getGroupCount() {
        //Log.i(TAG, "getGroupCount: result: "+this.expandableListTitle.size());
        return this.expandableListTitle.size();
    }

    @Override
    public long getGroupId(int listPosition) {

        //Log.i(TAG, "getGroup: result: "+listPosition);
        return listPosition;
    }

    @Override
    public View getGroupView(int listPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        //titulos

        //Log.i(TAG, "getGroupView: ListPosition: "+listPosition);

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.badge_display_row_title, null);
        }
        TextView listTitleTextView;
        listTitleTextView = (TextView) convertView.findViewById(R.id.listTitle);
        String text;
        if(isUnlocked(playerLvl, expandableListTitle.get(listPosition))){
            //Log.i(TAG, "getGroupView: -> STRING1: "+expandableListTitle.get(listPosition));

            CardView card = (CardView) convertView.findViewById(R.id.title_background);


            text = con.getString(con.getResources().getIdentifier(expandableListTitle.get(listPosition),"string", con.getPackageName()));
            listTitleTextView.setTypeface(null, Typeface.BOLD);
            listTitleTextView.setText(text);
            int[] attrs = new int[] { android.R.attr.selectableItemBackground /* index 0 */};
            TypedArray ta = con.obtainStyledAttributes(attrs);
            Drawable drawableFromTheme = ta.getDrawable(0);
            ta.recycle();
            card.setCardBackgroundColor(ContextCompat.getColor(convertView.getContext(), R.color.white_background));
            //convertView.setBackground(drawableFromTheme);
        }else{
            //Log.i(TAG, "getGroupView: -> STRING2: "+expandableListTitle.get(listPosition)+"_locked");
            text = con.getString(con.getResources().getIdentifier(expandableListTitle.get(listPosition)+"_locked","string", con.getPackageName()));
            listTitleTextView.setTypeface(null, Typeface.BOLD);
            listTitleTextView.setText(text);

            convertView.setEnabled(false);
            convertView.setClickable(false);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    return;
                }
            });
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
