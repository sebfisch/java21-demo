package sebfisch;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class StringTemplatesTests {
  @Test
  void testStringTemplate() {
    assertEquals("42", STR."\{6*7}");
  }
}
