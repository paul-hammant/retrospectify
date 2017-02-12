package retrospectify;

import org.jooby.Jooby;
import org.jooby.json.Jackson;

@SuppressWarnings({"unchecked", "rawtypes" })
public class App extends Jooby {

  {
    /** JSON: */
    use(new Jackson());
    use("/someUrl").get(() -> "x");
    extraRoutes();
    get("/index.html", new SSIHandler("/index.html", "text/html"));
    assets("/**");
  }

  protected void extraRoutes() {
  }

  public static void main(final String[] args) {
    run(App::new, args);
  }

}
