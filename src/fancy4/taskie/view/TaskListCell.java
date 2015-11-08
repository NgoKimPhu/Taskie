//@author A0130221H
package fancy4.taskie.view;

import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.Text;

class TaskListCell extends ListCell<String> {
	// ================================================================
    // Constants
    // ================================================================
	private static final String PLAIN_CLASS_CSS = "plain";
	private static final String TIME_CLASS_CSS = "time";
	private static final int STARTING_INDEX = 0;
	private static final int TIME_STARTING_INDEX = 5;
	private static final String TIME_FLAG = "-time";
	public static final String TIME_PLACEHOLDER = "%f";

	@Override protected void updateItem(String s, boolean empty) {
		super.updateItem(s, empty);
		if (!isEmpty()) {
			if (s.contains(TIME_FLAG)) {
				String time = s.substring(s.indexOf(TIME_FLAG) + TIME_STARTING_INDEX);
				System.out.println(time);
				String text = s.substring(STARTING_INDEX, s.indexOf(TIME_FLAG));
				setGraphic(createTextFlow(text, TIME_PLACEHOLDER, time));
				} else {
					setText(s);
				}
			} else {
				setGraphic(null);
			}
		}
	
	/**
	 * Create a FlowPane to display the content and time with different css styles.
	 */
	private Node createTextFlow(String... msg) {
		FlowPane flow = new FlowPane();
		boolean isTime = false;
		
		for (String s: msg) {
			if (TIME_PLACEHOLDER.equals(s)) {
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

