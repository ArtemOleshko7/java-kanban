public class Subtask extends Task {

    public Integer epicId;

    public Subtask(String name, String description, Status status, Integer epicId) {
        super(name, description, status);
        this.epicId = epicId;
    }

    public Subtask(Integer id, String name, String description, Status status, Integer epicId) {
        super(id, name, description, status);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "SubTask{" +"\n" +
                "EpicId =" + epicId + ","+
                "Status=" + super.getStatus() + "," +
                "Id=" + super.getId() + "," +
                "Name=" + super.getName() + "," +
                "Description=" + super.getDescription() + "," +
                '}'+ "\n";
    }
}
