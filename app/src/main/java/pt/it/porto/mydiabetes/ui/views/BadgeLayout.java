package pt.it.porto.mydiabetes.ui.views;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import pt.it.porto.mydiabetes.R;


/**
 * Created by Diogo on 24/07/2017.
 */

public class BadgeLayout extends RelativeLayout {

    private ImageView badgeBackGroundHolder;
    private ImageView badgeIcon;
    private TextView badgeTextHolder;
    private String myBackgroundPath;
    private String myIconPath;
    private String myStringPath;
    private int mylvl;



    public BadgeLayout(Context context, String backgroundPath,String imgPath, String imgTxtPath, int lvl) {
        super(context);
        this.myBackgroundPath = backgroundPath;
        this.myIconPath = imgPath;
        this.myStringPath = imgTxtPath;
        mylvl = lvl;
        init();
    }

    private void init() {
        if(mylvl<3){
            inflate(getContext(), R.layout.badge_holder, this);
        }else{
            inflate(getContext(), R.layout.badge_holder_advanced, this);
        }
        if(mylvl>0){
            badgeBackGroundHolder = (ImageView) findViewById(R.id.badge_background);
            badgeIcon = (ImageView) findViewById(R.id.badgeType);
            badgeTextHolder = (TextView) findViewById(R.id.badge_text);

            badgeBackGroundHolder.setImageResource(getContext().getResources().getIdentifier(myBackgroundPath,"drawable", getContext().getPackageName()));
            badgeIcon.setImageResource(getContext().getResources().getIdentifier(myIconPath,"drawable", getContext().getPackageName()));

            badgeTextHolder.setText(getContext().getResources().getIdentifier(myStringPath,"strings", getContext().getPackageName()));
        }
    }
}
