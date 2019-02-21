package com.github.steven.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * ConfigProperties.
 *
 * @author steven
 */
@Component
@ConfigurationProperties(prefix = "com.github.steven")
public class ConfigProperties extends AutoConfig {
}
