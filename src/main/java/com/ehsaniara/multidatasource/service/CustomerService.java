package com.ehsaniara.multidatasource.service;

import com.ehsaniara.multidatasource.model.Customer;

import java.util.Optional;

/**
 * @author Jay Ehsaniara, Dec 30 2019
 */
public interface CustomerService {

    Optional<Customer> getCustomer(Long id);

    Customer createCustomer(Customer customer);

    Customer updateCustomer(Customer customer);
}
