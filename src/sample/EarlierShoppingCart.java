package sample;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import se.chalmers.cse.dat216.project.IMatDataHandler;

import java.io.IOException;

public class EarlierShoppingCart extends AnchorPane {

    private Controller parentController;
    private IMatDataHandler iMatDataHandler = IMatDataHandler.getInstance();

    public EarlierShoppingCart(Controller controller) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("earlierShoppingCart.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        this.parentController = controller;
    }
}
