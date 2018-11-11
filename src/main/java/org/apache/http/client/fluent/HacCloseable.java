package org.apache.http.client.fluent;

import java.io.Closeable;

public interface HacCloseable extends Closeable {
    boolean isOpen();
}
