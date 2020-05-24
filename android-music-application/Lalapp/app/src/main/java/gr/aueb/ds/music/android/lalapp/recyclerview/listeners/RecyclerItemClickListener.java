package gr.aueb.ds.music.android.lalapp.recyclerview.listeners;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class RecyclerItemClickListener extends RecyclerView.SimpleOnItemTouchListener {
    private Context context;
    private RecyclerView recyclerView;
    private OnRecyclerClickListener listener;

    private GestureDetector gestureDetector = new GestureDetector(this.context, new GestureDetector.SimpleOnGestureListener(){
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
    }
}
