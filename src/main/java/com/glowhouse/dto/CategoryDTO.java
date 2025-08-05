package com.glowhouse.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data@NoArgsConstructor@AllArgsConstructor
public class CategoryDTO {

    private Long id;
    private String categoryName;
    private String image;

}
