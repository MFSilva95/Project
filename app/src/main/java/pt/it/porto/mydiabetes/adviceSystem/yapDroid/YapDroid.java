package pt.it.porto.mydiabetes.adviceSystem.yapDroid;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;

import pt.it.porto.mydiabetes.data.Advice;
import pt.it.porto.mydiabetes.data.Task;
import pt.up.yap.app.CreateFiles;
import pt.up.yap.lib.YAPCallback;
import pt.up.yap.lib.YAPEngine;
import pt.up.yap.lib.YAPListTerm;
import pt.up.yap.lib.YAPQuery;
import pt.up.yap.lib.YAPTerm;


public class YapDroid {

	private static final String TAG = "YAPDroid";
	private static YapDroid myYap;

	private YAPEngine eng = null;
	private Boolean running = false, compute = true;
	private AssetManager mgr;
	private String yapFolderPath;
	private String mydiabetesFolderPath;

	public static YapDroid newInstance(Context con) {
		if (myYap == null) {
			myYap = new YapDroid(con);
		}
		return myYap;
	}

	class DoNotDeleteErrorHandler implements DatabaseErrorHandler {
		private static final String TAG = "DoNotDeleteErrorHandler";

		public void onCorruption(SQLiteDatabase dbObj) {
			Log.e(TAG, "Corruption reported by sqlite on database: " + dbObj.getPath());
		}
	}

	private YapDroid(Context con) {

		String s = null;
		try {
			PackageManager m = con.getPackageManager();
			s = con.getPackageName();
			PackageInfo p = m.getPackageInfo(s, 0);
			mgr = con.getAssets();

			System.loadLibrary("gmp");
			System.loadLibrary("Yap");
			CreateFiles.setupfiles(con, mgr);

		} catch (PackageManager.NameNotFoundException e) {
			Log.e(TAG, "Couldn't find package  information in PackageManager", e);
		}

		eng = new YAPEngine(null, con.getExternalFilesDir("/Yap/pl/boot.yap").getAbsolutePath());
		mydiabetesFolderPath = con.getExternalFilesDir("/mydiabetes").getAbsolutePath();

		String currentSystemLanguage = Locale.getDefault().getLanguage();

		runQuery("consult(['" +
				getMyDiabetesFilePath("mainRuleSystem.pl") +
				"','" +
				getMyDiabetesFilePath("medicalRules.pl") +
				"', '" +
				getMyDiabetesFilePath("adviceQueries.pl") +
				"', '" +
				getMyDiabetesFilePath("langFiles/advice_msg_" + currentSystemLanguage + ".pl") +
				"']).", false);
	}

	public void turnONtrace(){
		String str = "trace.";

		YAPQuery q = null;
		YAPListTerm vs0;
		Boolean rc = true;

		q = eng.query(str);
		Log.i(TAG, "Query: "+q.text());
	}

	/**
	 * Gets the more urgent advice from the prolog
	 *
	 * @return String
	 */
	public ArrayList<Advice> getAllEndAdvices(Context con) {// buscar a lista de advices;


		/*String str = "testeMedico(R).";

		YAPQuery q = null;
		YAPListTerm vs0;
		Boolean rc = true;

		q = eng.query(str);
		Log.i(TAG, "Query: "+q.text());
		return null;*/

			ArrayList<Advice> adviceListFromYap = getAllAdvices("end"," _ ",con);
			Collections.sort(adviceListFromYap);
			return adviceListFromYap;
	}
	/**
	 * Gets the more urgent advice from the prolog
	 *
	 * @return String
	 */
	public ArrayList<Task> getYapMultipleTasks() {// buscar a lista de advices;

		ArrayList<Task> taskListFromYap = getAllTasks();
		Collections.sort(taskListFromYap);
		return taskListFromYap;
	}

	private ArrayList<String> runQuery(String str, Boolean more) {

		YAPQuery q = null;
		//YAPListTerm vs;
		//String result;
		YAPListTerm vs0;
		ArrayList<String> solutions = new ArrayList<>();
		try {
			Log.i("CENAS", "Q= " + str);
			// check if at initial query
			if (running) {
				if (q != null) q.close();
			}
			q = eng.query(str);
			// get the uninstantiated query variables.
			vs0 = q.namedVars();
			running = true;
			// start computing
			compute = true;
			Boolean rc = true;
			if (vs0.nil()) {
				if (compute && (rc = q.next())) {
					Log.i("YAPC", "yes\n:");
					running = compute = more;
				} else {
					Log.i("YAPC", "no\n");
					running = false;
					compute = false;
				}
			} else {
				while (compute && (rc = q.next())) {
					YAPListTerm vs = q.namedVars();
					while (!vs.nil()) {
						try {
							String result = vs.car().text();
							Log.i("RESULT: ", "THIS         :" + result);
								/*String realResult = result.substring(2);
								solutions.add(realResult);*/
							YAPTerm eq = vs.car();
							String cenas = eq.getArg(1).text() + " = " + eq.getArg(2).text();
							Log.i("CENAS NCEASD ASDAOSKD", "THIS         :" + cenas);
							vs = vs.cdr();
						} catch (Exception e) {

						}
					}
					compute = more;
				}
				return solutions;
			}
			if (!rc) {
				Log.i("YAPC", "no\n");
				if (q != null)
					q.close();
				q = null;
				compute = false;
				running = false;
			}
		} catch (Exception e) {
			Log.i("YAPC", "Exception thrown  :" + e);
			if (q != null)
				q.close();
			compute = true;
			running = false;
		}
		return null;
	}

	private ArrayList<Task> getAllTasks() {

		String str = "masterRule(task, TaskList).";

		YAPQuery q = null;
		YAPListTerm vs0;
		Boolean rc = true;
		ArrayList<Task> taskList = new ArrayList<>();

		q = eng.query(str);
		while (rc = q.next()) {
			YAPListTerm vs = q.namedVars();

			while (!vs.nil()) {
				YAPTerm eq = vs.car();
				YAPTerm yapResult = eq.getArg(2);

				if (yapResult.isList()) {

					YAPTerm resultHead = yapResult;
					YAPTerm resultRest = resultHead;

					do{
						resultHead = resultRest.getArg(1);
						resultRest = resultRest.getArg(2);

						String teste = resultHead.text();

						Task newTask = new Task();
						int index = 1;

						while (!resultHead.text().equals("[]")) {

							YAPTerm headRest = resultHead.getArg(2);

							switch (index) {
								case 1:
									newTask.setSummaryText(resultHead.getArg(1).text());
									break;
								case 2:
									newTask.setExpText(resultHead.getArg(1).text());
									break;
								case 3:
									if (!resultHead.getArg(1).text().equals("null")) {

										YAPTerm alarm = resultHead.getArg(1);

										String arg1 = alarm.getArg(1).text();
										String arg2 = alarm.getArg(2).getArg(1).text();
										String arg3 = alarm.getArg(2).getArg(2).getArg(1).text();

										String[] args = {arg1, arg2, arg3};
										newTask.setTaskArgs(args);
									}
									break;
								case 4:
									int urg = 0;
									try {
										urg = Integer.parseInt(resultHead.getArg(1).text());
									} catch (Exception e) {}
									newTask.setUrg(urg);
									break;
							}
							index++;
							resultHead = headRest;
						}
						taskList.add(newTask);

					}while(!resultRest.text().equals("[]"));
				}
				vs = vs.cdr();
			}
			return taskList;
		}
		return taskList;
	}

	public Advice getSingleAdvice(String condition, String type, Context con) {

		// masterRule(single_advice, 'start', 'Meal',X).
		String str = "masterRule( single_advice, "+condition+", "+type+", ListFilteredAdvices).";

		YAPQuery q = null;
		YAPListTerm vs0;
		Boolean rc = true;
		ArrayList<Advice> adviceList = new ArrayList<>();
		Advice newAdvice = null;

		q = eng.query(str);
		Log.i(TAG, "Query: "+q.text());

		while (rc = q.next()) {
			YAPListTerm vs = q.namedVars();

			while (!vs.nil()) {
				YAPTerm eq = vs.car();
				YAPTerm yapResult = eq.getArg(2);

				if (yapResult.isList()) {

//					msg('hadLowGlucose',  ['You have a low Glycemia value.', 'A new test will -be agended to check if your values have estabilized.',[adviceTimer,'alarmMessage',Glycemia,600,s], 9 ]).

					YAPTerm resultHead = yapResult;
					YAPTerm resultRest = resultHead;

					do{
						resultHead = resultRest.getArg(1);
						resultRest = resultRest.getArg(2);

						newAdvice = new Advice();
						int index = 1;

						while (!resultHead.text().equals("[]")) {

							YAPTerm headRest = resultHead.getArg(2);

							switch (index) {
								case 1:
									//Log.i(TAG, "sub1: "+resultHead.getArg(1).text());
									newAdvice.setSummaryText(resultHead.getArg(1).text());
									break;
								case 2:
									//Log.i(TAG, "sub2: "+resultHead.getArg(1).text());
									newAdvice.setExpandedText(resultHead.getArg(1).text());
									break;
								case 3:
									//Log.i(TAG, "sub3: "+resultHead.getArg(1).text());
									if (!resultHead.getArg(1).text().equals("null")) {

										YAPTerm alarm = resultHead.getArg(1);
										String arg1 = alarm.getArg(1).text();
										newAdvice.setType(arg1);

										String alarmMessage;
										String[] args;
										String newRegistryType;

										switch (arg1){
											case "SUGGESTION":
											case "QUESTION":
												newRegistryType = alarm.getArg(2).getArg(1).text();
												newAdvice.setRegistryType(newRegistryType);
												break;
											case "ALERT":
												//Log.i(TAG, "subsub1: "+arg1);
												alarmMessage = alarm.getArg(2).getArg(1).text();
												//Log.i(TAG, "subsub2: "+arg2);
												newRegistryType = alarm.getArg(2).getArg(2).getArg(1).text();
												//Log.i(TAG, "subsub3: "+arg3);
												String time = alarm.getArg(2).getArg(2).getArg(2).getArg(1).text();
												//Log.i(TAG, "subsub4: "+arg4);
												String timeU = alarm.getArg(2).getArg(2).getArg(2).getArg(2).getArg(1).text();
												//Log.i(TAG, "subsub5: "+arg5);
												args = new String[]{alarmMessage, newRegistryType, time, timeU};
												newAdvice.setNotification(args, con);
												break;
										}
									}
									break;
								case 4:
									int urg = 0;
									try {
										urg = Integer.parseInt(resultHead.getArg(1).text());
									} catch (Exception e) {}
									Log.i(TAG, "sub4: "+resultHead.getArg(1).text());
									newAdvice.setUrgency(urg);
									break;
							}
							index++;
							resultHead = headRest;
						}
						adviceList.add(newAdvice);

					}while(!resultRest.text().equals("[]"));
				}
				vs = vs.cdr();
			}
			return newAdvice;
		}
		return newAdvice;
	}

	private ArrayList<Advice> getAllAdvices(String condition, String type, Context con) {

		String str = "masterRule( multiple_advice, "+condition+", "+type+", ListFilteredAdvices).";

		YAPQuery q = null;
		YAPListTerm vs0;
		Boolean rc = true;
		ArrayList<Advice> adviceList = new ArrayList<>();

		q = eng.query(str);
		Log.i(TAG, "Query: "+q.text());

		while (rc = q.next()) {
			YAPListTerm vs = q.namedVars();

			while (!vs.nil()) {
				YAPTerm eq = vs.car();
				YAPTerm yapResult = eq.getArg(2);

				if (yapResult.isList()) {

//					msg('hadLowGlucose',  ['You have a low Glycemia value.', 'A new test will -be agended to check if your values have estabilized.',[adviceTimer,'alarmMessage',Glycemia,600,s], 9 ]).

					YAPTerm resultHead = yapResult;
					YAPTerm resultRest = resultHead;

					do{
						resultHead = resultRest.getArg(1);
						resultRest = resultRest.getArg(2);

						Advice newAdvice = new Advice();
						int index = 1;

						while (!resultHead.text().equals("[]")) {

							YAPTerm headRest = resultHead.getArg(2);

							switch (index) {
								case 1:
									//Log.i(TAG, "sub1: "+resultHead.getArg(1).text());
									newAdvice.setSummaryText(resultHead.getArg(1).text());
									break;
								case 2:
									//Log.i(TAG, "sub2: "+resultHead.getArg(1).text());
									newAdvice.setExpandedText(resultHead.getArg(1).text());
									break;
								case 3:
									//Log.i(TAG, "sub3: "+resultHead.getArg(1).text());
									if (!resultHead.getArg(1).text().equals("null")) {

										YAPTerm alarm = resultHead.getArg(1);
										String arg1 = alarm.getArg(1).text();
										newAdvice.setType(arg1);

										String alarmMessage;
										String[] args;
										String newRegistryType;

										switch (arg1){
											case "SUGGESTION":
											case "QUESTION":
												newRegistryType = alarm.getArg(2).getArg(1).text();
												newAdvice.setRegistryType(newRegistryType);
												break;
											case "ALERT":
												//Log.i(TAG, "subsub1: "+arg1);
												alarmMessage = alarm.getArg(2).getArg(1).text();
												//Log.i(TAG, "subsub2: "+arg2);
												newRegistryType = alarm.getArg(2).getArg(2).getArg(1).text();
												//Log.i(TAG, "subsub3: "+arg3);
												String time = alarm.getArg(2).getArg(2).getArg(2).getArg(1).text();
												//Log.i(TAG, "subsub4: "+arg4);
												String timeU = alarm.getArg(2).getArg(2).getArg(2).getArg(2).getArg(1).text();
												//Log.i(TAG, "subsub5: "+arg5);
												args = new String[]{alarmMessage, newRegistryType, time, timeU};
												newAdvice.setNotification(args, con);
												break;
										}
									}
									break;
								case 4:
									int urg = 0;
									try {
										urg = Integer.parseInt(resultHead.getArg(1).text());
									} catch (Exception e) {}
									Log.i(TAG, "sub4: "+resultHead.getArg(1).text());
									newAdvice.setUrgency(urg);
									break;
							}
							index++;
							resultHead = headRest;
						}
						adviceList.add(newAdvice);

					}while(!resultRest.text().equals("[]"));
				}
				vs = vs.cdr();
			}
			return adviceList;
		}
		return adviceList;
	}

	class JavaCallback extends YAPCallback {
		private static final String TAG = "JavaCallback";
		//TextView output;

		public JavaCallback(TextView outputText) {
			super();
			//output = outputText;
			Log.i(TAG, "java callback init");
		}

		public void run(String s) {
			Log.i(TAG, "java callback ");
			//output.append(s);
		}

	}

	public static Calendar string2Time(int time, String unit) {
		Calendar calendar = Calendar.getInstance();
		switch (unit) {
			case "s":
				calendar.setTimeInMillis(System.currentTimeMillis());
				calendar.add(Calendar.SECOND, time);
				return calendar;
			case "m":
				calendar.setTimeInMillis(System.currentTimeMillis());
				calendar.add(Calendar.MINUTE, time);
				//calendar.add(Calendar.SECOND, time);
				return calendar;
			case "h":
				calendar.setTimeInMillis(System.currentTimeMillis());
				calendar.add(Calendar.HOUR, time);
				//calendar.add(Calendar.SECOND, time);
				return calendar;
		}
		return null;
	}

	public static Calendar string2Time(String time) {
		Calendar calendar = Calendar.getInstance();
		String[] t = time.split(":");
		int timeV = Integer.parseInt(t[0]);
		switch (t[1]) {
			case "s":
				calendar.setTimeInMillis(System.currentTimeMillis());
				calendar.add(Calendar.SECOND, timeV);
				return calendar;
			case "m":
				calendar.setTimeInMillis(System.currentTimeMillis());
				calendar.add(Calendar.MINUTE, timeV);
				//calendar.add(Calendar.SECOND, time);
				return calendar;
			case "h":
				calendar.setTimeInMillis(System.currentTimeMillis());
				calendar.add(Calendar.HOUR, timeV);
				//calendar.add(Calendar.SECOND, time);
				return calendar;
		}
		return null;
	}


	/**
	 * Inserts a new rule in the prolog file.
	 */
	public void insertRule(String fact) {
		runQuery("assert("+fact+").", false);
	}

	/**
	 * Removes a rule of the prolog file.
	 */
	public void removeRule(String fact) {
		runQuery("retract("+fact+").", false);
	}
	/**
	 * Inserts a block so @param rule doenst run
	 */
	public void insertBlock(String rule) {}
	/**
	 * Blocks an prolog @param rule for @param timeBlocked seconds
	 */
	public void blockRule(String rule, int timeBlocked, String unit) {
		/*insertBlock(rule);
		final String tempString=rule;
		 scheduler.schedule( new Runnable() {
 			public void run() {
 				removeRule(tempString);
 			}
 		}, timeBlocked, string2Unit(unit));*/
	}

	private String getMyDiabetesFilePath(String fileName) {
		String myFilePath = mydiabetesFolderPath + "/" + fileName;
		return myFilePath;
	}


}