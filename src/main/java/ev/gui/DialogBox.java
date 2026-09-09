package ev.gui;

import java.io.IOException;
import java.util.Collections;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/**
 * One entry in the conversation: the speaker's picture next to what was said.
 *
 * <p>The box is built from its own FXML file, with the same object serving as both root
 * and controller, so the rest of the code can treat it as a plain control.
 */
public class DialogBox extends HBox {

    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    private DialogBox(String text, Image image) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            throw new IllegalStateException("Cannot load /view/DialogBox.fxml", e);
        }

        dialog.setText(text);
        displayPicture.setImage(image);
    }

    /**
     * Returns a box for something the user said, with the picture on the right.
     *
     * @param text what the user typed.
     * @param image the user's picture.
     * @return the new dialog box.
     */
    public static DialogBox getUserDialog(String text, Image image) {
        return new DialogBox(text, image);
    }

    /**
     * Returns a box for something EV said, with the picture on the left.
     *
     * @param text EV's reply.
     * @param image EV's picture.
     * @return the new dialog box.
     */
    public static DialogBox getEvDialog(String text, Image image) {
        DialogBox box = new DialogBox(text, image);
        box.flip();
        return box;
    }

    /**
     * Flips the box so that the picture is on the left and the text on the right, and
     * points the tail of the bubble at the speaker.
     */
    private void flip() {
        ObservableList<Node> children = FXCollections.observableArrayList(this.getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
        setAlignment(Pos.TOP_LEFT);
        dialog.getStyleClass().add("reply-label");
    }
}
