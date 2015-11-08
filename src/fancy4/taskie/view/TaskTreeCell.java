//@@author A0130221H
package fancy4.taskie.view;


import javafx.scene.Node;
import javafx.scene.control.TreeCell;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.Text;

class TaskTreeCell extends TreeCell<String> {
	// ================================================================
    // Constants
    // ================================================================
	private static final String PLAIN_CLASS_CSS = "plain";
	private static final String TIME_CLASS_CSS = "time";
	private static final String TITLE_CLASS_CSS = "titleCell";
	private static final int TITLE_STARTING_INDEX = 7;
	private static final int STARTING_INDEX = 0;
	private static final int TIME_STARTING_INDEX = 5;
	private static final String TITLE_FLAG = "-title";
	private static final String TIME_FLAG = "-time";
	private static final String PLACEHOLDER = "%f";

	
	@Override protected void updateItem(String s, boolean empty) {
		super.updateItem(s, empty);
		setDisclosureNode(null);
		if (!isEmpty()) {

			if (s.contains(TIME_FLAG)) {
				String time = s.substring(s.indexOf(TIME_FLAG) + TIME_STARTING_INDEX);
				System.out.println(time);
				String text = s.substring(STARTING_INDEX, s.indexOf(TIME_FLAG));
				setGraphic(createTextFlow(text, PLACEHOLDER, time));

			} else if (s.contains(TITLE_FLAG)) {
				String text = s.substring(TITLE_STARTING_INDEX);
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
			text.getStyleClass().addAll(TITLE_CLASS_CSS);
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
				text.getStyleClass().addAll(TIME_CLASS_CSS);
				isTime = false;
			} else {
				text.getStyleClass().add(PLAIN_CLASS_CSS);
			}
			flow.getChildren().add(text);
		}

		return flow;
	}


}

