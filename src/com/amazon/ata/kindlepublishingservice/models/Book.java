package com.amazon.ata.kindlepublishingservice.models;

import java.util.Objects;

/**
 * The type Book.
 */
public class Book {
    private String bookId;
    private String title;
    private String author;
    private String text;
    private String genre;
    private int version;

    /**
     * Instantiates a new Book.
     *
     * @param bookId  the book id
     * @param title   the title
     * @param author  the author
     * @param text    the text
     * @param genre   the genre
     * @param version the version
     */
    public Book(String bookId, String title, String author, String text, String genre, int version) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.text = text;
        this.genre = genre;
        this.version = version;
    }

    /**
     * Instantiates a new Book.
     *
     * @param builder the builder
     */
    public Book(Builder builder) {
        this.bookId = builder.bookId;
        this.title = builder.title;
        this.author = builder.author;
        this.text = builder.text;
        this.genre = builder.genre;
        this.version = builder.version;
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

    /**
     * Gets version.
     *
     * @return the version
     */
    public int getVersion() {
        return version;
    }

    /**
     * Sets version.
     *
     * @param version the version
     */
    public void setVersion(int version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Book book = (Book) o;
        return version == book.version &&
                       Objects.equals(bookId, book.bookId) &&
                       Objects.equals(title, book.title) &&
                       Objects.equals(author, book.author) &&
                       Objects.equals(text, book.text) &&
                       genre == book.genre;
    }

    @Override
    public int hashCode() {
        return Objects.hash(bookId, title, author, text, genre, version);
    }

    @Override
    public String toString() {
        return "Book{" +
                       "bookId='" + bookId + '\'' +
                       ", title='" + title + '\'' +
                       ", author='" + author + '\'' +
                       ", text='" + text + '\'' +
                       ", genre='" + genre + '\'' +
                       ", version=" + version +
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
        private int version;

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
         * With version builder.
         *
         * @param versionToUse the version to use
         *
         * @return the builder
         */
        public Builder withVersion(int versionToUse) {
            this.version = versionToUse;
            return this;
        }

        /**
         * Build book.
         *
         * @return the book
         */
        public Book build() {
            return new Book(this);
        }
    }
}
