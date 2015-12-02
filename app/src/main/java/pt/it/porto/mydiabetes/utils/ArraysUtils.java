package pt.it.porto.mydiabetes.utils;

import java.util.ArrayList;

public class ArraysUtils {

	public static int min(ArrayList<Integer> array){
		int result=Integer.MAX_VALUE;
		for (int i = 0; i < array.size(); i++) {
			if(array.get(i)<result){
				result=array.get(i);
			}
		}
		return result;
	}
}
