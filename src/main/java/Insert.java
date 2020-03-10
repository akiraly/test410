import static com.google.common.base.Preconditions.checkState;
import static java.util.stream.Collectors.joining;

import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.function.Function;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.statement.Update;

public interface Insert {

  static Insert into(String tableName) {
    return new Insert() {
      private final LinkedHashMap<String, Function<Update, Update>> updateCustomizers = new LinkedHashMap<>();

      @Override
      public Insert bind(String columnName, String columnValue) {
        updateCustomizers.put(columnName, u -> u.bind(columnName, columnValue));
        return this;
      }

      @Override
      public int execute(Handle h) {
        checkState(!updateCustomizers.isEmpty());
        String sql = "insert into " + tableName
            + " ("
            + String.join(", ", updateCustomizers.keySet())
            + ") values ("
            + updateCustomizers.keySet().stream().map(n -> ":" + n).collect(joining(", "))
            + ")";
        System.out.println(sql);
        Update update = h.createUpdate(sql);
        for (Entry<String, Function<Update, Update>> customizer : updateCustomizers.entrySet()) {
          update = customizer.getValue().apply(update);
        }
        return update.execute();
      }
    };
  }

  Insert bind(String columnName, String columnValue);

  int execute(Handle h);
}
