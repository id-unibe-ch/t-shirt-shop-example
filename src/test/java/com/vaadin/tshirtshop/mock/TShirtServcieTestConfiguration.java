package com.vaadin.tshirtshop.mock;

import com.vaadin.tshirtshop.TShirtService;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Profile("test-karibu")
@Configuration
public class TShirtServcieTestConfiguration {
  @Bean
  @Primary
  public TShirtService tShirtService() {
    return Mockito.mock(TShirtService.class);
  }
}
