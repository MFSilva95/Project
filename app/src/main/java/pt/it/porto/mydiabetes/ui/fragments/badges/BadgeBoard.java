package pt.it.porto.mydiabetes.ui.fragments.badges;

import  android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.data.BadgeRec;
import pt.it.porto.mydiabetes.database.DB_Read;
import pt.it.porto.mydiabetes.ui.listAdapters.BadgeListAdapter;
import pt.it.porto.mydiabetes.utils.LevelsPointsUtils;

/**
 * Created by Diogo on 24/07/2017.
 */



public class BadgeBoard extends Fragment {
    public enum BadgeName {photo, export, bp, log, hba1c, cholesterol, weight, disease, exercise}//send_data_badge,
    public enum Difficulty {daily, beginner, medium, advanced}
    public enum Marks {single, bronze, silver, gold}

    private int lvl;
    private HashMap<String, HashMap<String,HashMap<String,BadgeGlobalObjective.BadgeSingleObjective>>> allMedals;
    private LinkedList<BadgeRec> badgeList;
    private ExpandableListView expBadgeList;

    public class BadgeGlobalObjective {

        public class BadgeSingleObjective{

            private int nRecords;//conditions to trigger
            private Difficulty diff;
            private BadgeName name;

            public boolean isSingle() {
                return isSingle;
            }

            private boolean isSingle;

            public int getnRecords() {
                return nRecords;
            }
            public Difficulty getDiff() {
                return diff;
            }
            public BadgeName getName() {
                return name;
            }

            public Marks getMark() {
                return mark;
            }
            private String myBackgroundPath;
            private String myIconPath;

            public String getMyBackgroundPath() {
                return myBackgroundPath;
            }

            public String getMyIconPath() {
                return myIconPath;
            }

            public String getMyStringPath() {
                return myStringPath;
            }

            private String myStringPath;

            private Marks mark;

            public boolean isLocked() {
                return !unlocked;
            }

            private boolean unlocked = false;

            public void unlock(){
                this.unlocked = true;
            }

            public BadgeSingleObjective(int baseNumber, int increaseFact, Difficulty diff, BadgeName name, Marks mark){

                this.nRecords = 2;//(int) ((baseNumber*markToNumber(mark))*(Math.pow(increaseFact, diffToNumber(diff))));
                //baseNumber+increaseFact*(diffToNumber(diff))*(markToNumber(mark));
                this.diff = diff;
                this.name = name;
                this.mark = mark;

                if(mark.equals(Marks.single)){
                    this.myBackgroundPath = "medal_gold_"+diff.toString();
                    this.myIconPath = name.toString();
                    this.myStringPath = diff.toString()+"_"+mark.toString()+"_"+name.toString();
                }else {
                    this.myBackgroundPath = "medal_"+mark.toString()+"_"+diff.toString();
                    this.myIconPath = name.toString();
                    this.myStringPath = diff.toString()+"_"+mark.toString()+"_"+name.toString();
                }
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

            @Override
            public String toString() {
                return "BADGE{ DIFF: "+diff+ " | MARK: "+mark+" | NAME: "+name+" }";
            }
        }

        private BadgeName type;
        private int baseRecordNumber;
        private int increaseFactor;
        Difficulty[] difficulties;
        Marks[] marks;

        public BadgeGlobalObjective(BadgeName bType, Difficulty[] difficulties, Marks[] marks, int bBaseRecordNumber, int bIncreaseFactor){

            this.type = bType;
            this.difficulties = difficulties;
            this.baseRecordNumber = bBaseRecordNumber;
            this.increaseFactor = bIncreaseFactor;
            this.marks = marks;
        }


        public HashMap<String, HashMap<String, HashMap<String, BadgeSingleObjective>>> createAllSingleObjectives(){

            HashMap<String, HashMap<String,HashMap<String,BadgeSingleObjective>>> allMedals = new HashMap<>();

            HashMap<String, HashMap<String,BadgeSingleObjective>> allDiffMedals;
            HashMap<String,BadgeSingleObjective> allNameMedals;

            for(Difficulty diff: difficulties){
                allDiffMedals = new HashMap<>();
                allNameMedals = new HashMap<>();
                for(Marks mark:marks){
                    allNameMedals.put(mark.toString(), new BadgeSingleObjective(baseRecordNumber, increaseFactor, diff, type, mark));
                }
                allDiffMedals.put(type.toString(), allNameMedals);
                allMedals.put(diff.toString(), allDiffMedals);
            }
            return allMedals;
        }
    }

    // Type -> daily, [beginner, ...]
    // Medal -> bronze, silver, gold
    // ID -> randomID
    // Name -> photo, BP, etc

    //BadgeName bType, Difficulty[] difficulties, Marks[] marks, int bBaseRecordNumber, int bIncreaseFactor
    private BadgeGlobalObjective export_badge = new BadgeGlobalObjective(BadgeName.export, new Difficulty[]{Difficulty.beginner}, new Marks[]{Marks.single},1,1);
    private BadgeGlobalObjective photo_badge = new BadgeGlobalObjective(BadgeName.photo, new Difficulty[]{Difficulty.beginner}, new Marks[]{Marks.single},1,1);
    //private BadgeGlobalObjective send_data_badge = new BadgeGlobalObjective(BadgeName.send_data_badge, 1, 0, true);

    private BadgeGlobalObjective bp_badge = new BadgeGlobalObjective(BadgeName.bp, new Difficulty[]{Difficulty.beginner, Difficulty.medium, Difficulty.advanced}, new Marks[]{Marks.bronze, Marks.silver, Marks.gold},1,1);
    private BadgeGlobalObjective cholesterol_badge = new BadgeGlobalObjective(BadgeName.cholesterol, new Difficulty[]{Difficulty.beginner, Difficulty.medium, Difficulty.advanced}, new Marks[]{Marks.bronze, Marks.silver, Marks.gold},1,1);
    private BadgeGlobalObjective disease_badge = new BadgeGlobalObjective(BadgeName.disease, new Difficulty[]{Difficulty.beginner, Difficulty.medium, Difficulty.advanced}, new Marks[]{Marks.bronze, Marks.silver, Marks.gold},1,1);
    private BadgeGlobalObjective exercise_badge = new BadgeGlobalObjective(BadgeName.exercise, new Difficulty[]{Difficulty.beginner, Difficulty.medium, Difficulty.advanced}, new Marks[]{Marks.bronze, Marks.silver, Marks.gold},1,1);
    private BadgeGlobalObjective hba1c_badge = new BadgeGlobalObjective(BadgeName.hba1c, new Difficulty[]{Difficulty.beginner, Difficulty.medium, Difficulty.advanced}, new Marks[]{Marks.bronze, Marks.silver, Marks.gold},1,1);
    private BadgeGlobalObjective log_badge = new BadgeGlobalObjective(BadgeName.log, new Difficulty[]{Difficulty.beginner, Difficulty.medium, Difficulty.advanced}, new Marks[]{Marks.bronze, Marks.silver, Marks.gold},1,1);
    private BadgeGlobalObjective weight_badge = new BadgeGlobalObjective(BadgeName.weight, new Difficulty[]{Difficulty.beginner, Difficulty.medium, Difficulty.advanced}, new Marks[]{Marks.bronze, Marks.silver, Marks.gold},1,1);

    private BadgeGlobalObjective daily_badge = new BadgeGlobalObjective(BadgeName.log, new Difficulty[]{Difficulty.daily}, new Marks[]{Marks.bronze, Marks.silver, Marks.gold},1,1);



    LinkedList<BadgeGlobalObjective> myDiabetesMultiObjectives = new LinkedList<>();


    public static BadgeBoard newInstance() {

        Bundle args = new Bundle();
        BadgeBoard fragment = new BadgeBoard();
        fragment.setArguments(args);
        return fragment;
    }

    public BadgeBoard(){

        myDiabetesMultiObjectives.add(export_badge);
        myDiabetesMultiObjectives.add(photo_badge);

        myDiabetesMultiObjectives.add(bp_badge);
        myDiabetesMultiObjectives.add(cholesterol_badge);
        myDiabetesMultiObjectives.add(disease_badge);
        myDiabetesMultiObjectives.add(exercise_badge);
        myDiabetesMultiObjectives.add(hba1c_badge);
        myDiabetesMultiObjectives.add(log_badge);
        myDiabetesMultiObjectives.add(weight_badge);
        myDiabetesMultiObjectives.add(daily_badge);
        //myDiabetesMultiObjectives.add(send_data_badge);


        allMedals = new HashMap<>();
        for(BadgeGlobalObjective obj:myDiabetesMultiObjectives){
            addHashMap(allMedals, obj.createAllSingleObjectives());
        }
    }

    private void addHashMap(HashMap<String, HashMap<String, HashMap<String, BadgeGlobalObjective.BadgeSingleObjective>>> allMedals, HashMap<String, HashMap<String, HashMap<String, BadgeGlobalObjective.BadgeSingleObjective>>> allSingleObjectives) {

        for(String diffKey:allSingleObjectives.keySet()){
            if(allMedals.keySet().contains(diffKey)) {
                for(String name:allSingleObjectives.get(diffKey).keySet()){
                    if(allMedals.get(diffKey).keySet().contains(name)) {
                        for(String mark:allSingleObjectives.get(diffKey).get(name).keySet()){
                            allMedals.get(diffKey).get(name).put(mark, allSingleObjectives.get(diffKey).get(name).get(mark));
                        }
                    }else{
                        allMedals.get(diffKey).put(name, allSingleObjectives.get(diffKey).get(name));
                    }
                }
            }else {
                allMedals.put(diffKey, allSingleObjectives.get(diffKey));
            }

        }
    }

    public void unlock_medals(LinkedList<BadgeRec> unlockedBadgeList){
        for(BadgeRec badge:unlockedBadgeList){
            //String TAG = "cenas";
            //Log.i(TAG, "unlock_medals: "+badge.getType());
            //Log.i(TAG, "unlock_medals: "+badge.getName());
            //Log.i(TAG, "unlock_medals: "+badge.getMedal());
            allMedals.get(badge.getType()).get(badge.getName()).get(badge.getMedal()).unlock();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState){

        final View layout = inflater.inflate(R.layout.fragment_badge_board, container, false);
        if(badgeList==null){
            DB_Read read = new DB_Read(container.getContext());
            lvl = LevelsPointsUtils.getLevel(container.getContext(), read);
            badgeList = read.Badges_GetAll();
            read.close();
            if(badgeList!=null){
                unlock_medals(badgeList);
            }
        }



        expBadgeList = (ExpandableListView) layout.findViewById(R.id.expandableBadges);
        BadgeListAdapter expandableListAdapter = new BadgeListAdapter(container.getContext(), Difficulty2Array(), Types2Array(), allMedals, lvl);
        expBadgeList.setAdapter(expandableListAdapter);
        expBadgeList.expandGroup(0);

        return layout;
    }

    private ArrayList<String> Types2Array() {
        ArrayList<String> types = new ArrayList<>();
        for(BadgeName t:BadgeName.values()){
            types.add(t.toString());
        }
        return types;
    }

    public ArrayList<String> Difficulty2Array(){
        ArrayList<String> difs = new ArrayList<>();
        for(Difficulty diff:Difficulty.values()){
            difs.add(diff.toString());
        }
        return difs;
    }
}
