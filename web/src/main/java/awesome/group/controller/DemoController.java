package awesome.group.controller;

import awesome.group.aop.RestApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/demo")
public class DemoController {

    @Autowired
    private CaptchaService captchaService;

    private Semaphore semaphore = new Semaphore(5);//模仿并发为5

    @GetMapping("/mock")
    @RestApi
    public String mock() {
        mockProcess();
//        captchaService.analyzeNvc("test");
        return "mock";
    }

    @GetMapping("/doNothing")
    @RestApi
    public String doNothing() {
        try {
            Thread.sleep(200);//模拟接口RT
        } catch (Exception e) {

        }
        return "doNothing";
    }

    private void mockProcess() {
        boolean getConn = false;
        long start = System.currentTimeMillis();
        synchronized (this) {
            try {
                getConn = semaphore.tryAcquire(15, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                throw new RuntimeException("getConn interrupted, wait:" + (System.currentTimeMillis() - start));
            }
        }

        if (!getConn) {
            throw new RuntimeException("getConn timeout, wait:" + (System.currentTimeMillis() - start));
        }
        try {
            Thread.sleep(100);//模拟100ms接口调用
            semaphore.release();
        } catch (InterruptedException e) {
        }
    }

}
