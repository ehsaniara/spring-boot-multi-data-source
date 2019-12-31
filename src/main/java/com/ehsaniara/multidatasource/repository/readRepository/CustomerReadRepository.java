package com.ehsaniara.multidatasource.repository.readRepository;

import com.ehsaniara.multidatasource.model.Customer;
import org.springframework.data.repository.CrudRepository;

/**
 * @author Jay Ehsaniara, Dec 30 2019
 */
public interface CustomerReadRepository extends CrudRepository<Customer, Long> {
}
