package dev.mdalvz.documentgeneratorrenderlambda.lib;

import com.google.inject.Inject;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.print.PageMargin;
import org.openqa.selenium.print.PageSize;
import org.openqa.selenium.print.PrintOptions;

import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.Base64;

@AllArgsConstructor(onConstructor_ = @__(@Inject))
public class Renderer {

  private final @NonNull FileServer fileServer;

  public @NonNull ByteBuffer render() {
    final ChromeOptions options = new ChromeOptions();
    options.addArguments("--headless", "--no-sandbox", "--disable-dev-shm-usage");
    final ChromeDriver driver = new ChromeDriver(options);
    driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    try {
      driver.get("http://" + fileServer.getHost() + "/");
      final PrintOptions printOptions = new PrintOptions();
      printOptions.setBackground(true);
      printOptions.setPageMargin(new PageMargin(0.0d, 0.0d, 0.0d, 0.0d));
      printOptions.setOrientation(PrintOptions.Orientation.PORTRAIT);
      printOptions.setScale(1.0d);
      printOptions.setPageSize(new PageSize());
      return ByteBuffer.wrap(Base64.getDecoder().decode(driver.print(printOptions).getContent()));
    } finally {
      driver.quit();
    }
  }

}