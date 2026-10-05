package customer_api.controller;

import customer_api.model.response.CustomerResponse;
import customer_api.model.response.CustomerUpdateResponse;
import customer_api.model.request.CustomerUpdateRequest;
import customer_api.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping(value = "/{id}")
    public CustomerResponse getCustomer(@PathVariable Long id) {
        CustomerResponse response = customerService.getCustomerById(id);
        return response;
    }

    @PutMapping(value = "/{id}")
    public CustomerUpdateResponse updateCustomer(@PathVariable Long id,
            @Valid @RequestBody CustomerUpdateRequest request) {

        return customerService.updateCustomer(id, request);
    }
}
