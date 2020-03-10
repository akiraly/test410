import static org.assertj.core.api.Assertions.*;

import org.jdbi.v3.core.Jdbi;
import org.junit.Test;

public class JdbiTest {

  @Test
  public void test() {
    Jdbi db = Jdbi.create("jdbc:h2:mem:foo;TRACE_LEVEL_SYSTEM_OUT=2");
    db.withHandle(h -> {
      h.execute("create table foo (id varchar(64) primary key, name varchar(255) not null)");

      assertThat(h.createQuery("select count(*) from foo").mapTo(Long.class).one()).isEqualTo(0);

      h.createUpdate("insert into foo (id, name) values (:id, :name)")
          .bind("id", "baz")
          .bind("name", "Jane Doe")
          .execute();

      assertThat(h.createQuery("select count(*) from foo").mapTo(Long.class).one()).isEqualTo(1);

      Insert.into("foo")
          .bind("id", "bar")
          .bind("name", "John Doe")
          .execute(h);

      assertThat(h.createQuery("select count(*) from foo").mapTo(Long.class).one()).isEqualTo(2);

      return null;
    });
  }

}
