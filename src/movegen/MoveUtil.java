package movegen;

import static lib.Color.black;
import static lib.Color.white;
import static movegen.Magic.LegalMask;
import static movegen.Magic.MASK;

public class MoveUtil {

    private final static int    endShift            = 50;
    private final static int    pieceShift          = 56;
    private final static int    colorShift          = 57;
    private final static int    captureFlagShift    = 58;
    private final static int    promotionFlagShift  = 59;
    private final static int    legalMoveFlagShift  = 60;

    private final static long   Mask_1_Bit          = 0x1;
    private final static long   Mask_6_Bit          = 0x3F;
    private final static long   Mask_50_Bit         = LegalMask;

    public static long getRemoveMap(final long move){
        return move & LegalMask;
    }

    public static int getEndIndex(final long move){
        return (int) (move >>> endShift & Mask_6_Bit);
    }

    public static int getPiece(final long move){
        return (int) (move >>> pieceShift & Mask_1_Bit);
    }

    public static int getColor(final long move){
        return (int) (move >>> colorShift & Mask_1_Bit);
    }

    public static boolean isCapture(final long move){
        return (move >>> captureFlagShift & Mask_1_Bit) == 1;
    }

    public static boolean isPromotion(final long move){
        return (move >>> promotionFlagShift & Mask_1_Bit) == 1;
    }

    public static boolean isLegalMove(final long move){
        return (move >>> legalMoveFlagShift & Mask_1_Bit) == 1;
    }

    public static long createMove(final long remove_map, final int endPos, final int piece, final int color, final int capture, final int promotion){
        return remove_map | (long) endPos << endShift | (long) piece << pieceShift | (long) color << colorShift | (long) capture << captureFlagShift | (long) promotion << promotionFlagShift | MASK[legalMoveFlagShift];
    }

    static long createMove(final long remove_map, final int endPos, final int piece, final int color, final int capture){
        int promotion = 0;
        switch (color){
            case white:
                if((MASK[endPos] & 0x1fL) != 0) promotion = 1;
            case black:
                if((MASK[endPos] & 0x3e00000000000L) != 0) promotion = 1;
        }
        return remove_map | (long) endPos << endShift | (long) piece << pieceShift | (long) color << colorShift | (long) capture << captureFlagShift | (long) promotion << promotionFlagShift | MASK[legalMoveFlagShift];
    }

    static long removeLegal(final long move){
        return move ^ MASK[legalMoveFlagShift];
    }
}
