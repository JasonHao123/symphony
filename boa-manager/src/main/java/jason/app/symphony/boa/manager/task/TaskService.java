package jason.app.symphony.boa.manager.task;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

@Service
class TaskService {

    private final List<Task> taskRepository = new ArrayList<Task>();


    @PostConstruct
    private void init() {
    		Task task = new Task("Shopping", "Buy Milk and Butter...", "2017.01.01 13:22:42");
    		task.setId(1L);
        taskRepository.add(task);
        task = new Task("Books", "Read 'Lords of The Ring'", "2017.01.02 15:22:42");
        task.setId(2L);
        taskRepository.add(task);
    }

    Iterable<Task> findAll() {
        return taskRepository;
    }

    public Task findOne(Long id) {
        return taskRepository.get(id.intValue()-1);
    }
}
