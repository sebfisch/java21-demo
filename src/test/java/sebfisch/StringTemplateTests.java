package sebfisch;

import static java.lang.StringTemplate.RAW;
import static java.lang.StringTemplate.STR;
import static java.util.FormatProcessor.FMT;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import org.junit.jupiter.api.Test;

class StringTemplateTests {

    @Test
    void testDefaultTemplateProcessor() {
        String result = STR."\{1} + \{2} = \{1+2}";
        assertEquals("1 + 2 = 3", result);
    }

    @Test
    void testFormattingTemplateProcessor() {
        String result = FMT."%.2e\{1e-7}";
        assertEquals("1.00e-07", result);
    }

    @Test
    void testRawTemplateProcessor() {
        int x = 1, y = 2;

        StringTemplate template = RAW."\{x} + \{y} = \{x + y}";
        assertIterableEquals(List.of("", " + ", " = ", ""), template.fragments());
        assertIterableEquals(List.of(1, 2, 3), template.values());

        String result = STR.process(template);
        assertEquals("1 + 2 = 3", result);

        result = template.interpolate();
        assertEquals("1 + 2 = 3", result);
    }
}
