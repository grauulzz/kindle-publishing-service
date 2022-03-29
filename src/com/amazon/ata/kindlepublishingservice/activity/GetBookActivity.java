package com.amazon.ata.kindlepublishingservice.activity;

import com.amazon.ata.kindlepublishingservice.clients.RecommendationsServiceClient;
import com.amazon.ata.kindlepublishingservice.converters.CatalogItemConverter;
import com.amazon.ata.kindlepublishingservice.converters.RecommendationsCoralConverter;
import com.amazon.ata.kindlepublishingservice.dao.CatalogDao;
import com.amazon.ata.kindlepublishingservice.dynamodb.models.CatalogItemVersion;
import com.amazon.ata.kindlepublishingservice.models.requests.GetBookRequest;
import com.amazon.ata.kindlepublishingservice.models.response.GetBookResponse;
import com.amazon.ata.recommendationsservice.types.BookGenre;
import com.amazon.ata.recommendationsservice.types.BookRecommendation;

import java.util.List;
import javax.inject.Inject;

/**
 * Implementation of the GetBookActivity for the ATACurriculumKindlePublishingService's
 * GetBook API.
 *
 * This API allows the client to retrieve a book.
 */
public class GetBookActivity {
    private final RecommendationsServiceClient recommendationServiceClient;
    private final CatalogDao catalogDao;

    /**
     * Instantiates a new GetBookActivity object.
     *
     * @param catalogDao                  CatalogDao to access the Catalog table.
     * @param recommendationServiceClient Returns recommendations based on genre.
     */
    @Inject
    public GetBookActivity(CatalogDao catalogDao, RecommendationsServiceClient recommendationServiceClient) {
        this.catalogDao = catalogDao;
        this.recommendationServiceClient = recommendationServiceClient;
    }

    /**
     * Retrieves the book associated with the provided book id.
     *
     * @param request Request object containing the book ID associated with the book to get from the Catalog.
     *
     * @return GetBookResponse Response object containing the requested book.
     */
    public GetBookResponse execute(GetBookRequest request) {
        CatalogItemVersion catalogItem = catalogDao.getBookFromCatalog(request.getBookId());
        List<BookRecommendation> recommendations = recommendationServiceClient.getBookRecommendations(
                BookGenre.valueOf(catalogItem.getGenre().name()));
        return GetBookResponse.builder()
                .withBook(CatalogItemConverter.toBook(catalogItem))
                .withRecommendations(RecommendationsCoralConverter.toCoral(recommendations))
                .build();
    }
}
