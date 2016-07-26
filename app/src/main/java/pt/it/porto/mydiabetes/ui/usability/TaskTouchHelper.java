package pt.it.porto.mydiabetes.ui.usability;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import pt.it.porto.mydiabetes.ui.listAdapters.AdviceAdapter;
import pt.it.porto.mydiabetes.ui.listAdapters.TaskAdapter;

/**
 * Created by Diogo on 11/05/2016.
 */
public class TaskTouchHelper extends ItemTouchHelper.SimpleCallback {

    private TaskAdapter taskAdapter;

    public TaskTouchHelper(TaskAdapter TaskAdapter){
        super(ItemTouchHelper.RIGHT | ItemTouchHelper.RIGHT, ItemTouchHelper.RIGHT| ItemTouchHelper.RIGHT);
        this.taskAdapter = TaskAdapter;
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
            taskAdapter.remove(viewHolder.getAdapterPosition());
        }
    }
}
