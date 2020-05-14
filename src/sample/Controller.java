package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebHistory;
import se.chalmers.cse.dat216.project.IMatDataHandler;
import se.chalmers.cse.dat216.project.Product;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML FlowPane productFlowPane;
    @FXML StackPane mainViewStackPane;
    @FXML AnchorPane detailView,earlierShoppingCarts,supportView,shopView;
    @FXML ImageView productImg;
    private IMatDataHandler iMatDataHandler = IMatDataHandler.getInstance();
    private Map<String, ProductCardController> productCardControllerMap = new HashMap<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        for (Product product : iMatDataHandler.getProducts()) {
            ProductCardController productCardController = new ProductCardController(product, this);
            productCardControllerMap.put(product.getName(), productCardController);
        }

        updateProductList();
    }

    private void updateProductList() {
        //productFlowPane.getChildren().clear();
        List<Product> products = iMatDataHandler.getProducts();

        for (Product product : products) {
            productFlowPane.getChildren().add(productCardControllerMap.get(product.getName()));
        }
    }
    private void evaluateString(){


    }
    @FXML
    private void goToSupport(){
        supportView.toFront();
    }

    @FXML
    private void goEarlierShoppingCarts(){
        earlierShoppingCarts.toFront();
    }
    void populateDetailView(Product product){
        productImg.setImage(iMatDataHandler.getFXImage(iMatDataHandler.getProduct(product.getProductId())));
    }
    @FXML
    private void goToShopView(){
        shopView.toFront();
    }
    @FXML
    private void stackPaneBack(){

        mainViewStackPane.getChildren().get(3).toBack();

    }
}

