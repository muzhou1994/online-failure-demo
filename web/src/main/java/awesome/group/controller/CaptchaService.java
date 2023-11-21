package awesome.group.controller;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.afs.model.v20180112.AnalyzeNvcRequest;
import com.aliyuncs.afs.model.v20180112.AnalyzeNvcResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class CaptchaService {

    private static final String ACCESS_KEY_ID = "xxxx";
    public static final String ACCESS_KEY_SECRET = "xxxxxxx";
    private static final String REGION = "cn-hangzhou";
    private static IClientProfile profile;
    private static IAcsClient client;


    public static final int PASS = 200;

    @PostConstruct
    public void init() throws ClientException {
        profile = DefaultProfile.getProfile(REGION, ACCESS_KEY_ID, ACCESS_KEY_SECRET);
        client = new DefaultAcsClient(profile);
        DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", "afs", "afs.aliyuncs.com");
    }

    public Integer analyzeNvc(String str) {

        AnalyzeNvcRequest request = new AnalyzeNvcRequest();
        request.setData(str);
        request.setScoreJsonStr("{\"200\":\"PASS\",\"400\":\"NC\",\"600\":\"NC\",\"700\":\"NC\",\"800\":\"BLOCK\"}");

        try {
            AnalyzeNvcResponse response = client.getAcsResponse(request);
            return Integer.parseInt(response.getBizCode());
        } catch (ClientException e) {
        }
        return PASS;
    }
}
