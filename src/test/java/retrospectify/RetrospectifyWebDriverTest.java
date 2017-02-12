package retrospectify;

import org.junit.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.seleniumhq.selenium.fluent.FluentWebDriver;
import org.seleniumhq.selenium.fluent.FluentWebElement;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.openqa.selenium.By.className;
import static org.seleniumhq.selenium.fluent.Period.millis;
import static org.seleniumhq.selenium.fluent.Period.secs;

public class RetrospectifyWebDriverTest {

  public static class TestApp extends App {

    private boolean appStarted;

    @Override
    protected void extraRoutes() {
      get("test-harness/note.html", new SSIHandler("/test-harness/note.html", "text/html"));
    }

    public TestApp() {
      onStarted(() -> appStarted = true);
    }

  }

  private static ChromeDriver DRIVER;
  private static FluentWebDriver FWD;
  private static int testNum;

  private TestApp app;

  @BeforeClass
  public static void sharedForAllTests() {
    // Keep the WebDriver browser window open between tests
    DRIVER = new ChromeDriver();
    FWD = new FluentWebDriver(DRIVER);
  }

  @AfterClass
  public static void tearDown() {
    DRIVER.close();
    DRIVER.quit();
  }

  private String domain;

  @Before
  public void perTest() {
    // anySubDomainOf.devd.io maps to 127.0.0.1
    // I sure hope those people don't let the domain go, or remap it.
    // It is a decent way to ensure nothing is shared between tests (mostly)
    domain = "http://t" + testNum++ + ".devd.io:8080";
  }

  @After
  public void stopServer() {
    app.stop();
  }

  @Test
  public void positiveCardCanBeCreated() {
    startApp(new TestApp());
    openRetrospectifyPage("/index.html");
    createCardAssertContentsAndClose("positive", "positive", "This went well");
  }

  @Test
  public void neutralCardCanBeCreated() {
    startApp(new TestApp());
    openRetrospectifyPage("/index.html");
    createCardAssertContentsAndClose("neutral", "neutral", "Just a remark");
  }

  @Test
  public void negativeCardCanBeCreated() {
    startApp(new TestApp());
    openRetrospectifyPage("/index.html");
    createCardAssertContentsAndClose("negative","improvement", "This needs some improvement");
  }

  private void createCardAssertContentsAndClose(String openButton, String mood, String blurb) {
    FWD.within(secs(2)).button(className(openButton)).click();
    FluentWebElement div = FWD.within(millis(1500)).div(className("note " + mood));
    div.textarea().getAttribute("value").shouldBe(blurb);
    div.button(className("note-remove")).click();
    FWD.without(millis(200)).div(className("note " + mood));
  }

  @Test
  public void testNote() throws InterruptedException {
    startApp(new TestApp());
    openRetrospectifyPage("/test-harness/note.html");

    // TODO get the test harness working
    //Thread.sleep(10000000);
  }

  private void openRetrospectifyPage(String page) {
    DRIVER.get(domain + page);
  }

  private void startApp(TestApp app) {
    this.app = app;
    app.start("server.join=false");
    while (!app.appStarted) {
      try {
        Thread.sleep(15);
      } catch (InterruptedException e) {
      }
    }
  }

}
