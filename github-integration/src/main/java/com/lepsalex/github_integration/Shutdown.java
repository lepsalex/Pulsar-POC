package com.lepsalex.github_integration;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class Shutdown extends Thread {

    @NonNull
    Client client;

    @Override
    public void run() {
        client.shutdown();
    }
}
