package inu.graduation.sns.model.category.dto;

import inu.graduation.sns.domain.Category;
import lombok.Data;

@Data
public class CategoryDto {
    private Long id;
    private String name;

    public static CategoryDto from(Category category){
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.id = category.getId();
        categoryDto.name = category.getName();
        return categoryDto;
    }
}
