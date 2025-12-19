// import java.io.*;
// import java.util.*;
// public class DataLoader {
//  // LOAD QUESTIONS
//     public static List<Question> loadQuestions(String file) throws IOException {
//         List<Question> questions = new ArrayList<>();
//         BufferedReader br = new BufferedReader(new FileReader(file));
//         String line;

//         while ((line = br.readLine()) != null) {
//             String[] parts = line.split("\\|");
//             questions.add(new Question(parts[0], parts[1]));
//         }
//         br.close();
//         return questions;
//     }

//     // LOAD PEOPLE
//     public static List<Person> loadPeople(String file) throws IOException {
//         List<Person> people = new ArrayList<>();
//         BufferedReader br = new BufferedReader(new FileReader(file));
//         String line;

//         while ((line = br.readLine()) != null) {
//             String[] parts = line.split(",");
//             String name = parts[0];
//             Map<String, Boolean> attrs = new HashMap<>();

//             for (int i = 1; i < parts.length; i++) {
//                 String[] kv = parts[i].split("=");
//                 attrs.put(
//                     kv[0],
//                     kv[1].equals("null") ? null : Boolean.parseBoolean(kv[1])
//                 );
//             }
//             people.add(new Person(name, attrs));
//         }
//         br.close();
//         return people;
//     }

//     // SAVE PEOPLE (for learning new characters)
//     public static void savePeople(String file, List<Person> people) throws IOException {
//         PrintWriter pw = new PrintWriter(new FileWriter(file));
//         for (Person p : people) {
//             pw.print(p.name);
//             for (Map.Entry<String, Boolean> e : p.attributes.entrySet()) {
//                 pw.print("," + e.getKey() + "=" + e.getValue());
//             }
//             pw.println();
//         }
//         pw.close();
//     }
// }
// anything another comment

import java.io.*;
import java.util.*;

public class DataLoader {

    // LOAD QUESTIONS (remains pipe-separated for clarity)
    public static List<Question> loadQuestions(String file) throws IOException {
        List<Question> questions = new ArrayList<>();
        File f = new File(file);
        if (!f.exists()) return questions;

        BufferedReader br = new BufferedReader(new FileReader(f));
        String line;
        while ((line = br.readLine()) != null) {
            String[] parts = line.split("\\|");
            if (parts.length == 2) {
                questions.add(new Question(parts[0], parts[1]));
            }
        }
        br.close();
        return questions;
    }

    // LOAD PEOPLE (Handles names with commas like "Noval, Kineth M.")
    public static List<Person> loadPeople(String file) throws IOException {
        List<Person> people = new ArrayList<>();
        File f = new File(file);
        if (!f.exists()) return people;

        BufferedReader br = new BufferedReader(new FileReader(f));
        String line;

        while ((line = br.readLine()) != null) {
            if (line.trim().isEmpty()) continue;

            String[] parts = line.split(",");
            StringBuilder nameBuilder = new StringBuilder();
            Map<String, Boolean> attrs = new HashMap<>();

            boolean foundFirstAttribute = false;

            for (String part : parts) {
                if (part.contains("=")) {
                    foundFirstAttribute = true;
                    String[] kv = part.split("=");
                    String key = kv[0].trim();
                    String val = (kv.length > 1) ? kv[1].trim() : "null";
                    
                    attrs.put(key, val.equals("null") ? null : Boolean.parseBoolean(val));
                } else {
                    // If we haven't hit an '=' yet, this is part of the Name
                    if (!foundFirstAttribute) {
                        if (nameBuilder.length() > 0) nameBuilder.append(",");
                        nameBuilder.append(part);
                    }
                }
            }
            people.add(new Person(nameBuilder.toString().trim(), attrs));
        }
        br.close();
        return people;
    }

    // SAVE PEOPLE (Outputs: Name,key1=value1,key2=value2)
    public static void savePeople(String file, List<Person> people) throws IOException {
        PrintWriter pw = new PrintWriter(new FileWriter(file));
        for (Person p : people) {
            StringBuilder sb = new StringBuilder();
            sb.append(p.name); // No extra comma here, logic handles comma inside name
            
            for (Map.Entry<String, Boolean> e : p.attributes.entrySet()) {
                sb.append(",");
                sb.append(e.getKey()).append("=").append(e.getValue());
            }
            pw.println(sb.toString());
        }
        pw.close();
    }
}