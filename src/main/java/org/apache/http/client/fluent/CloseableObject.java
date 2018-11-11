package org.apache.http.client.fluent;

import java.io.Closeable;

public interface CloseableObject extends Closeable {
    boolean isOpen();
}
