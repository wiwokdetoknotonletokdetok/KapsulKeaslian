package org.gaung.wiwokdetok.kapsulkeaslian.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageInfo {

    private int size;

    private int currentPage;

    private int totalPages;

    private long totalElements;
}
