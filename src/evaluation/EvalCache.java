package evaluation;

import engine.Constants;

import java.util.Arrays;

public class EvalCache {

    private static final long[] keys = new long[0x10000];
    private static final int[] scores = new int[0x10000];

    public static void clearValues() {
        Arrays.fill(keys, 0);
        Arrays.fill(scores, 0);
    }

    static int getScore(final long key) {
        if (!Constants.ENABLE_EVAL_CACHE) return Integer.MIN_VALUE;
        final int score = scores[getIndex(key)];

        if ((keys[getIndex(key)] ^ score) == key) {
            Constants.evalCacheHits++;
            return score;
        }

        /* cache miss */
        if (Constants.ENABLE_COUNT) Constants.evalCacheMiss++;
        return Integer.MIN_VALUE;
    }

    static void addValue(final long key, final int score) {

        final int ttIndex = getIndex(key);

        keys[ttIndex] = key ^ score;
        scores[ttIndex] = score;
    }

    private static int getIndex(final long key) {
        return (int) (key >>> 50);
    }

}
