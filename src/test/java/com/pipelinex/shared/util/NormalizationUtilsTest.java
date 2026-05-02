package com.pipelinex.shared.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NormalizationUtilsTest {

    @Test
    void normalizesPhoneAndEmail() {
        assertThat(NormalizationUtils.normalizePhone(" +1 (555) 101-2222 ")).isEqualTo("+15551012222");
        assertThat(NormalizationUtils.normalizeEmail(" USER@Example.COM ")).isEqualTo("user@example.com");
        assertThat(NormalizationUtils.trimToNull("   ")).isNull();
    }
}
