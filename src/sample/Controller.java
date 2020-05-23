package sample;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import se.chalmers.cse.dat216.project.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Controller implements Initializable {
    @FXML FlowPane productFlowPane,cartFlowPane,finishFlowPane;
    @FXML StackPane mainViewStackPane;
    @FXML AnchorPane detailView, earlierShoppingCartsView, supportView, shopView, howToView,shoppingCartPane,confirmBox,storeView,wizardFirst,wizardSecond,wizardThird,wizardEnd;
    @FXML ImageView productImg,shoppingCartCloseImg;
    @FXML Label detailProductLabel,detailPrice,categoryLabel,cartNumberOffProducts,cartPriceTotal,finishNWares,finishTotal,finishTotalWithShipping;
    @FXML TextField searchBar;
    @FXML Text detailFacts, detailContent;
    @FXML Button supportBack1,shoppingCartButton,detailAdd;
    @FXML TreeView treeView, mainTreeView;
    @FXML Spinner detailSpinner;
    @FXML Accordion accordion;

    private ArrayList<ProductCardController> productList  = new ArrayList<>();; //created this in order to make the transition between categories faster
    private IMatDataHandler iMatDataHandler = IMatDataHandler.getInstance();
    private Map<String, ProductCardController> productCardControllerMap = new HashMap<>();
    private SpinnerValueFactory<Double> spinnerValueFactory  = new SpinnerValueFactory.DoubleSpinnerValueFactory(0,20,1,1);
    private Product selectedProduct;
    private EnumSet<ProductCategory> fruits = EnumSet.of(ProductCategory.EXOTIC_FRUIT, ProductCategory.FRUIT, ProductCategory.CITRUS_FRUIT, ProductCategory.MELONS);
    private EnumSet<ProductCategory> greens = EnumSet.of(ProductCategory.CABBAGE, ProductCategory.ROOT_VEGETABLE, ProductCategory.VEGETABLE_FRUIT);
    private List<Order> orders = new ArrayList<>();
    private Map<Integer, EarlierShoppingCart> earlierShoppingListMap = new HashMap<>();
    private Random rand = new Random();

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
        detailSpinner.setValueFactory(spinnerValueFactory);

        updateEarlierPurchaseList();

/*
        categoryAccordion.expandedPaneProperty().addListener(
                (ObservableValue<? extends TitledPane> ov, TitledPane old_val,
                 TitledPane new_val) -> {
                    if (new_val != null) {
                    sortedProductList(categoryAccordion.getExpandedPane().getText());
                    }
                });
        detailSpinner.setValueFactory(spinnerValueFactory);
 */

        fillTreeView();
        mainTreeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> sortedTree((TreeItem) newValue));
        treeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> sortedTree((TreeItem) newValue));
    }

    private void fillTreeView() {
        TreeItem mainRootItem = new TreeItem("Kategorier");
        mainRootItem.getChildren().add(new TreeItem("Så handlar du"));
        mainRootItem.getChildren().add(new TreeItem("Populärt"));

        mainTreeView.setRoot(mainRootItem);
        mainTreeView.setShowRoot(false);

        TreeItem rootItem = new TreeItem("Produkter");

        TreeItem fruitsGreens = new TreeItem("Frukt & Grönt");
        fruitsGreens.getChildren().add(new TreeItem("Frukter"));
        fruitsGreens.getChildren().add(new TreeItem("Grönsaker"));
        fruitsGreens.getChildren().add(new TreeItem("Bär"));
        rootItem.getChildren().add(fruitsGreens);

        TreeItem drinks = new TreeItem("Dryck");
        drinks.getChildren().add(new TreeItem<>("Varma drycker"));
        drinks.getChildren().add(new TreeItem<>("Kalla drycker"));
        rootItem.getChildren().add(drinks);

        TreeItem skafferiet = new TreeItem("Skafferi");
        skafferiet.getChildren().add(new TreeItem<>("Mjöl, socker, salt"));
        skafferiet.getChildren().add(new TreeItem<>("Pasta"));
        skafferiet.getChildren().add(new TreeItem<>("Potatis & Ris"));
        rootItem.getChildren().add(skafferiet);

        rootItem.getChildren().add(new TreeItem("Bröd & Bageri"));
        rootItem.getChildren().add(new TreeItem("Kött"));
        rootItem.getChildren().add(new TreeItem("Fisk & Skaldjur"));
        rootItem.getChildren().add(new TreeItem("Mejeri & Ost"));
        rootItem.getChildren().add(new TreeItem("Smaksättare"));
        rootItem.getChildren().add(new TreeItem("Nötter & Fröer"));
        rootItem.getChildren().add(new TreeItem("Gott"));

        treeView.setRoot(rootItem);
        treeView.setShowRoot(false);
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
        for(Product product : iMatDataHandler.findProducts(searchBar.getText())){
            productFlowPane.getChildren().add(new ProductCardController(product,this));
        }
        categoryLabel.setText("Sökning efter: " + searchBar.getText() + " (" + (productFlowPane.getChildren().size()-2) + " träffar)");

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

    private void updateProductSearchList(String search) {
        //productFlowPane.getChildren().clear();
        List<Product> products = iMatDataHandler.getProducts(getCategory(search));
        for (Product product : products) {
            productFlowPane.getChildren().add(productCardControllerMap.get(product.getName()));
        }
    }

    private void updateProductList() {
        productFlowPane.getChildren().clear();
        productFlowPane.getChildren().add(categoryLabel);
        categoryLabel.setText("Populärt");
        List<Product> products = iMatDataHandler.getProducts();
        for (Product product : products) {
            productFlowPane.getChildren().add(productCardControllerMap.get(product.getName()));
        }
    }

    private void sortedTree(TreeItem item) {
        if (getCategory(item.getValue().toString()) != null || item.getValue().toString().equals("Populärt") || item.getValue().toString().equals("Frukter") ||
                item.getValue().toString().equals("Grönsaker")) {

            productFlowPane.getChildren().clear();
            productFlowPane.getChildren().add(categoryLabel);
            categoryLabel.setText(item.getValue().toString());
            //productFlowPane.getChildren().add(supportBack1);

            switch (item.getValue().toString()) {
                case "Populärt":
                    updateProductList();
                    break;
                case "Frukter":
                    productFlowPane.getChildren().clear();
                    productFlowPane.getChildren().add(categoryLabel);
                    categoryLabel.setText(item.getValue().toString());

                    fruits.forEach(fruit -> {
                        for (Product product : iMatDataHandler.getProducts(fruit)) {
                            productFlowPane.getChildren().add(productCardControllerMap.get(product.getName()));
                        }
                    });
                    break;
                case "Grönsaker":
                    productFlowPane.getChildren().clear();
                    productFlowPane.getChildren().add(categoryLabel);
                    categoryLabel.setText(item.getValue().toString());

                    greens.forEach(vegetable -> {
                        for (Product product : iMatDataHandler.getProducts(vegetable)) {
                            productFlowPane.getChildren().add(productCardControllerMap.get(product.getName()));
                        }
                    });
                    break;
                default:
                    for (Product product : iMatDataHandler.getProducts(getCategory(item.getValue().toString()))) {
                        productFlowPane.getChildren().add(productCardControllerMap.get(product.getName()));
                    }
                    break;
            }
            shopView.toFront();
        }
        else if (item.getValue().toString().equals("Så handlar du")) {
            goToHowTo();
        }

    }

    private ProductCategory getCategory(String category){
        ProductCategory productCategory;
        switch (category){
            case "Bär": productCategory = ProductCategory.BERRY; break;
            case "Kött": productCategory = ProductCategory.MEAT; break;
            case "Fisk & Skaldjur": productCategory = ProductCategory.FISH; break;
            case "Bröd & Bageri": productCategory = ProductCategory.BREAD; break;
            case "Varma drycker": productCategory = ProductCategory.HOT_DRINKS; break;
            case "Kalla drycker": productCategory = ProductCategory.COLD_DRINKS; break;
            case "Mejeri & Ost": productCategory = ProductCategory.DAIRIES; break;
            case "Mjöl, socker, salt": productCategory = ProductCategory.FLOUR_SUGAR_SALT; break;
            case "Pasta": productCategory = ProductCategory.PASTA; break;
            case "Potatis & Ris": productCategory = ProductCategory.POTATO_RICE; break;
            case "Smaksättare": productCategory = ProductCategory.HERB; break;
            case "Nötter & Fröer": productCategory = ProductCategory.NUTS_AND_SEEDS; break;
            case "Gott": productCategory = ProductCategory.SWEET; break;
            default: productCategory = null; break;
        }
        return productCategory;
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
    }

    @FXML
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
            shoppingCartButton.setText(iMatDataHandler.getShoppingCart().getItems().size() + " vara " + round(iMatDataHandler.getShoppingCart().getTotal(),2) + " kr");
        }
        else if(items == 0){
            shoppingCartButton.setText("Varukorgen är tom");
        }
        else{
            shoppingCartButton.setText(iMatDataHandler.getShoppingCart().getItems().size() + " varor - " + round(iMatDataHandler.getShoppingCart().getTotal(),2) + " kr");
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
    private void goToWizardEnd(){
        wizardEnd.toFront();
    }

    @FXML
    private void goToStore(){
        storeView.toFront();
    }

    private void updateEarlierPurchaseList() {
        for (Order order : orders) {
            EarlierShoppingCart earlierShoppingCart = new EarlierShoppingCart(order, this);
            earlierShoppingListMap.put(order.getOrderNumber(), earlierShoppingCart);
            accordion.getPanes().add(earlierShoppingCart);
        }
    }

    @FXML
    private void endPurchase() {
        Order order = new Order();
        order.setOrderNumber(rand.nextInt());
        order.setDate(new Date());
        order.setItems(iMatDataHandler.getShoppingCart().getItems());
        orders.add(order);

        updateEarlierPurchaseList();
        emptyCart();
        goToStore();
    }

    void populateDetailView(Product product){
        selectedProduct = product;
        productImg.setImage(iMatDataHandler.getFXImage(iMatDataHandler.getProduct(product.getProductId())));
        detailFacts.setText("ICA Pommes frites Fryst är klassiskt räfflade frittar gjorda av fin potatis. Riktigt" +
                " bra pommes frites ska ju enligt vissa experter gärna tillagas 3 gånger och helst" +
                " då slutfriteras efter att ha blivit frysta. Med våra pommes slipper du en massa " +
                "skalande, skärande och förberedande och sparar du en massa tid. Du kan nu antingen" +
                " tillaga dem i ugnen eller lägga dem i het olja och fritera.");

        String productNutrition = "";
        String[] nutritions = {"Energi (kcal) 150 kcal", "Energi (kJ) 600 kJ", "Fett 4.40 g", "Varav mättat fett 0.50 g", "Kolhydrater 23 g", "Varav socker 0.50 g", "Fiber 3 g", "Protein 2.20 g", "Salt 0.10 g"};
        for (String nutrition : nutritions) {
            productNutrition += nutrition + "\n";
        }
        detailContent.setText(productNutrition);

        //detailContent.setText("Energi (kcal) 150 kcal,\nEnergi (kJ) 600 kJ,\nFett 4.40 g,\nVarav mättat fett 0.50 g, Kolhydrater 23 g, Varav socker 0.50 g, Fiber 3 g, Protein 2.20 g, Salt 0.10 g";
        detailProductLabel.setText(product.getName());
        detailPrice.setText(product.getPrice() + " " + product.getUnit());
    }

    @FXML
    private void stackPaneBack(){
        shopView.toFront();
       // mainViewStackPane.getChildren().get(4).toBack(); //

    }
}

