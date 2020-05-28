package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import se.chalmers.cse.dat216.project.IMatDataHandler;
import se.chalmers.cse.dat216.project.ShoppingItem;

import java.io.IOException;

public class EarlierPurchaseItem extends AnchorPane {
    @FXML Label levelName,levelUnitPrice,levelProductNumber,levelTotalPrice;
    @FXML ImageView shoppingCartImg;

    IMatDataHandler iMatDataHandler = IMatDataHandler.getInstance();
    private Controller controller;
    ShoppingItem item;

    public EarlierPurchaseItem(Controller controller, ShoppingItem item) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("earlierPurchaseItem.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        this.controller = controller;
        this.item = item;

        shoppingCartImg.setImage(iMatDataHandler.getFXImage(item.getProduct()));
        levelName.setText(item.getProduct().getName());
        levelUnitPrice.setText(item.getProduct().getPrice()  + " " + item.getProduct().getUnit());
        levelTotalPrice.setText(controller.round(item.getTotal(), 2) + " kr");
        if(item.getProduct().getUnitSuffix().equals("st") || item.getProduct().getUnitSuffix().equals("förp")  ) {
            levelProductNumber.setText(item.getAmount() + " " + item.getProduct().getUnitSuffix());
        }
        else {
            levelProductNumber.setText(controller.round(item.getAmount(), 2) + " " + item.getProduct().getUnitSuffix());
        }
    }
}
