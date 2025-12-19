import java.util.*;

public class AkinatorEngine {

    private List<Person> people;            // all people
    private List<Question> questions;       // all questions
    private Map<String, Boolean> answers;   // current answers
    private Set<String> asked;              // questions already asked

    public AkinatorEngine(List<Person> people, List<Question> questions) {
        this.people = people;
        this.questions = questions;
        this.answers = new HashMap<>();
        this.asked = new HashSet<>();
    }

    // Reset for a new game
    public void reset() {
        answers.clear();
        asked.clear();
    }

    // Choose next question (best split among people)
    public Question nextQuestion() {
        Question bestQ = null;
        int minDiff = Integer.MAX_VALUE;

        for (Question q : questions) {
            if (asked.contains(q.key)) continue;

            int yes = 0, no = 0;
            for (Person p : people) {
                Boolean val = p.attributes.get(q.key);
                if (val == null) continue;
                if (val) yes++; else no++;
            }

            int diff = Math.abs(yes - no);
            if (diff < minDiff) {
                minDiff = diff;
                bestQ = q;
            }
        }

        return bestQ;
    }

    // Record user's answer
    public void answer(Question q, Boolean answer) {
        asked.add(q.key);
        answers.put(q.key, answer);
    }

    // Check if game should stop
    public boolean finished() {
    // Stop only when all questions have been asked
    return asked.size() == questions.size();
}

    // Guess the most likely person
    public Person bestGuess() {
        Person best = null;
        int bestScore = -1;

        for (Person p : people) {
            int score = 0;
            for (String key : asked) {
                Boolean userAns = answers.get(key);
                Boolean personAns = p.attributes.get(key);
                if (personAns != null && userAns != null && personAns.equals(userAns)) {
                    score++;
                }
            }
            if (score > bestScore) {
                bestScore = score;
                best = p;
            }
        }

        return best;
    }

    // Calculate confidence %
    public int bestConfidence() {
        Person best = bestGuess();
        if (best == null) return 0;

        int matches = 0;
        int totalKnown = 0;

        for (String key : asked) {
            Boolean userAns = answers.get(key);
            Boolean personAns = best.attributes.get(key);
            if (personAns != null) {
                totalKnown++;
                if (personAns.equals(userAns)) matches++;
            }
        }

        if (totalKnown == 0) return 0;
        return (int) ((matches / (double) totalKnown) * 100);
    }

    // Learn a new person from current answers
    public Person learn(String name) {
        return new Person(name, new HashMap<>(answers));
    }

    public List<Question> getQuestions() {
        return questions;
    }
}
