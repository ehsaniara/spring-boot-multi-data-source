package com.ehsaniara.multidatasource.service;

import com.ehsaniara.multidatasource.model.Customer;
import com.ehsaniara.multidatasource.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Optional;

/**
 * @author Jay Ehsaniara, Dec 30 2019
 */
@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Optional<Customer> getCustomer(Long id) {
        return customerRepository.findById(id);
    }

    public Customer createCustomer(Customer customer) {

        Assert.notNull(customer, "Invalid customer");
        Assert.isNull(customer.getId(), "customer id should be null");
        Assert.notNull(customer.getName(), "Invalid customer name");

        return customerRepository.save(customer);
    }

    public Customer updateCustomer(Customer customer) {

        Assert.notNull(customer, "Invalid customer");
        Assert.notNull(customer.getId(), "Invalid customer id");

        return customerRepository.save(customer);
    }
}
