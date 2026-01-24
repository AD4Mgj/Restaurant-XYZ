package kul.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import kul.example.model.Order;
import kul.example.repository.OrderRepository;
import kul.example.service.UserSession;
import kul.example.utils.SceneUtils;

public class OrderHistoryController {

    @FXML
    private TableView<Order> orderTable;

    @FXML
    private TableColumn<Order, Long> idColumn;

    @FXML
    private TableColumn<Order, String> addressColumn;

    @FXML
    private TableColumn<Order, String> totalColumn;

    @FXML
    private TableColumn<Order, String> dateColumn;

    @FXML
    private void onBackClick() {
        SceneUtils.changeScene("/view/userDashboard.fxml");
    }

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleLongProperty(data.getValue().getId()).asObject());
        addressColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getAddress()));
        totalColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getTotal() + " zł"));
        dateColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getTimestamp()));

        loadOrders();
    }

    private void loadOrders() {
        String email = UserSession.get().getEmail();
        ObservableList<Order> list = FXCollections.observableArrayList(
                OrderRepository.findByUser(email)
        );
        orderTable.setItems(list);
    }
}