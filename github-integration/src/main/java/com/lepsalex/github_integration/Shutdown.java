package com.lepsalex.github_integration;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class Shutdown extends Thread {

    @NonNull
    PlugIntClient plugIntClient;

    @Override
    public void run() {
        plugIntClient.closePulsarConnection();
    }
}
