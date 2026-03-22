package apiEngine.Model.Requests;

import apiEngine.Model.ISBN;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
public class AddBookRequest {
    private UUID userId;
    private List<ISBN> collectionOfIsbns;
}
