package kul.example.controller;

import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import kul.example.model.Order;
import kul.example.repository.OrderRepository;
import kul.example.utils.SceneUtils;

public class AdminOrdersController {

    @FXML
    private TableView<Order> ordersTable;

    @FXML
    private TableColumn<Order, Long> idColumn;

    @FXML
    private TableColumn<Order, String> emailColumn;

    @FXML
    private TableColumn<Order, String> addressColumn;

    @FXML
    private TableColumn<Order, String> totalColumn;

    @FXML
    private TableColumn<Order, String> dateColumn;

    @FXML
    private TableColumn<Order, String> productsColumn;

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(data -> new SimpleLongProperty(data.getValue().getId()).asObject());
        emailColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getUserEmail()));
        addressColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAddress()));
        totalColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTotal() + " zł"));
        dateColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTimestamp()));
        productsColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getProducts())
        );

        loadOrders();
    }


    private void loadOrders() {
        ObservableList<Order> list = FXCollections.observableArrayList(
                OrderRepository.findAll()
        );
        ordersTable.setItems(list);
    }

    @FXML
    private void onBackClick() {
        SceneUtils.changeScene("/view/adminDashboard.fxml");
    }

}