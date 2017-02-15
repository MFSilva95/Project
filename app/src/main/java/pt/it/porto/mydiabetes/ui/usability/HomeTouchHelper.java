package pt.it.porto.mydiabetes.ui.usability;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import pt.it.porto.mydiabetes.ui.listAdapters.HomeAdapter;
import pt.it.porto.mydiabetes.utils.HomeElement;

/**
 * Created by Diogo on 11/05/2016.
 */
public class HomeTouchHelper extends ItemTouchHelper.SimpleCallback {

    private HomeAdapter homeAdapter;

    public HomeTouchHelper(HomeAdapter homeAdapter){
        super(ItemTouchHelper.RIGHT | ItemTouchHelper.RIGHT, ItemTouchHelper.RIGHT| ItemTouchHelper.RIGHT);
        this.homeAdapter = homeAdapter;
    }



    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        //TODO: Not implemented here
        return false;
    }

    @Override
    public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        HomeAdapter.ViewHolder thisHolder = (HomeAdapter.ViewHolder) viewHolder;
        if (homeAdapter.getFromHomeList(thisHolder.getAdapterPosition()).getDisplayType().equals(HomeElement.Type.LOGITEM) ||
                homeAdapter.getFromHomeList(thisHolder.getAdapterPosition()).getDisplayType().equals(HomeElement.Type.HEADER) ||
                homeAdapter.getFromHomeList(thisHolder.getAdapterPosition()).getDisplayType().equals(HomeElement.Type.SPACE)) return 0;
        return super.getSwipeDirs(recyclerView, viewHolder);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        //Remove item
        HomeAdapter.ViewHolder thisHolder = (HomeAdapter.ViewHolder) viewHolder;
        if (homeAdapter.getFromHomeList(thisHolder.getAdapterPosition()).getDisplayType().equals(HomeElement.Type.ADVICE) || homeAdapter.getFromHomeList(thisHolder.getAdapterPosition()).getDisplayType().equals(HomeElement.Type.TASK)){
            if(direction==ItemTouchHelper.RIGHT) {
                homeAdapter.remove(viewHolder.getAdapterPosition());
            }
        }
    }
}
