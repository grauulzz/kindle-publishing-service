package com.amazon.ata.kindlepublishingservice.models.response;

import com.amazon.ata.kindlepublishingservice.models.PublishingStatusRecord;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * The type Get publishing status response.
 */
public class GetPublishingStatusResponse {
    /**
     * The Publishing records.
     */
    Map<String, List<PublishingStatusRecord>> publishingRecords;
    private List<PublishingStatusRecord> publishingStatusHistory;

    /**
     * Instantiates a new Get publishing status response.
     *
     * @param publishingStatusHistory the publishing status history
     */
    public GetPublishingStatusResponse(List<PublishingStatusRecord> publishingStatusHistory) {
        this.publishingStatusHistory = publishingStatusHistory;
    }

    /**
     * Instantiates a new Get publishing status response.
     *
     * @param builder the builder
     */
    public GetPublishingStatusResponse(Builder builder) {
        this.publishingStatusHistory = builder.publishingStatusHistory;
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
     * Gets publishing status history.
     *
     * @return the publishing status history
     */
    public List<PublishingStatusRecord> getPublishingStatusHistory() {
        return publishingStatusHistory;
    }

    /**
     * Sets publishing status history.
     *
     * @param publishingStatusHistory the publishing status history
     */
    public void setPublishingStatusHistory(List<PublishingStatusRecord> publishingStatusHistory) {
        this.publishingStatusHistory = publishingStatusHistory;
    }

    /**
     * Gets publishing records.
     *
     * @return the publishing records
     */
    public Map<String, List<PublishingStatusRecord>> getPublishingRecords() {
        return publishingRecords;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GetPublishingStatusResponse that = (GetPublishingStatusResponse) o;
        return Objects.equals(publishingStatusHistory, that.publishingStatusHistory);
    }

    @Override
    public int hashCode() {
        return Objects.hash(publishingStatusHistory);
    }

    /**
     * The type Builder.
     */
    public static final class Builder {
        /**
         * The Publishing records.
         */
        Map<String, List<PublishingStatusRecord>> publishingRecords;
        private List<PublishingStatusRecord> publishingStatusHistory;

        private Builder() {

        }

        /**
         * With publishing status history builder.
         *
         * @param publishingStatusHistoryToUse the publishing status history to use
         *
         * @return the builder
         */
        public Builder withPublishingStatusHistory(List<PublishingStatusRecord> publishingStatusHistoryToUse) {
            this.publishingStatusHistory = publishingStatusHistoryToUse;
            return this;
        }

        /**
         * With publishing records map builder.
         *
         * @param publishingRecords the publishing records
         *
         * @return the builder
         */
        public Builder withPublishingRecordsMap(Map<String, List<PublishingStatusRecord>> publishingRecords) {
            this.publishingRecords = publishingRecords;
            return this;
        }

        /**
         * Build get publishing status response.
         *
         * @return the get publishing status response
         */
        public GetPublishingStatusResponse build() {
            return new GetPublishingStatusResponse(this);
        }
    }
}
