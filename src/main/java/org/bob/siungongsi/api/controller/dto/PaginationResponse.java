package org.bob.siungongsi.api.controller.dto;

public record PaginationResponse(long currentPage, long totalPages, long totalResults) {
  public static PaginationResponse of(long currentPage, long totalPages, long totalResults) {
    return new PaginationResponse(currentPage, totalPages, totalResults);
  }
}
