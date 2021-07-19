package inu.graduation.sns.model.category.response;

import inu.graduation.sns.domain.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponse {

    private Long id;
    private String name;

    public CategoryResponse(Category category){
        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.id = categoryResponse.getId();
        categoryResponse.name = categoryResponse.getName();
    }
}
