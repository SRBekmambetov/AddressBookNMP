package ru.srb.addressbooknmp.entity;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name = "person")
@EqualsAndHashCode(of = "id")
@ToString(of = "fio")
public class Person {

    private IntegerProperty id = new SimpleIntegerProperty();
    private StringProperty fio = new SimpleStringProperty("");
    private StringProperty phone = new SimpleStringProperty("");

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int getId() {
        return id.get();
    }

    @Column(name = "fio")
    public String getFio() {
        return fio.get();
    }

    @Column(name = "phone")
    public String getPhone() {
        return phone.get();
    }

    @Column(name = "photo")
    @Getter
    @Setter
    private byte[] photo;

    @Column(name = "address")
    @Getter
    @Setter
    private String address;

    public void setId(int id) {
        this.id.set(id);
    }

    public void setFio(String fio) {
        this.fio.set(fio);
    }

    public void setPhone(String phone) {
        this.phone.set(phone);
    }
}
