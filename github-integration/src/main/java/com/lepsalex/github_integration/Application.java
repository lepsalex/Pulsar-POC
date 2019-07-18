
package com.lepsalex.github_integration;

public class Application {

  public static void main(String[] args) {
    Client client = new Client();
    client.run();

    Runtime.getRuntime().addShutdownHook(new Shutdown(client));
  }
}
