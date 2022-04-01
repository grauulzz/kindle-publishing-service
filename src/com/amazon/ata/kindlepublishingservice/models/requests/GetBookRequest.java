package com.amazon.ata.kindlepublishingservice.models.requests;

import java.util.Objects;

/**
 * The type Get book request.
 */
public class GetBookRequest {
    private String bookId;

    /**
     * Instantiates a new Get book request.
     */
    public GetBookRequest() {
    }

    /**
     * Instantiates a new Get book request.
     *
     * @param builder the builder
     */
    public GetBookRequest(Builder builder) {
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
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GetBookRequest that = (GetBookRequest) o;
        return Objects.equals(bookId, that.bookId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bookId);
    }

    @Override
    public String toString() {
        return "GetBookRequest{" +
                       "bookId='" + bookId + '\'' +
                       '}';
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
         * Build get book request.
         *
         * @return the get book request
         */
        public GetBookRequest build() {
            return new GetBookRequest(this);
        }
    }
}
