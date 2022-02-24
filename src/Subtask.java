public class Subtask extends Task {

    public Subtask(String title, String description, int id) {
        super(title, description, id);
    }

    public Subtask() {
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status='" + status + '\'' +
                '}';
    }
}
