package inu.graduation.sns.controller;

import inu.graduation.sns.domain.Category;
import inu.graduation.sns.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    // 카테고리 리스트 조회
    @GetMapping("/categories")
    public List<Category> findAllCategoryList(){
        List<Category> allCateogryList = categoryService.findAllCateogryList();

        return allCateogryList;
    }
}
