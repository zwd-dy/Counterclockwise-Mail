package com.shadougao.email.entity.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Data
@Setter
@Getter
public class PageData<T> implements Serializable {

    private Integer pageNum;
    private Integer pageSize;
    private Integer totalPages;
    private Long totalNum;
    private List<T> pageData;

}
