package pc;

import bit.Bit;

public class Move {
    int start;
    int end;
    int[] remove;
    int[] all;
    long raw;
    Move(int start, int end, long remove, long raw){
        this.raw = raw;
        this.start = start;
        this.end  = end;

        this.remove = new int[Bit.bitCount(remove)];
        int c = 0;
        while (remove != 0) {
            this.remove[c] = Bit.Index(remove & -remove);
            remove = remove & (remove - 1);
            c++;
        }
        all = new int[this.remove.length + 2];
        all[0] = start;
        if (this.remove.length >= 0) System.arraycopy(this.remove, 0, all, 1, this.remove.length);
        all[all.length - 1] = end;
    }
}
