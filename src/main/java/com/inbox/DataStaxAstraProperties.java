package com.inbox;

import java.io.File;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "datastax.astra")
public class DataStaxAstraProperties {

    private File connectBundle;

    public File getSecureConnectBundle() {
        return connectBundle;
    }

    public void setSecureConnectBundle(File connectBundle) {
        this.connectBundle = connectBundle;
    }
}