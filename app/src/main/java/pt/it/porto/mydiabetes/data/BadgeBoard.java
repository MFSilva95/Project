package pt.it.porto.mydiabetes.data;

import  android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.aakira.expandablelayout.ExpandableRelativeLayout;

import java.util.LinkedList;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.database.DB_Read;
import pt.it.porto.mydiabetes.ui.views.BadgeLayout;
import pt.it.porto.mydiabetes.utils.LevelsPointsUtils;

/**
 * Created by Diogo on 24/07/2017.
 */



public class BadgeBoard extends Fragment{
    public enum badgeName{photo, export, bp, log, hba1c, cholesterol, weight, disease, exercise}

    private ExpandableRelativeLayout expandableBeginnerBadges;
    private ExpandableRelativeLayout expandableMediumBadges;
    private ExpandableRelativeLayout expandableAdvancedBadges;

    private RelativeLayout beginnerContainer;
    private RelativeLayout mediumContainer;
    private RelativeLayout advancedContainer;

    private LinearLayout beginner_expandable_container;
    private LinearLayout medium_expandable_container;
    private LinearLayout advanced_expandable_container;


    private LinkedList<BadgeRec> badgeList_b;
    private LinkedList<BadgeRec> badgeList_m;
    private LinkedList<BadgeRec> badgeList_a;

    class BadgeObjective{
        badgeName type;
        boolean isSingular;
        int baseRecordNumber;
        int increaseFactor;
        public BadgeObjective(badgeName bType, boolean bIsSingular, int bBaseRecordNumber, int bIncreaseFactor){
            this.type = bType;
            this.isSingular = bIsSingular;
            this.baseRecordNumber = bBaseRecordNumber;
            this.increaseFactor = bIncreaseFactor;
        }
    }

//    3 Layouts

    // Type -> daily, [beginner, ...]
    // Medal -> bronze, silver, gold
    // ID -> randomID
    // Name -> photo, BP, etc

    //private BadgeObjective export_badge = new BadgeObjective(badgeName.export, true, 1, 0);
    //private BadgeObjective photo_badge = new BadgeObjective(badgeName.photo, true, 1, 0);

    private BadgeObjective bp_badge = new BadgeObjective(badgeName.bp, false, 3, 1);
    private BadgeObjective cholesterol_badge = new BadgeObjective(badgeName.cholesterol, false, 3, 1);
    private BadgeObjective disease_badge = new BadgeObjective(badgeName.disease, false, 3, 1);
    private BadgeObjective exercise_badge = new BadgeObjective(badgeName.exercise, false, 3, 1);
    private BadgeObjective hba1c_badge = new BadgeObjective(badgeName.hba1c, false, 3, 1);
    private BadgeObjective log_badge = new BadgeObjective(badgeName.log, false, 3, 1);
    private BadgeObjective weight_badge = new BadgeObjective(badgeName.weight, false, 3, 1);



    LinkedList<BadgeObjective> myDiabetesObjectives = new LinkedList<>();

    public static BadgeBoard newInstance() {

        Bundle args = new Bundle();

        BadgeBoard fragment = new BadgeBoard();
        fragment.setArguments(args);
        return fragment;
    }

    public BadgeBoard(){

        //myDiabetesObjectives.add(export_badge);
        //myDiabetesObjectives.add(photo_badge);

        myDiabetesObjectives.add(bp_badge);
        myDiabetesObjectives.add(cholesterol_badge);
        myDiabetesObjectives.add(disease_badge);
        myDiabetesObjectives.add(exercise_badge);
        myDiabetesObjectives.add(hba1c_badge);
        myDiabetesObjectives.add(log_badge);
        myDiabetesObjectives.add(weight_badge);




//        if(beginnerLayout is visible ){
//            for each objective ->
//            add new LayoutRow
//            if is singular add
//                    else bronze, silver, gold
//        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState){

        // Inflate the layout for this fragment
        final View layout = inflater.inflate(R.layout.fragment_badge_board, container, false);

        DB_Read read = new DB_Read(container.getContext());
        int lvl = LevelsPointsUtils.getLevel(container.getContext(), read);
        LinkedList<BadgeRec> badgeList = read.Badges_GetAll_NONDAILY();

        badgeList_b = read.Badges_GetAll_NONDAILY();
        badgeList_m = read.Badges_GetAll_NONDAILY();
        badgeList_a = read.Badges_GetAll_NONDAILY();

        read.close();

        /*layout = inflater.inflate(R.layout.fragment_badges_list, container, false);
        list = (ListView) layout.findViewById(R.id.list);

        list.setAdapter(new BadgeListAdapter(badgeList, getContext()));
        list.setEmptyView(layout.findViewById(R.id.list_empty));*/


        expandableBeginnerBadges = (ExpandableRelativeLayout) layout.findViewById(R.id.expandableBeginnerBadges);
        expandableBeginnerBadges.collapse();
        beginner_expandable_container = (LinearLayout) layout.findViewById(R.id.beginnerBadgeContent);

        beginnerContainer = (RelativeLayout) layout.findViewById(R.id.beginnerBadgesTitle);
        beginnerContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!expandableBeginnerBadges.isExpanded()){
                    fillExpandable(beginner_expandable_container, myDiabetesObjectives, 1, layout.getContext());
                    //unlockBadges(badgeList_b, container.getContext());
                }
                expandableBeginnerBadges.toggle();

            }
        });

        if(lvl >= LevelsPointsUtils.BADGES_MEDIUM_UNLOCK_LEVEL ) {

            expandableMediumBadges = (ExpandableRelativeLayout) layout.findViewById(R.id.expandableMediumBadge);
            expandableMediumBadges.collapse();

            mediumContainer = (RelativeLayout) layout.findViewById(R.id.mediumBadgesTitle);
            mediumContainer.setBackground(ContextCompat.getDrawable(container.getContext(), R.drawable.background_empty));

            TextView m_title = (TextView) layout.findViewById(R.id.textMedium);
            m_title.setText(getString(R.string.medium));

            medium_expandable_container = (LinearLayout) layout.findViewById(R.id.mediumBadgeContent);

            mediumContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!expandableMediumBadges.isExpanded()){
                        fillExpandable(medium_expandable_container, myDiabetesObjectives, 2, layout.getContext());
                        //unlockBadges(badgeList_m);
                    }
                    expandableMediumBadges.toggle();

                }
            });

            if(lvl >= LevelsPointsUtils.BADGES_ADVANCED_UNLOCK_LEVEL ) {

                expandableAdvancedBadges = (ExpandableRelativeLayout) layout.findViewById(R.id.expandableAdvancedBadge);
                expandableAdvancedBadges.collapse();

                advancedContainer = (RelativeLayout) layout.findViewById(R.id.advancedBadgesTitle);
                advancedContainer.setBackground(ContextCompat.getDrawable(container.getContext(), R.drawable.background_empty));

                TextView a_title = (TextView) layout.findViewById(R.id.textAdvanced);
                a_title.setText(getString(R.string.advanced));

                advanced_expandable_container = (LinearLayout) layout.findViewById(R.id.advancedBadgeContent);

                advancedContainer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!expandableAdvancedBadges.isExpanded()){

                            fillExpandable(advanced_expandable_container, myDiabetesObjectives, 3, layout.getContext());
                            //unlockBadges(badgeList_a);
                        }
                        expandableAdvancedBadges.toggle();
                    }
                });
            }
        }

        //unlock_badges(badgeList_b);
        return layout;
    }

    private void fillExpandable(LinearLayout badgeContent, LinkedList<BadgeObjective> myDiabetesObjectives, int lvl, Context con) {

        Log.i("cenas", "fillExpandable: "+myDiabetesObjectives.size());
        for(BadgeObjective obj:myDiabetesObjectives){
            try{
                Log.i("cenas", "fillExpandable inside: "+obj.type);
                LinearLayout badgeRow = new LinearLayout(con);
                badgeRow.setOrientation(LinearLayout.HORIZONTAL);
                badgeRow.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                BadgeLayout bronzeMedal = null;
                BadgeLayout silverMedal = null;
                BadgeLayout goldMedal = null;
                if (!obj.isSingular){
                    switch (lvl){
                        case 1:
                            bronzeMedal = new BadgeLayout(con,"medal_locked_beginner","","beginner_bronze_"+obj.type,0);
                            silverMedal = new BadgeLayout(con,"medal_locked_beginner","","beginner_silver_"+obj.type,0);
                            goldMedal = new BadgeLayout(con,"medal_locked_beginner","","beginner_gold_"+obj.type,0);
                            break;
                        case 2:
                            bronzeMedal = new BadgeLayout(con,"medal_locked_beginner","","medium_bronze_"+obj.type,0);
                            silverMedal = new BadgeLayout(con,"medal_locked_beginner","","medium_silver_"+obj.type,0);
                            goldMedal = new BadgeLayout(con,"medal_locked_beginner","","medium_gold_"+obj.type,0);
                            break;
                        case 3:
                            bronzeMedal = new BadgeLayout(con,"medal_locked_advanced","","advanced_bronze_"+obj.type,0);
                            silverMedal = new BadgeLayout(con,"medal_locked_advanced","","advanced_silver_"+obj.type,0);
                            goldMedal = new BadgeLayout(con,"medal_locked_advanced","","advanced_gold_"+obj.type,0);
                            break;
                    }
                }
                badgeRow.addView(bronzeMedal);
                badgeRow.addView(silverMedal);
                badgeRow.addView(goldMedal);

                badgeContent.addView(badgeRow);
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }

}
