package search;

import lib.Util;

import java.security.Key;

public class TTUtil {

    public static final int FLAG_EXACT   = 0;
    public static final int FLAG_LOWER   = 1;
    public static final int FLAG_UPPER   = 2;
    public final long[] Keys             = new long[0x100000];
    public final int[]  Values           = new int[0x100000];
    public final long[] Moves            = new long[0x100000];


    public int getIndex(long key){
        return (int) (key & 0xFFFFF);
    }

    public void setValue(long key, int value){
        Keys[getIndex(key)]     = key;
        Values[getIndex(key)]   = value;
    }

    public int getValue(long key){
        if(Keys[getIndex(key)] != key) return 0;
        return Values[getIndex(key)];
    }

    public long getMove(long key){
        if(Keys[getIndex(key)] != key) return 0;
        return Moves[getIndex(key)];
    }

    public int getFlag(int value){
        return value >>> 15 & 0b11;
    }

    public int getScore(int value, int ply){
        int score = (int) (value & 0x7fff);
        if (score > Util.SCORE_MATE_BOUND) {
            score -= ply;
        } else if (score < -Util.SCORE_MATE_BOUND) {
            score += ply;
        }
        return score;
    }

    public int getDepth(int value){
        return value >>> 17 & 0x7f;
    }

    public void addValue(long key, int score, int flag, int ply, long move){
        int index = getIndex(key);
        if(Values[index] != 0 && getDepth(Values[index]) <= ply || ply <= 1) return;
        Keys[index] = key;
        Values[index] = score | flag << 15 | ply << 17;
        Moves[index] = move;
    }
}
