package com.amazon.ata.kindlepublishingservice.models.requests;

import com.amazon.ata.kindlepublishingservice.models.Book;

import java.util.Objects;


/**
 * The type Submit book for publishing request.
 */
public class SubmitBookForPublishingRequest {
    private String bookId;
    private String title;
    private String author;
    private String text;
    private String genre;

    /**
     * Instantiates a new Submit book for publishing request.
     *
     * @param bookId the book id
     * @param title  the title
     * @param author the author
     * @param text   the text
     * @param genre  the genre
     */
    public SubmitBookForPublishingRequest(String bookId, String title, String author, String text, String genre) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.text = text;
        this.genre = genre;
    }

    /**
     * Instantiates a new Submit book for publishing request.
     *
     * @param builder the builder
     */
    public SubmitBookForPublishingRequest(Builder builder) {
        this.bookId = builder.bookId;
        this.title = builder.title;
        this.author = builder.author;
        this.text = builder.text;
        this.genre = builder.genre;
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
     * Gets text.
     *
     * @return the text
     */
    public String getText() {
        return text;
    }

    /**
     * Sets text.
     *
     * @param text the text
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Gets genre.
     *
     * @return the genre
     */
    public String getGenre() {
        return genre;
    }

    /**
     * Sets genre.
     *
     * @param genre the genre
     */
    public void setGenre(String genre) {
        this.genre = genre;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SubmitBookForPublishingRequest request = (SubmitBookForPublishingRequest) o;
        return getBookId().equals(request.getBookId()) &&
                       getTitle().equals(request.getTitle()) &&
                       getAuthor().equals(request.getAuthor()) &&
                       getText().equals(request.getText()) &&
                       getGenre().equals(request.getGenre());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getBookId(), getTitle(), getAuthor(), getText(), getGenre());
    }

    @Override
    public String toString() {
        return "SubmitBookForPublishingRequest{" +
                       "bookId='" + bookId + '\'' +
                       ", title='" + title + '\'' +
                       ", author='" + author + '\'' +
                       ", text='" + text + '\'' +
                       ", genre='" + genre + '\'' +
                       '}';
    }

    /**
     * The type Builder.
     */
    public static final class Builder {

        private String bookId;
        private String title;
        private String author;
        private String text;
        private String genre;

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
         * With text builder.
         *
         * @param textToUse the text to use
         *
         * @return the builder
         */
        public Builder withText(String textToUse) {
            this.text = textToUse;
            return this;
        }

        /**
         * With genre builder.
         *
         * @param genreToUse the genre to use
         *
         * @return the builder
         */
        public Builder withGenre(String genreToUse) {
            this.genre = genreToUse;
            return this;
        }

        /**
         * Build submit book for publishing request.
         *
         * @return the submit book for publishing request
         */
        public SubmitBookForPublishingRequest build() {
            return new SubmitBookForPublishingRequest(this);
        }

        /**
         * With book builder.
         *
         * @param book the book
         *
         * @return the builder
         */
        public Builder withBook(Book book) {
            this.bookId = book.getBookId();
            this.title = book.getTitle();
            this.author = book.getAuthor();
            this.text = book.getText();
            this.genre = book.getGenre();
            return this;
        }
    }
}
