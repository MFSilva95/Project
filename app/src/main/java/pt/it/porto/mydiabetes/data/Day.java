package pt.it.porto.mydiabetes.data;


import android.annotation.SuppressLint;

import java.util.Calendar;
import java.util.LinkedList;

import pt.it.porto.mydiabetes.utils.HomeElement;

@SuppressLint("ParcelCreator")
public class Day extends HomeElement {

    private LinkedList<LogBookEntry> logBookEntries;
    private LinkedList<WeightRec> weightList;
    private LinkedList<ExerciseRec> exerciseList;
    private LinkedList<DiseaseRec> diseaseList;
    private LinkedList<BloodPressureRec> bloodPressureList;
    private LinkedList<HbA1cRec> hbA1cList;
    private LinkedList<CholesterolRec> cholesterolList;
    private String date;

    public Day(String date, LinkedList<LogBookEntry> logBookEntries, LinkedList<ExerciseRec> exerciseList, LinkedList<WeightRec> weightList,
               LinkedList<DiseaseRec> diseaseList, LinkedList<BloodPressureRec> bloodPressureList,
               LinkedList<HbA1cRec> hbA1cList, LinkedList<CholesterolRec> cholesterolList) {
        super(Type.DAY);
        this.logBookEntries = logBookEntries;
        this.cholesterolList = cholesterolList;
        this.hbA1cList = hbA1cList;
        this.bloodPressureList = bloodPressureList;
        this.exerciseList = exerciseList;
        this.weightList = weightList;
        this.diseaseList = diseaseList;
        this.date = date;
    }

    public LinkedList<LogBookEntry> getLogBookEntries() {
        return logBookEntries;
    }

    public LinkedList<WeightRec> getWeightList() {
        return weightList;
    }

    public LinkedList<ExerciseRec> getExerciseList() {
        return exerciseList;
    }

    public LinkedList<DiseaseRec> getDiseaseList() {
        return diseaseList;
    }

    public LinkedList<BloodPressureRec> getBloodPressureList() {
        return bloodPressureList;
    }

    public LinkedList<HbA1cRec> getHbA1cList() {
        return hbA1cList;
    }

    public LinkedList<CholesterolRec> getCholesterolList() {
        return cholesterolList;
    }

    public String getDay(){
        return date;
    }

    public boolean isEmpty(){
        if(logBookEntries.size() == 0 && exerciseList.size() == 0 && weightList.size() == 0 && diseaseList.size() == 0 && bloodPressureList.size() == 0 &&
                hbA1cList.size() == 0 && cholesterolList.size() == 0){
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "Day{" +
                "day=" + getDay() +
                ", log=" + logBookEntries.size() +
                ", peso=" + weightList.size() +
                ", exercicio=" + exerciseList.size() +
                ", doenca=" + diseaseList.size() +
                ", pressao=" + bloodPressureList.size() +
                ", hba1c=" + hbA1cList.size() +
                ", colesterol="  + cholesterolList.size() +
                '}';
    }
}