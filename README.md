# DroidStorageLibrary

DroidStorageLibrary is a small library for Android that allows you to create your DB and perform some CRUD operations. 

Example:
``` java

this.driver = Driver.getSingleDriverInstance()
                .onContext(this)
                .onCursorFactory(null)
                .onDBName("MY_DB")
                .onDBVersion(1)
                .onTable(TableExample.class, new TableExample());

this.driver.createDb();

[..]

private void testInsertSomeRows() throws StorageException {
    for (int i = 0; i < 3; i++) {
	Table.Row row = new Table.Row();
	for (int j = 1; j < TableExample.COLUMNS.length; j++) {
	    row.addCell(new Table.Cell<java.lang.String>(TableExample.COLUMNS[j], "value_" + i + "_" + j, 0));
	}
	driver.insert(TableExample.class, row);
    }
}


```
### Version
Beta 1.0
