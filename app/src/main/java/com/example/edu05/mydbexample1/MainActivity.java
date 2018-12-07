package com.example.edu05.mydbexample1;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private MemoAdapter madapter;
    public static final int REQUEST_CODE_INSERT = 1000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(MainActivity.this,MemoActivity.class),REQUEST_CODE_INSERT);
            }
        });
        ListView listView = findViewById(R.id.memo_list);

        Cursor cursor = getMemoCursor();
        madapter = new MemoAdapter(this,cursor);
        listView.setAdapter(madapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this,MemoActivity.class);

                Cursor cursor = (Cursor)madapter.getItem(position);

                String title = cursor.getString(cursor.getColumnIndexOrThrow(MemoContract.MemoEntry.COLUMN_NAME_TITLE));
                String contents = cursor.getString(cursor.getColumnIndexOrThrow(MemoContract.MemoEntry.COLUMN_NAME_CONTENTS));


                intent.putExtra("id",id);
                intent.putExtra("title",title);
                intent.putExtra("contents",contents);

                startActivityForResult(intent,REQUEST_CODE_INSERT);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final long deleteId = id;
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("메모삭제");
                builder.setMessage("메모를 삭제 하세겠습니까?");
                builder.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SQLiteDatabase db = MemoDbHelper.getsInstance(MainActivity.this).getWritableDatabase();
                        db.delete(MemoContract.MemoEntry.TABLE_NAME,
                                MemoContract.MemoEntry._ID +"="+deleteId,null);
                    }
                });
                builder.setNegativeButton("취소",null);
                builder.show();
                return true;
            }
        });



    }//커서에 받은것을 리스트 뷰에 뿌리기 위해서는 커서 어댑터가 필요하다.


    private Cursor getMemoCursor() {
        MemoDbHelper dbHelper = MemoDbHelper.getsInstance(this);
        return dbHelper.getReadableDatabase().query(MemoContract.MemoEntry.TABLE_NAME,
                null, null, null, null, null,MemoContract.MemoEntry._ID
        +" DESC");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_INSERT && requestCode == RESULT_OK){
            madapter.swapCursor(getMemoCursor());  //최신 데이터로 swap변경시킴
        }
    }

    private  static class  MemoAdapter extends CursorAdapter{

           public MemoAdapter(Context context, Cursor c) {
               super(context, c, false);
           }

           @Override
           public View newView(Context context, Cursor cursor, ViewGroup parent) {
               return LayoutInflater.from(context).inflate(R.layout.simple_list_test1,parent,false);
           }

           @Override
           public void bindView(View view, Context context, Cursor cursor) {
               TextView titleText = view.findViewById(R.id.text1);
               titleText.setText(cursor.getString(cursor.getColumnIndexOrThrow(MemoContract.MemoEntry.COLUMN_NAME_TITLE)));
           }
       }
    }

