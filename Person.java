
import java.util.Map;

public class Person {
    String name;
    Map<String, Boolean> attributes;

    public Person(String name, Map<String, Boolean> attributes) {
        this.name = name;
        this.attributes = attributes;
    }

    public boolean matches(String key, Boolean answer) {
        Boolean value = attributes.get(key);
        if (answer == null || value == null) return true;
        return value.equals(answer);
    }
}
