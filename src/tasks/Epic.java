package tasks;

import java.util.HashMap;

public class Epic extends Task {

    private HashMap<Integer, Subtask> subtasksInEpic = new HashMap<>();

    public Epic(String title, String description, int id) {
        super(title, description, id);
    }

    public HashMap<Integer, Subtask> getSubtasksInEpic() {
        return subtasksInEpic;
    }

    public void setSubtasksInEpic(HashMap<Integer, Subtask> subtasksInEpic) {
        this.subtasksInEpic = subtasksInEpic;
    }

    @Override
    public String toString() {
        return "tasks.Epic{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status='" + status + '\'' +
                ", subtasks=" + subtasksInEpic.size() +
                '}';
    }
}
