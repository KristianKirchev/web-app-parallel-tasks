package com.krs.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.krs.dto.task.SortTask;
import com.krs.service.sort.model.AlgorithmType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SortingDetailsDTO implements TaskDTO {

    private AlgorithmType sortingMethod;
    private List<BigDecimal> data;
    private Long sortingTimeMillis;

    public SortingDetailsDTO(SortTask sortTask) {
        this.sortingMethod = sortTask.getAlgorithmType();
        this.data = sortTask.getUnsortedArray();
        this.sortingTimeMillis = 0L;
    }
}
