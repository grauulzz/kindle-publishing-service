//package com.amazon.ata.kindlepublishingservice.publishing;
//
//import java.util.concurrent.LinkedBlockingQueue;
//import java.util.concurrent.ThreadFactory;
//import java.util.concurrent.ThreadPoolExecutor;
//import java.util.concurrent.TimeUnit;
//
//public class TrackableSingleThreadPoolExecutor extends ThreadPoolExecutor {
//    /*
//     * Task must be held as a volatile variable even in SingleThreadedExecutor.
//     * - A thread is destroyed and new one is recreated when an exception is thrown and caught.
//     */
//    private volatile TrackableRunnable activeTask;
//
//    private TrackableSingleThreadPoolExecutor(ThreadFactory threadFactory) {
//        super(1, 1, 0L, TimeUnit.MILLISECONDS,
//                new LinkedBlockingQueue<>(), threadFactory);
//    }
//
//    @Override
//    protected void beforeExecute(Thread thread, Runnable runnable) {
//        if (!(runnable instanceof TrackableRunnable)) {
//            throw new IllegalArgumentException("Executed task must be an instance of "
//                                                       + TrackableRunnable.class.getSimpleName());
//        }
//
//        this.activeTask = (TrackableRunnable) runnable;
//    }
//
//    @Override
//    protected void afterExecute(Runnable runnable, Throwable thread) {
//        this.activeTask = null;
//    }
//
//    public TrackableRunnable getActiveTask() {
//        return activeTask;
//    }
//
//    public static class TrackableRunnable implements Runnable {
//
//        private final BookPublishingManager context;
//
//        public TrackableRunnable(BookPublishingManager context) {
//            this.context = context;
//        }
//
//        @Override
//        public void run() {
//            // Some interesting computation.
//        }
//
//        public BookPublishingManager getContext() {
//            return context;
//        }
//    }
//}
