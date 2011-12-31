package util;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

public class Collections3 {
    private Collections3() {
    }

    public static <T> T single(List<T> list) {
        checkArgument(list.size() == 1);
        return list.get(0);
    }
}
