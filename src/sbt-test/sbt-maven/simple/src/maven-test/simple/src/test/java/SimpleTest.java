/*
 * Copyright (C) from 2023 The sbt contributors <https://github.com/sbt>
 */

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;

public class SimpleTest {
    @Test
    public void testGoal() throws IOException {
        File hello = new File("target/generated-sources/simple/simple.txt");
        assertThat(hello).exists();
        String content = Files.readString(hello.toPath(), UTF_8);
        assertThat(content).contains("Hi, I'm Simple Mojo!");
    }
}
