package customer_api.service;

import customer_api.model.entity.Customer;
import customer_api.exception.CustomerDuplicateException;
import customer_api.exception.CustomerNotFoundException;
import customer_api.model.response.CustomerResponse;
import customer_api.model.response.CustomerUpdateResponse;
import customer_api.model.request.CustomerUpdateRequest;
import customer_api.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;

    public CustomerResponse getCustomerById(Long id) {

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("Customer with id " + id + " not found!"));

        return new CustomerResponse(customer.getId(),
                customer.getName(),
                customer.getEmail(),
                customer.getPhoto());
    }

    public CustomerUpdateResponse updateCustomer(
            Long id,
            CustomerUpdateRequest request) {

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(
                        "Customer with id " + id + " not found"
                ));

        if (Objects.equals(customer.getName(), request.getName())
                && Objects.equals(customer.getEmail(), request.getEmail())
                && Objects.equals(customer.getPhoto(), request.getPhoto())) {

            return new CustomerUpdateResponse(false);
        }

        customerRepository.findDuplicateCustomer(
                        id,
                        request.getName(),
                        request.getEmail(),
                        request.getPhoto()
                )
                .ifPresent(duplicate -> {
                    throw new CustomerDuplicateException(
                            "Customer with the same name, email, or photo already exists!"
                    );
                });

        customer.setName(request.getName());
        customer.setEmail(request.getEmail());
        customer.setPhoto(request.getPhoto());

        customerRepository.save(customer);

        return new CustomerUpdateResponse(true);
    }
}
