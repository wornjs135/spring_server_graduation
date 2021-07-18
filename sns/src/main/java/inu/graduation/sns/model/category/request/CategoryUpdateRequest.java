package inu.graduation.sns.model.category.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class CategoryUpdateRequest {

    @NotBlank
    private String name;
}
