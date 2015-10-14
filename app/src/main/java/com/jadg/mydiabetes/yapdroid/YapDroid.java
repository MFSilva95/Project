package com.jadg.mydiabetes.yap2android;

import java.util.Vector;
//import java.util.concurrent.Executors;
//import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class YapDroid{
	
	int flag;
	//private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	
	public YapDroid(){
		flag = 0;
	}
	/**
	 * Inserts a new rule in the prolog file.
	 */
	public static void insertRule(String rule){
		
	}
	/**
	 * Removes a rule of the prolog file.
	 */
	public static void removeRule(String rule){
		
	}
	/**
	 * Blocks an prolog @param rule for @param timeBlocked seconds
	 */
	public static void blockRule(String rule,int timeBlocked, String unit ){
		/*insertBlock(rule);
		final String tempString=rule;
		 scheduler.schedule( new Runnable() {
 			public void run() {
 				removeRule(tempString);
 			}
 		}, timeBlocked, string2Unit(unit));*/
	}
	public static TimeUnit string2Unit(String unit) {
		switch(unit){
		case "s":
			return TimeUnit.SECONDS;
		case "m":
			return TimeUnit.MINUTES;
		case "h":
			return TimeUnit.HOURS;
		}
		return null;
	}
	/**
	 * Inserts a block so @param rule doenst run
	 * 
	 */
	public static void insertBlock(String rule){
		
	}
	/**
	 * Gets the more urgent advice from the prolog
	 *  
	 * @return String
	 */
	public static String getYapAdvice(){
		return null;
	}
	/**
	 * Gets the tasks the user has to do
	 * from the RuledBasedSystem
	 * @return
	 */
	public static Vector<String> geAllTasks(){
		return null;
	}
	public static String[] getPersonalizedTasks(){
		String[] allTasks = new String[] { "Fazer exercicio", "Testar glicose", "Tomar insulina", "Fazer Refeições"};
		return allTasks;
	}
}