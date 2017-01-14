package com.qwert2603.vkautomessage.util;

import java.util.Collection;
import java.util.Iterator;

public final class CollectionUtils {

    public interface Predicate<T> {
        boolean call(T t);
    }

    public static <T> void removeIf(Collection<T> collection, Predicate<T> predicate) {
        Iterator<T> each = collection.iterator();
        while (each.hasNext()) {
            if (predicate.call(each.next())) {
                each.remove();
            }
        }
    }

}
