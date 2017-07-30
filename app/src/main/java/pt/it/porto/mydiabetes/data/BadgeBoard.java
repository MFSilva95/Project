package pt.it.porto.mydiabetes.data;

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
import pt.it.porto.mydiabetes.database.DB_Read;
import pt.it.porto.mydiabetes.ui.listAdapters.BadgeListAdapter;
import pt.it.porto.mydiabetes.utils.LevelsPointsUtils;

/**
 * Created by Diogo on 24/07/2017.
 */



public class BadgeBoard extends Fragment{
    public enum BadgeName {bp, log, hba1c, cholesterol, weight, disease, exercise, export, photo,send_data_badge}
    public enum Difficulty {beginner, medium, advanced}
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

            public BadgeSingleObjective(int baseNumber, int increaseFact, Difficulty diff, BadgeName name, Marks mark, boolean isSingle){

                this.nRecords = 2;//(int) ((baseNumber*markToNumber(mark))*(Math.pow(increaseFact, diffToNumber(diff))));
                //baseNumber+increaseFact*(diffToNumber(diff))*(markToNumber(mark));
                this.diff = diff;
                this.name = name;
                this.mark = mark;

                this.isSingle = isSingle;

                this.myBackgroundPath = "medal_"+mark.toString()+"_"+diff.toString();
                this.myIconPath = name.toString();
                this.myStringPath = diff.toString()+"_"+mark.toString()+"_"+name.toString();
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
        private boolean isSingle;

        public boolean isSingle(){
            return isSingle;
        }

        public BadgeGlobalObjective(BadgeName bType, int bBaseRecordNumber, int bIncreaseFactor, Boolean isSingle){

            this.type = bType;
            this.baseRecordNumber = bBaseRecordNumber;
            this.increaseFactor = bIncreaseFactor;
            this.isSingle = isSingle;
        }


        public HashMap<String, HashMap<String, HashMap<String, BadgeSingleObjective>>> createAllSingleObjectives(){

            HashMap<String, HashMap<String,HashMap<String,BadgeSingleObjective>>> allMedals = new HashMap<>();

            HashMap<String, HashMap<String,BadgeSingleObjective>> allDiffMedals = new HashMap<>();
            HashMap<String,BadgeSingleObjective> allNameMedals = new HashMap<>();

            if(isSingle) {
                allNameMedals.put(Marks.single.toString(), new BadgeSingleObjective(baseRecordNumber, increaseFactor, Difficulty.beginner, type, Marks.single, true));
                allDiffMedals.put(type.toString(), allNameMedals);
                allMedals.put(Difficulty.beginner.toString(), allDiffMedals);

            }else{
                for(Difficulty diff: Difficulty.values()){
                    allDiffMedals = new HashMap<>();
                    allNameMedals = new HashMap<>();
                    for(Marks mark:Marks.values()){
                        if(!mark.equals(Marks.single)) {
                            allNameMedals.put(mark.toString(), new BadgeSingleObjective(baseRecordNumber, increaseFactor, diff, type, mark, false));//name,mark));
                        }
                    }
                    allDiffMedals.put(type.toString(), allNameMedals);
                    allMedals.put(diff.toString(), allDiffMedals);
                }
            }
            return allMedals;
        }
    }

    // Type -> daily, [beginner, ...]
    // Medal -> bronze, silver, gold
    // ID -> randomID
    // Name -> photo, BP, etc

    private BadgeGlobalObjective export_badge = new BadgeGlobalObjective(BadgeName.export, 1, 0, true);
    private BadgeGlobalObjective photo_badge = new BadgeGlobalObjective(BadgeName.photo, 1, 0, true);
    //private BadgeGlobalObjective send_data_badge = new BadgeGlobalObjective(BadgeName.send_data_badge, 1, 0, true);

    private BadgeGlobalObjective bp_badge = new BadgeGlobalObjective(BadgeName.bp, 3, 1, false);
    private BadgeGlobalObjective cholesterol_badge = new BadgeGlobalObjective(BadgeName.cholesterol, 3, 1, false);
    private BadgeGlobalObjective disease_badge = new BadgeGlobalObjective(BadgeName.disease, 3, 1, false);
    private BadgeGlobalObjective exercise_badge = new BadgeGlobalObjective(BadgeName.exercise, 3, 1, false);
    private BadgeGlobalObjective hba1c_badge = new BadgeGlobalObjective(BadgeName.hba1c, 3, 1, false);
    private BadgeGlobalObjective log_badge = new BadgeGlobalObjective(BadgeName.log, 3, 1, false);
    private BadgeGlobalObjective weight_badge = new BadgeGlobalObjective(BadgeName.weight, 3, 1, false);



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
        //myDiabetesMultiObjectives.add(send_data_badge);


        allMedals = new HashMap<>();
        //HashMap<String, HashMap<String, HashMap<String,HashMap<String,BadgeGlobalObjective.BadgeSingleObjective>>>> allMedalsTest = new HashMap<>();
        for(BadgeGlobalObjective obj:myDiabetesMultiObjectives){

            addHashMap(allMedals, obj.createAllSingleObjectives());
            //HashMap<String, HashMap<String, HashMap<String, BadgeGlobalObjective.BadgeSingleObjective>>> singleMedals = obj.createAllSingleObjectives();
            //Log.i("cenas", "BadgeBoard-SingleIteration: "+singleMedals);
            //allMedalsTest.put("",singleMedals);
            //allMedals.putAll(singleMedals);
            Log.i("cenas", "AllMedals: "+allMedals);
            //Log.i("cenas", "AllMedals: "+allMedalsTest);
        }
    }

    private void addHashMap(HashMap<String, HashMap<String, HashMap<String, BadgeGlobalObjective.BadgeSingleObjective>>> allMedals, HashMap<String, HashMap<String, HashMap<String, BadgeGlobalObjective.BadgeSingleObjective>>> allSingleObjectives) {

        for(String diffKey:allSingleObjectives.keySet()){
            if(allMedals.keySet().contains(diffKey)) {
                for(String name:allSingleObjectives.get(diffKey).keySet()){
                    if(allMedals.get(diffKey).keySet().contains(name)) {
                        for(String mark:allSingleObjectives.get(diffKey).get(name).keySet()){
//                            if(allMedals.get(diffKey).get(name).keySet().contains(mark)){
//
//                            }else{
//
//                            }
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
            //Difficulty, BadgeName, Mark
            String TAG = "cenas";
            Log.i(TAG, " -: "+allMedals);
            Log.i(TAG, "--: "+badge.getType());
            Log.i(TAG, "--: "+badge.getName());
            Log.i(TAG, "--: "+badge.getMedal());

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

        if(badgeList==null){
            DB_Read read = new DB_Read(container.getContext());
            lvl = LevelsPointsUtils.getLevel(container.getContext(), read);
            badgeList = read.Badges_GetAll_NONDAILY();
            read.close();

            unlock_medals(badgeList);
        }



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
