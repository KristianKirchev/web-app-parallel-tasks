package com.krs.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.krs.dto.TaskDTO;
import com.krs.exception.AwsException;
import com.krs.service.AwsS3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class AwsS3ServiceImpl implements AwsS3Service {

    private static final String TASK_DETAILS_KEY_TEMPLATE = "task-details/%s-details.json";
    private static final String CONTENT_TYPE_JSON = "application/json";

    private final S3Client s3Client;
    private final String bucketName;
    private final ObjectMapper objectMapper;

    @Override
    public void saveTaskDetails(UUID id, TaskDTO taskDTO) {
        log.info("S3, saving task Id: {}, type: {}", id, taskDTO.getClass().getSimpleName());

        String key = String.format(TASK_DETAILS_KEY_TEMPLATE, id);

        try {
            String jsonDetails = objectMapper.writeValueAsString(taskDTO);

            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(CONTENT_TYPE_JSON)
                    .build();

            s3Client.putObject(putRequest, RequestBody.fromString(jsonDetails));

        } catch (JsonProcessingException e) {
            throw new AwsException("Problem processing json when saving: " + e, e);
        } catch (S3Exception e) {
            throw new AwsException("Problem with S3 when saving: " + e, e);
        } catch (SdkClientException e) {
            throw new AwsException("Problem with SdkClient when saving: " + e, e);
        } catch (Exception e) {
            throw new AwsException("Unexpected problem when saving: " + e, e);
        }
    }

    @Override
    public void updateTaskDetails(UUID id, TaskDTO taskDTO) {
        log.info("S3, updating task Id: {}, type: {}", id, taskDTO.getClass().getSimpleName());

        saveTaskDetails(id, taskDTO);
    }

    @Override
    public <T> T readTaskDetails(UUID id, Class<T> clazz) {
        log.info("S3, reading task Id: {}", id);

        String key = String.format(TASK_DETAILS_KEY_TEMPLATE, id);

        try {
            GetObjectRequest getRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            ResponseInputStream<GetObjectResponse> s3Object = s3Client.getObject(getRequest);

            String jsonData = filterChunkedDataFromStream(s3Object);

            return objectMapper.readValue(jsonData, clazz);
        } catch (IOException e) {
            throw new AwsException("Error reading details from S3: " + e, e);
        } catch (SdkException e) {
            throw new AwsException("Problem with Sdk when reading: " + e, e);
        }
    }

    private String filterChunkedDataFromStream(ResponseInputStream<GetObjectResponse> s3Object) throws IOException {
        StringBuilder filteredData = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(s3Object));

        String line;
        boolean isJsonStart = false;

        while ((line = reader.readLine()) != null) {
            if (line.startsWith("chunk-signature") || line.startsWith("x-amz") || line.isEmpty()) {
                continue;
            }

            if (!isJsonStart && line.trim().startsWith("{")) {
                isJsonStart = true;
            }

            if (isJsonStart) {
                filteredData.append(line.trim());
            }
        }

        return filteredData.toString().trim();
    }
}
