package com.phegondev.InventoryManagementSystem;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.phegondev.InventoryManagementSystem.dto.*;
import com.phegondev.InventoryManagementSystem.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ControllerLayerTDDTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String adminEmail;
    private final String adminPassword = "adminpassword";
    private String adminToken;

    @BeforeEach
    void setup() throws Exception {
        if (adminEmail == null) {
            adminEmail = "admin_" + System.currentTimeMillis() + "@example.com";
            RegisterRequest req = new RegisterRequest();
            req.setName("Admin");
            req.setEmail(adminEmail);
            req.setPassword(adminPassword);
            req.setPhoneNumber("123456789");
            req.setRole(UserRole.ADMIN);

            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isOk());
        }
        adminToken = getAdminToken();
    }

    private String getAdminToken() throws Exception {
        LoginRequest loginReq = new LoginRequest();
        loginReq.setEmail(adminEmail);
        loginReq.setPassword(adminPassword);

        String response = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode json = objectMapper.readTree(response);
        if (json.get("token") == null) {
            throw new IllegalStateException("Respuesta de login sin 'token': " + response);
        }
        return json.get("token").asText();
    }

    @Test
    void testRegisterUser() throws Exception {
        RegisterRequest req = new RegisterRequest();
        req.setName("TDD User");
        req.setEmail("tdduser_" + System.currentTimeMillis() + "@example.com");
        req.setPassword("password");
        req.setPhoneNumber("123456789");
        req.setRole(UserRole.MANAGER);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));
    }

    @Test
    void testCreateCategory() throws Exception {
        CategoryDTO dto = new CategoryDTO();
        dto.setName("TDD Category " + System.currentTimeMillis());

        mockMvc.perform(post("/api/categories/add")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));
    }

    @Test
    void testAddSupplier() throws Exception {
        SupplierDTO dto = new SupplierDTO();
        dto.setName("TDD Supplier " + System.currentTimeMillis());
        dto.setAddress("Test Address");

        mockMvc.perform(post("/api/suppliers/add")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));
    }

    @Test
    void testGetAllUsers() throws Exception {
        mockMvc.perform(get("/api/users/all")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));
    }

    @Test
    void testGetAllCategories() throws Exception {
        mockMvc.perform(get("/api/categories/all")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));
    }

    @Test
    void testGetAllProducts() throws Exception {
        mockMvc.perform(get("/api/products/all")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));
    }

    @Test
    void testGetAllSuppliers() throws Exception {
        mockMvc.perform(get("/api/suppliers/all")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));
    }





    // Obtener usuarios: resultado esperado (ya existe)
    @Test
    void testGetAllUsersExpected() throws Exception {
        mockMvc.perform(get("/api/users/all")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));
    }

    // Obtener categorías: resultado esperado
    @Test
    void testGetAllCategoriesExpected() throws Exception {
        mockMvc.perform(get("/api/categories/all")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));
    }

    // Obtener proveedores: resultado esperado
    @Test
    void testGetAllSuppliersExpected() throws Exception {
        mockMvc.perform(get("/api/suppliers/all")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));
    }

    @Test
    void testCreateCategoryWithPost() throws Exception {
        CategoryDTO dto = new CategoryDTO();
        dto.setName("Nueva Categoría " + System.currentTimeMillis());

        mockMvc.perform(post("/api/categories/add")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));
    }

    @Test
    void testSimpleCreateCategory() throws Exception {
        CategoryDTO dto = new CategoryDTO();
        dto.setName("CategoriaSimple_" + System.currentTimeMillis());

        mockMvc.perform(post("/api/categories/add")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));
    }

    @Test
    void testAddSupplierUno() throws Exception {
        SupplierDTO dto = new SupplierDTO();
        dto.setName("ProveedorUno_" + System.currentTimeMillis());
        dto.setAddress("Dirección Uno");

        mockMvc.perform(post("/api/suppliers/add")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));
    }

    @Test
    void testGetCategoriesSimple() throws Exception {
        mockMvc.perform(get("/api/categories/all")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));
    }

    @Test
    void testPostCategorySimple() throws Exception {
        CategoryDTO dto = new CategoryDTO();
        dto.setName("CategoriaPostSimple_" + System.currentTimeMillis());

        mockMvc.perform(post("/api/categories/add")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));
    }

    @Test
    void testDeleteCategoryStatusOnly() throws Exception {
        // 1. Crear la categoría
        String categoryName = "CategoriaDeleteSimple_" + System.currentTimeMillis();
        CategoryDTO dto = new CategoryDTO();
        dto.setName(categoryName);

        mockMvc.perform(post("/api/categories/add")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        // 2. Obtener el ID de la categoría recién creada
        String allCategoriesResponse = mockMvc.perform(get("/api/categories/all")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode categoriesJson = objectMapper.readTree(allCategoriesResponse);
        JsonNode categoriesArray = categoriesJson.get("categories");
        if (categoriesArray == null || !categoriesArray.isArray()) {
            throw new IllegalStateException("La respuesta de /api/categories/all no contiene el campo 'categories' como array: " + allCategoriesResponse);
        }
        Long categoryId = null;
        for (JsonNode category : categoriesArray) {
            if (category.get("name").asText().equals(categoryName)) {
                categoryId = category.get("id").asLong();
                break;
            }
        }
        if (categoryId == null) {
            throw new IllegalStateException("No se encontró la categoría creada. Respuesta: " + allCategoriesResponse);
        }

        // 3. Eliminar la categoría
        mockMvc.perform(delete("/api/categories/delete/" + categoryId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());

        // 4. Verificar que la categoría ya no existe
        String afterDeleteResponse = mockMvc.perform(get("/api/categories/all")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode afterDeleteJson = objectMapper.readTree(afterDeleteResponse);
        JsonNode afterDeleteArray = afterDeleteJson.get("categories");
        if (afterDeleteArray == null || !afterDeleteArray.isArray()) {
            throw new IllegalStateException("La respuesta de /api/categories/all tras borrar no contiene 'categories' como array: " + afterDeleteResponse);
        }
        boolean exists = false;
        for (JsonNode category : afterDeleteArray) {
            if (category.get("id").asLong() == categoryId) {
                exists = true;
                break;
            }
        }
        org.junit.jupiter.api.Assertions.assertFalse(exists, "La categoría no fue eliminada correctamente.");
    }

    @Test
    void testDeleteSupplierStatusOnly() throws Exception {
        // 1. Crear el proveedor
        String supplierName = "ProveedorDeleteSimple_" + System.currentTimeMillis();
        SupplierDTO dto = new SupplierDTO();
        dto.setName(supplierName);
        dto.setAddress("Dirección de prueba");

        mockMvc.perform(post("/api/suppliers/add")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        // 2. Obtener el ID del proveedor recién creado
        String allSuppliersResponse = mockMvc.perform(get("/api/suppliers/all")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode suppliersJson = objectMapper.readTree(allSuppliersResponse);
        JsonNode suppliersArray = suppliersJson.get("suppliers");
        if (suppliersArray == null || !suppliersArray.isArray()) {
            throw new IllegalStateException("La respuesta de /api/suppliers/all no contiene el campo 'suppliers' como array: " + allSuppliersResponse);
        }
        Long supplierId = null;
        for (JsonNode supplier : suppliersArray) {
            if (supplier.get("name").asText().equals(supplierName)) {
                supplierId = supplier.get("id").asLong();
                break;
            }
        }
        if (supplierId == null) {
            throw new IllegalStateException("No se encontró el proveedor creado. Respuesta: " + allSuppliersResponse);
        }

        // 3. Eliminar el proveedor
        mockMvc.perform(delete("/api/suppliers/delete/" + supplierId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());

        // 4. Verificar que el proveedor ya no existe
        String afterDeleteResponse = mockMvc.perform(get("/api/suppliers/all")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode afterDeleteJson = objectMapper.readTree(afterDeleteResponse);
        JsonNode afterDeleteArray = afterDeleteJson.get("suppliers");
        if (afterDeleteArray == null || !afterDeleteArray.isArray()) {
            throw new IllegalStateException("La respuesta de /api/suppliers/all tras borrar no contiene 'suppliers' como array: " + afterDeleteResponse);
        }
        boolean exists = false;
        for (JsonNode supplier : afterDeleteArray) {
            if (supplier.get("id").asLong() == supplierId) {
                exists = true;
                break;
            }
        }
        org.junit.jupiter.api.Assertions.assertFalse(exists, "El proveedor no fue eliminado correctamente.");
    }



    @Test
    void testGetProductById() throws Exception {
        mockMvc.perform(get("/api/products/1")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }

    @Test
    void testGetProductById_2() throws Exception {
        mockMvc.perform(get("/api/products/2")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }

    @Test
    void testGetCategoryById_3() throws Exception {
        mockMvc.perform(get("/api/categories/3")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }

    @Test
    void testGetCategoryById_4() throws Exception {
        mockMvc.perform(get("/api/categories/4")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }

    @Test
    void testGetCategoryById_6() throws Exception {
        mockMvc.perform(get("/api/categories/6")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteCategoryById() throws Exception {
        mockMvc.perform(delete("/api/categories/delete/9")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteCategoryById_2() throws Exception {
        mockMvc.perform(delete("/api/categories/delete/28")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteCategoryById_3() throws Exception {
        mockMvc.perform(delete("/api/categories/delete/27")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteSupplierById_1() throws Exception {
        mockMvc.perform(delete("/api/suppliers/delete/25")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteSupplierById_2() throws Exception {
        mockMvc.perform(delete("/api/suppliers/delete/22")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }


}
