package engine;

import lib.Util;
import pc.ResultUtil;
import search.TimeUtil;

public class Engine {

    public static void sendInfo(){
        ResultUtil.currentBestMove  = Util.bestMoveStr;
        ResultUtil.currentBestScore = Util.bestScore;
        ResultUtil.maxPly           = Constants.maxDepth;
        ResultUtil.currentDepth     = Constants.maxWindowDepth;
        if(TimeUtil.getTime() <= 0) ResultUtil.speed = (Constants.qNodes + Constants.sNodes);
        else ResultUtil.speed = (double) ((Constants.qNodes + Constants.sNodes) / TimeUtil.getTime());
        ResultUtil.timeSearch = TimeUtil.getTime();
        ResultUtil.research = Constants.AWPResearchCount;
        ResultUtil.nodes = Constants.qNodes + Constants.sNodes;

    }

    public static void sendScoreInfo(int state){
        ResultUtil.scoreState = state;
    }
}
