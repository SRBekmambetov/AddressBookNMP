package ru.srb.addressbooknmp.objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Locale;

@Getter
@Setter
@AllArgsConstructor
public class Lang {

    private int index;
    private String code;
    private String name;
    private Locale locale;

    @Override
    public String toString() {
        return name;
    }
}
