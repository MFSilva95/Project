package pt.it.porto.mydiabetes.data;

import android.app.Fragment;

import java.util.LinkedList;

/**
 * Created by Diogo on 24/07/2017.
 */



public class BadgeBoard extends Fragment{
    public enum badgeName{photo, export, bp, log, hba1c, cholesterol, weight, disease, exercise}

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

    3 Layouts

    // Type -> daily, [beginner, ...]
    // Medal -> bronze, silver, gold
    // ID -> randomID
    // Name -> photo, BP, etc

    private badgeName type;
    private String name;
    private String medal;

    private BadgeObjective bp_badge = new BadgeObjective(badgeName.bp, false, 3, 1);



    LinkedList<BadgeRec> myDiabetesObjectives;

    public BadgeBoard(){

        if(beginnerLayout is visible ){
            for each objective ->
            add new LayoutRow
            if is singular add
                    else bronze, silver, gold
        }

    }

}
