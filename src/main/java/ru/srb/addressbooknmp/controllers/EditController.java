package ru.srb.addressbooknmp.controllers;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.srb.addressbooknmp.entity.Person;
import ru.srb.addressbooknmp.fxml.SpringFxmlView;
import ru.srb.addressbooknmp.utils.DialogManager;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ResourceBundle;

@Component
public class EditController {

    private static final FileChooser fileChooser = new FileChooser();

    @Autowired
    private SpringFxmlView editView;

    @Getter
    private Person person;

    private ResourceBundle resourceBundle;

    private boolean saveClicked = false; // для определения нажатой кнопки

    @FXML
    private Button btnOk;

    @FXML
    private Button btnCancel;

    @FXML
    private TextField txtFIO;

    @FXML
    private TextArea txtAddress;

    @FXML
    private ImageView imagePhoto;

    @FXML
    private TextField txtPhone;

    @FXML
    public void initialize() {
        this.resourceBundle = editView.getResourceBundle();
        if (person != null) {
            txtFIO.setText(person.getFio());
            txtPhone.setText(person.getPhone());
            txtAddress.setText(person.getAddress());

            if (person.getPhoto() != null){
                imagePhoto.setImage(new Image(new ByteArrayInputStream(person.getPhoto())));
            }
        }
    }

    public void setPerson(Person person) {
        if (person == null) {
            return;
        }
        saveClicked = false;
        this.person = person;
    }

    public boolean isSaveClicked() {
        return saveClicked;
    }

    public void uploadPhoto() {
        File file = fileChooser.showOpenDialog(imagePhoto.getScene().getWindow());
        if (file != null) {
            try {
                imagePhoto.setImage(new Image(new FileInputStream(file)));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void loadPhoto(MouseEvent mouseEvent) {
        uploadPhoto();
    }

    // сохранить все измененные значения
    public void actionSave(ActionEvent actionEvent) {
        if (!checkValues()) {
            return;
        }
        person.setFio(txtFIO.getText());
        person.setPhone(txtPhone.getText());
        person.setAddress(txtAddress.getText());
        person.setPhoto(convertImage(imagePhoto.getImage()));
        saveClicked = true;
        actionClose(actionEvent);
    }

    private byte[] convertImage(Image image) {
        BufferedImage bImage = SwingFXUtils.fromFXImage(image, null);

        byte[] bytes = null;
        try (ByteArrayOutputStream s = new ByteArrayOutputStream()){
            ImageIO.write(bImage, "jpg", s);
            bytes = s.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }

    public void actionClose(ActionEvent actionEvent) {
        Node source = (Node) actionEvent.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.hide();
    }

    private boolean checkValues() {
        if (txtFIO.getText().trim().length() == 0 || txtPhone.getText().trim().length() == 0) {
            DialogManager.showInfoDialog(resourceBundle.getString("error"), resourceBundle.getString("fill_field"));
            return false;
        }
        return true;
    }
}
