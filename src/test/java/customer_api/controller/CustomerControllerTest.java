package customer_api.controller;

import customer_api.model.request.CustomerUpdateRequest;
import customer_api.model.response.CustomerResponse;
import customer_api.model.response.CustomerUpdateResponse;
import customer_api.service.CustomerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CustomerService customerService;

    @Test
    void shouldReturnCustomerWhenIdExists() throws Exception {

        CustomerResponse response = new CustomerResponse(
                1L,
                "Joni",
                "joni@gmail.com",
                "joni.jpg"
        );

        when(customerService.getCustomerById(1L))
                .thenReturn(response);

        mockMvc.perform(get("/api/v1/customer/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Joni"))
                .andExpect(jsonPath("$.email").value("joni@gmail.com"))
                .andExpect(jsonPath("$.photo").value("joni.jpg"));
    }

    @Test
    void shouldUpdateCustomerWhenRequestIsValid() throws Exception {

        CustomerUpdateResponse response = new CustomerUpdateResponse(true);

        when(customerService.updateCustomer(
                eq(1L),
                any(CustomerUpdateRequest.class)
        )).thenReturn(response);

        mockMvc.perform(put("/api/v1/customer/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "name": "Joni New",
                                "email": "joni.new@gmail.com",
                                "photo": "joni-new.jpg"
                            }
                            """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.updated").value(true));
    }

    @Test
    void shouldReturnBadRequestWhenRequestIsInvalid() throws Exception {

        mockMvc.perform(put("/api/v1/customer/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "name": "",
                                "email": "joni@gmail.com",
                                "photo": "joni.jpg"
                            }
                            """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Name is required"));
    }
}