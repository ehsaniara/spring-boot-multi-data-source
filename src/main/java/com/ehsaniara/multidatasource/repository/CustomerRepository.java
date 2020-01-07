package com.ehsaniara.multidatasource.repository;

import com.ehsaniara.multidatasource.model.Customer;
import com.ehsaniara.multidatasource.repository.readRepository.CustomerReadRepository;
import com.ehsaniara.multidatasource.repository.writeRepository.CustomerWriteRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomerRepository implements CustomerRepositoryCombo {

    private final CustomerReadRepository readRepository;
    private final CustomerWriteRepository writeRepository;

    public CustomerRepository(CustomerReadRepository customerReadRepository, CustomerWriteRepository customerWriteRepository) {
        this.readRepository = customerReadRepository;
        this.writeRepository = customerWriteRepository;
    }

    @Override
    public <S extends Customer> S save(S s) {
        return writeRepository.save(s);
    }

    @Override
    public <S extends Customer> Iterable<S> saveAll(Iterable<S> iterable) {
        return writeRepository.saveAll(iterable);
    }

    @Override
    public Optional<Customer> findById(Long aLong) {
        return readRepository.findById(aLong);
    }

    @Override
    public boolean existsById(Long aLong) {
        return readRepository.existsById(aLong);
    }

    @Override
    public Iterable<Customer> findAll() {
        return readRepository.findAll();
    }

    @Override
    public Iterable<Customer> findAllById(Iterable<Long> iterable) {
        return readRepository.findAllById(iterable);
    }

    @Override
    public long count() {
        return readRepository.count();
    }

    @Override
    public void deleteById(Long aLong) {
        writeRepository.deleteById(aLong);
    }

    @Override
    public void delete(Customer customer) {
        writeRepository.delete(customer);
    }

    @Override
    public void deleteAll(Iterable<? extends Customer> iterable) {
        writeRepository.deleteAll(iterable);
    }

    @Override
    public void deleteAll() {
        writeRepository.deleteAll();
    }
}
