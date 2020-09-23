package com.example.thearena.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.example.thearena.Classes.User;
import com.example.thearena.Utils.Constants;

public class InnerDatabaseHandler extends SQLiteOpenHelper {
    private static final String TAG = "SQL";

    public InnerDatabaseHandler(Context context) {
        super(context, Constants.DATABASE_NAME, null, Constants.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.d(TAG, "Creating Database & table");
        String CREATE_TABLE = "CREATE TABLE "
                + Constants.TABLE_NAME
                + " ("
                + Constants.KEY_ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + Constants.KEY_EMAIL
                + " TEXT,"
                + Constants.KEY_PASSWORD
                + " TEXT"
                + ");";


        Log.d(TAG, CREATE_TABLE);
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        String DROP_TABLE = String.valueOf("DROP TABLE IF EXISTS");
        sqLiteDatabase.execSQL(DROP_TABLE, new String[]{Constants.TABLE_NAME});

        onCreate(sqLiteDatabase);
    }

    /*
        CRUD: addUser, getUserEmail, updateUserPassword , dropTables.
        addUser : this function making sure that there always one person inside the inner user table.
                    if it has already a user it's making sure to update what it's necessary.
        getUserEmail : this function read the Email from the inner user table.
        updateUserPassword : this function update the user password when it been called.
        dropTables : right now it is not in use, this function is deleting the table.
     */
    public void addUser(String email, String hashedPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Constants.KEY_EMAIL, email);
        values.put(Constants.KEY_PASSWORD, hashedPassword);
        Log.d(TAG, "addUser: "+email);
        if (getUsersCount() == 0){
            db.insert(Constants.TABLE_NAME, null, values);
        }
        else{
            db.update(Constants.TABLE_NAME,values,"id =? ",new String[]{"1"});
        }
        db.close(); //closing db connection
    }

    private void dropTables() {
        String DROP_TABLES = "DROP TABLE IF EXISTS "+Constants.TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL(DROP_TABLES);
        onCreate(db);
        db.close();
    }

//    public User getUserFromInnerDb(int id) {
//        SQLiteDatabase db = this.getReadableDatabase();
//
//        Cursor cursor = db.query(Constants.TABLE_NAME
//                , new String[]{Constants.KEY_EMAIL, Constants.KEY_PASSWORD}
//                , Constants.KEY_ID + "=?"
//                , new String[]{String.valueOf(id)}
//                , null, null, null);
//
//        if (cursor != null)
//            cursor.moveToFirst();
//
//        assert cursor != null;
//        return new User(cursor.getString(1));
//    }

    public int getUsersCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectAll = "SELECT * FROM " + Constants.TABLE_NAME;
        Cursor cursor = db.rawQuery(selectAll, null);
        return cursor.getCount();
    }

    public String getUserEmail(){
        String QUERY = "SELECT email FROM "+Constants.TABLE_NAME +";";
        SQLiteDatabase db = this.getReadableDatabase() ;

        Cursor cursor = db.rawQuery(QUERY,null);
        if (cursor != null)
            cursor.moveToFirst();

        assert cursor != null;
        return cursor.getString(0);
    }

    public void updateUserPassword(String newHashedPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Constants.KEY_PASSWORD, newHashedPassword);

        db.update(Constants.TABLE_NAME
                , values
                , Constants.KEY_ID + " =?"
                , new String[]{"1"});
    }
}
