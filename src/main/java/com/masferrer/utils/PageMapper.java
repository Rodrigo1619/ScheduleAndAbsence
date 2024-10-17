package com.masferrer.utils;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com.masferrer.models.dtos.PageDTO;

@Component
public class PageMapper {
    public <T,U> PageDTO<T> map(List<T> customList, Page<U> page) {
        PageDTO<T> response = new PageDTO<>(
            customList,
            page.getNumber(),
            page.getSize(),
            page.getTotalElements(),
            page.getTotalPages()
        );

        return response;
    }

    public <T> PageDTO<T> map(Page<T> page) {
        PageDTO<T> response = new PageDTO<>(
            page.getContent(),
            page.getNumber(),
            page.getSize(),
            page.getTotalElements(),
            page.getTotalPages()
        );

        return response;
    }
}
