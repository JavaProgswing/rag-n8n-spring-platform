package dev.yashasvi.ragplatform;

import dev.yashasvi.ragplatform.config.RagProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(RagProperties.class)
public class RagN8nSpringPlatformApplication {

	public static void main(String[] args) {
		SpringApplication.run(RagN8nSpringPlatformApplication.class, args);
	}

}
