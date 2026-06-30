package com.stockiq.inventory.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "oauth")
@Data
public class OAuthConfig {

  private Google google = new Google();
  private Github github = new Github();

  @Data
  public static class Google {
    private String clientId;
  }

  @Data
  public static class Github {
    private String clientId;
    private String clientSecret;
  }
}
