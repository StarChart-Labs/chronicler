package org.starchartlabs.chronicler.main.app;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MainAppServerConfiguration {

    @Bean
    public RootRestServer rootRestServer(@Value("${github.app.installation.url}") String installationUrl) {
        return new RootRestServer(installationUrl);
    }

}
