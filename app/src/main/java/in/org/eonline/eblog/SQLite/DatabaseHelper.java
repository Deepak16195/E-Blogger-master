package in.org.eonline.eblog.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import in.org.eonline.eblog.Models.BlogModel;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "EBlog.db";
    public static final String TABLE_USER = "userProfile";
    public static final String col_1 = "firstName";
    public static final String col_2 = "lastName";
    public static final String col_3 = "email";
    public static final String col_4 = "mobileNumber";
    public static final String TABLE_BLOG = "userBlog";
    public static final String blog_col_1 = "blogId";
    public static final String blog_col_2 = "blogHeader";
    public static final String blog_col_3 = "blogContent";
    public static final String blog_col_4 = "blogFooter";


    private static final String SQL_REGISTER_USER =" Create TABLE " + TABLE_USER + "(" + col_1 + " TEXT, " + col_2 + " TEXT, " + col_3 + " TEXT, " + col_4 + " TEXT )" ;
    private static final String SQL_BLOG_ID =" Create TABLE " + TABLE_BLOG + "(" + blog_col_1 + " TEXT, " + blog_col_2 + " TEXT, " + blog_col_3 + " TEXT, " + blog_col_4 + " TEXT )" ;
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME,null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_REGISTER_USER);
        sqLiteDatabase.execSQL(SQL_BLOG_ID);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE  IF EXISTS " + SQL_REGISTER_USER);
        sqLiteDatabase.execSQL("DROP TABLE  IF EXISTS " + SQL_BLOG_ID);
    }

    public boolean insertUserDataInSQLite(String firstName, String lastName, String email , String mobileNumber){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(col_1, firstName);
        cv.put(col_2, lastName);
        cv.put(col_3, email);
        cv.put(col_4, mobileNumber);

        long result = db.insert(TABLE_USER, null, cv);
        if (result == -1)  // if result is -1 then we could not insert value in database table so we are returning false boolean
            return false;
        else
            return true;   // else we will return true boolean because result is not -1 so data is inserted successfully
    }

    public boolean insertBlogDataInSQLite(String blogId, String blogHeader, String blogContent, String blogFooter){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(blog_col_1, blogId);
        cv.put(blog_col_2, blogHeader);
        cv.put(blog_col_3, blogContent);
        cv.put(blog_col_4, blogFooter);

        long result = db.insert(TABLE_BLOG, null, cv);
        if (result == -1)  // if result is -1 then we could not insert value in database table so we are returning false boolean
            return false;
        else
            return true;   // else we will return true boolean because result is not -1 so data is inserted successfully
    }

     public Cursor retrieveBlogIdFromSqlite() {
        String selectQuery = " SELECT " + "*" + " FROM " + TABLE_BLOG;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        //if TABLE has rows
         List<BlogModel> blogDetailsList = new ArrayList<>();

        if (cursor.moveToFirst()) {
            //Loop through the table rows
            do {
                BlogModel model = new BlogModel();
                model.setBlogId(cursor.getString(0));
                //Add movie details to list
                blogDetailsList.add(model);
            } while (cursor.moveToNext());
        }
        db.close();
        return cursor;
    }

    /*public Cursor checkUserCredentials(String blogId) {
        String columns[] = {col_1, col_2};
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query("registeruser", columns, "email=? and password=?", new String[]{blogId}, null, null, null);
        return cursor;
    } */
}

