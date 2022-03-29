package com.amazon.ata.kindlepublishingservice.models.requests;

import java.util.Objects;

/**
 * The type Remove book from catalog request.
 */
public class RemoveBookFromCatalogRequest {
    private String bookId;

    /**
     * Instantiates a new Remove book from catalog request.
     */
    public RemoveBookFromCatalogRequest() {
    }

    /**
     * Instantiates a new Remove book from catalog request.
     *
     * @param builder the builder
     */
    public RemoveBookFromCatalogRequest(Builder builder) {
        this.bookId = builder.bookId;
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
     * Gets book id.
     *
     * @return the book id
     */
    public String getBookId() {
        return bookId;
    }

    /**
     * Sets book id.
     *
     * @param bookId the book id
     */
    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RemoveBookFromCatalogRequest that = (RemoveBookFromCatalogRequest) o;
        return Objects.equals(bookId, that.bookId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bookId);
    }

    /**
     * The type Builder.
     */
    public static final class Builder {
        private String bookId;

        private Builder() {

        }

        /**
         * With book id builder.
         *
         * @param bookIdToUse the book id to use
         *
         * @return the builder
         */
        public Builder withBookId(String bookIdToUse) {
            this.bookId = bookIdToUse;
            return this;
        }

        /**
         * Build remove book from catalog request.
         *
         * @return the remove book from catalog request
         */
        public RemoveBookFromCatalogRequest build() {
            return new RemoveBookFromCatalogRequest(this);
        }
    }
}
