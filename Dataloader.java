import java.io.*;
import java.util.*;
public class DataLoader {
 // LOAD QUESTIONS
    public static List<Question> loadQuestions(String file) throws IOException {
        List<Question> questions = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;

        while ((line = br.readLine()) != null) {
            String[] parts = line.split("\\|");
            questions.add(new Question(parts[0], parts[1]));
        }
        br.close();
        return questions;
    }

    // LOAD PEOPLE
    public static List<Person> loadPeople(String file) throws IOException {
        List<Person> people = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;

        while ((line = br.readLine()) != null) {
            String[] parts = line.split(",");
            String name = parts[0];
            Map<String, Boolean> attrs = new HashMap<>();

            for (int i = 1; i < parts.length; i++) {
                String[] kv = parts[i].split("=");
                attrs.put(
                    kv[0],
                    kv[1].equals("null") ? null : Boolean.parseBoolean(kv[1])
                );
            }
            people.add(new Person(name, attrs));
        }
        br.close();
        return people;
    }

    // SAVE PEOPLE (for learning new characters)
    public static void savePeople(String file, List<Person> people) throws IOException {
        PrintWriter pw = new PrintWriter(new FileWriter(file));
        for (Person p : people) {
            pw.print(p.name);
            for (Map.Entry<String, Boolean> e : p.attributes.entrySet()) {
                pw.print("," + e.getKey() + "=" + e.getValue());
            }
            pw.println();
        }
        pw.close();
    }
}
