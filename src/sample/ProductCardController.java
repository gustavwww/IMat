package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import se.chalmers.cse.dat216.project.IMatDataHandler;
import se.chalmers.cse.dat216.project.Product;

import java.io.IOException;

public class ProductCardController extends AnchorPane {
    @FXML
    private ImageView productImage;
    @FXML
    private Label productName, productPrice,priceForUnits;
    @FXML
    private
    Spinner productCardSpinner;
    private SpinnerValueFactory<Double> spinnerValueFactory  = new SpinnerValueFactory.DoubleSpinnerValueFactory(0,20,0,1);
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

        productCardSpinner.setValueFactory(spinnerValueFactory);
    }


    @FXML private void addProduct(){

        if(product.getUnitSuffix().equals("st")||product.getUnitSuffix().equals("förp")){
            if(Double.parseDouble(productCardSpinner.getEditor().getText()) % 1 == 0){
                parentController.addProduct(Double.parseDouble(productCardSpinner.getEditor().getText()),product);
                System.out.println("hej");
            }

            return;
        }
        if(Double.parseDouble(productCardSpinner.getEditor().getText()) >0) {

            parentController.addProduct(Double.valueOf(productCardSpinner.getEditor().getText()), product);

        }
    }
    @FXML
    private void goToDetailView(){
        parentController.detailView.toFront();
        parentController.populateDetailView(product);
    }
}


