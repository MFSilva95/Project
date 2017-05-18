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

public class InsulinCalcView extends LinearLayout {

    private LinearLayout blockIOB;
    FragmentInsulinMealCalcBinding binding;


    public InsulinCalcView(Context context) {
        super(context);
        init();
    }


    public void init() {
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.fragment_insulin_meal_calc, this, true);
        this.blockIOB = (LinearLayout) findViewById(R.id.block_iob);

        if (!BuildConfig.IOB_AVAILABLE) {
            this.blockIOB.setVisibility(View.GONE);
        }
    }

    public void setInsulinCalculator(InsulinCalculator calculator) {
        binding.setInsulinCalc(calculator);
        binding.executePendingBindings();
    }
}
