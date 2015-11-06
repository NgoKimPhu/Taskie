package fancy4.taskie.view;

import javafx.css.PseudoClass;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListCell;
import javafx.scene.control.TreeCell;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

class TaskTreeCell extends TreeCell<String> {
	public static final String PLACEHOLDER = "%f";
	
	private PseudoClass titileCell = PseudoClass.getPseudoClass("titileCell");
	@Override protected void updateItem(String s, boolean empty) {
		super.updateItem(s, empty);
		setDisclosureNode(null);
		if (!isEmpty()) {

			if (s.contains("-time")) {
				String time = s.substring(s.indexOf("-time")+5);
				System.out.println(time);
				String text = s.substring(0, s.indexOf("-time"));
				setGraphic(createTextFlow(text, PLACEHOLDER, time));

			} else if (s.contains("-title")) {
				String text = s.substring(7);
				//String title = "title";
				setGraphic(createTitleFlow(text)); 
			} else {
				setGraphic(createTextFlow(s));
			}
		} else {
			setGraphic(null);
		}
	}

	private Node createTitleFlow(String... msg) {
		FlowPane flow = new FlowPane();


		for (String s: msg) {


			Text text = new Text(s);

			text.getStyleClass().addAll("titleCell");


			flow.getChildren().add(text);
		}

		return flow;
	}

	private Node createTextFlow(String... msg) {
		FlowPane flow = new FlowPane();
		boolean isTime = false;

		for (String s: msg) {
			if (PLACEHOLDER.equals(s)) {
				isTime = true;
				continue;
			}

			Text text = new Text(s);
			if (isTime) {
				text.getStyleClass().addAll("time");
				isTime = false;
			} else {
				text.getStyleClass().add("plain");
			}
			flow.getChildren().add(text);
		}

		return flow;
	}

	private Node createSpacer(int width) {
		HBox spacer = new HBox();
		spacer.setMinWidth(HBox.USE_PREF_SIZE);
		spacer.setPrefWidth(width);
		spacer.setMaxWidth(HBox.USE_PREF_SIZE);

		return spacer;
	}
	
}

