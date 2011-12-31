package util;

import java.util.Collection;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

public class Collections3 {
    private Collections3() {
    }

    public static <T> T single(List<T> list) {
        checkArgument(list.size() == 1);
        return list.get(0);
    }

    public static <T> T single(Collection<T> collection) {
        int i = 0;
        T result = null;
        for (T item : collection) {
            i++;
            result = item;
        }
        checkArgument(i == 1);
        return result;
    }
}
