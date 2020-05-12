package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import se.chalmers.cse.dat216.project.Product;

import java.io.IOException;

public class ProductCardController extends AnchorPane {
    private Product product;
    @FXML ImageView productImage;
    @FXML Label productName;
    @FXML Label productPrice;
    public ProductCardController() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("productCard.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        productName.setText(product.getName());
        productPrice.setText(product.getPrice()+" kr/"+product.getUnit());
        //productImage.setImage();

    }
}


