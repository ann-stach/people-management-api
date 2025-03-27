package pl.kurs.person.model.query;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
public class FindPersonQuery {

    private List<QueryCriteria> criteria = new ArrayList<>();

    @Getter
    @Setter
    @ToString
    public static class QueryCriteria {
        private String key;
        private String operation;
        private Object value;
    }

}
