package pt.it.porto.mydiabetes.ui.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.ui.fragments.badges.BadgeBoard;
import pt.it.porto.mydiabetes.utils.LevelsPointsUtils;


/**
 * Created by Diogo on 24/07/2017.
 */

public class BadgeLayout{

    private ImageView badgeBackGroundHolder;
    private ImageView badgeIcon;
    private TextView badgeTextHolder;
    private String myBackgroundPath;
    private String myIconPath;
    private String myStringPath;
    private int mylvl;

    private BadgeBoard.Difficulty diff;
    private BadgeBoard.BadgeName name;
    private BadgeBoard.Marks mark;
    private boolean unlocked = false;
    private Context con;


    public BadgeLayout(Context context, BadgeBoard.Difficulty diff, BadgeBoard.BadgeName name, BadgeBoard.Marks mark, int lvl) {

        this.con = context;
        this.diff = diff;
        this.name = name;
        this.mark = mark;


        this.myBackgroundPath = "medal_"+mark.toString()+"_"+diff.toString();
        this.myIconPath = name.toString();
        this.myStringPath = diff.toString()+"_"+mark.toString()+"_"+name.toString();

        mylvl = lvl;
    }

    public View init() {

        View newBadge;
        LayoutInflater layoutInflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(mylvl< LevelsPointsUtils.BADGES_ADVANCED_UNLOCK_LEVEL){
            newBadge = layoutInflater.inflate(R.layout.badge_holder, null);
        }else{
            newBadge = layoutInflater.inflate(R.layout.badge_holder_advanced, null);
        }
        String TAG = "cenas";
        badgeTextHolder = (TextView) newBadge.findViewById(R.id.badge_text);
//        Log.i(TAG, "TEXT = "+myStringPath);
        badgeTextHolder.setText(con.getResources().getIdentifier(myStringPath,"string", con.getPackageName()));
        if(mylvl>0){
            badgeBackGroundHolder = (ImageView) newBadge.findViewById(R.id.badge_background);
            badgeIcon = (ImageView) newBadge.findViewById(R.id.badge_symbol);



//            Log.i(TAG, "BACKGROUND = "+myBackgroundPath);
//            Log.i(TAG, "ICON PATH = "+myIconPath);

            badgeBackGroundHolder.setImageResource(con.getResources().getIdentifier(myBackgroundPath,"drawable", con.getPackageName()));
            badgeIcon.setImageResource(con.getResources().getIdentifier(myIconPath,"drawable", con.getPackageName()));

        }
        return newBadge;
    }
}
