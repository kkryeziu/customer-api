package customer_api.repository;

import customer_api.model.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    @Query("SELECT c FROM Customer c " +
            "WHERE c.id <> :id " +
            "AND (c.name = :name OR c.email = :email OR c.photo = :photo)")
    Optional<Customer> findDuplicateCustomer(@Param("id") Long id, @Param("name") String name, @Param("email") String email,
                                             @Param("photo") String photo);

}
