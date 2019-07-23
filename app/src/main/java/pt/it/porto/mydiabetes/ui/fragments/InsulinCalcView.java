package pt.it.porto.mydiabetes.ui.fragments;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import pt.it.porto.mydiabetes.BuildConfig;
import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.databinding.FragmentInsulinMealCalcBinding;
import pt.it.porto.mydiabetes.utils.InsulinCalculator;

/**
 * MAIN INSU CALC VIEW -
 */

public class InsulinCalcView extends LinearLayout {

    private LinearLayout blockIOB;
    FragmentInsulinMealCalcBinding binding;


    public InsulinCalcView(Context context) {
        super(context);
        init();
    }


    public void init() {
        if(binding==null){
            binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.fragment_insulin_meal_calc, this, true);
        }
        if (!BuildConfig.IOB_AVAILABLE) {
            if(blockIOB==null){
                this.blockIOB = (LinearLayout) findViewById(R.id.block_iob);
            }
            this.blockIOB.setVisibility(View.GONE);
        }
    }

    public void setInsulinCalculator(InsulinCalculator calculator) {
        binding.setInsulinCalc(calculator);
        binding.executePendingBindings();
    }
}
