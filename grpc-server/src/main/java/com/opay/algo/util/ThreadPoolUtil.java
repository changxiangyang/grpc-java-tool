package com.opay.algo.util;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.concurrent.*;

/**
 * @author cxy
 */
public class ThreadPoolUtil {

    private static final Log logger = LogFactory.getLog(ThreadPoolUtil.class);

    /**
     * 根据cpu的数量动态的配置核心线程数和最大线程数
     */
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    /**
     * 核心线程数 = CPU核心数 + 1
     */
    private static final int CORE_POOL_SIZE = CPU_COUNT + 1;
    /**
     * 线程池最大线程数 = CPU核心数 * 2 + 1
     */
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    /**
     * 非核心线程闲置时超时1s
     */
    private static final int KEEP_ALIVE = 1;
    /**
     * 线程池的对象
     */
    private ThreadPoolExecutor executor;

    public volatile static ThreadPoolUtil threadPool;

    private TimeUnit unit = TimeUnit.SECONDS;

    public ThreadPoolUtil() {
        LinkedBlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>(1024);
        final ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("hbase-thread-%d").build();
        executor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE, unit, workQueue, threadFactory, new ThreadPoolExecutor.AbortPolicy());
    }

    static  {
        if (threadPool == null) {
            threadPool = new ThreadPoolUtil();
        }
    }

    public void awaitTermination() throws InterruptedException {
        logger.info("Thread pool ,awaitTermination started, please wait till all the jobs complete.");
        long timeout = 10;
        executor.awaitTermination(timeout, unit);
    }

    public void execute(Runnable t) {
        executor.execute(t);
    }

    public void execute(Thread t) {
        executor.execute(t);
    }

    public int getQueueSize() {
        return executor.getQueue().size();
    }

    public void shutdown() {
        getExecutor().shutdown();
    }

    public ThreadPoolExecutor getExecutor() {
        return executor;
    }

    public Future<?> submit(Runnable t) {
        return executor.submit(t);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public Future<?> submit(Callable t) {
        return getExecutor().submit(t);
    }

}
