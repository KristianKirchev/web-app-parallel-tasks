import com.krs.SpringWebApplication;
import com.krs.entity.GeneralTaskInfo;
import com.krs.repository.TaskRepository;
import com.krs.service.sort.model.TaskStatus;
import com.krs.service.sort.model.TaskType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = SpringWebApplication.class)
@Testcontainers
@ActiveProfiles({"it"})
@Transactional
public class GeneralTaskInfoRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", postgres::getDriverClassName);
    }

    @Autowired
    private TaskRepository taskRepository;

    private final UUID taskUuid = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

    @BeforeEach
    void setup() {
        taskRepository.save(new GeneralTaskInfo(taskUuid, TaskStatus.PENDING.getValue(), 4, TaskType.SORT.getValue()));
    }

    @Test
    public void shouldStartContainer_whenContainerStart() {
        assertTrue(postgres.isRunning());
    }

    @Test
    public void givenTaskDetails_whenSaveTask_returnTask() {
        Optional<GeneralTaskInfo> insertedTask = taskRepository.findById(taskUuid);

        assertTrue(insertedTask.isPresent());
        assertEquals(TaskStatus.PENDING.getValue(), insertedTask.get().getStatus());
        assertEquals(4, insertedTask.get().getThreads());
    }

    @Test
    public void givenTaskExists_whenFindTaskStatusById_thenReturnTaskStatus() {
        Optional<String> status = taskRepository.findStatusById(taskUuid);

        assertTrue(status.isPresent());
        assertEquals(TaskStatus.PENDING.getValue(), status.get());
    }

    @Test
    public void givenTaskExists_whenFindTaskThreadNumById_thenReturnThreadCount() {
        Optional<Integer> threads = taskRepository.findThreadsById(taskUuid);

        assertTrue(threads.isPresent());
        assertEquals(4, threads.get());
    }

    @Test
    public void givenTaskExists_whenUpdateTaskStatus_thenStatusIsUpdated() {
        int updatedRows = taskRepository.updateTaskStatus(taskUuid, TaskStatus.IN_PROGRESS.getValue());

        assertEquals(1, updatedRows, "Expected one row to be updated");

        Optional<String> status = taskRepository.findStatusById(taskUuid);

        assertTrue(status.isPresent(), "Status should exist after update");
        assertEquals(TaskStatus.IN_PROGRESS.getValue(), status.get(), "GeneralTaskInfo status should be updated to COMPLETED");
    }

    @Test
    public void givenTaskExists_whenDeleteById_thenTaskIsDeleted() {
        Optional<GeneralTaskInfo> task = taskRepository.findById(taskUuid);
        assertTrue(task.isPresent(), "GeneralTaskInfo should exist before deletion");

        taskRepository.deleteById(taskUuid);

        Optional<GeneralTaskInfo> deletedTask = taskRepository.findById(taskUuid);
        assertFalse(deletedTask.isPresent(), "GeneralTaskInfo should be deleted");
    }
}
