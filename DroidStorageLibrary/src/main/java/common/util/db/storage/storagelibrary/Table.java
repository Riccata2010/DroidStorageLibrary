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

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public interface Table {

    public static final Cell __NULL_CELL__ = new Cell().setSelector(Cell.NULL_SELECTOR);

    String gatTableName();

    String getCreateTableQuery();

    String[] getColumns();

    void wipe(final SQLiteDatabase database) throws StorageException;

    void insert(final SQLiteDatabase database, final Row row) throws StorageException;

    Cursor all(final SQLiteDatabase database) throws StorageException;

    int count(final SQLiteDatabase database) throws StorageException;

    <E> boolean delete(final SQLiteDatabase database, final Cell<E> cell) throws StorageException;

    <E> Cursor query(final SQLiteDatabase database, final Cell<E> cell) throws StorageException;

    <E> void update(final SQLiteDatabase database, final Cell<E> cell) throws StorageException;

    Row get(Cursor cursor);

    class DefaultCounter {
        public static int count(final SQLiteDatabase database, final String table) {
            Cursor cursor = null;
            int result = 0;
            try {
                cursor = database.rawQuery("select count(*) from " + table, null);
                cursor.moveToFirst();
                result = cursor.getInt(0);
            } catch (Exception exc) {
                exc.printStackTrace();
            } finally {
                if (cursor != null)
                    cursor.close();
            }
            return result;
        }
    }

    class BackupTable {

        public static void cloneTable(final SQLiteDatabase database, final Table table) {

            dropTable(database, table);

            try {

                String create_table = table.getCreateTableQuery();
                int i = create_table.indexOf("(");

                if (i < 0)
                    throw new NullPointerException("bad table");

                String hpar = create_table.substring(0, i).trim();

                create_table = hpar + "_CLONE" + create_table.substring(i);

                database.execSQL(create_table);
                database.execSQL("INSERT INTO " + table.gatTableName() + "_CLONE SELECT * FROM " + table.gatTableName());

            } catch (Exception exc) {
                exc.printStackTrace();
            }
        }

        public static void dropTable(final SQLiteDatabase database, final Table table) {
            try {
                database.execSQL("DROP TABLE IF EXISTS " + table.gatTableName() + "_CLONE");
            } catch (Exception exc) {
                exc.printStackTrace();
            }
        }

        public static void rollbackTable(final SQLiteDatabase database, final Table table) {

            try {
                table.wipe(database);
            } catch (Exception exc) {
                exc.printStackTrace();
            }

            try {
                database.execSQL("DROP TABLE IF EXISTS " + table.gatTableName());
            } catch (Exception exc) {
                exc.printStackTrace();
            }

            try {
                database.execSQL(table.getCreateTableQuery());
                database.execSQL("INSERT INTO " + table.gatTableName() + " SELECT * FROM " + table.gatTableName() + "_CLONE");
            } catch (Exception exc) {
                exc.printStackTrace();
            }

            dropTable(database, table);
        }
    }

    class RowList extends ArrayList<Row> {

        public Row getRow(int index) {
            if (index < 0 || index > this.size()) {
                System.out.println("WARNING {RowList} - List index out of bounds.");
                return null;
            }
            return this.get(index);
        }
    }

    class Row extends ArrayList<Cell> {

        public Row addCell(Cell cell) {
            this.add(cell);
            return this;
        }

        public <E> E getCellValue(int index) {
            if (index < 0 || index > this.size()) {
                System.out.println("WARNING {Row} - List index out of bounds.");
                return null;
            }
            Cell cell = this.get(index);
            if (cell != null)
                return (E) cell.getValue();
            return null;
        }
    }

    class Cell<E> {

        public final static short NULL_SELECTOR = -1;

        private String args = null;
        private String key = null;
        private E value = null;
        private short selector = NULL_SELECTOR;
        private int type = -1;
        private Map<String, String> map = null;

        public Cell() {
        }

        public Cell(String key, E value, int type) {
            this.key = key;
            this.value = value;
            this.type = type;
        }

        public Map<String, String> getMap() {
            return this.map;
        }

        public Cell setMap(Map<String, String> a) {
            this.map = a;
            return this;
        }

        public String getArgs() {
            return this.args;
        }

        public Cell setArgs(String a) {
            this.args = a;
            return this;
        }

        public String getKey() {
            return this.key;
        }

        public Cell setKey(String a) {
            this.key = a;
            return this;
        }

        public E getValue() {
            return this.value;
        }

        public Cell setValue(E a) {
            this.value = a;
            return this;
        }

        public int getType() {
            return this.type;
        }

        public Cell setType(int a) {
            this.type = a;
            return this;
        }

        public short getSelector() {
            return this.selector;
        }

        public Cell setSelector(short a) {
            this.selector = a;
            return this;
        }

        @Override
        public String toString() {
            return "{" + this.key + "=" + this.value + "}";
        }
    }

    class Mapper<K, V> {

        private K[] keys;

        public static <K, V> Mapper<K, V> with(K... keys) {
            Mapper<K, V> m = new Mapper<K, V>();
            m.keys = keys;
            return m;
        }

        public Map<K, V> map(V... values) {
            Map<K, V> ris = new HashMap<K, V>();
            for (int i = 0; i < keys.length; i++) {
                ris.put(keys[i], values[i]);
            }
            return ris;
        }
    }
}
