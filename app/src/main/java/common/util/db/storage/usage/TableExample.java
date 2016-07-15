/**
 * The MIT License (MIT)
 * <p/>
 * DroidStorageLibrary - Copyright (c) 2016 Riccata2010
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package common.util.db.storage.usage;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import common.util.db.storage.storagelibrary.Common;
import common.util.db.storage.storagelibrary.StorageException;
import common.util.db.storage.storagelibrary.Table;

public class TableExample implements Table {

    public static final String TABLE_EXAMPLE            = "TABLE_EXAMPLE";
    public static final String ID                       = "ID";
    public static final String TEXT_APP                 = "TEXT_APP";
    public static final String VALUE_APP                = "VALUE_APP";
    public static final String KEY_APP                  = "KEY_APP";
    public static final String NOTE_APP                 = "NOTE_APP";

    public static final String[] COLUMNS = new String[] { ID, TEXT_APP, VALUE_APP, KEY_APP, NOTE_APP };

    private final String CREATE_TABLE_EXAMPLE = "CREATE TABLE IF NOT EXISTS " + TABLE_EXAMPLE +
            " (" + ID   + " integer primary key autoincrement, "
            + TEXT_APP  + " text not null, "
            + VALUE_APP + " text not null, "
            + KEY_APP   + " text not null, "
            + NOTE_APP  + " text not null);";


    @Override
    public String gatTableName() {
        return TABLE_EXAMPLE;
    }

    @Override
    public String getCreateTableQuery() {
        return CREATE_TABLE_EXAMPLE;
    }

    @Override
    public String[] getColumns() {
        return COLUMNS;
    }

    @Override
    public void wipe(SQLiteDatabase database) throws StorageException {
        database.execSQL("DELETE FROM " + TABLE_EXAMPLE);
    }

    @Override
    public void insert(SQLiteDatabase database, Row row) throws StorageException {

        ContentValues content = new ContentValues();

        for (Cell<String> cell : row) {
            content.put(cell.getKey(), cell.getValue());
        }

        long ris = database.insert(TABLE_EXAMPLE, null, content);
        Common.log("TABLE_EXAMPLE - result of insert is: " + ris);
    }

    @Override
    public Cursor all(SQLiteDatabase database) throws StorageException {
        Cursor cursor = database.query(TABLE_EXAMPLE, COLUMNS, null, null, null, null, null);
        return cursor;
    }

    @Override
    public int count(SQLiteDatabase database) throws StorageException {
        return Table.DefaultCounter.count(database, TABLE_EXAMPLE);
    }

    @Override
    public <String> boolean delete(SQLiteDatabase database, Cell<String> cell) throws StorageException {
        return false;
    }

    @Override
    public <String> Cursor query(SQLiteDatabase database, Cell<String> cell) throws StorageException {
        return null;
    }

    @Override
    public <String> void update(SQLiteDatabase database, Cell<String> cell) throws StorageException {
    }

    @Override
    public Row get(Cursor cursor) {
        Row row = null;
        if (cursor != null) {
            row = new Row();
            for (int i = 0; i < this.getColumns().length; i++) {
                try {

                    Cell<String> cell = new Cell<String>();
                    cell.setKey(this.getColumns()[i]);

                    switch(i) {
                        case 0: {
                            cell.setValue(String.valueOf(cursor.getLong(0)));
                            break;
                        }
                        default: {
                            String value = cursor.getString(i);
                            cell.setValue(value != null ? value : "");
                        }
                    }
                    row.addCell(cell);
                } catch (Exception exc) {
                    exc.printStackTrace();
                }
            }
        } else {
            Common.log("SETTINGS TABLE - cursor is null");
        }
        return row;
    }
}
