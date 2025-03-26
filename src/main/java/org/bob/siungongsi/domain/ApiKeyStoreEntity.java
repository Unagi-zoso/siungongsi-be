package org.bob.siungongsi.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name = "api_key_store",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"key_name"})})
public class ApiKeyStoreEntity extends ModifiableEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String keyName;

  private String apiKey;

  public ApiKeyStoreEntity() {}

  public ApiKeyStoreEntity(String keyName, String apiKey) {
    this.keyName = keyName;
    this.apiKey = apiKey;
  }

  public String getApiKey() {
    return apiKey;
  }

  public void updateApiKey(String apiKey) {
    this.apiKey = apiKey;
  }
}
