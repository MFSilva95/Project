package pt.it.porto.mydiabetes.ui.usability;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import pt.it.porto.mydiabetes.ui.listAdapters.AdviceAdapter;

/**
 * Created by Diogo on 11/05/2016.
 */
public class AdviceTouchHelper extends ItemTouchHelper.SimpleCallback {

    private AdviceAdapter adviceAdapter;

    public AdviceTouchHelper(AdviceAdapter adviceAdapter){
        super(ItemTouchHelper.RIGHT | ItemTouchHelper.RIGHT, ItemTouchHelper.RIGHT| ItemTouchHelper.RIGHT);
        this.adviceAdapter = adviceAdapter;
    }



    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        //TODO: Not implemented here
        return false;
    }


    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        //Remove item
        if(direction==ItemTouchHelper.RIGHT) {
            adviceAdapter.remove(viewHolder.getAdapterPosition());
        }
    }
}
