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
package common.util.db.storage.storagelibrary;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Driver {

    private static Driver instance = null;
    private static Map<Class, Table> table_map = new HashMap<Class, Table>();

    private Context context = null;
    private String db_name = null;
    private SQLiteDatabase.CursorFactory factory = null;
    private int db_version = 1;
    private SQLiteStorageHelper helper = null;
    private Lock lock = new ReentrantLock();
    private SQLiteDatabase database = null;

//------------------------------------------------------------------------------------------------//
//------------------------------------------------------------------------------------------------//
//------------------------------------------------------------------------------------------------//

    private Driver() {
    }

//------------------------------------------------------------------------------------------------//
//------------------------------------------------------------------------------------------------//
//------------------------------------------------------------------------------------------------//

    public static Driver getSingleDriverInstance() {
        return instance == null ? instance = new Driver() : instance;
    }

    public Driver onTable(Class class_table, Table table_instance) {
        table_map.put(class_table, table_instance);
        Common.log("{onTable} - table_map: " + table_map);
        return this;
    }

    public Driver onContext(Context context) {
        this.context = context;
        return this;
    }

    public Driver onDBName(String db_name) {
        this.db_name = db_name;
        return this;
    }

    public Driver onCursorFactory(SQLiteDatabase.CursorFactory factory) {
        this.factory = factory;
        return this;
    }

    public Driver onDBVersion(int db_version) {
        this.db_version = db_version;
        return this;
    }

    public void createDb() {
        this.helper = new SQLiteStorageHelper(context, db_name, factory, db_version);
        this.helper.getWritableDatabase();
    }

    public boolean isDBCreated() {
        return this.helper != null ? this.helper.isDBCreated() : false;
    }

//------------------------------------------------------------------------------------------------//
//------------------------------------------------------------------------------------------------//
//------------------------------------------------------------------------------------------------//

    public void insert(Class table_class, Table.Row row) throws StorageException {
        Table table = table_map.get(table_class);
        if (table != null) {
            try {
                helper.openForWrite("insert");
                table.insert(this.database, row);
            } catch (Exception exc) {
                throw new StorageException(exc.getMessage())
                        .setErrorType(StorageException.ErrorType.ERROR)
                        .setException(exc);
            } finally {
                helper.close();
            }
        }
    }

    public <E> void update(Class table_class, Table.Cell<E> cell) throws StorageException {
        Table table = table_map.get(table_class);
        if (table != null) {
            try {
                helper.openForWrite("update");
                table.update(this.database, cell);
            } catch (Exception exc) {
                throw new StorageException(exc.getMessage())
                        .setErrorType(StorageException.ErrorType.ERROR)
                        .setException(exc);
            } finally {
                helper.close();
            }
        }
    }

    public <E> void delete(Class table_class, Table.Cell<E> cell) throws StorageException {
        Table table = table_map.get(table_class);
        if (table != null) {
            try {
                helper.openForWrite("delete");
                table.delete(this.database, cell);
            } catch (Exception exc) {
                throw new StorageException(exc.getMessage())
                        .setErrorType(StorageException.ErrorType.ERROR)
                        .setException(exc);
            } finally {
                helper.close();
            }
        }
    }

    public <E> Table.Row query(Class table_class, Table.Cell<E> cell, int column) throws StorageException {
        Table table = table_map.get(table_class);
        Table.Row list = null;
        if (table != null && cell != null) {
            Cursor cursor = null;
            try {
                helper.openForRead("query");
                cursor = table.query(database, cell);
                if (cursor != null) {
                    cursor.moveToFirst();
                    while (!cursor.isAfterLast()) {
                        list = table.get(cursor);
                        if (list != null && list.size() > column && cell.getValue().equals(list.getCellValue(column))) {
                            break;
                        }
                        cursor.moveToNext();
                    }
                }
            } catch (Exception exc) {
                throw new StorageException(exc.getMessage())
                        .setErrorType(StorageException.ErrorType.ERROR)
                        .setException(exc);
            } finally {

                if (cursor != null) {
                    cursor.close();
                }

                helper.close();
            }
        } else {
            Common.log("{query} - table_map  : " + table_map);
            Common.log("{query} - cell       : " + cell);
            Common.log("{query} - column     : " + column);
        }
        return list;
    }

    public void wipeAll() throws StorageException {
        if (table_map != null && !table_map.isEmpty()) {
            Iterator<Class> ite = table_map.keySet().iterator();
            while (ite.hasNext()) {
                wipe(ite.next());
            }
        }
    }

    public void wipe(Class table_class) throws StorageException {
        Table table = table_map.get(table_class);
        if (table != null) {
            try {
                helper.openForWrite("wipe");
                table.wipe(this.database);
            } catch (Exception exc) {
                throw new StorageException(exc.getMessage())
                        .setErrorType(StorageException.ErrorType.ERROR)
                        .setException(exc);
            } finally {
                helper.close();
            }
        }
    }

    public int count(Class table_class) throws StorageException {
        int size = -1;
        Table table = table_map.get(table_class);
        if (table != null) {
            try {
                helper.openForRead("count");
                size = table.count(database);
            } catch (Exception exc) {
                throw new StorageException(exc.getMessage())
                        .setErrorType(StorageException.ErrorType.ERROR)
                        .setException(exc);
            } finally {
                helper.close();
            }
        }
        return size;
    }

    public Table.RowList all(Class table_class, TableQueryListener listener) throws StorageException {

        Table table = table_map.get(table_class);
        Table.RowList all_rows = null;

        if (table != null) {

            all_rows = new Table.RowList();

            Cursor cursor = null;

            try {

                if (listener != null) {
                    listener.onStartProcess();
                }

                helper.openForRead("all");

                cursor = table.all(database);

                if (cursor != null) {
                    cursor.moveToFirst();
                    while ((listener == null || listener.loadNext()) && !cursor.isAfterLast()) {

                        Table.Row row = table.get(cursor);

                        Common.log("{DRIVER} - row: " + row);

                        if (row != null) {
                            all_rows.add(row);
                        }

                        if (listener != null) {
                            listener.onQueryResult(row);
                        }

                        cursor.moveToNext();
                    }
                } else {
                    Common.log("{DRIVER} - cursor is null.");
                }
            } catch (Exception exc) {
                throw new StorageException(exc.getMessage())
                        .setErrorType(StorageException.ErrorType.ERROR)
                        .setException(exc);
            } finally {

                if (cursor != null) {
                    cursor.close();
                }

                helper.close();

                if (listener != null) {
                    listener.onEndProcess();
                }
            }
        }
        return all_rows;
    }

//------------------------------------------------------------------------------------------------//
//------------------------------------------------------------------------------------------------//
//------------------------------------------------------------------------------------------------//

    public class SQLiteStorageHelper extends SQLiteOpenHelper {

        private boolean is_db_created = false;

        private SQLiteStorageHelper(Context context,
                                    String name,
                                    SQLiteDatabase.CursorFactory factory,
                                    int version) {
            super(context, name, factory, version);
            Common.log("{SQLiteStorageHelper} - CONSTRUCTOR");
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                Common.log("{SQLiteStorageHelper} - ---------------------------------------------");
                Common.log("{SQLiteStorageHelper} - onCreate");
                Common.log("{SQLiteStorageHelper} - ---------------------------------------------");
                createTables(db);
            } catch (Exception exc) {
                Common.log("{SQLiteStorageHelper} - EXC:" + exc);
                exc.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Common.log("onUpgrade - table_map: " + table_map.size() +
                    " oldVersion: " + oldVersion + " newVersion: " + newVersion);
            createTables(db);
        }

        private void createTables(SQLiteDatabase db) {

            try {

                Common.log("{SQLiteStorageHelper} - ---------------------------------------------");
                Common.log("{SQLiteStorageHelper} - createTables");
                Common.log("{SQLiteStorageHelper} - ---------------------------------------------");

                Common.log("createTables - table_map: " + table_map.size());
                Iterator<Class> keys = table_map.keySet().iterator();

                while (keys.hasNext()) {
                    db.execSQL(table_map.get(keys.next()).getCreateTableQuery());
                }

                is_db_created = true;

            } catch (Exception exc) {
                Common.log("{SQLiteStorageHelper} - ---------------------------------------------");
                Common.log("{SQLiteStorageHelper} - createTables - EXC: " + exc);
                Common.log("{SQLiteStorageHelper} - ---------------------------------------------");
                exc.printStackTrace();
            }
        }

        private void openForRead(String log) {
            lock.lock();
            Common.log("{SQLiteStorageHelper} - openForRead log: " + log);
            database = this.getReadableDatabase();
        }

        private void openForWrite(String log) {
            lock.lock();
            Common.log("{SQLiteStorageHelper} - openForWrite log: " + log);
            database = this.getWritableDatabase();
        }

        @Override
        public void close() {
            Common.log("{SQLiteStorageHelper} - close");
            database.close();
            lock.unlock();
        }

        public boolean isDBCreated() {
            return is_db_created;
        }
    }
}