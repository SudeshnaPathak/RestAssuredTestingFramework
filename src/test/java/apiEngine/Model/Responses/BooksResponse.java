package apiEngine.Model.Responses;

import apiEngine.Model.Book;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BooksResponse {
    private List<Book> books;
}
