package classes;


public class QueryValidator {
    private final String query;

    public QueryValidator(String query) {
        this.query = query;
    }

    public String validateQuery() {
        return query.replaceAll("'", "''");
    }
}
