package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.FlowPane;
import se.chalmers.cse.dat216.project.IMatDataHandler;
import se.chalmers.cse.dat216.project.Order;
import se.chalmers.cse.dat216.project.ShoppingItem;

import java.io.IOException;

public class EarlierShoppingCart extends TitledPane {
    @FXML FlowPane earlierPurchases;
    @FXML Label totalPrice;
    @FXML TitledPane listTitledPane;

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

        // set date of purchase to DD MM YR format
        String[] strArr = order.getDate().toLocaleString().split(" ");
        String date  = "";
        for (int i = 0; i < strArr.length - 1; i++) {
            date += strArr[i] + " ";
        }
        listTitledPane.setText(date);

        for (ShoppingItem item : order.getItems()) {
            EarlierPurchaseItem item1 = new EarlierPurchaseItem(controller, item);
            item1.setMaxWidth(970);
            earlierPurchases.getChildren().add(item1);
        }
    }

    @FXML private void buyThisCart(){
        parentController.buyEarlierCart(order);
    }
}
