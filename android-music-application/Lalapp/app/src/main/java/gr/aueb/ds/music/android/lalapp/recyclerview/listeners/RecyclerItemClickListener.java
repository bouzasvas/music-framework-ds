package gr.aueb.ds.music.android.lalapp.recyclerview.listeners;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerItemClickListener extends RecyclerView.SimpleOnItemTouchListener {
    private Context context;
    private RecyclerView recyclerView;
    private OnRecyclerClickListener listener;

    private GestureDetector gestureDetector = new GestureDetector(this.context, new GestureDetector.SimpleOnGestureListener(){
        @Override
        public void onLongPress(MotionEvent e) {
            View childView = recyclerView.findChildViewUnder(e.getX(), e.getY());
            if (childView == null) return;
            listener.onItemLongPress(childView, recyclerView.getChildAdapterPosition(childView));
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            View childView = recyclerView.findChildViewUnder(e.getX(), e.getY());
            if (childView == null) return false;

            listener.onItemClick(childView, recyclerView.getChildAdapterPosition(childView));
            return true;
        }
    });

    public RecyclerItemClickListener(Context context, RecyclerView recyclerView, OnRecyclerClickListener listener) {
        this.context = context;
        this.recyclerView = recyclerView;
        this.listener = listener;
    }

    @Override
    public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
        return gestureDetector.onTouchEvent(e);
    }

    public interface OnRecyclerClickListener {
        void onItemClick(View view, int position);
        default void onItemLongPress(View view, int position) {}
    }
}
