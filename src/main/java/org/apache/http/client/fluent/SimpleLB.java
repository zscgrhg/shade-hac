package org.apache.http.client.fluent;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class SimpleLB implements LoadBlancer {
    private static final AtomicInteger COUNTER = new AtomicInteger(0);

    public RealServer select(List<RealServer> list) {
        int size = list.size();
        if (size == 1) {
            return list.get(0);
        }
        int count = COUNTER.incrementAndGet();
        if (count >= Math.max(size, 256)) {
            COUNTER.weakCompareAndSet(count, 0);
            count = COUNTER.incrementAndGet();
        }
        return list.get(count % size);
    }

}
