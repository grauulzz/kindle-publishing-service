package com.amazon.ata.kindlepublishingservice.publishing;

import com.amazon.ata.kindlepublishingservice.App;
import com.amazon.ata.kindlepublishingservice.dao.CatalogDao;
import com.amazon.ata.kindlepublishingservice.dao.PublishingStatusDao;
import com.amazon.ata.kindlepublishingservice.dynamodb.models.CatalogItemVersion;
import com.amazon.ata.kindlepublishingservice.dynamodb.models.PublishingStatusItem;
import com.amazon.ata.kindlepublishingservice.enums.PublishingRecordStatus;
import com.amazon.ata.kindlepublishingservice.exceptions.BookNotFoundException;
import com.amazon.ata.kindlepublishingservice.models.response.SubmitBookForPublishingResponse;
import com.amazon.ata.kindlepublishingservice.utils.KindlePublishingUtils;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


public class BookPublishTask implements Callable<CatalogItemVersion> {

    public static final CatalogDao CATALOG_DAO = App.component.provideCatalogDao();
    public static final PublishingStatusDao PUBLISHING_STATUS_DAO = App.component.providePublishingStatusDao();
    private final BookPublishRequest request;

    BookPublishTask(BookPublishRequest request) {
        this.request = request;
    }

    @Override
    public CatalogItemVersion call() throws Exception {

        publishWithId(request);

        return CATALOG_DAO.isExsitingCatalogItem(request.getBookId())
                .orElseThrow(() -> new Exception("Catalog item not found"));
    }

    private static void publishWithId(BookPublishRequest request) {
        CatalogItemVersion item = new CatalogItemVersion();
        item.setBookId(request.getBookId());
        item.setGenre(request.getGenre());
        item.setAuthor(request.getAuthor());
        item.setText(request.getText());
        item.setTitle(request.getTitle());
        CATALOG_DAO.saveItem(item);
    }

    private static void publishWithoutId(BookPublishRequest request) {
        CatalogItemVersion item = new CatalogItemVersion();
        String generatedBookId = KindlePublishingUtils.generateBookId();
        item.setBookId(generatedBookId);
        item.setGenre(request.getGenre());
        item.setAuthor(request.getAuthor());
        item.setText(request.getText());
        item.setTitle(request.getTitle());
        CATALOG_DAO.saveItem(item);
    }

    static ExecutorService createExecutor() {
        return new ThreadPoolExecutor(
                0,
                Integer.MAX_VALUE,
                60,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>());
    }

}








//        BookPublishingManager.publishToCatalog(request);
//        GetPublishingStatusRequest getPublishingStatusRequest = GetPublishingStatusRequest.builder().withPublishingRecordId(request.getPublishingRecordId()).build();
//
//        GetBookRequest getBookRequest = GetBookRequest.builder().withBookId(request.getBookId()).build();
//        GetBookResponse response = App.component.provideGetBookActivity().execute(getBookRequest);
//        if (response.)



//    public static List<BookPublishTask> createTasks(List<BookPublishRequest> requests) {
//        List<BookPublishTask> tasks = new ArrayList<BookPublishTask>();
//        for (BookPublishRequest request : requests) {
//            tasks.add(new BookPublishTask(request));
//        }
//        return tasks;
//    }
//
//    public static List<BookPublishRequest> executeTasks(List<BookPublishTask> tasks, ExecutorService executor) {
//        List<BookPublishRequest> requests = new ArrayList<BookPublishRequest>();
//        List<Future<BookPublishRequest>> futures = new ArrayList<Future<BookPublishRequest>>();
//        for (BookPublishTask task : tasks) {
//            futures.add(executor.submit(task));
//        }
//        for (Future<BookPublishRequest> future : futures) {
//            try {
//                requests.add(future.get());
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            } catch (ExecutionException e) {
//                throw new RuntimeException(e);
//            }
//        }
//        return requests;
//    }






//    ExecutorService executorService =
//            new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
//                    new LinkedBlockingQueue<Runnable>());
//
//    Runnable runnableTask = () -> {
//        try {
//            TimeUnit.MILLISECONDS.sleep(300);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    };
//
//    Callable<String> callableTask = () -> {
//        TimeUnit.MILLISECONDS.sleep(300);
//        return "Task's execution";
//    };
//
//    List<Callable<String>> callableTasks = new ArrayList<>();
//        callableTasks.add(callableTask);
//                callableTasks.add(callableTask);
//                callableTasks.add(callableTask);
//
//                executorService.execute(runnableTask);
//
//                Future<String> future = executorService.submit(callableTask);
//                                       try {
//                                       String result = executorService.invokeAny(callableTasks);
//                                       } catch (InterruptedException | ExecutionException e) {
//                                       e.printStackTrace();
//                                       }