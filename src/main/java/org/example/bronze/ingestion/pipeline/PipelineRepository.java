package org.example.bronze.ingestion.pipeline;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.bronze.util.Constants;
import org.example.bronze.util.DatabaseConfig;

import javax.sql.DataSource;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class PipelineRepository
{
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final DataSource dataSource = DatabaseConfig.getDataSource();

    public static List<IngestionPipeline> loadActivePipelines()
    {
        List<IngestionPipeline> pipelines = new ArrayList<>();
        String query = Constants.GET_PIPELINES;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery())
        {

            while (rs.next())
            {
                long pipelineId = rs.getLong("pipelineId");
                String configFilePath = rs.getString("configFilePath");

                try
                {
                    JsonNode root = objectMapper.readTree(Path.of(configFilePath).toFile());
                    String connector = root.get("connector").asText();

                    IngestionPipeline pipeline = PipelineFactory.createPipelineFromJson(root, connector, pipelineId);
                    if (pipeline != null)
                    {
                        pipelines.add(pipeline);
                    }
                    else
                    {
                        Constants.logger.error("Pipeline {} not found", pipelineId);
                    }

                } catch (Exception e)
                {
                    Constants.logger.error("Error reading pipeline", e);
                }
            }

        } catch (Exception e)
        {
            Constants.logger.error("Error reading pipeline", e);
        }

        return pipelines;
    }
}
