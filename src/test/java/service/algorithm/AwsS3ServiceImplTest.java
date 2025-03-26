package service.algorithm;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.krs.dto.SortingDetailsDTO;
import com.krs.exception.AwsException;
import com.krs.service.impl.AwsS3ServiceImpl;
import com.krs.service.sort.model.AlgorithmType;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
@Testcontainers
public class AwsS3ServiceImplTest {

    @Container
    private static final LocalStackContainer localstack = new LocalStackContainer(
            DockerImageName.parse("localstack/localstack:latest")
    ).withServices(LocalStackContainer.Service.S3);

    private AwsS3ServiceImpl awsS3Service;
    private final UUID taskUuid = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

    @DynamicPropertySource
    static void dynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("aws.s3.endpoint", () -> localstack.getEndpointOverride(LocalStackContainer.Service.S3).toString());
        registry.add("aws.s3.region", localstack::getRegion);
        registry.add("aws.accessKeyId", () -> "testAccessKey");
        registry.add("aws.secretAccessKey", () -> "testSecretKey");
    }

    @BeforeEach
    void setUp() {
        ObjectMapper objectMapper = new ObjectMapper();
        S3Client s3Client = S3Client.builder()
                .endpointOverride(localstack.getEndpointOverride(LocalStackContainer.Service.S3))
                .region(software.amazon.awssdk.regions.Region.of(localstack.getRegion()))
                .build();

        localstack.start();
        String bucketName = "test-bucket";
        s3Client.createBucket(CreateBucketRequest.builder().bucket(bucketName).build());

        awsS3Service = new AwsS3ServiceImpl(s3Client, bucketName, objectMapper);
    }


    @Test
    public void shouldSaveAndVerifySortingDetails_whenValidDataProvided() throws IOException {
        List<BigDecimal> data = List.of(BigDecimal.TEN, BigDecimal.ZERO);
        AlgorithmType sortingMethod = AlgorithmType.QUICK;

        awsS3Service.saveTaskDetails(taskUuid, new SortingDetailsDTO()
                .setData(data)
                .setSortingMethod(sortingMethod)
                .setSortingTimeMillis(0L));

        SortingDetailsDTO savedDetails = awsS3Service.readTaskDetails(taskUuid, SortingDetailsDTO.class);

        assertNotNull(savedDetails);
        assertEquals(sortingMethod, savedDetails.getSortingMethod());
        assertEquals(data, savedDetails.getData());
    }

    @Test
    public void shouldReadTaskDetails_whenValidUuid_thenReturnDetails() throws JsonProcessingException {
        List<BigDecimal> data = List.of(BigDecimal.valueOf(2), BigDecimal.valueOf(5), BigDecimal.valueOf(10));
        AlgorithmType sortingMethod = AlgorithmType.MERGE;

        awsS3Service.saveTaskDetails(taskUuid, new SortingDetailsDTO()
                .setData(data)
                .setSortingMethod(sortingMethod)
                .setSortingTimeMillis(0L));

        SortingDetailsDTO details = awsS3Service.readTaskDetails(taskUuid, SortingDetailsDTO.class);

        assertNotNull(details);
        assertEquals(sortingMethod, details.getSortingMethod());
        assertEquals(data, details.getData());
    }

    @Test
    public void shouldUpdateTaskDetails_whenValidDataProvided() throws JsonProcessingException {
        List<BigDecimal> initialData = List.of(BigDecimal.valueOf(3), BigDecimal.valueOf(6));
        AlgorithmType sortingMethod = AlgorithmType.MERGE;

        awsS3Service.saveTaskDetails(taskUuid, new SortingDetailsDTO()
                .setData(initialData)
                .setSortingMethod(sortingMethod)
                .setSortingTimeMillis(0L));

        List<BigDecimal> updatedData = List.of(BigDecimal.ZERO, BigDecimal.ONE);
        long sortingTimeMillis = 1000L;

        awsS3Service.updateTaskDetails(taskUuid, new SortingDetailsDTO()
                .setData(updatedData)
                .setSortingMethod(sortingMethod)
                .setSortingTimeMillis(sortingTimeMillis));

        SortingDetailsDTO updatedDetails = awsS3Service.readTaskDetails(taskUuid, SortingDetailsDTO.class);

        assertNotNull(updatedDetails);
        assertEquals(updatedData, updatedDetails.getData());
        assertEquals(sortingTimeMillis, updatedDetails.getSortingTimeMillis());
    }


    @Test
    public void shouldThrowAwsException_whenReadingDetailsFails() {
        assertThrows(AwsException.class, () -> awsS3Service.readTaskDetails(UUID.randomUUID(), SortingDetailsDTO.class));
    }

    @Test
    public void shouldThrowAwsException_whenReadingDetailsWithInvalidUuid() {
        assertThrows(AwsException.class, () -> awsS3Service.readTaskDetails(UUID.randomUUID(), SortingDetailsDTO.class));
    }
}