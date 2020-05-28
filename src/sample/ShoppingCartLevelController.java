package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import se.chalmers.cse.dat216.project.IMatDataHandler;
import se.chalmers.cse.dat216.project.Product;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;

public class ShoppingCartLevelController extends AnchorPane {
    @FXML ImageView shoppingCartImg;
    @FXML Label levelName,levelUnitPrice,levelProductNumber,levelTotalPrice;
    IMatDataHandler iMatDataHandler = IMatDataHandler.getInstance();
    Product product;
    private Controller parentController;

    public ShoppingCartLevelController(Product product, Controller controller, double amount) {
        this.product = product;
        parentController = controller;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("shoppingCartLevel.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }


        levelUnitPrice.setText(product.getPrice() + " " + product.getUnit());
        levelTotalPrice.setText(parentController.round(product.getPrice()*amount,2)+" kr");
        if(product.getUnitSuffix().equals("st")||product.getUnitSuffix().equals("förp")  ){ //för att vi inte ska få 2.0 st cola burkar, vi skall få 2 st

            levelProductNumber.setText((int) amount+" "+product.getUnitSuffix());
        }
        else {
            levelProductNumber.setText(parentController.round(amount,2)+" "+product.getUnitSuffix());
        }
        levelName.setText(product.getName());
        shoppingCartImg.setImage(iMatDataHandler.getFXImage(iMatDataHandler.getProduct(product.getProductId())));
    }

    @FXML private void addProduct(){
        parentController.addProduct(1,product);
    }

    @FXML private void removeProduct(){
        parentController.removeProduct(1,product);
    }
    @FXML private void removeWholeProduct(){
        parentController.removeWholeProduct(product);
    }

    void disableButtons(){

    }
}
