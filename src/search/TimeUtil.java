package search;

import engine.Constants;


public class TimeUtil {

    private static long start_time;
    private static long TimeMax = 10000;
    private static boolean running = false;
    private static long runningTime = 0;

    /* 超时重置 */
    public static void setThinkingTime(int time){
        TimeMax = time;
    }

    /* 开始运行程序 */
    public static void start(){
        running = true;
        start_time = System.currentTimeMillis();
    }

    public static void end(){
        running = false;
        runningTime = System.currentTimeMillis() - start_time;
    }

    /* 思考时间结束 */
    public static boolean isTimeLeft(){
        if(Constants.ENABLE_COUNT) Constants.ThinkingTime = TimeUtil.getTime();
        return System.currentTimeMillis() - start_time > TimeMax;
    }

    /* 获取思考时间 */
    public static long getTime(){
        return running ? System.currentTimeMillis() - start_time : runningTime;
    }
}
