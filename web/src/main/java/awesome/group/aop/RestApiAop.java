package awesome.group.aop;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Aspect
@Component
@Order(1)
public class RestApiAop {

    @Autowired
    private ObjectMapper objectMapper;

    private static final Logger LOGGER = LoggerFactory.getLogger(RestApiAop.class);


    @Around("@annotation(RestApi)")
    public Object advice(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getMethod().getName();
        Throwable exception = null;
        long start = System.currentTimeMillis();
        Object[] params = joinPoint.getArgs();
        List<Object> legalParams = new ArrayList<>();
        for (Object obj : params) {
            if (obj instanceof HttpSession || obj instanceof HttpServletRequest || obj instanceof HttpServletResponse) {
                continue;
            }
            legalParams.add(obj);
        }
        params = legalParams.toArray();

        Object ret = null;
        try {
            ret = joinPoint.proceed();
        } catch (Throwable e) {
            exception = e;
        } finally {
            int cost = (int) (System.currentTimeMillis() - start);
            String paramStr = "";
            try {
                paramStr = objectMapper.writeValueAsString(params);
            } catch (JsonProcessingException e) {
            }

            String retStr = "";
            if (ret != null) {
                try {
                    retStr = objectMapper.writeValueAsString(ret);
                } catch (JsonProcessingException e) {
                }
            }

            String msg = String.format("cost:%s, method:%s, param:%s, ret:%s", cost, methodName, paramStr, retStr);
            if (exception == null) {
                LOGGER.info(msg);
            } else {
                LOGGER.error(msg, exception);
            }
        }
        return ret;
    }
}
