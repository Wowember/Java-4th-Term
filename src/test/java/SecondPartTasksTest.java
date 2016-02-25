import org.junit.Test;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import static org.junit.Assert.*;
import static ru.spbau.mit.wowember.SecondPartTasks.*;

public class SecondPartTasksTest {

    @Test
    public void testFindQuotes() throws IOException {
        String[] str = new String[]{"char data[] = {'a', 'b', 'c'};",
                "String str = new String(data);",
                " ",
                "Here are some more examples of how strings can be used:",
                "",
                "System.out.println(\"abc\");",
                "String cde = \"cde\";",
                "System.out.println(\"abc\" + cde);",
                "String c = \"abc\".substring(2,3);",
                "String d = cde.substring(1, 2);",
                " ",
                "The class String includes methods for examining individual characters of the sequence, for comparing strings,",
                "for searching strings, for extracting substrings, and for creating a copy of a string with all characters",
                "translated to uppercase or to lowercase. Case mapping is based on the Unicode Standard version specified by the Character class."};

        String[] str2 = new String[] {"srcBegin - index of the first character in the string to copy.",
                "srcEnd - index after the last character in the string to copy.",
                "dst - the destination array.",
                "dstBegin - the start offset in the destination array.",
                "Throws:\n",
                "IndexOutOfBoundsException - If any of the following is true:",
                "srcBegin is negative.",
                "srcBegin is greater than srcEnd",
                "srcEnd is greater than the length of this string",
                "dstBegin is negative",
                "dstBegin+(srcEnd-srcBegin) is larger than dst.length"};

        List<String> ans = Arrays.asList(str[3], str[8], str[9], str[11], str[12], str2[0], str2[1], str2[8]);
        assertEquals(findQuotes(Arrays.asList("src/test/resources/File1.txt", "src/test/resources/File2.txt"), "string"), ans);
        assertNull(findQuotes(Arrays.asList("src/test/resources/File.txt", "src/test/resources/File2.txt"), "string"));
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
        documentation.put("Java6", Collections.singletonList(java6AboutString));
        documentation.put("Java7", Collections.singletonList(java7AboutCharacter));
        documentation.put("Java8", Arrays.asList(java8AboutCollectors, java8AboutStream));

        assertEquals("Java8", findPrinter(documentation));
        assertNull(findPrinter(new HashMap<>()));
    }

    @Test
    public void testCalculateGlobalOrder() {
        Map<String, Integer> school = new HashMap<>();
        school.put("Pen", 100);
        school.put("Paper", 200);
        school.put("Human", 100);

        Map<String, Integer> university = new HashMap<>();
        university.put("Marker", 10000000);
        university.put("Human", 50);

        Map<String, Integer> kindergarten = new HashMap<>();
        kindergarten.put("Crayons", 150);
        kindergarten.put("Human", 30);

        Map<String, Integer> educationSystem = new HashMap<>();
        educationSystem.put("Pen", 100);
        educationSystem.put("Paper", 200);
        educationSystem.put("Marker", 10000000);
        educationSystem.put("Crayons", 150);
        educationSystem.put("Human", 180);

        assertEquals(educationSystem, calculateGlobalOrder(Arrays.asList(school, university, kindergarten)));
    }
}