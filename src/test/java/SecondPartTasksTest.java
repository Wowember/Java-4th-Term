import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static ru.spbau.mit.wowember.SecondPartTasks.calculateGlobalOrder;
import static ru.spbau.mit.wowember.SecondPartTasks.findPrinter;
import static ru.spbau.mit.wowember.SecondPartTasks.piDividedBy4;

public class SecondPartTasksTest {

    @Test
    public void testFindQuotes() {

    }

    @Test
    public void testPiDividedBy4() {
        assertEquals(Math.PI / 4, piDividedBy4(), 0.01);
    }

    @Test
    public void testFindPrinter() {
        String java6AboutString = "The String class represents character strings." +
                " All string literals in Java programs, such as \"abc\"," +
                "are implemented as instances of this class.\n" +
                "Strings are constant; their values cannot be changed after they are created." +
                "String buffers support mutable strings. Because String objects are immutable they can be shared. For example:\n" +
                "String str = \"abc\";";

        String java7AboutCharacter = "The Character class wraps a value of the primitive type char in an object." +
                "An object of type Character contains a single field whose type is char.";

        String java8AboutStream = "A sequence of elements supporting sequential and parallel aggregate operations." +
                "The following example illustrates an aggregate operation using Stream and IntStream:\n" +
                "     int sum = widgets.stream()\n" +
                "                      .filter(w -> w.getColor() == RED)\n" +
                "                      .mapToInt(w -> w.getWeight())\n" +
                "                      .sum();";

        String java8AboutCollectors = "Implementations of Collector that implement various useful reduction operations," +
                "such as accumulating elements into collections, summarizing elements according to various criteria, etc.";

        Map<String, List<String>> documentation = new HashMap<>();
        documentation.put("Java6", Arrays.asList(java6AboutString));
        documentation.put("Java7", Arrays.asList(java7AboutCharacter));
        documentation.put("Java8", Arrays.asList(java8AboutCollectors, java8AboutStream));

        assertEquals("Java8", findPrinter(documentation));
    }

    @Test
    public void testCalculateGlobalOrder() {
        Map<String, Integer> school = new HashMap<>();
        school.put("Pen", 100);
        school.put("Peper", 200);
        school.put("Human", 100);

        Map<String, Integer> university = new HashMap<>();
        university.put("Marker", 10000000);
        university.put("Human", 50);

        Map<String, Integer> kindergarten = new HashMap<>();
        kindergarten.put("Crayons", 150);
        kindergarten.put("Human", 30);

        Map<String, Integer> educationSystem = new HashMap<>();
        educationSystem.put("Pen", 100);
        educationSystem.put("Peper", 200);
        educationSystem.put("Marker", 10000000);
        educationSystem.put("Crayons", 150);
        educationSystem.put("Human", 180);

        assertEquals(educationSystem, calculateGlobalOrder(Arrays.asList(school, university, kindergarten)));
    }
}