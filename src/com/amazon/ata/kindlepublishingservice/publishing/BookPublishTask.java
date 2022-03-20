package com.amazon.ata.kindlepublishingservice.publishing;

import com.amazon.ata.kindlepublishingservice.App;
import com.amazon.ata.kindlepublishingservice.activity.SubmitBookForPublishingActivity;

import com.amazon.ata.kindlepublishingservice.models.Book;
import com.amazon.ata.kindlepublishingservice.models.requests.SubmitBookForPublishingRequest;
import com.amazon.ata.kindlepublishingservice.models.response.SubmitBookForPublishingResponse;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;


public class BookPublishTask implements Runnable {
    private final BookPublishingManager context;

    public BookPublishTask(BookPublishingManager context) {
        this.context = context;
    }

    @Override
    public void run() {
        App.logger.info("BookPublishTask started");
        while (true) {
            BookPublishRequest request = context.nextRequest();

        }
    }

    public static class Handler implements RequestHandler<SubmitBookForPublishingRequest,
                                                                 SubmitBookForPublishingResponse> {

        private final SubmitBookForPublishingActivity submitBookForPublishingActivity =
                App.component.provideSubmitBookForPublishingActivity();

        @Override
        public SubmitBookForPublishingResponse handleRequest(SubmitBookForPublishingRequest request, Context context) {
            return submitBookForPublishingActivity.execute(request);
        }
    }
}
