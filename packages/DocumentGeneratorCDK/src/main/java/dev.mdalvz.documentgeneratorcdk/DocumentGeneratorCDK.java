package dev.mdalvz.documentgeneratorcdk;

import dev.mdalvz.documentgeneratorcdk.stack.DocumentGeneratorStack;
import lombok.NonNull;
import software.amazon.awscdk.App;

public class DocumentGeneratorCDK {

  public static void main(final @NonNull String[] args) {
    final App app = new App();
    new DocumentGeneratorStack(app);
    app.synth();
  }

}
