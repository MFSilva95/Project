package pt.it.porto.mydiabetes.utils;

public class DbUtils {
	public static String toString(String[] elements) {
		StringBuilder result = new StringBuilder(elements.length * 7); // assuming that 7 is the normal size of a column name in the database
		for (int i = 0; i < elements.length; i++) {
			result.append(elements[i]);
			if(i!=elements.length-1){
				result.append(',');
			}
		}
		return result.toString();
	}
}
