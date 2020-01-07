package com.ehsaniara.multidatasource.repository;

import com.ehsaniara.multidatasource.repository.readRepository.CustomerReadRepository;
import com.ehsaniara.multidatasource.repository.writeRepository.CustomerWriteRepository;

public interface CustomerRepositoryCombo extends CustomerReadRepository, CustomerWriteRepository {

}
