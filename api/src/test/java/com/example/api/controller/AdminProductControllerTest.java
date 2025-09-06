package com.example.api.controller;

import com.example.api.controller.admin.AdminProductController;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminProductController.class)
@ContextConfiguration(classes = {AdminProductController.class})
class AdminProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AdminProductAppService productService;

    @MockitoBean
    private ProductApiMapper mapper;

    @Test
    void createProduct_whenValidRequest_thenReturnProductResponse() throws Exception {
        // Arrange
        ProductCreateDto createDto =
                new ProductCreateDto("Product A", "Category1", BigDecimal.valueOf(100), 10);
        ProductDto productDto =
                new ProductDto(1L, "Product A", "Category1", BigDecimal.valueOf(100), 10, true);
        ProductResponse product =
                new ProductResponse(1L, "Product A", "Category1", BigDecimal.valueOf(100), 10, true);

        when(mapper.toCommand(any(ProductCreateRequest.class))).thenReturn(createDto);
        when(productService.create(any(ProductCreateDto.class))).thenReturn(productDto);
        when(mapper.toResponse(any(ProductDto.class))).thenReturn(product);

        // Act & Assert
        mockMvc.perform(post("/api/admin/products")
                        .contentType("application/json")
                        .content("""
                                 {
                                   "name": "Product A",
                                   "description": "Test Product",
                                   "category": "Category1",
                                   "price": 100,
                                   "stock": 10
                                 }
                                 """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Product A"))
                .andExpect(jsonPath("$.data.category").value("Category1"));

        // Verify bằng ArgumentCaptor
        ArgumentCaptor<ProductCreateDto> captor = ArgumentCaptor.forClass(ProductCreateDto.class);
        verify(productService, times(1)).create(captor.capture());
        ProductCreateDto actualDto = captor.getValue();

        assert actualDto.name().equals("Product A");
        assert actualDto.category().equals("Category1");
    }
}
