package sample;

import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import se.chalmers.cse.dat216.project.IMatDataHandler;
import se.chalmers.cse.dat216.project.Product;
import se.chalmers.cse.dat216.project.ProductCategory;
import se.chalmers.cse.dat216.project.ShoppingItem;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.util.*;

public class Controller implements Initializable {
    @FXML FlowPane productFlowPane, earlierShoppingCartFlowPane,cartFlowPane,finishFlowPane;
    @FXML StackPane mainViewStackPane;
    @FXML AnchorPane detailView, earlierShoppingCartsView, supportView, shopView, howToView,shoppingCartPane,confirmBox,storeView,wizardFirst,wizardSecond,wizardThird;
    @FXML ImageView productImg,shoppingCartCloseImg;
    @FXML Label detailProductLabel,detailPrice,categoryLabel,cartNumberOffProducts,cartPriceTotal,finishNWares,finishTotal,finishTotalWithShipping;
    @FXML TextField searchBar;
    @FXML TextArea detailContent,detailFacts;
    @FXML Button supportBack1,shoppingCartButton,detailAdd;
    @FXML Accordion categoryAccordion;
    @FXML Spinner detailSpinner;
    private ArrayList<ProductCardController> productList  = new ArrayList<>();; //created this in order to make the transition between categories faster
    private IMatDataHandler iMatDataHandler = IMatDataHandler.getInstance();
    private Map<String, ProductCardController> productCardControllerMap = new HashMap<>();
    private SpinnerValueFactory<Double> spinnerValueFactory  = new SpinnerValueFactory.DoubleSpinnerValueFactory(0,20,1,1);
    private Product selectedProduct;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        iMatDataHandler.getShoppingCart().addProduct(iMatDataHandler.getProduct(33),3);
        for (Product product : iMatDataHandler.getProducts()) {
            ProductCardController productCardController = new ProductCardController(product, this);
            productCardControllerMap.put(product.getName(), productCardController);
            productList.add(productCardController);
        }
        updateProductList();
        updateCart();
        EarlierShoppingCart earlierShoppingCart = new EarlierShoppingCart(this);
        earlierShoppingCartFlowPane.getChildren().add(earlierShoppingCart);



        categoryAccordion.expandedPaneProperty().addListener(
                (ObservableValue<? extends TitledPane> ov, TitledPane old_val,
                 TitledPane new_val) -> {
                    if (new_val != null) {
                    sortedProductList(categoryAccordion.getExpandedPane().getText());
                    }
                });
        detailSpinner.setValueFactory(spinnerValueFactory);
    }
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
    @FXML private void emptyConfirmBoxToFront(){
        confirmBox.toFront();
    }
    @FXML private void confirmBoxToBack(){
        confirmBox.toBack();
    }
    @FXML private void emptyCart(){
        iMatDataHandler.getShoppingCart().clear();

        confirmBox.toBack();
        updateCart();
    }
    @FXML
    private void search(){
        productFlowPane.getChildren().clear();
        productFlowPane.getChildren().add(categoryLabel);
        storeView.toFront();
        //productFlowPane.getChildren().add(supportBack1);
        shopView.toFront();
        for(Product product: iMatDataHandler.findProducts(searchBar.getText())){
            productFlowPane.getChildren().add(new ProductCardController(product,this));
        }
        categoryLabel.setText("sökning efter: "+searchBar.getText()+" ("+(productFlowPane.getChildren().size()-2)+" träffar)");

    }
    private void updateProductList() {
        //productFlowPane.getChildren().clear();
        List<Product> products = iMatDataHandler.getProducts();
        for (Product product : products) {
            productFlowPane.getChildren().add(productCardControllerMap.get(product.getName()));
        }
    }
    @FXML
    private void sortedProductList(String search){
        productFlowPane.getChildren().clear();
        productFlowPane.getChildren().add(categoryLabel);
        //productFlowPane.getChildren().add(supportBack1);
        productCardControllerMap.clear();
        for (Product product : iMatDataHandler.getProducts(getCategory(search))) {
            ProductCardController productCardController = new ProductCardController(product, this);
            productCardControllerMap.put(product.getName(), productCardController);
        }
        updateProductSearchList(search);
        categoryLabel.setText(search);
        shopView.toFront();
    }
    private ProductCategory getCategory(String category){
        ProductCategory productCategory;
        switch (category){
            case "Bär": productCategory = ProductCategory.BERRY;
            break;
            case "Grönsaker": productCategory = ProductCategory.ROOT_VEGETABLE;
            break;
            case "Frukt": productCategory = ProductCategory.FRUIT;
            break;
            default: productCategory = ProductCategory.BREAD;
            break;
        }
        return productCategory;
    }
    private void updateProductSearchList(String search) {
        //productFlowPane.getChildren().clear();
        List<Product> products = iMatDataHandler.getProducts(getCategory(search));

        for (Product product : products) {
            productFlowPane.getChildren().add(productCardControllerMap.get(product.getName()));
        }
    }
    private int populateShoppingCart(){ //lägger in en shoppingCartLevel för varje unik vara
        cartFlowPane.getChildren().clear();
        finishFlowPane.getChildren().clear();
        int products = 0;
        for(ShoppingItem shoppingItem : iMatDataHandler.getShoppingCart().getItems()){
            cartFlowPane.getChildren().add(new ShoppingCartLevelController(shoppingItem.getProduct(),this,shoppingItem.getAmount()));
            finishFlowPane.getChildren().add(new ShoppingCartLevelController(shoppingItem.getProduct(),this,shoppingItem.getAmount()));
            if(shoppingItem.getProduct().getUnit().equals("kg")){
                products = products+1;
            }
           else {
               products = products + (int) shoppingItem.getAmount();
            }

        }
        cartPriceTotal.setText("Totalpris: "+round(iMatDataHandler.getShoppingCart().getTotal()+49,2)+" kr"); //+49 i och med frakt
        cartNumberOffProducts.setText("Totalt "+products+" varor");
        finishNWares.setText("Totalt "+products+" varor");
        finishTotalWithShipping.setText(""+round(iMatDataHandler.getShoppingCart().getTotal()+49,2));
        finishTotal.setText(""+round(iMatDataHandler.getShoppingCart().getTotal(),2));
        return products;
    }@FXML
    private void detailAddProduct(){
        if(selectedProduct.getUnitSuffix().equals("st")||selectedProduct.getUnitSuffix().equals("förp")){
            if(Double.parseDouble(detailSpinner.getEditor().getText()) % 1 == 0){
                addProduct(Double.parseDouble(detailSpinner.getEditor().getText()),selectedProduct);

            }
            return;
        }
        if(Double.parseDouble(detailSpinner.getEditor().getText()) >0) {

            addProduct(Double.valueOf(detailSpinner.getEditor().getText()), selectedProduct);

        }

    }
    void addProduct(double amount,Product product){
       for(ShoppingItem shoppingItem : iMatDataHandler.getShoppingCart().getItems()){
           if(shoppingItem.getProduct().equals(product)){
               shoppingItem.setAmount(shoppingItem.getAmount()+amount);
               updateCart();

               return;
           }
       }
       iMatDataHandler.getShoppingCart().addProduct(product,amount);
       updateCart();
    }
    void removeProduct(double amount, Product product){
        for(ShoppingItem shoppingItem : iMatDataHandler.getShoppingCart().getItems()){
            if(shoppingItem.getProduct().equals(product)){
                shoppingItem.setAmount(shoppingItem.getAmount()-amount);
                if(shoppingItem.getAmount()<= 0){
                    iMatDataHandler.getShoppingCart().getItems().remove(shoppingItem);
                    updateCart();
                    return;
                }
            }
        }
       updateCart();

    }
    private void updateCart(){
        int items = populateShoppingCart();
        if(items == 1){
            shoppingCartButton.setText("Varukorg "+iMatDataHandler.getShoppingCart().getItems().size()+" vara "+round(iMatDataHandler.getShoppingCart().getTotal(),2)+" kr");
        }
        else if(items == 0){
            shoppingCartButton.setText("Varukorgen är tom");
        }
        else{
            shoppingCartButton.setText("Varukorg "+iMatDataHandler.getShoppingCart().getItems().size()+" olika varor "+round(iMatDataHandler.getShoppingCart().getTotal(),2)+" kr");
        }

    }
    @FXML
    private void mouseTrap(Event event){
        event.consume();
    }
    @FXML
    private void closeShoppingCart(){
        shoppingCartPane.toBack();
    }
    @FXML private void ShoppingCartHoverEnter(){
        shoppingCartCloseImg.setImage(new Image(getClass().getClassLoader().getResourceAsStream(
                "sample/resources/icon_close_hover.png")));
    }
    @FXML private void ShoppingCartHoverLeave(){
        shoppingCartCloseImg.setImage(new Image(getClass().getClassLoader().getResourceAsStream(
                "sample/resources/icon_close.png")));
    }
    @FXML
    private void goToPopularView(){
        productFlowPane.getChildren().clear();
        productFlowPane.getChildren().add(categoryLabel);
        //productFlowPane.getChildren().add(supportBack1);
        categoryLabel.setText("Populärt");
        for (ProductCardController productCardController : productList) {
            productFlowPane.getChildren().add(productCardController);
        }
        goToShopView();
    }
    @FXML
    private void goToShoppingCart(){
        updateCart();
        shoppingCartPane.toFront();

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
    @FXML
    private void goToWizardFirst(){
        shoppingCartPane.toBack();
        confirmBox.toBack();
        wizardFirst.toFront();
    }
    @FXML
    private void goToWizardSecond(){
        wizardSecond.toFront();
    }
    @FXML
    private void goToWizardThird(){
        wizardThird.toFront();
    }
    @FXML
    private void goToStore(){
        storeView.toFront();
    }
    void populateDetailView(Product product){
        selectedProduct = product;
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
        shopView.toFront();
       // mainViewStackPane.getChildren().get(4).toBack(); //

    }
}

