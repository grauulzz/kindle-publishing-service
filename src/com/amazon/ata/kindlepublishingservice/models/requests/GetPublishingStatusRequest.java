package com.amazon.ata.kindlepublishingservice.models.requests;

import java.util.Objects;

/**
 * The type Get publishing status request.
 */
public class GetPublishingStatusRequest {
    private String publishingRecordId;

    /**
     * Instantiates a new Get publishing status request.
     *
     * @param publishingRecordId the publishing record id
     */
    public GetPublishingStatusRequest(String publishingRecordId) {
        this.publishingRecordId = publishingRecordId;
    }

    /**
     * Instantiates a new Get publishing status request.
     *
     * @param builder the builder
     */
    public GetPublishingStatusRequest(Builder builder) {
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
        GetPublishingStatusRequest that = (GetPublishingStatusRequest) o;
        return publishingRecordId.equals(that.publishingRecordId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(publishingRecordId);
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
         * Build get publishing status request.
         *
         * @return the get publishing status request
         */
        public GetPublishingStatusRequest build() {
            return new GetPublishingStatusRequest(this);
        }
    }
}
