import java.util.UUID;

public class Task {

    String id;

    int priority;

    String name;
    int importance;
    int urgency;
    int length;

    Task(String name,int importance,int urgency,int length){

        this.id = UUID.randomUUID().toString();

        this.name = name;
        this.importance = importance;
        this.urgency = urgency;
        this.length = length;
    }

    void printDetails(){
        // printf
    }
}
