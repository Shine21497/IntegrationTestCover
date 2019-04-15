
package com.shine.integrationtestcover.service;

        import org.junit.Test;
        import org.junit.runner.RunWith;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.boot.test.context.SpringBootTest;
        import org.springframework.test.context.junit4.SpringRunner;

        import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProgramInstructTest {

    @Autowired
    ProgramInstrumentService ProgramInstrumentService;

    @Test
    public void test() throws IOException {
        ProgramInstrumentService.doInstrumentation("junit-4.10.jar");

        System.out.println("success!");
    }

}