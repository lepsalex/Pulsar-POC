
package com.lepsalex.github_integration;

public class Application {

  public static void main(String[] args) {
    PlugIntClient plugIntClient = new PlugIntClient();
    plugIntClient.run();

    Runtime.getRuntime().addShutdownHook(new Shutdown(plugIntClient));
  }
}
