package org.gaung.wiwokdetok.kapsulkeaslian.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WebResponse<T> {

    private T data;

    private String errors;

    private PageInfo pageInfo;
}
