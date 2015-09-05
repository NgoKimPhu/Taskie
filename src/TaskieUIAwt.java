import java.awt.*;   

public class TaskieUIAwt extends Frame {
	public TaskieUIAwt() {
		setLayout(new GridLayout(0, 1, 0, 0));
		add(new Label("Today's task(s):"));
		add(new Label("Order pizza at 7pm"));
		add(new Label("Refelection by 11pm"));
		TextField input = new TextField();
		add(input);
		// input.addActionListener(this);
		
		setTitle("Taskie");
		setSize(350, 120);
		setVisible(true);
	}
	public static void main(String[] args) {
        new TaskieUIAwt();
    }
}
