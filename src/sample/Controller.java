package sample;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Callback;
import javafx.util.Duration;
import se.chalmers.cse.dat216.project.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

public class Controller implements Initializable {
    @FXML FlowPane productFlowPane,cartFlowPane,finishFlowPane;
    @FXML StackPane mainViewStackPane;
    @FXML AnchorPane detailView, supportView, shopView, howToView,shoppingCartPane,confirmBox,storeView,wizardFirst,wizardSecond,wizardThird,wizardEnd,earlierpAnchorP;
    @FXML ImageView productImg;
    @FXML Label detailProductLabel,detailPrice,categoryLabel,cartNumberOffProducts,cartPriceTotal,finishNWares,finishTotal,finishTotalWithShipping,endDateLabel,fillAllWarningLabel,fillAllWarningLabelSecond,verifiedLabel;
    @FXML TextField searchBar, firstNameField,lastNameField,mailField,phoneField,postCodeField,deliveryAddressField,cardNumberField,holdersNameField,verificationCodeField,validMonthField,validYearField;
    @FXML Text detailFacts, detailContent,customerInfoText,cardInfoText,customerInfoText1,cardInfoText1;
    @FXML Button shoppingCartButton,detailAdd,beginShopButton,isEmptyButton;
    @FXML TreeView treeView, mainTreeView;
    @FXML Spinner detailSpinner,timeSpinnerHour,timeSpinnerMin;
    @FXML Accordion accordion;
    @FXML SplitMenuButton cardMenuButton;
    @FXML DatePicker datePicker;
    @FXML TextFlow noEarlierListText,isEmptyTextFlow;
    @FXML ScrollPane earlierShoppingCartsView,shopScrollPane;
    @FXML CheckBox saveInfoCheckBox,savePayInfoCheckBox;

    private ArrayList<ProductCardController> productList  = new ArrayList<>();; //created this in order to make the transition between categories faster
    private IMatDataHandler iMatDataHandler = IMatDataHandler.getInstance();
    private Map<String, ProductCardController> productCardControllerMap = new HashMap<>();
    private SpinnerValueFactory<Double> spinnerValueFactory  = new SpinnerValueFactory.DoubleSpinnerValueFactory(0,20,1,1);
    private SpinnerValueFactory<Integer> valueFactoryHours  = new SpinnerValueFactory.IntegerSpinnerValueFactory(0,23,13,1);
    private SpinnerValueFactory<Integer> valueFactoryMin  = new SpinnerValueFactory.IntegerSpinnerValueFactory(0,59,0,1);
    private Product selectedProduct;
    private EnumSet<ProductCategory> fruits = EnumSet.of(ProductCategory.EXOTIC_FRUIT, ProductCategory.FRUIT, ProductCategory.CITRUS_FRUIT, ProductCategory.MELONS);
    private EnumSet<ProductCategory> greens = EnumSet.of(ProductCategory.CABBAGE, ProductCategory.ROOT_VEGETABLE, ProductCategory.VEGETABLE_FRUIT);
    private EnumSet<ProductCategory> drinks = EnumSet.of(ProductCategory.HOT_DRINKS, ProductCategory.COLD_DRINKS);
    private EnumSet<ProductCategory> pantry = EnumSet.of(ProductCategory.FLOUR_SUGAR_SALT, ProductCategory.POTATO_RICE, ProductCategory.PASTA);
    boolean addChanged = false;
    int nItems = 0;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        for (Product product : iMatDataHandler.getProducts()) {
            ProductCardController productCardController = new ProductCardController(product, this);
            productCardControllerMap.put(product.getName(), productCardController);
            productList.add(productCardController);
        }
        updateProductList();

        timeSpinnerHour.setValueFactory(valueFactoryHours);
        timeSpinnerMin.setValueFactory(valueFactoryMin);
        detailSpinner.setValueFactory(spinnerValueFactory);
        Customer customer = iMatDataHandler.getCustomer();
        CreditCard creditCard = iMatDataHandler.getCreditCard();
        if (creditCard.getCardType().equals("Visa")||creditCard.getCardType().equals("Mastercard")){
            cardMenuButton.setText(creditCard.getCardType());
        }
            cardNumberField.setText(creditCard.getCardNumber());
            validMonthField.setText(""+creditCard.getValidMonth());
            validYearField.setText(""+creditCard.getValidYear());
            verificationCodeField.setText(""+creditCard.getVerificationCode());
            holdersNameField.setText(""+creditCard.getHoldersName());
            firstNameField.setText(customer.getFirstName());
            lastNameField.setText(customer.getLastName());
            mailField.setText(customer.getEmail());
            deliveryAddressField.setText(customer.getAddress());
            postCodeField.setText(customer.getPostCode());
            phoneField.setText(customer.getPhoneNumber());

        if (iMatDataHandler.getOrders().size() != 0) {
            noEarlierListText.setDisable(true);
            noEarlierListText.setVisible(false);
            beginShopButton.setDisable(true);
            beginShopButton.setVisible(false);
            for (Order order : iMatDataHandler.getOrders()) {
                updateEarlierPurchaseList(order);
            }
        }

        Callback<DatePicker, DateCell> dayCellFactory = new Callback<DatePicker, DateCell>()
        {
            public DateCell call(final DatePicker datePicker)
            {
                return new DateCell()
                {
                    @Override
                    public void updateItem(LocalDate item, boolean empty)
                    {
                        super.updateItem(item, empty);
                        DayOfWeek day = DayOfWeek.from(item);
                        LocalDate today = LocalDate.now();
                        setDisable(empty || item.compareTo(today) < 0);
                        if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY)
                        {
                            this.setTextFill(Color.RED);
                        }
                    }
                };
            }
        };
        datePicker.setDayCellFactory(dayCellFactory);

        fillTreeView();
        mainTreeView.getSelectionModel().clearSelection();
        mainTreeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                treeView.getSelectionModel().clearSelection();
                sortedTree((TreeItem) newValue);
            }
        });
        treeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                mainTreeView.getSelectionModel().clearSelection();
                sortedTree((TreeItem) newValue);
            }
        });

        // Info text and button if cart is empty
        if (iMatDataHandler.getShoppingCart().getItems().size() != 0){
            cartFlowPane.getChildren().remove(isEmptyTextFlow);
            cartFlowPane.getChildren().remove(isEmptyButton);
        }
        updateCart();
        fillAllWarningLabel.setVisible(false);
        fillAllWarningLabelSecond.setVisible(false);
        cartTimerBig.setCycleCount(1);
        /*
        ButtonThread buttonThread = new ButtonThread();
        buttonThread.setController(this);

        buttonThread.start();

         */

    }

    private void fillTreeView() {
        TreeItem mainRootItem = new TreeItem("Kategorier");
        mainRootItem.getChildren().add(new TreeItem("Så handlar du"));
        mainRootItem.getChildren().add(new TreeItem("Support"));
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
        if (iMatDataHandler.getShoppingCart().getItems().size() != 0) {
            confirmBox.toFront();
        }else {

        }
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
        categoryLabel.setText("Sökning efter: " + searchBar.getText() + " (" + (productFlowPane.getChildren().size()-1) + " träffar)");

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

    private void clearProductView(TreeItem item) {
        productFlowPane.getChildren().clear();
        productFlowPane.getChildren().add(categoryLabel);
        categoryLabel.setText(item.getValue().toString());
    }

    private boolean isProductCategory(TreeItem item) {
        return (getCategory(item.getValue().toString()) != null || item.getValue().toString().equals("Populärt")
                || item.getValue().toString().equals("Frukter") || item.getValue().toString().equals("Grönsaker")
                || item.getValue().toString().equals("Frukt & Grönt")
                || item.getValue().toString().equals("Dryck") || item.getValue().toString().equals("Skafferi"));
    }

    private void sortedTree(TreeItem item) {
        if (isProductCategory(item)) {
            clearProductView(item);
            switch (item.getValue().toString()) {
                case "Populärt": updateProductList(); break;
                case "Frukt & Grönt":
                    clearProductView(item);
                    item.setExpanded(true);
                    fruits.forEach(fruit -> {
                        for (Product product : iMatDataHandler.getProducts(fruit)) {
                            productFlowPane.getChildren().add(productCardControllerMap.get(product.getName()));
                        }
                    });
                    greens.forEach(vegetable -> {
                        for (Product product : iMatDataHandler.getProducts(vegetable)) {
                            productFlowPane.getChildren().add(productCardControllerMap.get(product.getName()));
                        }
                    });
                    break;
                case "Dryck":
                    clearProductView(item);
                    item.setExpanded(true);
                    drinks.forEach(drink -> {
                        for (Product product : iMatDataHandler.getProducts(drink)) {
                            productFlowPane.getChildren().add(productCardControllerMap.get(product.getName()));
                        }
                    });
                    break;
                case "Skafferi":
                    clearProductView(item);
                    item.setExpanded(true);
                    pantry.forEach(pantryItem -> {
                        for (Product product : iMatDataHandler.getProducts(pantryItem)) {
                            productFlowPane.getChildren().add(productCardControllerMap.get(product.getName()));
                        }
                    });
                    break;
                case "Frukter":
                    clearProductView(item);
                    fruits.forEach(fruit -> {
                        for (Product product : iMatDataHandler.getProducts(fruit)) {
                            productFlowPane.getChildren().add(productCardControllerMap.get(product.getName()));
                        }
                    });
                    break;
                case "Grönsaker":
                    clearProductView(item);;
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
            shopScrollPane.setVvalue(0);
        }
        else if (item.getValue().toString().equals("Så handlar du")) {
            goToHowTo();
        }
        else if (item.getValue().toString().equals("Support")) {
            goToSupport();
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
            EarlierPurchaseItem item = new EarlierPurchaseItem(this, shoppingItem);
            item.setPrefWidth(1077);
            finishFlowPane.getChildren().add(item);
            if(shoppingItem.getProduct().getUnit().equals("kg")){
                products = products+1;
            }
           else {
               products = products + (int) shoppingItem.getAmount();
           }
        }
        cartPriceTotal.setText("Totalpris: "+round(iMatDataHandler.getShoppingCart().getTotal()+49,2)+" kr"); //+49 i och med frakt
        cartNumberOffProducts.setText("Totalt "+ iMatDataHandler.getShoppingCart().getItems().size() + " varor");
        finishNWares.setText("Totalt "+products+" varor");
        finishTotalWithShipping.setText(""+round(iMatDataHandler.getShoppingCart().getTotal()+49,2) + " kr");
        finishTotal.setText(""+round(iMatDataHandler.getShoppingCart().getTotal(),2) + " kr");
        return products;
    }

    @FXML
    private void detailAddProduct(){
        if(selectedProduct.getUnitSuffix().equals("st")||selectedProduct.getUnitSuffix().equals("förp")){
            if(Double.parseDouble(detailSpinner.getEditor().getText()) % 1 == 0 && Double.parseDouble(detailSpinner.getEditor().getText()) > 0){
                addProduct(Double.parseDouble(detailSpinner.getEditor().getText()),selectedProduct);

            }
            return;
        }
        if(Double.parseDouble(detailSpinner.getEditor().getText()) >0) {

            addProduct(Double.valueOf(detailSpinner.getEditor().getText()), selectedProduct);

        }
    }

    Timeline cartTimerBig = new Timeline(new KeyFrame(Duration.seconds(0.5), new EventHandler<ActionEvent>() {

        @Override
        public void handle(ActionEvent event) {
            shoppingCartButton.setPrefHeight(25);
            shoppingCartButton.setPrefWidth(240);
            shoppingCartButton.setLayoutX(1010);
            shoppingCartButton.setLayoutY(10);
            shoppingCartButton.setFont(Font.font("System", FontWeight.findByWeight(FontWeight.BOLD.getWeight()),14));
            updateCart();
        }
    }));

    void addProduct(double amount,Product product){
        shoppingCartButton.setPrefHeight(40);
        shoppingCartButton.setPrefWidth(260);
        shoppingCartButton.setLayoutX(1000);
        shoppingCartButton.setLayoutY(2.5);
        shoppingCartButton.setText("Tillagd i varukorg!");
        shoppingCartButton.setFont(Font.font("System", FontWeight.findByWeight(FontWeight.BOLD.getWeight()),18));
        cartTimerBig.play();
       for(ShoppingItem shoppingItem : iMatDataHandler.getShoppingCart().getItems()){
           if(shoppingItem.getProduct().equals(product)){
               shoppingItem.setAmount(shoppingItem.getAmount()+amount);

               System.out.println("switchar till true");
               addChanged = true;
               return;


           }
       }
       iMatDataHandler.getShoppingCart().addProduct(product,amount);

       addChanged = true;

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

    void removeWholeProduct( Product product){
        for(ShoppingItem shoppingItem : iMatDataHandler.getShoppingCart().getItems()){
            if(shoppingItem.getProduct().equals(product)){

                    iMatDataHandler.getShoppingCart().getItems().remove(shoppingItem);
                    updateCart();
                    return;
            }
        }
        updateCart();
    }

    synchronized void updateCart(){
        nItems = populateShoppingCart();

        if(nItems == 1){
            cartFlowPane.getChildren().remove(isEmptyTextFlow);
            cartFlowPane.getChildren().remove(isEmptyButton);
            shoppingCartButton.setText(iMatDataHandler.getShoppingCart().getItems().size() + " vara " + round(iMatDataHandler.getShoppingCart().getTotal(),2) + " kr");
        }
        else if(nItems == 0){
            shoppingCartButton.setText("Varukorgen är tom");
            cartFlowPane.getChildren().add(isEmptyTextFlow);
            cartFlowPane.getChildren().add(isEmptyButton);
        }
        else{
            shoppingCartButton.setText(iMatDataHandler.getShoppingCart().getItems().size() + " varor - " + round(iMatDataHandler.getShoppingCart().getTotal(),2) + " kr");
        }

    }
    void buyEarlierCart(Order order){
        for(ShoppingItem shoppingItem: order.getItems()){
            addProduct(shoppingItem.getAmount(),shoppingItem.getProduct());
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
        goToStore();
    }

    @FXML
    private void goToShoppingCart(){
        updateCart();
        shoppingCartPane.toFront();

    }
    @FXML
    private void goToShopView(){ shopView.toFront();}

    @FXML
    private void goToSupport(){ supportView.toFront();
    storeView.toFront();}

    @FXML
    private void goToHowTo(){ howToView.toFront();}

    @FXML
    private void goEarlierShoppingCarts(){
        earlierShoppingCartsView.toFront();
        storeView.toFront();
    }

    @FXML
    private void goToWizardFirst(){
        if (iMatDataHandler.getShoppingCart().getItems().size() != 0) {
            shoppingCartPane.toBack();
            confirmBox.toBack();
            wizardFirst.toFront();
        }
    }

    @FXML
    private void goToWizardSecond(){
        createCustomer();
        if(customerIsLegit()) {
            wizardSecond.toFront();
            //fillAllWarningLabel.setTextFill(Paint.valueOf("BLACK"));
            fillAllWarningLabel.setVisible(false);
        }
        else{
            fillAllWarningLabel.setTextFill(Paint.valueOf("RED"));
            fillAllWarningLabel.setVisible(true);
        }
    }

    @FXML
    private void goToWizardThird(){
        setCreditCard();
        if(payInfoIsLegit()) {
            wizardThird.toFront();
            //fillAllWarningLabelSecond.setTextFill(Paint.valueOf("BLACK"));
            fillAllWarningLabelSecond.setVisible(false);
            showAllInfo();
        }
        else {
            fillAllWarningLabelSecond.setTextFill(Paint.valueOf("RED"));
            fillAllWarningLabelSecond.setVisible(true);
        }
    }

    @FXML
    private void goToWizardEnd(){
        showAllInfo();
        endPurchase();
        wizardEnd.toFront();
    }

    @FXML
    private void goToStore(){
        storeView.toFront();
    }

    private boolean customerIsLegit() {
        phoneField.setText(convertStringToNoSpaces(phoneField.getText()));
        postCodeField.setText(convertStringToNoSpaces(postCodeField.getText()));
        return (iMatDataHandler.isCustomerComplete() && !datePicker.getEditor().getText().equals("")
                && !timeSpinnerMin.getEditor().getText().equals("") && !timeSpinnerHour.getEditor().getText().equals("")
                && phoneField.getText().length() == 10 && phoneField.getText().matches("[0-9]+")
                && postCodeField.getText().length() == 5 && postCodeField.getText().matches("[0-9]+"));
    }

    private String convertStringToNoSpaces(String string) {
        string = string.replaceAll(" ","");
        return string;
    }

    private boolean payInfoIsLegit() {
        cardNumberField.setText(convertStringToNoSpaces(cardNumberField.getText()));
        if((cardMenuButton.getText().equals("Visa") || cardMenuButton.getText().equals("Mastercard"))
                && cardNumberField.getText().matches("[0-9]+") && cardNumberField.getText().length() == 16
                && !holdersNameField.getText().equals("") && verificationCodeField.getText().matches("[0-9]+")
                && verificationCodeField.getText().length() == 3 && validMonthField.getText().matches("[0-9]+")
                && validYearField.getText().matches("[0-9]+")
                && validYearField.getText().length() == 2){
            return Integer.parseInt(validMonthField.getText())<13 && ((Integer.parseInt("20"+validYearField.getText())>=new Date().getYear()+1900 && Integer.parseInt(validMonthField.getText())>new Date().getMonth()+1)
            || Integer.parseInt("20"+validYearField.getText())>new Date().getYear()+1900);
        }
        return false;
    }

    private void updateEarlierPurchaseList(Order order) {
        noEarlierListText.setDisable(true);
        noEarlierListText.setVisible(false);
        beginShopButton.setDisable(true);
        beginShopButton.setVisible(false);
        EarlierShoppingCart earlierShoppingCart = new EarlierShoppingCart(order,this);
        earlierShoppingCart.totalPrice.setText(round(getTotal(order), 2) + " kr");
        accordion.getPanes().add(0, earlierShoppingCart);
    }

    private double getTotal(Order order) {
        double total = 0;
        for (ShoppingItem item : order.getItems()) {
            total += item.getTotal();
        }
        return total;
    }

    @FXML
    private void endPurchase() {
        updateEarlierPurchaseList(iMatDataHandler.placeOrder(true));
        updateCart();
        verifiedLabel.setText("Ett bekräftelsemejl har skickats till " + mailField.getText());

        if(!saveInfoCheckBox.isSelected()){
            iMatDataHandler.reset();
            firstNameField.setText("");
            lastNameField.setText("");
            mailField.setText("");
            deliveryAddressField.setText("");
            postCodeField.setText("");
            phoneField.setText("");
            datePicker.getEditor().setText("");
        }
        if (!savePayInfoCheckBox.isSelected()) {
            iMatDataHandler.reset();
            cardNumberField.setText("");
            validMonthField.setText("");
            validYearField.setText("");
            verificationCodeField.setText("");
            holdersNameField.setText("");
            cardMenuButton.setText("välj kort");
        }
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

    private void createCustomer() {
        Customer customer = iMatDataHandler.getCustomer();
        customer.setFirstName(firstNameField.getText());
        customer.setLastName(lastNameField.getText());
        customer.setEmail(mailField.getText());
        customer.setPhoneNumber(phoneField.getText());
        customer.setPostCode(postCodeField.getText());
        customer.setPostAddress(postCodeField.getText());
        customer.setAddress(deliveryAddressField.getText());
        customer.setMobilePhoneNumber(phoneField.getText());
    }

    private void setCreditCard() {
        CreditCard creditCard = iMatDataHandler.getCreditCard();
        creditCard.setCardNumber(cardNumberField.getText());
        creditCard.setHoldersName(holdersNameField.getText());

        try {
            creditCard.setVerificationCode(Integer.parseInt(verificationCodeField.getText()));
            creditCard.setValidMonth(Integer.parseInt(validMonthField.getText()));
            creditCard.setValidYear(Integer.parseInt(validYearField.getText()));
        }catch (NumberFormatException e) {
            System.out.println("Incorrect number");
        }
    }

    @FXML
    private void setVisa(){
        iMatDataHandler.getCreditCard().setCardType("Visa");
        cardMenuButton.setText("Visa");

    }

    @FXML
    private void setMastercard(){
        iMatDataHandler.getCreditCard().setCardType("Mastercard");
        cardMenuButton.setText("Mastercard");
    }

    private void showAllInfo() {
        if(Integer.parseInt(timeSpinnerMin.getEditor().getText())<10){
            endDateLabel.setText(datePicker.getValue().toString()+" "+timeSpinnerHour.getEditor().getText()+":0"+timeSpinnerMin.getEditor().getText()+"");
        }
        else {
            endDateLabel.setText(datePicker.getValue().toString()+" "+timeSpinnerHour.getEditor().getText()+":"+timeSpinnerMin.getEditor().getText());
        }

        customerInfoText.setText("Namn: " + iMatDataHandler.getCustomer().getFirstName() + " " + iMatDataHandler.getCustomer().getLastName() + "\n"
                + "Leveransdress: " + iMatDataHandler.getCustomer().getAddress() + " " + iMatDataHandler.getCustomer().getPostCode() + "\n" + "E-mail: " +  iMatDataHandler.getCustomer().getEmail() + "\n"
                + "Tel: " + iMatDataHandler.getCustomer().getPhoneNumber());
        cardInfoText.setText("Korttyp: " + iMatDataHandler.getCreditCard().getCardType() + "\n" + "Kortinnehavare: " + iMatDataHandler.getCreditCard().getHoldersName() + "\n" + "Kortnr: " + iMatDataHandler.getCreditCard().getCardNumber() + "\n"
                + "Utgångsdatum: " + iMatDataHandler.getCreditCard().getValidMonth() + " " + iMatDataHandler.getCreditCard().getValidYear() + "\n"
                + "CVC: " + iMatDataHandler.getCreditCard().getVerificationCode());
        customerInfoText1.setText("Namn: " + iMatDataHandler.getCustomer().getFirstName() + " " + iMatDataHandler.getCustomer().getLastName() + "\n"
                + "Leveransdress: " + iMatDataHandler.getCustomer().getAddress() + " " + iMatDataHandler.getCustomer().getPostCode() + "\n" + "E-mail: " +  iMatDataHandler.getCustomer().getEmail() + "\n"
                + "Tel: " + iMatDataHandler.getCustomer().getPhoneNumber());
        cardInfoText1.setText("Korttyp: " + iMatDataHandler.getCreditCard().getCardType() + "\n" + "Kortinnehavare: " + iMatDataHandler.getCreditCard().getHoldersName() + "\n" + "Kortnr: " + iMatDataHandler.getCreditCard().getCardNumber() + "\n"
                + "Utgångsdatum: " + iMatDataHandler.getCreditCard().getValidMonth() + " " + iMatDataHandler.getCreditCard().getValidYear() + "\n"
                + "CVC: " + iMatDataHandler.getCreditCard().getVerificationCode());

    }
}

