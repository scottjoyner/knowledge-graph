package com.mkv.custom;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Record;
import org.neo4j.driver.Session;
import org.neo4j.driver.Value;
import org.neo4j.harness.Neo4j;
import org.neo4j.harness.Neo4jBuilders;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class EmbeddingsTest {

    private Neo4j embeddedDatabaseServer;

    @BeforeAll
    void initializeNeo4j() {
        this.embeddedDatabaseServer = Neo4jBuilders.newInProcessBuilder()
                .withDisabledServer()
                .withProcedure(Embeddings.class)
                .build();
    }

    @AfterAll
    void closeNeo4j() {
        this.embeddedDatabaseServer.close();
    }

    @Test
    void generateEmbeddings() {
        // This is in a try-block, to make sure we close the driver after the test
        try(Driver driver = GraphDatabase.driver(embeddedDatabaseServer.boltURI());
            Session session = driver.session()) {

            // When
            Record result = session.run("CALL com.mkv.custom.embeddings('Hello') YIELD embeddings RETURN embeddings").single();

            // Then
            Value actual_embeddings = result.get("embeddings");

            List<Double> expected_embeddings = List.of(-0.0627717524766922, 0.054958831518888474, 0.05216477811336517);

            assertThat(actual_embeddings.get(0).asDouble()).isEqualTo(expected_embeddings.get(0));
            assertThat(actual_embeddings.get(1).asDouble()).isEqualTo(expected_embeddings.get(1));
            assertThat(actual_embeddings.get(2).asDouble()).isEqualTo(expected_embeddings.get(2));
        }
    }
}
