package tasks;

public class Subtask extends Task {

    protected int idOfEpic;

    public Subtask(String title, String description, int idOfNewTask, int idOfEpic) {
        super(title, description, idOfNewTask);
        this.idOfEpic = idOfEpic;
    }

    public int getIdOfEpic() {
        return idOfEpic;
    }

    public void setIdOfEpic(int idOfEpic) {
        this.idOfEpic = idOfEpic;
    }

    @Override
    public String toString() {
        return "tasks.Subtask{" +
                "idOfEpic=" + idOfEpic +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status='" + status + '\'' +
                '}';
    }
}
