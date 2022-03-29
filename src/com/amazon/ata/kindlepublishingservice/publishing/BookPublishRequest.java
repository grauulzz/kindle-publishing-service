package com.amazon.ata.kindlepublishingservice.publishing;

import com.amazon.ata.recommendationsservice.types.BookGenre;
import java.util.Objects;

/**
 * Class representing a book publish request object.
 */
public final class BookPublishRequest {

    private final String publishingRecordId;
    private final String bookId;
    private final String title;
    private final String author;
    private final String text;
    private final BookGenre genre;

    private BookPublishRequest(Builder builder) {
        this.publishingRecordId = builder.publishingRecordId;
        this.bookId = builder.bookId;
        this.title = builder.title;
        this.author = builder.author;
        this.text = builder.text;
        this.genre = builder.genre;
    }

    /**
     * Creates a builder object to aid in BookPublishRequest object creation.
     *
     * @return the builder to create BookPublishRequest objects
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
     * Gets book id.
     *
     * @return the book id
     */
    public String getBookId() {
        return bookId;
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
     * Gets author.
     *
     * @return the author
     */
    public String getAuthor() {
        return author;
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
     * Gets genre.
     *
     * @return the genre
     */
    public BookGenre getGenre() {
        return genre;
    }

    /**
     * BookPublishRequest builder static inner class.
     */
    public static final class Builder {
        private String publishingRecordId;
        private String bookId;
        private String title;
        private String author;
        private String text;
        private BookGenre genre;

        /**
         * Builder constructor.
         */
//CHECKSTYLE:OFF:HiddenField
        public Builder() {
        }

        /**
         * Sets the publishing record id and returns a reference to this Builder to the methods can be chained
         * together.
         *
         * @param publishingRecordId The publishing record id to set.
         *
         * @return a reference to this Builder.
         */
        public Builder withPublishingRecordId(String publishingRecordId) {
            this.publishingRecordId = publishingRecordId;
            return this;
        }

        /**
         * Sets the bookId and returns a reference to this Builder to the methods can be chained
         * together.
         *
         * @param bookId The book id to set.
         *
         * @return a reference to this Builder.
         */
        public Builder withBookId(String bookId) {
            this.bookId = bookId;
            return this;
        }

        /**
         * Sets the title and returns a reference to this Builder to the methods can be chained
         * together.
         *
         * @param title The title to set.
         *
         * @return a reference to this Builder.
         */
        public Builder withTitle(String title) {
            this.title = title;
            return this;
        }

        /**
         * Sets the author and returns a reference to this Builder to the methods can be chained
         * together.
         *
         * @param author The author to set.
         *
         * @return a reference to this Builder.
         */
        public Builder withAuthor(String author) {
            this.author = author;
            return this;
        }

        /**
         * Sets the text and returns a reference to this Builder to the methods can be chained
         * together.
         *
         * @param text The text to set.
         *
         * @return a reference to this Builder.
         */
        public Builder withText(String text) {
            this.text = text;
            return this;
        }

        /**
         * Sets the genre and returns a reference to this Builder to the methods can be chained
         * together.
         *
         * @param genre The genre to set.
         *
         * @return a reference to this Builder.
         */
        public Builder withGenre(BookGenre genre) {
            this.genre = genre;
            return this;
        }

        /**
         * Returns a BookPublishRequest built from the parameters previously set.
         *
         * @return a BookPublishRequest with parameters of this Builder.
         */
        public BookPublishRequest build() {
            return new BookPublishRequest(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookPublishRequest request = (BookPublishRequest) o;
        return getPublishingRecordId().equals(request.getPublishingRecordId()) && getBookId().equals(request.getBookId()) && getTitle().equals(request.getTitle()) && getAuthor().equals(request.getAuthor()) && getText().equals(request.getText()) && getGenre() == request.getGenre();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPublishingRecordId(), getBookId(), getTitle(), getAuthor(), getText(), getGenre());
    }

    @Override
    public String toString() {
        return "BookPublishRequest{" +
                       "publishingRecordId='" + publishingRecordId + '\'' +
                       ", bookId='" + bookId + '\'' +
                       ", title='" + title + '\'' +
                       ", author='" + author + '\'' +
                       ", text='" + text + '\'' +
                       ", genre=" + genre +
                       '}';
    }
}
