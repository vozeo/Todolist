package com.vozeo.todolist;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.ViewHolder> {

    private List<Todo> mTodoList;

    public TodoAdapter(List<Todo> todoList) {
        mTodoList = todoList;
    }
    public void setTodoAdapter(List<Todo> todoList) {
        mTodoList = todoList;
    }

    public List<Todo> getTodoList() {
        return mTodoList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_todo, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Todo todo = mTodoList.get(position);
        viewHolder.todoTitle.setText(todo.getTitle());
        viewHolder.todoDate.setText(todo.getDate());
        viewHolder.todoTime.setText(todo.getTime());
    }

    @Override
    public int getItemCount() {
        return mTodoList.size();
    }

    public Todo getTodo(int position) {
        return mTodoList.get(position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView todoTitle;
        private final TextView todoDate;
        private final TextView todoTime;

        public ViewHolder(View view) {
            super(view);
            todoTitle = (TextView) view.findViewById(R.id.todo_title);
            todoDate = (TextView) view.findViewById(R.id.todo_date);
            todoTime = (TextView) view.findViewById(R.id.todo_time);
        }
    }
}
