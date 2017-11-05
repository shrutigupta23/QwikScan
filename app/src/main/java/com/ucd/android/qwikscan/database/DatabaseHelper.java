package com.ucd.android.qwikscan.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.ucd.android.qwikscan.model.Barcode;
import com.ucd.android.qwikscan.model.Order;
import com.ucd.android.qwikscan.model.Product;
import com.ucd.android.qwikscan.model.TransactionEntry;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 2;

    // Database Name
    private static final String DATABASE_NAME = "qwikScan";

    public DatabaseHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    /** Method to create all the required database tables on the phone
     ** @param db
     */

    private void createTables(SQLiteDatabase db){

        final String SQL_CREATE_PRODUCT_TABLE = "CREATE TABLE IF NOT EXISTS " +
                Product.TABLE_NAME + " ( " +
                Product.COLUMN_PID + " INTEGER NOT NULL UNIQUE, " +
                Product.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, " +
                Product.COLUMN_PRODUCT_PRICE + " INTEGER NOT NULL, " +
                "PRIMARY KEY(" + Product.COLUMN_PID + ") )";
        final String SQL_CREATE_BARCODE_TABLE = "CREATE TABLE IF NOT EXISTS " +
                Barcode.TABLE_NAME +" ( " +
                Barcode.COLUMN_ID + " VARCHAR NOT NULL UNIQUE, " +
                Barcode.COLUMN_PRODUCTID + " INTEGER NOT NULL, " +
                "PRIMARY KEY(" + Barcode.COLUMN_ID + ") )";

        final String SQL_CREATE_ORDER_TABLE = "CREATE TABLE IF NOT EXISTS " + Order.TABLE_NAME + "("+
                Order.PRODUCT_AMOUNT + "," +
                Order.PRODUCT_NAME +","+
                Order.QUANTITY + ","+
                Order.ORDER_ID + "," +
                " FOREIGN KEY ("+
                Order.ORDER_ID +") REFERENCES "+
                TransactionEntry.TABLE_NAME+"("+TransactionEntry.ORDER_ID +")"+
                ");";

        final String SQL_CREATE_TRANSACTION_TABLE = "CREATE TABLE IF NOT EXISTS "+TransactionEntry.TABLE_NAME + "("+
                TransactionEntry.ORDER_ID + " PRIMARY KEY " + ","+
                TransactionEntry.DATE + " DATE " + ","+
                TransactionEntry.TOTAL_AMOUNT +  ");";

        db.execSQL(SQL_CREATE_PRODUCT_TABLE);
        db.execSQL(SQL_CREATE_BARCODE_TABLE);
        db.execSQL(SQL_CREATE_TRANSACTION_TABLE);
        db.execSQL(SQL_CREATE_ORDER_TABLE);



    }

    /**
     * Insert the product data and barcode data into the tables
     * @param database
     */
    private void insertRequiredData(SQLiteDatabase database){
        insertRequiredProducts(database);
        insertRequiredBarcodes(database);
    }

    /**
     * Insert the product data into the table. Ideally this should come from server.
     * @param db
     */
    private void insertRequiredProducts(SQLiteDatabase db){
//        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from "+Product.TABLE_NAME , null);
        // db.close();
        if(!cursor.moveToFirst()) {

            //   db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(Product.COLUMN_PID, 1);
            values.put(Product.COLUMN_PRODUCT_NAME, "dark choco");
            values.put(Product.COLUMN_PRODUCT_PRICE, 25);

            // Inserting Row
            db.insert(Product.TABLE_NAME, null, values);
            values.clear();

            values.put(Product.COLUMN_PID, 4);
            values.put(Product.COLUMN_PRODUCT_NAME, "german salami");
            values.put(Product.COLUMN_PRODUCT_PRICE, 15);

            // Inserting Row
            db.insert(Product.TABLE_NAME, null, values);
            values.clear();

            values.put(Product.COLUMN_PID, 5);
            values.put(Product.COLUMN_PRODUCT_NAME, "Monster Beer");
            values.put(Product.COLUMN_PRODUCT_PRICE, 15);

            // Inserting Row
            db.insert(Product.TABLE_NAME, null, values);
            values.clear();

            values.put(Product.COLUMN_PID, 6);
            values.put(Product.COLUMN_PRODUCT_NAME, "chow mein sauce");
            values.put(Product.COLUMN_PRODUCT_PRICE, 25);

            // Inserting Row
            db.insert(Product.TABLE_NAME, null, values);
            values.clear();

            values.put(Product.COLUMN_PID, 2);
            values.put(Product.COLUMN_PRODUCT_NAME, "koka noodles");
            values.put(Product.COLUMN_PRODUCT_PRICE, 5);

            // Inserting Row
            db.insert(Product.TABLE_NAME, null, values);
            values.clear();

            values.put(Product.COLUMN_PID, 3);
            values.put(Product.COLUMN_PRODUCT_NAME, "kelkin popcorn");
            values.put(Product.COLUMN_PRODUCT_PRICE, 3);

            // Inserting Row
            db.insert(Product.TABLE_NAME, null, values);
            values.clear();

        }
    }
    /**
     * Insert the barcode data into the table. Ideally this should come from server.
     * @param db
     */
    private void insertRequiredBarcodes(SQLiteDatabase db){

        Cursor cursor = db.rawQuery("select * from "+Barcode.TABLE_NAME , null);
        if(!cursor.moveToFirst()) {

            ContentValues values = new ContentValues();
            values.put(Barcode.COLUMN_ID,"5052109996741");
            values.put(Barcode.COLUMN_PRODUCTID, 6);
            db.insert(Barcode.TABLE_NAME, null, values);
            values.clear();

            values.put(Barcode.COLUMN_ID, "5060335635488");
            values.put(Barcode.COLUMN_PRODUCTID, 5);
            db.insert(Barcode.TABLE_NAME, null, values);
            values.clear();

            values.put(Barcode.COLUMN_ID, "5000358553765");
            values.put(Barcode.COLUMN_PRODUCTID, 4);
            db.insert(Barcode.TABLE_NAME, null, values);
            values.clear();

            values.put(Barcode.COLUMN_ID, "5011032581332");
            values.put(Barcode.COLUMN_PRODUCTID, 3);
            db.insert(Barcode.TABLE_NAME, null, values);
            values.clear();

            values.put(Barcode.COLUMN_ID, "8888056103867");
            values.put(Barcode.COLUMN_PRODUCTID, 2);
            db.insert(Barcode.TABLE_NAME, null, values);
            values.clear();

            values.put(Barcode.COLUMN_ID, "5000189120563");
            values.put(Barcode.COLUMN_PRODUCTID, 1);
            db.insert(Barcode.TABLE_NAME, null, values);
            values.clear();

        }
    }
    /**
     * Method to drop all the tables
     * @param db
     */
    private void dropTables(SQLiteDatabase db){

        db.execSQL("DROP TABLE IF EXISTS " + Order.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TransactionEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Barcode.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Product.TABLE_NAME);

    }

    /**
     * Method to add a new transaction entry in the Transaction table
     * @param transactionEntry
     */
    public  void addTransactionEntry(TransactionEntry transactionEntry) {

        SQLiteDatabase db = this.getWritableDatabase();
        String insert_query = "INSERT INTO "+TransactionEntry.TABLE_NAME+
                " ("+TransactionEntry.DATE+","+
                TransactionEntry.TOTAL_AMOUNT+","+
                TransactionEntry.ORDER_ID+")"+
                "VALUES("+
                transactionEntry.getDate()+","+
                transactionEntry.getTotalAmount()+","+
                transactionEntry.getOrderId()+");";
        Log.d("sql",insert_query.toString());
        db.execSQL(insert_query);
        db.close();
    }

    /**
     * Method to add a new order entry
     * @param order
     */

    public void addOrderEntry(Order order){
        SQLiteDatabase db = this.getWritableDatabase();
        String midQuery = "(SELECT "+TransactionEntry.ORDER_ID+" FROM "+TransactionEntry.TABLE_NAME+" WHERE "+TransactionEntry.ORDER_ID+
                "='"+order.getOrderId()+"')";
        String insertOrderQuery =
                "INSERT INTO "+Order.TABLE_NAME+" ("+
                        Order.QUANTITY+","+
                        Order.ORDER_ID+","+
                        Order.PRODUCT_AMOUNT+","+
                        Order.PRODUCT_NAME+") VALUES (" +
                        "'" + order.getQuantity() +"'" +"," +
                        midQuery + "," +
                        "'" +  order.getProductAmount()+"'" +","+
                        "'" +  order.getProductName()+"'"+");";
        Log.d("order insert",insertOrderQuery);
        db.execSQL(insertOrderQuery);
        db.close();


    }

    /**
     * Method to get the product details for a barcode
     * @param i
     * @return
     */
    public Barcode getBarcodeDetails(String i) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("select a.* from "+ Product.TABLE_NAME+" AS a, "+Barcode.TABLE_NAME+" AS b where b.id = '"+ i +"' and a.pid = b.pid", null);

        if (cursor != null && cursor.moveToFirst()){
            Barcode cont = new Barcode(cursor.getString(0),cursor.getString(1), cursor.getString(2));
            // return contact
            cursor.close();
            return cont;
        }
        cursor = db.rawQuery("select * from "+Barcode.TABLE_NAME, null);
        if (cursor.moveToFirst()) {
            do {
                Log.d("BARCODE ",cursor.getString(0) + " " + cursor.getString(1));
            } while (cursor.moveToNext());
        }
        db.close();
        return null;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        createTables(sqLiteDatabase);

        insertRequiredData(sqLiteDatabase);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        dropTables(sqLiteDatabase);
        onCreate(sqLiteDatabase);
    }
}
