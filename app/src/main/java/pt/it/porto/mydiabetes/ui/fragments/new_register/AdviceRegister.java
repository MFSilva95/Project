package pt.it.porto.mydiabetes.ui.fragments.new_register;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.res.ResourcesCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import lecho.lib.hellocharts.model.Line;
import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.data.Advice;
import pt.it.porto.mydiabetes.data.CarbsRec;
import pt.it.porto.mydiabetes.ui.activities.NewHomeRegistry;
import pt.it.porto.mydiabetes.utils.ImageUtils;

/**
 * Created by Diogo on 06/07/2017.
 */

public class AdviceRegister extends LinearLayout{

    Advice myAdvice;
    TextView textPanel;

    public AdviceRegister(Context context, Advice advice) {
        super(context);
        myAdvice = advice;
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.dialog_exp_advice, this);
        this.textPanel = (TextView) findViewById(R.id.popup_text);
        textPanel.setText(myAdvice.getExpandedText());
    }








































































}
