package com.pipelinex.leads;

import com.pipelinex.support.AbstractPostgresIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class LeadRepositoryIntegrationTest extends AbstractPostgresIntegrationTest {

    @Autowired
    LeadRepository leadRepository;

    @Test
    void repScopedSearchReturnsOnlyAssignedActiveLeads() {
        var results = leadRepository.searchRep(2L, null, null, null, false, false, Sort.by("id"));
        assertThat(results).extracting(Lead::getId).containsExactly(1L, 3L);
    }
}
