//@author A0130221H
package fancy4.taskie.view;

import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListCell;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

class TaskListCell extends ListCell<String> {
	public static final String TIME_PLACEHOLDER = "%f";

	@Override protected void updateItem(String s, boolean empty) {
		super.updateItem(s, empty);
		if (!isEmpty()) {
			if (s.contains("-time")) {
				String time = s.substring(s.indexOf("-time")+5);
				System.out.println(time);
				String text = s.substring(0, s.indexOf("-time"));
				setGraphic(createTextFlow(text, TIME_PLACEHOLDER, time));

				} else {
					setText(s);
				}
			} else {
				setGraphic(null);
			}
		}
	
	


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

