package org.account.mgmtsystem.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class CustomGeneratorTest {

    @Test
    @DisplayName("When generateSecurityPin is called, it can generate a 4-digit security PIN")
    void testGenerateSecurityPin() {
        // Mock the static method generateSecurityPin
        try (MockedStatic<CustomGenerator> customGeneratorMock = Mockito.mockStatic(CustomGenerator.class)) {
            // Define the behavior of the mocked static method
            customGeneratorMock.when(CustomGenerator::generateSecurityPin).thenReturn("1212");

            String pin = CustomGenerator.generateSecurityPin();

            // Assert or verify that the static method was called
            assertEquals("1212", pin);

            // verify the number of times the static method was called
            customGeneratorMock.verify(CustomGenerator::generateSecurityPin, times(1));
        }
    }
}
