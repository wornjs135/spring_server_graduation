package inu.graduation.sns.model.category.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategorySaveRequest {

    @NotBlank
    private String name;
}
