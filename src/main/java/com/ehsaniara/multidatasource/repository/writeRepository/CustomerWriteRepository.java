package com.ehsaniara.multidatasource.repository.writeRepository;

import com.ehsaniara.multidatasource.model.Customer;
import org.springframework.data.repository.CrudRepository;

/**
 * @author Jay Ehsaniara, Dec 30 2019
 */
public interface CustomerWriteRepository extends CrudRepository<Customer, Long> {
}
