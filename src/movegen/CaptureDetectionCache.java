package movegen;

import engine.Constants;

import java.util.Arrays;

public class CaptureDetectionCache {
    private static final long[] keys = new long[0x10000];
    private static final byte[] flags = new byte[0x10000];
    static {
        Arrays.fill(flags, Byte.MAX_VALUE);
    }

    public static void clearValues() {
        Arrays.fill(keys, 0);
        Arrays.fill(flags, Byte.MAX_VALUE);
    }

    public static byte getFlag(final long key) {
        final byte flag = flags[getIndex(key)];

        if (keys[getIndex(key)] == key) {
            Constants.capdetectCacheHits++;
            return flag;
        }

        /* cache miss */
        Constants.capdetectCacheMiss++;
        return Byte.MAX_VALUE;
    }

    public static void addValue(final long key, final boolean flag) {

        final int ttIndex = getIndex(key);

        keys[ttIndex] = key;
        flags[ttIndex] = (byte) (flag ? 1 : 0);
    }

    private static int getIndex(final long key) {
        return (int) (key >>> 50);
    }
}
