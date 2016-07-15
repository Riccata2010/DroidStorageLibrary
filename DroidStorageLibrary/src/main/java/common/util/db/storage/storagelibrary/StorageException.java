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

public class StorageException extends Exception {

    private ErrorType type = ErrorType.WARNING;

    public StorageException(String message) {
        super(message);
    }

    public StorageException setException(Exception exc) {
        this.setStackTrace(exc.getStackTrace());
        return this;
    }

    public ErrorType getErrorType() {
        return this.type;
    }

    public StorageException setErrorType(ErrorType type) {
        this.type = type;
        return this;
    }

    public static enum ErrorType {

        WARNING, ERROR, FATAL
    }
}
