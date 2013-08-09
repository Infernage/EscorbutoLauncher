package elr.modules.threadsystem;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Special ThreadPoolExecutor. There is an unique instance when the program is running.
 * @author Infernage
 */
public class ThreadPool extends ThreadPoolExecutor{
    private static ThreadPool instance = null;
    
    /**
     * Creates a new instance.
     */
    public static void startThreadPool(){
        if (instance != null) return;
        instance = new ThreadPool(10);
    }
    
    public static void shutdownPool(){
        if (instance != null) instance.shutdown();
    }
    
    /**
     * Gets the instance.
     * @return The unique instance.
     */
    public static ThreadPool getInstance(){
        if (instance == null) startThreadPool();
        return instance;
    }
    
    public ThreadPool(int threads){
        super(threads, threads, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
    }
    
    @Override
    public void shutdown(){
        instance = null;
        super.shutdown();
    }
    
    @Override
    public List<Runnable> shutdownNow(){
        instance = null;
        return super.shutdownNow();
    }
}
