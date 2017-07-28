package pt.it.porto.mydiabetes.data;

import  android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.database.DB_Read;
import pt.it.porto.mydiabetes.ui.listAdapters.BadgeListAdapter;
import pt.it.porto.mydiabetes.ui.views.BadgeLayout;
import pt.it.porto.mydiabetes.utils.LevelsPointsUtils;

/**
 * Created by Diogo on 24/07/2017.
 */



public class BadgeBoard extends Fragment{
    public enum BadgeName {photo, export, bp, log, hba1c, cholesterol, weight, disease, exercise}
    public enum Difficulty {beginner, medium, advanced}
    public enum Marks {bronze, silver, gold}
    private HashMap<String, HashMap<String,HashMap<String,BadgeGlobalObjective.BadgeSingleObjective>>> allMedals;

    private ExpandableListView expBadgeList;

    //Objectivos   Trigger -> function -> getAllObjectives(Type) -> check conditions -> check if medal given -> ?give

    // HASHMAP
        // BEGINNER HASHMAP
          // BP
          // photo
            //BRONZE  BadgeLayoyt
            //SILVER
            //GOLD
        // MEDIUM
        // ADVANCED

    private HashMap<Difficulty, LinkedList<LinkedList<BadgeGlobalObjective.BadgeSingleObjective>>> expandableListMap;

    private LinkedList<BadgeRec> badgeList_b;
    private LinkedList<BadgeRec> badgeList_m;
    private LinkedList<BadgeRec> badgeList_a;




    public class BadgeGlobalObjective {

        public class BadgeSingleObjective{

            private int nRecords;//conditions to trigger
            private Difficulty diff;
            private BadgeName name;
            private Marks mark;
            private boolean unlocked = false;

            public void unlock(){
                this.unlocked = true;
            }

            public BadgeSingleObjective(int baseNumber, int increaseFact, Difficulty diff, BadgeName name, Marks mark){

                this.nRecords = (int) ((baseNumber*markToNumber(mark))*(Math.pow(increaseFact, diffToNumber(diff))));
                        //baseNumber+increaseFact*(diffToNumber(diff))*(markToNumber(mark));
                this.diff = diff;
                this.name = name;
                this.mark = mark;
            }

            private int markToNumber(Marks mark) {
                return Marks.valueOf(mark.toString()).ordinal();
            }

            private int diffToNumber(Difficulty diff) {
                return Difficulty.valueOf(diff.toString()).ordinal();
            }

            public String getMedalID(){
                //return type.toString()+"_"+medal+"_"+name;
                return diff+"_"+mark+"_"+name;
            }
        }

        private BadgeName type;
        private int baseRecordNumber;
        private int increaseFactor;

        public BadgeGlobalObjective(BadgeName bType, int bBaseRecordNumber, int bIncreaseFactor){

            this.type = bType;
            this.baseRecordNumber = bBaseRecordNumber;
            this.increaseFactor = bIncreaseFactor;
        }

        public HashMap<String, HashMap<String, HashMap<String, BadgeSingleObjective>>> createAllSingleObjectives(){

            HashMap<String, HashMap<String,HashMap<String,BadgeSingleObjective>>> allMedals = new HashMap<>();
            for(Difficulty diff: Difficulty.values()){
                HashMap<String, HashMap<String,BadgeSingleObjective>> allDiffMedals = new HashMap<>();
                for(BadgeName name:BadgeName.values()){
                    HashMap<String,BadgeSingleObjective> allNameMedals = new HashMap<>();
                    for(Marks mark:Marks.values()){
                        allNameMedals.put((mark.toString()),new BadgeSingleObjective(baseRecordNumber,increaseFactor,diff,name,mark));
                    }
                    allDiffMedals.put((name.toString()), allNameMedals);
                }
                allMedals.put((diff.toString()), allDiffMedals);
            }
            return allMedals;
        }
    }

    // Type -> daily, [beginner, ...]
    // Medal -> bronze, silver, gold
    // ID -> randomID
    // Name -> photo, BP, etc

    private BadgeGlobalObjective export_badge = new BadgeGlobalObjective(BadgeName.export, 1, 0);
    private BadgeGlobalObjective photo_badge = new BadgeGlobalObjective(BadgeName.photo, 1, 0);

    private BadgeGlobalObjective bp_badge = new BadgeGlobalObjective(BadgeName.bp, 3, 1);
    private BadgeGlobalObjective cholesterol_badge = new BadgeGlobalObjective(BadgeName.cholesterol, 3, 1);
    private BadgeGlobalObjective disease_badge = new BadgeGlobalObjective(BadgeName.disease, 3, 1);
    private BadgeGlobalObjective exercise_badge = new BadgeGlobalObjective(BadgeName.exercise, 3, 1);
    private BadgeGlobalObjective hba1c_badge = new BadgeGlobalObjective(BadgeName.hba1c, 3, 1);
    private BadgeGlobalObjective log_badge = new BadgeGlobalObjective(BadgeName.log, 3, 1);
    private BadgeGlobalObjective weight_badge = new BadgeGlobalObjective(BadgeName.weight, 3, 1);



    LinkedList<BadgeGlobalObjective> myDiabetesMultiObjectives = new LinkedList<>();
    LinkedList<BadgeGlobalObjective> myDiabetesSingleObjectives = new LinkedList<>();


    public static BadgeBoard newInstance() {

        Bundle args = new Bundle();

        BadgeBoard fragment = new BadgeBoard();
        fragment.setArguments(args);
        return fragment;
    }

    public BadgeBoard(){

        myDiabetesSingleObjectives.add(export_badge);
        myDiabetesSingleObjectives.add(photo_badge);

        myDiabetesMultiObjectives.add(bp_badge);
        myDiabetesMultiObjectives.add(cholesterol_badge);
        myDiabetesMultiObjectives.add(disease_badge);
        myDiabetesMultiObjectives.add(exercise_badge);
        myDiabetesMultiObjectives.add(hba1c_badge);
        myDiabetesMultiObjectives.add(log_badge);
        myDiabetesMultiObjectives.add(weight_badge);

        allMedals = new HashMap<>();
        for(BadgeGlobalObjective obj:myDiabetesMultiObjectives){
            allMedals.putAll(obj.createAllSingleObjectives());
        }

        String TAG = "cenas";

        Log.i("cenas", "----------------------------------------");
        //HashMap<String, HashMap<String,HashMap<String,BadgeGlobalObjective.BadgeSingleObjective>>>
        Log.i(TAG, "BadgeBoard: lvl:0 "+allMedals.keySet());
        for(HashMap<String,HashMap<String,BadgeGlobalObjective.BadgeSingleObjective>> s:allMedals.values()){
            Log.i(TAG, "BadgeBoard: lvl:1 "+s.keySet());
            for(HashMap<String,BadgeGlobalObjective.BadgeSingleObjective> s1:s.values()){
                Log.i(TAG, "BadgeBoard: lvl:2 "+s1.keySet());
            }
        }
        Log.i("cenas", "----------------------------------------");

    }

    public void unlock_medals(LinkedList<BadgeRec> unlockedBadgeList){
        for(BadgeRec badge:unlockedBadgeList){
            //Difficulty, BadgeName, Mark
            allMedals.get(badge.getType()).get(badge.getName()).get(badge.getMedal()).unlock();
        }
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
        read.close();

        unlock_medals(badgeList);

        expBadgeList = (ExpandableListView) layout.findViewById(R.id.expandableBadges);
        BadgeListAdapter expandableListAdapter = new BadgeListAdapter(container.getContext(), Difficulty2Array(), allMedals, lvl);
        expBadgeList.setAdapter(expandableListAdapter);

        return layout;
    }

    public ArrayList<String> Difficulty2Array(){
        ArrayList<String> difs = new ArrayList<>();
        for(Difficulty diff:Difficulty.values()){
            difs.add(diff.toString());
        }
        return difs;
    }
}
