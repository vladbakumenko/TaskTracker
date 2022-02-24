import java.util.HashMap;

public class Epic extends Task {

    HashMap<Integer, Subtask> mapOfSubtask = new HashMap<>();

    public Epic(String title, String description, int id) {
        super(title, description, id);
    }

    public Epic() {
    }

    @Override
    public void setStatus(String wrong) {
        super.setStatus(wrong);
        System.out.println("dfdfg");
    }

    @Override
    public String toString() {
        return "Epic{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status='" + status + '\'' +
                ", subtasks=" + mapOfSubtask.size() +
                '}';
    }
}
