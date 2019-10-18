package com.ugos.jiprolog.android;

import android.content.Context;
import android.util.Log;

import com.ugos.jiprolog.engine.JIPEngine;
import com.ugos.jiprolog.engine.JIPQuery;
import com.ugos.jiprolog.engine.JIPRuntimeException;
import com.ugos.jiprolog.engine.JIPSyntaxErrorException;
import com.ugos.jiprolog.engine.JIPTerm;
import com.ugos.jiprolog.engine.JIPVariable;


public class ARBS_engine {


    private static ARBS_engine instance;
    JIPEngine engineInstance;
    private JIPEngine jip;
    private JIPQuery jipQuery;
    static String TAG = "ARBS_engine_test: ";

    private ARBS_engine(){
        if(jip == null){
            jip = new JIPEngine();

        }
    }

    public String getAdvice(String condition, String recordType, int n){
        /*getAllAdvices( Condition, RecordType, ListAllAdvices),
                quick_sort(ListAllAdvices, OrderedAdviceIDList),
                length(OrderedAdviceIDList, ListLength),
                minNumAdv(NumberAdvice, ListLength, MinNumber),
                filterByProfile( ListFilteredIds, OrderedAdviceIDList, MinNumber),
                getListFilteredAdvices( ListFilteredIds, ListFilteredAdvices ). */

        //runQ("consult('INTERNAL://com/ugos/jiprolog/resources/list.pl').");
        //runQ("reverse([1,2,3,4], X).");
        //runQ("getAllAdvices( end, _, ListAllAdvices).");
        runQ("qsort(1,[1,4,9,8], OrderedAdviceIDList).");
        //runQ("length([1,2,3,5,6], ListLength).");
        //runQ("minNumAdv(2, 1, MinNumber).");
        //runQ("getMostUrgentAdvice( "+n+", "+condition+", "+recordType+", MostUrgentAdvice).");
        return "";
    }

    public static ARBS_engine getARBS_Instance(){
        if(instance == null){
            instance = new ARBS_engine();
            try {
                instance.init_consult();
            } catch (Exception e) {
                Log.i(TAG, "runQ: "+ e.getMessage());
            }

        }
        return instance;
    }

    private void init_consult() throws Exception {

        runQ("consult('INTERNAL://com/ugos/jiprolog/resources/ARBS/mainRuleSystem.pl').");
        runQ("consult('INTERNAL://com/ugos/jiprolog/resources/ARBS/adviceQueries.pl').");
        runQ("consult('INTERNAL://com/ugos/jiprolog/resources/ARBS/medicalRules.pl').");

    }

    public void setARBSLanguage(String lang){
        runQ("setLanguage('"+lang+"').");
    }

    public void runQ(String goal){

        if (jipQuery == null || !goal.equals("y")) {
            JIPTerm queryTerm = null;

            // parse query
            try {
                queryTerm = jip.getTermParser().parseTerm(goal);
                jipQuery = jip.openSynchronousQuery(queryTerm);
            } catch (JIPSyntaxErrorException ex) {
                ex.printStackTrace();
            }
        }
        if (jipQuery == null)
            return;

        // check if there is another solution
        if (jipQuery.hasMoreChoicePoints()) {
            JIPTerm solution = null;
            try {
                solution = jipQuery.nextSolution();
            } catch (JIPRuntimeException ex) {
                Log.i(TAG, goal+" runQ: "+ ex.getMessage());
                jipQuery.close();
                jipQuery = null;
                return;
            } catch (Exception ex) {
                Log.i(TAG, goal+" runQ: "+ ex.getMessage());
                jipQuery.close();
                jipQuery = null;
                return;
            }

            if (solution == null) {
                Log.i(TAG, goal+" runQ: ANSWER = NO");
                jipQuery.close();
                jipQuery = null;
            } else {
                // Show Solution
                Log.i(TAG, goal+" runQ: ANSWER = YES");

                JIPVariable[] vars = solution.getVariables();

                for (int i = 0; i < vars.length; i++) {
                    if (!vars[i].isAnonymous()) {
                        Log.i(TAG, goal+" runQ: ANSWER = "+vars[i].getName() + " = " + vars[i].toString(jip));
                    }
                }

                if (!jipQuery.hasMoreChoicePoints()) {
                    jipQuery.close();
                    jipQuery = null;
                } else {
                    runQ("y");
                }
            }
        } else {
            jipQuery.close();
            jipQuery = null;
        }
    }
}
