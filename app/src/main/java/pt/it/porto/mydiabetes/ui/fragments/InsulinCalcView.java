package pt.it.porto.mydiabetes.ui.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import pt.it.porto.mydiabetes.BuildConfig;
import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.utils.LocaleUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class InsulinCalcView extends LinearLayout {


    private static final String ARG_FACTOR_GLYCEMIA = "Args_factor_glycemia";
    private static final String ARG_FACTOR_CARBS = "Args_factor_carbs";

    private TextView correctionGlycemia;
    private TextView correctionCarbs;
    private TextView insulinOnBoard;
    private TextView resultTotal;
    private TextView resultRound;
    private LinearLayout blockIOB;
    private int iRatio;
    private int cRatio;


    public InsulinCalcView(Context context, int iRatio, int cRatio) {
        super(context);
        this.iRatio = iRatio;
        this.cRatio = cRatio;
        init();
    }


    public void init() {
        inflate(getContext(), R.layout.fragment_insulin_meal_calc, this);
        this.resultRound = (TextView) findViewById(R.id.result_round);
        this.resultTotal = (TextView) findViewById(R.id.result_total);
        this.insulinOnBoard = (TextView) findViewById(R.id.insulin_on_board);
        this.correctionCarbs = (TextView) findViewById(R.id.correction_carbs);
        this.correctionGlycemia = (TextView) findViewById(R.id.correction_glycemia);
        this.blockIOB = (LinearLayout) findViewById(R.id.block_iob);

        if(!BuildConfig.IOB_AVAILABLE){
            this.blockIOB.setVisibility(View.GONE);
        }
        TextView txtView;
        if(cRatio != -1){
            txtView = (TextView) findViewById(R.id.txt_correction_carbs);
            txtView.setText(String.format(txtView.getText().toString(), cRatio));
        }
        if(iRatio != -1){
            txtView = (TextView) findViewById(R.id.txt_correction_glycemia);
            txtView.setText(String.format(txtView.getText().toString(), iRatio));
        }
    }


    public void setResult(float result, float resultRound) {
        this.resultTotal.setText(String.format(LocaleUtils.ENGLISH_LOCALE, "%.1f", result));// using ENGLISH locale to have dot instead of comma
        this.resultRound.setText(String.format(LocaleUtils.ENGLISH_LOCALE, "(%.1f)", resultRound > 0 ? resultRound : 0)); // if lower than one, the recommendation is 0
    }

    public void setInsulinOnBoard(float insulinOnBoard) {
        if(!BuildConfig.IOB_AVAILABLE){
            return;
        }
        if(Float.compare(insulinOnBoard, 0)==0){
            this.blockIOB.setVisibility(View.GONE);
        }else {
            this.insulinOnBoard.setText(String.format(LocaleUtils.ENGLISH_LOCALE, "%.1f", insulinOnBoard));
            this.blockIOB.setVisibility(View.VISIBLE);
        }
    }

    public void setCorrectionCarbs(float correctionCarbs) {
        this.correctionCarbs.setText(String.format(LocaleUtils.ENGLISH_LOCALE, "%.1f", correctionCarbs));
    }

    public void setCorrectionGlycemia(float correctionGlycemia) {
        this.correctionGlycemia.setText(String.format(LocaleUtils.ENGLISH_LOCALE, "%.1f", correctionGlycemia));
    }
}
