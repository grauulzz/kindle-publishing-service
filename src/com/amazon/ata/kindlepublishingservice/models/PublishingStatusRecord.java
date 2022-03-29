package com.amazon.ata.kindlepublishingservice.models;

import java.util.Objects;

/**
 * The type Publishing status record.
 */
public class PublishingStatusRecord {
    private String status;
    private String statusMessage;
    private String bookId;

    /**
     * Instantiates a new Publishing status record.
     *
     * @param status        the status
     * @param statusMessage the status message
     * @param bookId        the book id
     */
    public PublishingStatusRecord(String status, String statusMessage, String bookId) {
        this.status = status;
        this.statusMessage = statusMessage;
        this.bookId = bookId;
    }

    /**
     * Instantiates a new Publishing status record.
     *
     * @param builder the builder
     */
    public PublishingStatusRecord(Builder builder) {
        this.status = builder.status;
        this.statusMessage = builder.statusMessage;
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
     * Gets status.
     *
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets status.
     *
     * @param status the status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Gets status message.
     *
     * @return the status message
     */
    public String getStatusMessage() {
        return statusMessage;
    }

    /**
     * Sets status message.
     *
     * @param statusMessage the status message
     */
    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
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
        PublishingStatusRecord that = (PublishingStatusRecord) o;
        return status == that.status &&
                       Objects.equals(statusMessage, that.statusMessage) &&
                       Objects.equals(bookId, that.bookId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, statusMessage, bookId);
    }

    @Override
    public String toString() {
        return "PublishingStatusRecord{" +
                       "status='" + status + '\'' +
                       ", statusMessage='" + statusMessage + '\'' +
                       ", bookId='" + bookId + '\'' +
                       '}';
    }

    /**
     * The type Builder.
     */
    public static final class Builder {
        private String status;
        private String statusMessage;
        private String bookId;

        private Builder() {

        }

        /**
         * With status builder.
         *
         * @param statusToUse the status to use
         *
         * @return the builder
         */
        public Builder withStatus(String statusToUse) {
            this.status = statusToUse;
            return this;
        }

        /**
         * With status message builder.
         *
         * @param statusMessageToUse the status message to use
         *
         * @return the builder
         */
        public Builder withStatusMessage(String statusMessageToUse) {
            this.statusMessage = statusMessageToUse;
            return this;
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
         * Build publishing status record.
         *
         * @return the publishing status record
         */
        public PublishingStatusRecord build() {
            return new PublishingStatusRecord(this);
        }
    }
}
