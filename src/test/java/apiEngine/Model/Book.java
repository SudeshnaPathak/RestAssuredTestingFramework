package apiEngine.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Book {
    public String isbn;
    public String title;
    public String subTitle;
    public String author;
    public String publish_date1;
    public String publisher;
    public Integer pages;
    public String description;
    public String website;
}
