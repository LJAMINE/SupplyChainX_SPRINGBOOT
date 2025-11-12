package org.example.supplychainx.livraison;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.supplychainx.common.security.AuthService;
import org.example.supplychainx.common.security.AuthenticatedUser;
import org.example.supplychainx.common.security.Role;
import org.example.supplychainx.livraison.entity.Customer;
import org.example.supplychainx.livraison.repository.CustomerRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
 import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.nullable;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for Customer endpoints.
 * Auth is mocked (AuthService) to bypass header-based authentication done by SecurityAspect.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
public class CustomerControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ObjectMapper objectMapper;

    // Mock the AuthService used by SecurityAspect so tests don't need to send headers or hit user repo
    @MockBean
    private AuthService authService;

    private Customer existing;

    @BeforeEach
    void setup(){
        // stub AuthService to return an authenticated principal for any email/password (including null)

        var principal = new AuthenticatedUser(1L, "test@example.com", Role.GESTIONNAIRE_COMMERCIAL);
        when(authService.authenticate(nullable(String.class), nullable(String.class))).thenReturn(principal);

        customerRepository.deleteAll();

        existing = new Customer();
        existing.setName("ACME");
        existing.setAddress("rue 2");
        existing.setCity("casa");
        existing.setPhone("0615155212");
        existing.setCreatedAt(LocalDate.now());
        existing = customerRepository.save(existing);
    }

    @AfterEach
    void tearDown() {
        // nothing special to clear because SecurityAspect sets and clears SecurityContext per request
        customerRepository.deleteAll();
    }

    @Test
    void getList_returnsPagedCustomers() throws Exception {
        mockMvc.perform(get("/api/customers")
                        .param("page","0")
                        .param("size","20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("ACME"));
    }

    @Test
    void getById_existing_returnsCustomer() throws Exception {
        mockMvc.perform(get("/api/customers/{id}", existing.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(existing.getId()))
                .andExpect(jsonPath("$.name").value("ACME"));
    }

    @Test
    void create_valid_createsAndReturns201() throws Exception {
        var payload = """
                {
                  "name": "NEWCO",
                  "address": "rue 10",
                  "city": "rabat",
                  "phone": "0611223344"
                }
                """;

        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("NEWCO"));
    }

    @Test
    void update_existing_updatesAndReturnsOk() throws Exception {
        var payload = """
                {
                  "name": "ACME-EDIT",
                  "address": "rue updated",
                  "city": "casa",
                  "phone": "0615155212"
                }
                """;

        mockMvc.perform(put("/api/customers/{id}", existing.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("ACME-EDIT"));
    }

    @Test
    void delete_existing_deletesAndReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/customers/{id}", existing.getId()))
                .andExpect(status().isNoContent());
    }
}