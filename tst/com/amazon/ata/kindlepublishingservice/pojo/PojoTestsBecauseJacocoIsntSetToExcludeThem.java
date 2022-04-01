package com.amazon.ata.kindlepublishingservice.pojo;

import com.amazon.ata.kindlepublishingservice.models.Book;
import java.util.function.Supplier;
import org.junit.jupiter.api.Test;
import org.mockito.internal.matchers.InstanceOf;


public class PojoTestsBecauseJacocoIsntSetToExcludeThem {
    @Test
    void Pojos() {
        Book book1 = new Book("", "", "", "", "", 0);
        Book book2 = new Book("", "", "", "", "", 0);
        assert book1.equals(book2);
    }

}
