package io.javabrains.betterreadsdataloader.connection;

import org.springframework.boot.context.properties.ConfigurationProperties;
import java.io.File;

@ConfigurationProperties(prefix ="datastax.astra")
public class DataStaxAstraProperties {

    private File secureConnectionBundle;

    public File getSecureConnectionBundle() {
        return secureConnectionBundle;
    }

    public void setSecureConnectionBundle(File secureConnectionBundle) {
        this.secureConnectionBundle = secureConnectionBundle;
    }
    

}
