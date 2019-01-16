package ru.srb.addressbooknmp.service;

import javafx.collections.ObservableList;
import org.springframework.data.domain.Page;
import ru.srb.addressbooknmp.entity.Person;

public interface AddressBook {

    void add(Person person);

    void update(Person person);

    void delete(Person person);

    Page findAll(int from, int count);

    Page findAll(int from, int count, String... text);
}
