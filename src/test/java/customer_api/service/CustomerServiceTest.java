package customer_api.service;

import customer_api.exception.CustomerDuplicateException;
import customer_api.exception.CustomerNotFoundException;
import customer_api.model.entity.Customer;
import customer_api.model.request.CustomerUpdateRequest;
import customer_api.model.response.CustomerResponse;
import customer_api.model.response.CustomerUpdateResponse;
import customer_api.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class CustomerServiceTest {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CacheManager cacheManager;

    @MockitoBean
    private CustomerRepository customerRepository;

    @BeforeEach
    void clearCache() {
        cacheManager.getCache("customer").clear();
    }

    @Test
    void shouldReturnCustomerWhenIdExists() {

        Customer customer = new Customer(
                1L,
                "Joni",
                "joni@gmail.com",
                "joni.jpg"
        );

        when(customerRepository.findById(1L))
                .thenReturn(Optional.of(customer));

        CustomerResponse result =
                customerService.getCustomerById(1L);

        assertEquals(1L, result.getId());
        assertEquals("Joni", result.getName());
        assertEquals("joni@gmail.com", result.getEmail());
        assertEquals("joni.jpg", result.getPhoto());
    }

    @Test
    void shouldThrowExceptionWhenCustomerDoesNotExist() {

        when(customerRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                CustomerNotFoundException.class,
                () -> customerService.getCustomerById(1L)
        );
    }

    @Test
    void shouldUpdateCustomerWhenDataIsDifferent() {

        Customer customer = new Customer(
                1L,
                "Joni",
                "joni@gmail.com",
                "joni.jpg"
        );

        CustomerUpdateRequest request = new CustomerUpdateRequest();
        request.setName("Joni New");
        request.setEmail("joni@gmail.com");
        request.setPhoto("joni.jpg");

        when(customerRepository.findById(1L))
                .thenReturn(Optional.of(customer));

        when(customerRepository.findDuplicateCustomer(
                1L,
                request.getName(),
                request.getEmail(),
                request.getPhoto()
        )).thenReturn(Optional.empty());

        CustomerUpdateResponse result =
                customerService.updateCustomer(1L, request);

        assertTrue(result.isUpdated());

        assertEquals("Joni New", customer.getName());
        assertEquals("joni@gmail.com", customer.getEmail());
        assertEquals("joni.jpg", customer.getPhoto());

        verify(customerRepository).save(customer);
    }

    @Test
    void shouldReturnFalseWhenCustomerDataIsUnchanged() {

        Customer customer = new Customer(
                1L,
                "Joni",
                "joni@gmail.com",
                "joni.jpg"
        );

        CustomerUpdateRequest request = new CustomerUpdateRequest();
        request.setName("Joni");
        request.setEmail("joni@gmail.com");
        request.setPhoto("joni.jpg");

        when(customerRepository.findById(1L))
                .thenReturn(Optional.of(customer));

        CustomerUpdateResponse result =
                customerService.updateCustomer(1L, request);

        assertFalse(result.isUpdated());

        verify(customerRepository, never()).save(customer);
    }

    @Test
    void shouldThrowExceptionWhenDuplicateCustomerExists() {

        Customer customer = new Customer(
                1L,
                "Joni",
                "joni@gmail.com",
                "joni.jpg"
        );

        Customer duplicateCustomer = new Customer(
                2L,
                "Mali",
                "mali@gmail.com",
                "mali.jpg"
        );

        CustomerUpdateRequest request = new CustomerUpdateRequest();
        request.setName("Mali");
        request.setEmail("newemail@gmail.com");
        request.setPhoto("new-photo.jpg");

        when(customerRepository.findById(1L))
                .thenReturn(Optional.of(customer));

        when(customerRepository.findDuplicateCustomer(
                1L,
                request.getName(),
                request.getEmail(),
                request.getPhoto()
        )).thenReturn(Optional.of(duplicateCustomer));

        assertThrows(
                CustomerDuplicateException.class,
                () -> customerService.updateCustomer(1L, request)
        );

        verify(customerRepository, never()).save(customer);
    }

    @Test
    void shouldReturnCustomerFromCacheOnSecondRequest() {

        Customer customer = new Customer(
                1L,
                "Joni",
                "joni@gmail.com",
                "joni.jpg"
        );

        when(customerRepository.findById(1L))
                .thenReturn(Optional.of(customer));

        CustomerResponse firstResult =
                customerService.getCustomerById(1L);

        CustomerResponse secondResult =
                customerService.getCustomerById(1L);

        assertEquals(firstResult.getId(), secondResult.getId());
        assertEquals(firstResult.getName(), secondResult.getName());
        assertEquals(firstResult.getEmail(), secondResult.getEmail());
        assertEquals(firstResult.getPhoto(), secondResult.getPhoto());

        verify(customerRepository, times(1))
                .findById(1L);
    }

    @Test
    void shouldEvictCacheWhenCustomerIsUpdated() {

        Customer customer = new Customer(
                1L,
                "Joni",
                "joni@gmail.com",
                "joni.jpg"
        );

        when(customerRepository.findById(1L))
                .thenReturn(Optional.of(customer));

        CustomerResponse firstResult =
                customerService.getCustomerById(1L);

        assertEquals("Joni", firstResult.getName());

        CustomerUpdateRequest request = new CustomerUpdateRequest();
        request.setName("Joni New");
        request.setEmail("joni@gmail.com");
        request.setPhoto("joni.jpg");

        when(customerRepository.findDuplicateCustomer(
                1L,
                request.getName(),
                request.getEmail(),
                request.getPhoto()
        )).thenReturn(Optional.empty());

        customerService.updateCustomer(1L, request);

        CustomerResponse secondResult =
                customerService.getCustomerById(1L);

        assertEquals("Joni New", secondResult.getName());

        verify(customerRepository, times(3))
                .findById(1L);
    }
}