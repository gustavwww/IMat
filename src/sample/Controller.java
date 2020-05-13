package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.FlowPane;
import se.chalmers.cse.dat216.project.IMatDataHandler;
import se.chalmers.cse.dat216.project.Product;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML FlowPane productFlowPane;

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
}

