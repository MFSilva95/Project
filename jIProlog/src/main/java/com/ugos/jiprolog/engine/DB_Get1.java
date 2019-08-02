/*
 * 23/04/2014
 *
 * Copyright (C) 1999-2014 Ugo Chirico - http://www.ugochirico.com
 *
 * This is free software; you can redistribute it and/or
 * modify it under the terms of the Affero GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Affero GNU General Public License for more details.
 *
 * You should have received a copy of the Affero GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package com.ugos.jiprolog.engine;

import com.ugos.jiprolog.extensions.database.JDBCClausesDatabase;

import java.sql.SQLException;
import java.util.Hashtable;

final class DB_Get1 extends BuiltIn
{
    public final boolean unify(final Hashtable varsTbl)
    {

        int numberRecords;
        String tableName = (String) ((Atom) getParam(1)).getName();//getParam(1).getRealTerm()getRealTerm().toString();
        try{
            numberRecords = (int)((Expression) getParam(2)).getValue();
        }catch (Exception e){
            numberRecords = -1;
        }

        System.out.println("UNIFY_DB_GET: -- [1] "+tableName);
        System.out.println("UNIFY_DB_GET: -- [2] "+numberRecords);

        try {
            apply(tableName, numberRecords);
        } catch (SQLException e) {
            return false;
        }

        return true;
    }

    protected final void apply(String tableName, int nRec) throws SQLException {
        //System.out.println("Apply_Begin");

        JDBCClausesDatabase jipDB = null;
        try {
            jipDB = (JDBCClausesDatabase)Class.forName("com.ugos.jiprolog.extensions.database.JDBCClausesDatabase").newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        jipDB.setFunctor(tableName, 1);

        JIPEngine e = getJIPEngine();
        jipDB.setJIPEngine(e);
        jipDB.setAttributes(tableName,"jdbc:sqldroid:", e.getDbConn());
        jipDB._getResultSet_(nRec);

        System.out.println("NEW: db:"+tableName+"/"+jipDB.getArity());
        e.getGlobalDB().addClausesDatabase(jipDB, "db", tableName+"/"+jipDB.getArity());

        //System.out.println("Apply_END");
    }
}
