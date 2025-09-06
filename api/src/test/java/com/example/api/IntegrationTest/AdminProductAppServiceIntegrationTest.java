package com.example.api.IntegrationTest;

import com.example.application.exceptions.ApplicationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test") // chỉ định load application-test.properties
class AdminProductAppServiceIntegrationTest {

    @Autowired
    private AdminProductAppService productService;

    @Autowired
    private ProductRepository productRepository;

    // --- CREATE ---
    @Test
    void create_whenValidRequest_thenPersistAndReturnDto() {
        ProductCreateDto dto =
                new ProductCreateDto("Product A", "Category1", BigDecimal.valueOf(100), 10);

        ProductDto result = productService.create(dto);

        assertThat(result).isNotNull();
        assertThat(result.id()).isNotNull();
        assertThat(result.name()).isEqualTo("Product A");
        assertThat(result.category()).isEqualTo("Category1");
        assertThat(result.price()).isEqualByComparingTo("100");
        assertThat(result.stock()).isEqualTo(10);

        assertThat(productRepository.findAll()).hasSize(1);
    }

    // --- GET BY ID ---
    @Test
    void getProductById_whenExists_thenReturnDto() {
        ProductCreateDto dto =
                new ProductCreateDto("Product B", "Category2", BigDecimal.valueOf(200), 5);
        ProductDto created = productService.create(dto);

        ProductDto found = productService.getProductById(created.id());

        assertThat(found).isNotNull();
        assertThat(found.id()).isEqualTo(created.id());
        assertThat(found.name()).isEqualTo("Product B");
    }

    @Test
    void getProductById_whenNotExists_thenThrowException() {
        assertThrows(ApplicationException.class, () -> productService.getProductById(999L));
    }

    // --- DELETE ---
    @Test
    void delete_whenExists_thenRemoveFromDb() {
        ProductDto created = productService.create(
                new ProductCreateDto("Product C", "Category1", BigDecimal.valueOf(300), 7)
        );

        productService.delete(created.id());

        assertThat(productRepository.findById(created.id())).isEmpty();
    }

    @Test
    void delete_whenNotExists_thenDoNothingOrThrow() {
        // repo.deleteById() mặc định không ném exception nếu không có record
        productService.delete(999L);
        assertThat(productRepository.findAll()).isEmpty();
    }

    // --- LIST ALL ---
    @Test
    void listAllProducts_whenMultipleProducts_thenReturnList() {
        productRepository.deleteAll();

        productService.create(new ProductCreateDto("Product D", "CategoryX", BigDecimal.valueOf(10), 2));
        productService.create(new ProductCreateDto("Product E", "CategoryX", BigDecimal.valueOf(20), 3));

        List<ProductDto> all = productService.listAllProducts();

        assertThat(all).hasSize(2);
        assertThat(all).extracting("name").containsExactlyInAnyOrder("Product D", "Product E");
    }

    // --- LIST WITH FILTER ---
    @Test
    void listProducts_whenFilterApplied_thenReturnPageResult() {
        productRepository.deleteAll();

        productService.create(new ProductCreateDto("Phone", "Electronics", BigDecimal.valueOf(500), 20));
        productService.create(new ProductCreateDto("Laptop", "Electronics", BigDecimal.valueOf(1500), 10));
        productService.create(new ProductCreateDto("Shirt", "Clothing", BigDecimal.valueOf(50), 100));

        ProductFilter filter = new ProductFilter("Electronics", null, null, true, null);

        PageResult<ProductDto> pageResult = productService.listProducts(filter, 0, 10);

        assertThat(pageResult.content()).hasSize(2);
        assertThat(pageResult.content()).extracting("category")
                .containsOnly("Electronics");
        assertThat(pageResult.totalElements()).isEqualTo(2);
    }
}
