package com.ugos.jiprolog.android;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.ugos.jiprolog.engine.JIPEngine;
import com.ugos.jiprolog.engine.JIPQuery;
import com.ugos.jiprolog.engine.JIPRuntimeException;
import com.ugos.jiprolog.engine.JIPSyntaxErrorException;
import com.ugos.jiprolog.engine.JIPTerm;
import com.ugos.jiprolog.engine.JIPVariable;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

import pt.it.porto.mydiabetes.utils.DbUtils;




public class JIProlog_call {
    private JIPEngine jip;
    private JIPQuery jipQuery;
    private String TAG = "JIPROLOG_CALL";

    public JIProlog_call(Context context, String JIPQuery){

        if(jip!=null){return;}

        long startTime=System.currentTimeMillis();
        jip = new JIPEngine(); //JIPrologFactory.newInstance(context);
        try {
            jip.setDbConn(getDbConnection(context));
        } catch (Exception e) {
            e.printStackTrace();
        }
        runQ("X=2.");
        runQ("db_get('activity_log', 5).");
        runQ("db:activity_log(A,B,C,D).");

        jip.releaseAllResources();

        Log.d(TAG, "Time taken in seconds:"+String.valueOf((System.currentTimeMillis()-startTime)/1000));
    }

    public void releaseAllRes(){
        if(jip!=null){
            jip.releaseAllResources();
            jip=null;}
    }

    public Connection getDbConnection(Context context) throws Exception {

        File db_file = DbUtils.get_database_file(context);
        if(!db_file.exists()){
            throw new Exception("database does not exist");
        }
        DriverManager.registerDriver((Driver) Class.forName("org.sqldroid.SQLDroidDriver").newInstance());
        String jdbcUrl = "jdbc:sqldroid:" + db_file.getPath();
        return DriverManager.getConnection(jdbcUrl);
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
                Log.i(TAG, "runQ: ");ex.getMessage();
                jipQuery.close();
                jipQuery = null;
                return;
            } catch (Exception ex) {
                Log.i(TAG, "runQ: ");ex.getMessage();
                jipQuery.close();
                jipQuery = null;
                return;
            }

            if (solution == null) {
                Log.i(TAG, "runQ: ANSWER = NO");
                jipQuery.close();
                jipQuery = null;
            } else {
                // Show Solution
                Log.i(TAG, "runQ: ANSWER = YES");

                JIPVariable[] vars = solution.getVariables();

                for (int i = 0; i < vars.length; i++) {
                    if (!vars[i].isAnonymous()) {
                        Log.i(TAG, "runQ: ANSWER = "+vars[i].getName() + " = " + vars[i].toString(jip));
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
