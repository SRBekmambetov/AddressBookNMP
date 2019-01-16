package ru.srb.addressbooknmp.fxml;

import org.springframework.stereotype.Component;

@Component
public class EditView extends SpringFxmlView {

    private static final String FXML_EDIT = "ru/srb/addressbooknmp/fxml/edit.fxml";

    public EditView() {
        super(EditView.class.getClassLoader().getResource(FXML_EDIT));
    }
}
