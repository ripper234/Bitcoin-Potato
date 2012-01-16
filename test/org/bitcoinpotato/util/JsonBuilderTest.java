package org.bitcoinpotato.util;

import org.junit.Test;
import play.test.UnitTest;

public class JsonBuilderTest extends UnitTest {
    @Test
    public void sanity() {
        // http://stackoverflow.com/questions/8876271/how-to-serialize-a-jsonobject-without-too-much-quotes/8876377#8876377
        String json = new JsonBuilder()
                .add("key1", "value1")
                .add("key2", "value2")
                .add("key3", new JsonBuilder()
                        .add("innerKey", "value3"))
                .toJson();

        json = json;
    }
}
