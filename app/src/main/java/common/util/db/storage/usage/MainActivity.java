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

import android.app.Activity;
import android.os.Bundle;

import common.util.db.storage.storagelibrary.Common;
import common.util.db.storage.storagelibrary.Driver;
import common.util.db.storage.storagelibrary.StorageException;
import common.util.db.storage.storagelibrary.Table;
import common.util.db.storage.storagelibrary.TableQueryListener;

public class MainActivity extends Activity {

    private Driver driver = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initDB();
        try {
            if (this.driver != null && this.driver.isDBCreated()) {
                Common.log("The database is created, now begin some tests.");
                testInsertSomeRows();
                testCount();
                testRead();
            }
        } catch (StorageException e) {
            e.printStackTrace();
        }
    }

    private void initDB() {

        Common.DEBUG = true;

        this.driver = Driver.getSingleDriverInstance()
                .onContext(this)
                .onCursorFactory(null)
                .onDBName("MY_DB")
                .onDBVersion(1)
                .onTable(TableExample.class, new TableExample());

        this.driver.createDb();
    }

    private void testInsertSomeRows() throws StorageException {
        for (int i = 0; i < 3; i++) {
            Table.Row row = new Table.Row();
            for (int j = 1; j < TableExample.COLUMNS.length; j++) {
                row.addCell(new Table.Cell<java.lang.String>(TableExample.COLUMNS[j], "value_" + i + "_" + j, 0));
            }
            driver.insert(TableExample.class, row);
        }
    }

    private void testCount() throws StorageException {
        int count = driver.count(TableExample.class);
        Common.log("The rows count is: " + count);
    }

    private void testRead() throws StorageException {
        Table.RowList rows = driver.all(TableExample.class, new TableQueryListener() {

            @Override
            public void onStartProcess() {
            }

            @Override
            public void onQueryResult(Table.Row row) {
            }

            @Override
            public void onEndProcess() {
            }

            @Override
            public boolean loadNext() {
                return true;
            }
        });

        if (!rows.isEmpty()) {
            for (int i = 0; i < rows.size(); i++) {
                for (int j = 0; j < rows.get(i).size(); j++) {
                    Common.log("CELL VALUE IS: " + rows.get(i).getCellValue(j));
                }
            }
        }
    }
}
