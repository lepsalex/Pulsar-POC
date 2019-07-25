
package com.lepsalex.github_integration;

public class Application {

    public static void main(String[] args) {
        PlugIntClient plugIntClient = new PlugIntClient();
        Runtime.getRuntime().addShutdownHook(new Shutdown(plugIntClient));

        plugIntClient.run();
    }
}
