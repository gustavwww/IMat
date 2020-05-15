package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import se.chalmers.cse.dat216.project.IMatDataHandler;
import se.chalmers.cse.dat216.project.Product;

import java.net.URL;
import java.util.*;

public class Controller implements Initializable {
    @FXML FlowPane productFlowPane, earlierShoppingCartFlowPane;
    @FXML StackPane mainViewStackPane;
    @FXML AnchorPane detailView, earlierShoppingCartsView, supportView, shopView, howToView;
    @FXML ImageView productImg;
    @FXML Label detailProductLabel,detailPrice;
    @FXML TextArea detailContent,detailFacts;

    private IMatDataHandler iMatDataHandler = IMatDataHandler.getInstance();
    private Map<String, ProductCardController> productCardControllerMap = new HashMap<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        for (Product product : iMatDataHandler.getProducts()) {
            ProductCardController productCardController = new ProductCardController(product, this);
            productCardControllerMap.put(product.getName(), productCardController);
        }
        updateProductList();

        EarlierShoppingCart earlierShoppingCart = new EarlierShoppingCart(this);
        earlierShoppingCartFlowPane.getChildren().add(earlierShoppingCart);
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
    private void goToShopView(){ shopView.toFront();}

    @FXML
    private void goToSupport(){ supportView.toFront();}

    @FXML
    private void goToHowTo(){ howToView.toFront();}

    @FXML
    private void goEarlierShoppingCarts(){
        earlierShoppingCartsView.toFront();
    }
    void populateDetailView(Product product){

        productImg.setImage(iMatDataHandler.getFXImage(iMatDataHandler.getProduct(product.getProductId())));
        detailFacts.setText("ICA Pommes frites Fryst är klassiskt räfflade frittar gjorda av fin potatis. Riktigt" +
                " bra pommes frites ska ju enligt vissa experter gärna tillagas 3 gånger och helst" +
                " då slutfriteras efter att ha blivit frysta. Med våra pommes slipper du en massa " +
                "skalande, skärande och förberedande och sparar du en massa tid. Du kan nu antingen" +
                " tillaga dem i ugnen eller lägga dem i het olja och fritera.");
        detailContent.setText("Energi (kcal) 150 kcal, Energi (kJ) 600 kJ, Fett 4.40 g, Varav mättat fett 0.50 g, Kolhydrater 23 g, Varav socker 0.50 g, Fiber 3 g, Protein 2.20 g, Salt 0.10 g");
        detailProductLabel.setText(product.getName());
        detailPrice.setText(product.getPrice() + " " + product.getUnit());



    }
    @FXML
    private void stackPaneBack(){

        mainViewStackPane.getChildren().get(3).toBack();

    }
}

