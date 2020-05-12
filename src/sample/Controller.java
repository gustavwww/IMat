package sample;

import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import se.chalmers.cse.dat216.project.IMatDataHandler;

public class Controller {
    //private IMatDataHandler iMatDataHandler = IMatDataHandler.getInstance();
    @FXML GridPane productGridPane;

    public Controller() {

    }
    public void  initialize(){
        productGridPane.add(new ProductCardController(),0,0);
    }


}

