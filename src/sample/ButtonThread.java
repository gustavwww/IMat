package sample;

import javafx.application.Platform;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import se.chalmers.cse.dat216.project.IMatDataHandler;

public class ButtonThread extends Thread{
    private Controller controller = null;



    public void run(){
        System.out.println("starting thread m'babies");
        //här efter kör vi koden i tråden
        boolean changed = true;
        double height = controller.shoppingCartButton.getHeight();
        double width = controller.shoppingCartButton.getWidth();
        IMatDataHandler iMatDataHandler = IMatDataHandler.getInstance();

       while (true){
           try {
               sleep(100);
           } catch (InterruptedException e) {
               e.printStackTrace();
           }

           if (controller.addChanged){

               System.out.println("DILDOSNOPP");
               controller.shoppingCartButton.setPrefHeight(40);
               controller.shoppingCartButton.setPrefWidth(260);
               controller.shoppingCartButton.setLayoutX(1000);
               controller.shoppingCartButton.setLayoutY(2.5);
               controller.shoppingCartButton.setText("Tillagd i varukorg!");
               controller.shoppingCartButton.setFont(Font.font("System", FontWeight.findByWeight(FontWeight.BOLD.getWeight()),18));
               System.out.println("innan");
               try {
                   sleep(1000);
               } catch (InterruptedException e) {
                   e.printStackTrace();
               }

               if(controller.nItems == 1){
                   controller.cartFlowPane.getChildren().remove(controller.isEmptyTextFlow);
                   controller.cartFlowPane.getChildren().remove(controller.isEmptyButton);

                   controller.shoppingCartButton.setText(iMatDataHandler.getShoppingCart().getItems().size() + " vara " + controller.round(iMatDataHandler.getShoppingCart().getTotal(),2) + " kr");
               }
               else if(controller.nItems == 0){
                   controller.shoppingCartButton.setText("Varukorgen är tom");
                   controller.cartFlowPane.getChildren().add(controller.isEmptyTextFlow);
                   controller.cartFlowPane.getChildren().add(controller.isEmptyButton);
               }
               else{
                   controller.shoppingCartButton.setText(iMatDataHandler.getShoppingCart().getItems().size() + " varor - " + controller.round(iMatDataHandler.getShoppingCart().getTotal(),2) + " kr");
               }
               System.out.println("efter");
               controller.shoppingCartButton.setPrefHeight(25);
               controller.shoppingCartButton.setPrefWidth(240);
               controller.shoppingCartButton.setLayoutX(1010);
               controller.shoppingCartButton.setLayoutY(10);
               controller.shoppingCartButton.setFont(Font.font("System", FontWeight.findByWeight(FontWeight.BOLD.getWeight()),14));
               controller.addChanged = false;
           }



       }
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public static void main(String[] args) {
        (new Thread(new ButtonThread())).start();
    }
}
