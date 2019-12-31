package com.ehsaniara.multidatasource.controller;

import com.ehsaniara.multidatasource.handler.ResourceNotFoundException;
import com.ehsaniara.multidatasource.model.Customer;
import com.ehsaniara.multidatasource.service.CustomerService;
import org.springframework.web.bind.annotation.*;

/**
 * @author Jay Ehsaniara, Dec 30 2019
 */
@RestController
public class CustomerControllerImpl {

    private final CustomerService customerService;

    public CustomerControllerImpl(CustomerService customerService) {
        this.customerService = customerService;
    }

    @RequestMapping(path = "/customer/{id}", method = RequestMethod.GET)
    public Customer getCustomer(@PathVariable("id") Long id) {

        return customerService.getCustomer(id).orElseThrow(() -> new ResourceNotFoundException("Invalid Customer"));
    }

    @RequestMapping(path = "/customer", method = RequestMethod.POST)
    public Customer createCustomer(@RequestBody Customer customer) {
        return customerService.createCustomer(customer);
    }

    @RequestMapping(path = "/customer", method = RequestMethod.PUT)
    public Customer updateCustomer(@RequestBody Customer customer) {
        return customerService.updateCustomer(customer);
    }
}
