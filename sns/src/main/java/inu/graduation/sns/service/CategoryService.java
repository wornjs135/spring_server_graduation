package inu.graduation.sns.service;

import inu.graduation.sns.domain.Category;
import inu.graduation.sns.exception.CategoryException;
import inu.graduation.sns.model.category.request.CategorySaveRequest;
import inu.graduation.sns.model.category.request.CategoryUpdateRequest;
import inu.graduation.sns.model.category.response.CategoryResponse;
import inu.graduation.sns.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    // 카테고리 생성
    @Transactional
    public CategoryResponse createCategory(CategorySaveRequest categorySaveRequest) {
        if(categoryRepository.findByName(categorySaveRequest.getName()).isPresent()){
            throw new CategoryException("이미 생성된 카테고리입니다.");
        }
        Category category = Category.createCategory(categorySaveRequest.getName());
        Category savedCategory = categoryRepository.save(category);

        return new CategoryResponse(savedCategory);
    }

    // 카테고리 수정
    @Transactional
    public CategoryResponse updateCategory(Long categoryId, CategoryUpdateRequest categoryUpdateRequest) {
        Category findCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryException("없는 카테고리입니다."));
        if(categoryRepository.findByName(categoryUpdateRequest.getName()).isPresent()){
            throw new CategoryException("이미 생성된 카테고리입니다.");
        }
        findCategory.updateCategory(categoryUpdateRequest.getName());

        return new CategoryResponse(findCategory);
    }

    // 카테고리 삭제
    @Transactional
    public boolean deleteCategory(Long categoryId) {
        Category findCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryException("없는 카테고리입니다."));
        categoryRepository.delete(findCategory);

        return true;
    }

    // 카테고리 리스트 조회
    public List<CategoryResponse> findAllCateogryList() {
        List<Category> allCategoryList = categoryRepository.findAll();
        return allCategoryList.stream().map(category -> new CategoryResponse(category))
                .collect(Collectors.toList());
    }
}
