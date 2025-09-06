package com.example.api.dto.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {
  private List<T> content;
  private int number;
  private int size;
  private long totalElements;
}
