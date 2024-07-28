import org.junit.jupiter.api.Test;
import service.HistoryManager;
import service.Managers;
import service.TaskManager;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ManagersTest {
    @Test
    void shouldReturnNonNullManagers() {
        Managers managers = new Managers();
        TaskManager taskManager = managers.getDefault();
        HistoryManager historyManager = managers.getDefaultHistory();
        assertNotNull(historyManager);
        assertNotNull(taskManager);
    }

}