package com.vozeo.todolist;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private final List<Todo> todoList = new ArrayList<>();
    RecyclerView recyclerView;
    TodoAdapter adapter;
    private TextInputEditText taskText;
    private int mYear;
    private int mMonth;
    private int mDay;
    private int mHour, mMin;
    public TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int hour, int minute) {
            mHour = hour;
            mMin = minute;
            if (minute < 10) {
                todoTime = hour + ":" + "0" + minute;
            } else {
                todoTime = hour + ":" + minute;
            }
        }
    };
    private Calendar calendar;
    private String todoDate = null, todoTime = null;
    public DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            mYear = year;
            mMonth = monthOfYear;
            mDay = dayOfMonth;
            todoDate = year + "年" + (monthOfYear + 1) + "月" + dayOfMonth + "日";
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.todo_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new TodoAdapter(todoList);
        freshTodoList();

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                int swipeFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
                return makeMovementFlags(dragFlags, swipeFlags);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                adapter.notifyItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                adapter.getTodo(position).delete();
                freshTodoList();
            }
        });
        itemTouchHelper.attachToRecyclerView(recyclerView);

        LitePal.initialize(this);
        LitePal.getDatabase();

        calendar = Calendar.getInstance();
        getDateAndName();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab_add);
        fab.setOnClickListener(view -> showDialog());
    }

    protected void freshTodoList() {
        List<Todo> todos = LitePal.findAll(Todo.class);
        adapter.setTodoAdapter(todos);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.about)
            buildAboutDialog();
        else if (id == R.id.timer) {
            Intent intent = new Intent(MainActivity.this, TimerActivity.class);
            startActivity(intent);
        }
        return true;
    }

    private void getDateAndName() {
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDay = calendar.get(Calendar.DAY_OF_MONTH);
        mHour = calendar.get(Calendar.HOUR_OF_DAY);
        mMin = calendar.get(Calendar.MINUTE);
    }

    private void buildAboutDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
        dialog.setTitle("About")
                .setMessage("这是一个任务管理软件，作者是成笑行。\n" +
                        "左右滑动可以删除任务，长按可以拖动排序。\n" +
                        "单击时钟按钮可以开启一个番茄钟。\n" +
                        "祝使用愉快！o(*￣▽￣*)o~~~"
                )
                .setPositiveButton("OK", (thisDialog, which) -> thisDialog.dismiss())
                .show();
    }

    private void showDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.dialog_new_todo);
        bottomSheetDialog.show();
        bottomSheetDialog.setCancelable(false);
        taskText = bottomSheetDialog.findViewById(R.id.task);

        todoDate = null;
        todoTime = null;

        Calendar calendarTime = Calendar.getInstance();
        calendarTime.setTimeInMillis(System.currentTimeMillis());
        calendarTime.set(Calendar.YEAR, mYear);
        calendarTime.set(Calendar.MONTH, mMonth);
        calendarTime.set(Calendar.DAY_OF_MONTH, mDay);
        calendarTime.set(Calendar.HOUR_OF_DAY, mHour);
        calendarTime.set(Calendar.MINUTE, mMin);
        calendarTime.set(Calendar.SECOND, 0);

        Button button = bottomSheetDialog.findViewById(R.id.button_task);
        ImageButton dateButton = bottomSheetDialog.findViewById(R.id.todo_date);
        ImageButton timeButton = bottomSheetDialog.findViewById(R.id.todo_time);

        assert dateButton != null;
        dateButton.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this, onDateSetListener, mYear, mMonth, mDay);
            datePickerDialog.setCancelable(true);
            datePickerDialog.setCanceledOnTouchOutside(true);
            datePickerDialog.show();
        });

        assert timeButton != null;
        timeButton.setOnClickListener(v -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this, onTimeSetListener, mHour, mMin, true);
            timePickerDialog.setCancelable(true);
            timePickerDialog.setCanceledOnTouchOutside(true);
            timePickerDialog.show();
        });

        assert button != null;
        button.setOnClickListener(v -> {
            if (todoDate == null) {
                Toast.makeText(MainActivity.this, "没有设置日期", Toast.LENGTH_SHORT).show();
            } else if (todoTime == null) {
                Toast.makeText(MainActivity.this, "没有设置时间", Toast.LENGTH_SHORT).show();
            } else {
                String inputText = Objects.requireNonNull(taskText.getText()).toString();
                newTodo(inputText, todoDate, todoTime);
                Toast.makeText(MainActivity.this, todoDate + todoTime, Toast.LENGTH_SHORT).show();
                bottomSheetDialog.dismiss();
                freshTodoList();
            }
        });
    }

    private void newTodo(String title, String date, String time) {
        Todo todo = new Todo();
        todo.setTitle(title);
        todo.setDate(date);
        todo.setTime(time);
        todo.save();
    }
}