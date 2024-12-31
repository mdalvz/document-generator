package dev.mdalvz.documentgeneratorrenderlambda.lib;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.SimpleFileServer;
import lombok.Getter;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.util.UUID;

@Getter
public class FileServer {

  private static final int PORT = 35085;

  private final String host = "localhost:" + PORT;

  private final File root;

  public FileServer() {
    this.root = new File("/tmp/" + UUID.randomUUID());
    try {
      FileUtils.forceMkdir(this.root);
    } catch (final IOException e) {
      throw new UncheckedIOException(e);
    }
    start();
  }

  public void reset() {
    try {
      FileUtils.cleanDirectory(this.root);
    } catch (final IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private void start() {
    new Thread(() -> {
      final InetSocketAddress address =  new InetSocketAddress(PORT);
      final HttpServer server = SimpleFileServer.createFileServer(
          address,
          this.root.toPath(),
          SimpleFileServer.OutputLevel.INFO);
      server.start();
    }).start();
    try {
      Thread.sleep(1000);
    } catch (final InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

}
