package com.stockiq.inventory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class InventoryBackendApplication {

  public static void main(String[] args) {
    SpringApplication.run(InventoryBackendApplication.class, args);
  }
}
