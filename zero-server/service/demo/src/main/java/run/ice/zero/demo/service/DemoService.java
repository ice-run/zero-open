package run.ice.zero.demo.service;

import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import run.ice.zero.api.demo.model.Cat;
import run.ice.zero.api.demo.model.Dog;
import run.ice.zero.common.helper.ThreadHelper;
import run.ice.zero.demo.config.AppConfig;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @author DaoDao
 */
@Slf4j
@Service
public class DemoService {

    @Resource
    private AppConfig appConfig;

    @Resource
    private ThreadHelper threadHelper;

    public Dog demo(Cat cat) {

        log.info("slogan : {}", appConfig.getSlogan());

        String name = cat.getName();
        name = new StringBuilder(name).reverse().toString();

        Dog dog = new Dog();
        dog.setName(name);

        CompletableFuture<Dog> future1 = threadHelper.virtual(args -> test((Cat) args[0], (Dog) args[1]), cat, dog);
        CompletableFuture<Dog> future2 = threadHelper.virtual(args -> test((Cat) args[0], (Dog) args[1]), cat, dog);
        CompletableFuture<Dog> future3 = threadHelper.virtual(args -> test((Cat) args[0], (Dog) args[1]), cat, dog);

        CompletableFuture.allOf(future1, future2, future3).join();

        Dog dog1;
        Dog dog2;
        Dog dog3;
        try {
            dog1 = future1.get();
            dog2 = future2.get();
            dog3 = future3.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        log.info("dog1: {}", dog1);
        log.info("dog2: {}", dog2);
        log.info("dog3: {}", dog3);

        return dog;
    }

    @SneakyThrows
    private Dog test(Cat cat, Dog dog) {
        Thread.sleep(3000L);
        log.info("cat: {}", cat);
        dog.setName(cat.getName());
        log.info("dog: {}", dog);
        return dog;
    }

}
