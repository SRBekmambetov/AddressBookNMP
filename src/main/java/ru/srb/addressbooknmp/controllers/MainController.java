package ru.srb.addressbooknmp.controllers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import ru.srb.addressbooknmp.entity.Person;
import ru.srb.addressbooknmp.fxml.EditView;
import ru.srb.addressbooknmp.fxml.MainView;
import ru.srb.addressbooknmp.objects.Lang;
import ru.srb.addressbooknmp.service.AddressBook;
import ru.srb.addressbooknmp.utils.DialogManager;
import ru.srb.addressbooknmp.utils.LocaleManager;

import java.util.Observable;
import java.util.ResourceBundle;

@Component
public class MainController extends Observable {

    private static final String RU_CODE = "ru";
    private static final String EN_CODE = "en";
    private static final int PAGE_SIZE = 10;
    private static final int MAX_PAGE_SHOW = 10;

    @Autowired
    private AddressBook addressBook;

    @Autowired
    private MainView mainView;

    @Autowired
    private EditView editView;

    @Autowired
    private EditController editController;

    private Stage editDialogStage;

    private Page page;// текущие постраничные данные

    private ResourceBundle resourceBundle;

    private ObservableList<Person> personList;

    @FXML
    private Button btnAdd;

    @FXML
    private Button btnEdit;

    @FXML
    private Button btnDelete;

    @FXML
    private TextField txtSearch;

    @FXML
    private Button btnSearch;

    @FXML
    private TableView tableAddressBook;

    @FXML
    private TableColumn<Person, String> columnFIO;

    @FXML
    private TableColumn<Person, String> columnPhone;

    @FXML
    private Label labelCount;

    @FXML
    private Pagination pagination;

    @FXML
    private ComboBox comboLocales;

    @FXML
    public void initialize() {
        pagination.setMaxPageIndicatorCount(MAX_PAGE_SHOW);
        this.resourceBundle = mainView.getResourceBundle();
        columnFIO.setCellValueFactory(new PropertyValueFactory<Person, String>("fio"));
        columnPhone.setCellValueFactory(new PropertyValueFactory<Person, String>("phone"));
        fillData();
        initListeners();
    }

    private void fillData() {
        fillLangComboBox();
        fillTable();
    }

    // для показа данных с первой страницы
    private void fillTable() {
        if (txtSearch.getText().trim().length() == 0) {
            page = addressBook.findAll(0, PAGE_SIZE);
        } else {
            page = addressBook.findAll(0, PAGE_SIZE, txtSearch.getText());
        }
        fillPagination(page);
        pagination.setCurrentPageIndex(0);
        updateCountLabel(page.getTotalElements());
    }

    // для показа данных с любой страницы
    private void fillTable(int pageNumber) {
        if (txtSearch.getText().trim().length() == 0) {
            page = addressBook.findAll(pageNumber, PAGE_SIZE);
        }else {
            page = addressBook.findAll(pageNumber, PAGE_SIZE, txtSearch.getText());
        }
        fillPagination(page);
        updateCountLabel(page.getTotalElements());
    }

    private void fillPagination(Page page) {
        if (page.getTotalPages() <= 1) {
            pagination.setDisable(true);
        } else {
            pagination.setDisable(false);
        }
        pagination.setPageCount(page.getTotalPages());

        personList = FXCollections.observableArrayList(page.getContent());
        tableAddressBook.setItems(personList);
    }

    private void fillLangComboBox() {

        Lang langRU = new Lang(0, RU_CODE, resourceBundle.getString("ru"), LocaleManager.RU_LOCALE);
        Lang langEN = new Lang(1, EN_CODE, resourceBundle.getString("en"), LocaleManager.EN_LOCALE);

        comboLocales.getItems().add(langRU);
        comboLocales.getItems().add(langEN);


        if (LocaleManager.getCurrentLang() == null){
            comboLocales.getSelectionModel().select(0);
            LocaleManager.setCurrentLang(langRU);
        }else{
            comboLocales.getSelectionModel().select(LocaleManager.getCurrentLang().getIndex());
        }
    }

    private void updateCountLabel(long count) {
        labelCount.setText(resourceBundle.getString("count") + ": " + count);
    }

    private void initListeners() {

        // слушает двойное нажатие для редактирования записи
        tableAddressBook.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() == 2) {
                    btnEdit.fire();
                }
            }
        });

        // слушает переключение языка
        comboLocales.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                Lang selectedLang = (Lang)observable.getValue();

                LocaleManager.setCurrentLang(selectedLang);

                // уведомить всех слушателей, что произошла смена языка
                setChanged();
                notifyObservers(selectedLang);
            }
        });

        pagination.currentPageIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                fillTable(newValue.intValue());
            }
        });
    }

    public void actionButtonPressed(ActionEvent actionEvent) {

        Object source = actionEvent.getSource();

        // если нажата не кнопка - выходим из метода
        if (!(source instanceof Button)) {
            return;
        }

        Person selectedPerson = (Person) tableAddressBook.getSelectionModel().getSelectedItem();

        Button clickedButton = (Button) source;

        boolean dataChanged = false;

        switch (clickedButton.getId()) {
            case "btnAdd":
                editController.setPerson(new Person());
                showDialog();

                if (editController.isSaveClicked()) {
                    addressBook.add(editController.getPerson());
                    dataChanged = true;
                }

                break;

            case "btnEdit":
                if (!personIsSelected(selectedPerson)) {
                    return;
                }
                editController.setPerson(selectedPerson);
                showDialog();

                if (editController.isSaveClicked()) {
                    addressBook.update(selectedPerson);
                    dataChanged = true;
                }

                break;

            case "btnDelete":
                if (!personIsSelected(selectedPerson) || !(confirmDelete())) {
                    return;
                }

                dataChanged = true;
                addressBook.delete(selectedPerson);
                break;
        }

        // обновить список, если запись была изменена
        if (dataChanged) {
            actionSearch(actionEvent);
        }
    }

    private void showDialog() {

        if (editDialogStage == null) {
            editDialogStage = new Stage();
            editDialogStage.setMinHeight(150);
            editDialogStage.setMinWidth(300);
            editDialogStage.setResizable(false);
            editDialogStage.initModality(Modality.WINDOW_MODAL);
            editDialogStage.initOwner(comboLocales.getParent().getScene().getWindow());
            editDialogStage.centerOnScreen();
        }

        editDialogStage.setScene(new Scene(editView.getView(LocaleManager.getCurrentLang().getLocale())));

        editDialogStage.setTitle(resourceBundle.getString("edit"));

        editDialogStage.showAndWait(); // для ожидания закрытия окна
    }

    private boolean personIsSelected(Person selectedPerson) {
        if(selectedPerson == null){
            DialogManager.showInfoDialog(resourceBundle.getString("error"), resourceBundle.getString("select_person"));
            return false;
        }
        return true;
    }

    private boolean confirmDelete() {
        if (DialogManager.showConfirmDialog(resourceBundle.getString("confirm"), resourceBundle.getString("confirm_delete")).get() == ButtonType.OK){
            return true;
        } else {
            return false;
        }
    }

    public void actionSearch(ActionEvent actionEvent) {

        fillTable();
    }
}
