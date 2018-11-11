package org.apache.http.client.fluent;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class HacSimpleLB implements HacLoadBlancer {
    private static final AtomicLong COUNTER = new AtomicLong(0);

    public HacRealServer select(List<HacRealServer> list) {
        int size = list.size();
        if (size == 1) {
            return list.get(0);
        }
        long count = COUNTER.getAndIncrement();
        return list.get((int) (count % size));
    }


}
