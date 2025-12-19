import java.util.Map;
import java.util.HashMap;

public class Person {

    public String name;
    public Map<String, Boolean> attributes;

    // REQUIRED constructor
    public Person(String name, Map<String, Boolean> attributes) {
        this.name = name;
        this.attributes = attributes;
    }

    // Optional: empty constructor (safe to keep)
    public Person() {
        this.name = "";
        this.attributes = new HashMap<>();
    }
}
