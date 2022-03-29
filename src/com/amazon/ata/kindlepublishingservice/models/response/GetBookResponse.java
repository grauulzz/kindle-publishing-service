package com.amazon.ata.kindlepublishingservice.models.response;

import com.amazon.ata.kindlepublishingservice.models.Book;
import com.amazon.ata.kindlepublishingservice.models.BookRecommendation;
import java.util.List;
import java.util.Objects;

/**
 * The type Get book response.
 */
public class GetBookResponse {
    private Book book;
    private List<BookRecommendation> recommendations;

    /**
     * Instantiates a new Get book response.
     *
     * @param book            the book
     * @param recommendations the recommendations
     */
    public GetBookResponse(Book book, List<BookRecommendation> recommendations) {
        this.book = book;
        this.recommendations = recommendations;
    }

    /**
     * Instantiates a new Get book response.
     *
     * @param builder the builder
     */
    public GetBookResponse(Builder builder) {
        this.book = builder.book;
        this.recommendations = builder.recommendations;
    }

    /**
     * Builder builder.
     *
     * @return the builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Gets book.
     *
     * @return the book
     */
    public Book getBook() {
        return book;
    }

    /**
     * Sets book.
     *
     * @param book the book
     */
    public void setBook(Book book) {
        this.book = book;
    }

    /**
     * Gets recommendations.
     *
     * @return the recommendations
     */
    public List<BookRecommendation> getRecommendations() {
        return recommendations;
    }

    /**
     * Sets recommendations.
     *
     * @param recommendations the recommendations
     */
    public void setRecommendations(List<BookRecommendation> recommendations) {
        this.recommendations = recommendations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GetBookResponse that = (GetBookResponse) o;
        return Objects.equals(book, that.book) &&
                       Objects.equals(recommendations, that.recommendations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(book, recommendations);
    }

    /**
     * The type Builder.
     */
    public static final class Builder {
        private Book book;
        private List<BookRecommendation> recommendations;

        private Builder() {

        }

        /**
         * With book builder.
         *
         * @param book the book
         *
         * @return the builder
         */
        public Builder withBook(Book book) {
            this.book = book;
            return this;
        }

        /**
         * With recommendations builder.
         *
         * @param recommendations the recommendations
         *
         * @return the builder
         */
        public Builder withRecommendations(List<BookRecommendation> recommendations) {
            this.recommendations = recommendations;
            return this;
        }

        /**
         * Build get book response.
         *
         * @return the get book response
         */
        public GetBookResponse build() {
            return new GetBookResponse(this);
        }
    }
}
