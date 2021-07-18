package inu.graduation.sns.service;

import inu.graduation.sns.domain.Category;
import inu.graduation.sns.exception.CategoryException;
import inu.graduation.sns.model.category.request.CategorySaveRequest;
import inu.graduation.sns.model.category.request.CategoryUpdateRequest;
import inu.graduation.sns.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    // 카테고리 생성
    @Transactional
    public Category createCategory(CategorySaveRequest categorySaveRequest) {
        if(categoryRepository.findByName(categorySaveRequest.getName()).isPresent()){
            throw new CategoryException("이미 생성된 카테고리입니다.");
        }
        Category category = Category.createCategory(categorySaveRequest.getName());

        return categoryRepository.save(category);
    }

    // 카테고리 수정
    @Transactional
    public boolean updateCategory(Long categoryId, CategoryUpdateRequest categoryUpdateRequest) {
        Category findCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryException("없는 카테고리입니다."));

        return findCategory.updateCategory(categoryUpdateRequest.getName());
    }

    // 카테고리 삭제
    @Transactional
    public boolean deleteCategory(Long categoryId) {
        Category findCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryException("없는 카테고리입니다."));
        categoryRepository.delete(findCategory);

        return true;
    }

    public List<Category> findAllCateogryList() {
        List<Category> allCategoryList = categoryRepository.findAll();

        return allCategoryList;
    }
}
