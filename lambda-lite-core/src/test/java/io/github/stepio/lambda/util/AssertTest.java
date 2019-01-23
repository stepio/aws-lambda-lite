package io.github.stepio.lambda.util;

import org.junit.Test;

import java.util.MissingFormatArgumentException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static io.github.stepio.lambda.util.Assert.hasLength;
import static io.github.stepio.lambda.util.Assert.message;
import static io.github.stepio.lambda.util.Assert.notNull;

public class AssertTest {

    @Test
    public void notNullWithDummyValues() {
        notNull(Integer.valueOf(42), "Test comment");
        assertThatThrownBy(() -> notNull(null, "Instance <%s> is empty", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Instance <null> is empty");
        assertThatThrownBy(() -> notNull(null, "Instance <%s> is empty because of <%s>", null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Instance <null> is empty because of <null>");
    }

    @Test
    public void hasLengthWithDummyValues() {
        hasLength("42", "Test comment");
        assertThatThrownBy(() -> hasLength("", "Instance <%s> is empty", ""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Instance <> is empty");
    }

    @Test
    public void messageWithNoParams() {
        assertThat(message(null)).isEqualTo(null);
        assertThat(message("")).isEqualTo("");
        assertThat(message("Qwerty123")).isEqualTo("Qwerty123");
        assertThatThrownBy(() -> message("Text %s"))
                .isInstanceOf(MissingFormatArgumentException.class)
                .hasMessage("Format specifier '%s'");
    }

    @Test
    public void messageWithParams() {
        assertThat(message("Qwerty123", "value")).isEqualTo("Qwerty123");
        assertThat(message("Text %s", "value")).isEqualTo("Text value");
        assertThat(message("Text %s %s", "value1", "value2")).isEqualTo("Text value1 value2");
        assertThatThrownBy(() -> message("Text %s %s", "value"))
                .isInstanceOf(MissingFormatArgumentException.class)
                .hasMessage("Format specifier '%s'");
    }
}
