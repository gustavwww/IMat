package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import se.chalmers.cse.dat216.project.IMatDataHandler;
import se.chalmers.cse.dat216.project.Product;

import java.io.IOException;

public class ProductCardController extends AnchorPane {
    @FXML
    ImageView productImage;
    @FXML
    Label productName, productPrice;

    private Controller parentController;
    private Product product;
    private IMatDataHandler iMatDataHandler = IMatDataHandler.getInstance();

    public ProductCardController(Product product, Controller controller) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("productCard.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        this.product = product;
        this.parentController = controller;

        productName.setText(product.getName());
        productPrice.setText(product.getPrice() + " " + product.getUnit());
        productImage.setImage(iMatDataHandler.getFXImage(iMatDataHandler.getProduct(product.getProductId())));
    }
}