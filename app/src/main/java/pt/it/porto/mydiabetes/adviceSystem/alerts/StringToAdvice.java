package pt.it.porto.mydiabetes.adviceSystem.alerts;

import android.app.DialogFragment;
import android.content.Context;

//import pt.it.porto.mydiabetes.adviceSystem.yapDroid.YapDroid;

//import pt.it.porto.mydiabetes.adviceSystem.yapDroid.YapDroid;

public class StringToAdvice{

	public StringToAdvice(){}
	
	
	/**
	 * 
	 * Receives a String and returns the advice it represents
	 * 
	 * @param stringAdvice
	 * @param context
	 * @return
	 */
	public DialogFragment getAdviceFromString(String stringAdvice,Context context){
		
		//"type:msgExercicioMenos, gravidade:2,texto:Deve fazer mais exercicio!,popup:simples,opt:disable-1000-m"
		// text:cenas cenas cenas /popup:adviceTimer/test:Glycemia/time:600-s/
		String[] listRequest = stringAdvice.split("/");
		
		//-----types
		String messageType = null;
		String urgency = null;
		String message = null;
		String testType = null;
		String adviceType = null;
		int time = 0;
		String time_unit = "s";
		//-----options
		boolean toDisable = false;
		int timeDisabled = 0;
		String timeDisabledUnit = "s";
		boolean playSound = false;
		
		
		for(String subString:listRequest){
			String[] atributes = subString.split(":");
			String type = atributes[0];
			String value = atributes[1];
			
			switch(type){
			case "type":
				messageType = value;
				break;
			case "gravidade":
				urgency = value;
				break;
			case "text":
				message = value;
				break;
			case "popup":
				adviceType = value;
				break;
			case "test":
				testType = value;
				break;
			case "opt":
				String opt_type=atributes[0];
				String opt_value=atributes[1];
				switch(opt_type){
				case "disable":
					toDisable = true;
					String[] timeDisableDivider = opt_value.split("-");
					timeDisabled = Integer.parseInt(timeDisableDivider[0]);
					timeDisabledUnit=timeDisableDivider[1];
					break;
				case "sound":
					playSound = true;
					break;
				}
				break;
			case "time":
				String[] timeSpliter = value.split("-");
				time = Integer.parseInt(timeSpliter[0]);
				time_unit=timeSpliter[1];
				break;
			}
		}
		
		//System.out.println("type "+messageType+" urgency "+ urgency+" message "+message+" testType "+testType+" time "+time+" timeUnit "+time_unit);
		if(toDisable){
			//YapDroid.blockRule(messageType, timeDisabled, timeDisabledUnit);
		}
		
		switch(adviceType){
		case "simpleAdvice":
			
			SimpleAdvice advice = new SimpleAdvice();
			advice = advice.newInstance(message, urgency);
			//if(playSound){advice.setSound_efect_on(true);}
			return advice;
			
		case "suggestTest":
			
			AdviseATest advise_test = new AdviseATest();
			advise_test = advise_test.newInstance(message, urgency, testType, context);
			//new AdviseATest(message, urgency, testType, context);
			//if(playSound){advise_test.setSound_efect_on(true);}
			return advise_test;
			
		case "adviceTimer":
			
			AlertWithTimer timed_advise_test = new AlertWithTimer();
			//Calendar calendar = YapDroid.string2Time(time,time_unit);

			//System.out.println("Msg: "+message+", Urgency: "+urgency+", TestType: "+testType+", context "+context+", time "+time+" , timeU: "+YapDroid.string2Unit(time_unit));
			//timed_advise_test = timed_advise_test.newInstance(message, urgency, testType, context, calendar);
			//if(playSound){timed_advise_test.setSound_efect_on(true);}
			return timed_advise_test;
		}
		return null;
		
	}
	
}