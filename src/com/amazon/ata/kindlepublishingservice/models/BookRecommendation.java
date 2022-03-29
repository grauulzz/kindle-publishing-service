package com.amazon.ata.kindlepublishingservice.models;

import java.util.Objects;

/**
 * The type Book recommendation.
 */
public class BookRecommendation {
    private String title;
    private String author;
    private String asin;

    /**
     * Instantiates a new Book recommendation.
     *
     * @param title  the title
     * @param author the author
     * @param asin   the asin
     */
    public BookRecommendation(String title, String author, String asin) {
        this.title = title;
        this.author = author;
        this.asin = asin;
    }

    /**
     * Instantiates a new Book recommendation.
     *
     * @param builder the builder
     */
    public BookRecommendation(Builder builder) {
        this.title = builder.title;
        this.author = builder.author;
        this.asin = builder.asin;
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
     * Gets title.
     *
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets title.
     *
     * @param title the title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets author.
     *
     * @return the author
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Sets author.
     *
     * @param author the author
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * Gets asin.
     *
     * @return the asin
     */
    public String getAsin() {
        return asin;
    }

    /**
     * Sets asin.
     *
     * @param asin the asin
     */
    public void setAsin(String asin) {
        this.asin = asin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookRecommendation that = (BookRecommendation) o;
        return Objects.equals(title, that.title) &&
                       Objects.equals(author, that.author) &&
                       Objects.equals(asin, that.asin);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, author, asin);
    }

    /**
     * The type Builder.
     */
    public static final class Builder {
        private String title;
        private String author;
        private String asin;

        private Builder() {

        }

        /**
         * With title builder.
         *
         * @param titleToUse the title to use
         *
         * @return the builder
         */
        public Builder withTitle(String titleToUse) {
            this.title = titleToUse;
            return this;
        }

        /**
         * With author builder.
         *
         * @param authorToUse the author to use
         *
         * @return the builder
         */
        public Builder withAuthor(String authorToUse) {
            this.author = authorToUse;
            return this;
        }

        /**
         * With asin builder.
         *
         * @param asinToUse the asin to use
         *
         * @return the builder
         */
        public Builder withAsin(String asinToUse) {
            this.asin = asinToUse;
            return this;
        }

        /**
         * Build book recommendation.
         *
         * @return the book recommendation
         */
        public BookRecommendation build() {
            return new BookRecommendation(this);
        }
    }
}
