//package pt.it.porto.mydiabetes.middleHealth.utils;
//
//import android.content.Context;
//import android.content.Intent;
//
//import pt.it.porto.mydiabetes.ui.activities.MealActivity;
//
//public class IntentBuilder {
//    public static Intent buildMealIntent(Context context, int measureId) {
//        Intent intent = new Intent(context, MealActivity.class);
//        intent.putExtra(MealActivity.BUNDLE_EXTRAS_GLYCEMIA_ID, measureId);
//        return intent;
//    }
//
////    public static Intent buildWeightDetailIntent(Context context, long measureId) {
////        Intent intent = new Intent(context, WeightDetail.class);
////        intent.putExtra(WeightDetail.EXTRAS_KEY_MEASURE_ID, String.valueOf(measureId));
////
////        return intent;
////    }
////
////    public static Intent buildBloodPressureDetailIntent(Context context, long measureId) {
////        Intent intent = new Intent(context, BloodPressureDetail.class);
////        intent.putExtra(BloodPressureDetail.EXTRAS_KEY_MEASURE_ID, String.valueOf(measureId));
////
////        return intent;
////    }
//}