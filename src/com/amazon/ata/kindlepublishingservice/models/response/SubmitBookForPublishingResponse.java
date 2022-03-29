package com.amazon.ata.kindlepublishingservice.models.response;

import java.util.Objects;

/**
 * The type Submit book for publishing response.
 */
public class SubmitBookForPublishingResponse {
    private String publishingRecordId;

    /**
     * Instantiates a new Submit book for publishing response.
     *
     * @param publishingRecordId the publishing record id
     */
    public SubmitBookForPublishingResponse(String publishingRecordId) {
        this.publishingRecordId = publishingRecordId;
    }

    /**
     * Instantiates a new Submit book for publishing response.
     *
     * @param builder the builder
     */
    public SubmitBookForPublishingResponse(Builder builder) {
        this.publishingRecordId = builder.publishingRecordId;
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
     * Gets publishing record id.
     *
     * @return the publishing record id
     */
    public String getPublishingRecordId() {
        return publishingRecordId;
    }

    /**
     * Sets publishing record id.
     *
     * @param publishingRecordId the publishing record id
     */
    public void setPublishingRecordId(String publishingRecordId) {
        this.publishingRecordId = publishingRecordId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubmitBookForPublishingResponse that = (SubmitBookForPublishingResponse) o;
        return Objects.equals(publishingRecordId, that.publishingRecordId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(publishingRecordId);
    }

    @Override
    public String toString() {
        return "SubmitBookForPublishingResponse{" +
                       "publishingRecordId='" + publishingRecordId + '\'' +
                       '}';
    }

    /**
     * The type Builder.
     */
    public static final class Builder {
        private String publishingRecordId;

        private Builder() {

        }

        /**
         * With publishing record id builder.
         *
         * @param publishingRecordIdToUse the publishing record id to use
         *
         * @return the builder
         */
        public Builder withPublishingRecordId(String publishingRecordIdToUse) {
            this.publishingRecordId = publishingRecordIdToUse;
            return this;
        }

        /**
         * Build submit book for publishing response.
         *
         * @return the submit book for publishing response
         */
        public SubmitBookForPublishingResponse build() {
            return new SubmitBookForPublishingResponse(this);
        }
    }
}
