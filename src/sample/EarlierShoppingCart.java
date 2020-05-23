package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Spinner;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import se.chalmers.cse.dat216.project.IMatDataHandler;
import se.chalmers.cse.dat216.project.Order;
import se.chalmers.cse.dat216.project.ShoppingCart;
import se.chalmers.cse.dat216.project.ShoppingItem;

import java.io.IOException;

public class EarlierShoppingCart extends TitledPane {
    @FXML ListView earlierPurchases;
    @FXML Label totalPrice;
    @FXML TitledPane listTitledPane;

    private IMatDataHandler iMatDataHandler = IMatDataHandler.getInstance();
    private Controller parentController;
    private Order order;

    public EarlierShoppingCart(Order order, Controller controller) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("earlierShoppingCart.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        this.parentController = controller;
        this.order = order;

        listTitledPane.setText(order.getDate().toString());

        for (ShoppingItem item : order.getItems()) {
            earlierPurchases.getItems().add(item.getProduct());
        }
    }
}
