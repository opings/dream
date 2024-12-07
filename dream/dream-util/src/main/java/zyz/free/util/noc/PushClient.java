package zyz.free.util.noc;

import cn.jiguang.common.ClientConfig;
import cn.jiguang.common.ServiceHelper;
import cn.jiguang.common.connection.HttpProxy;
import cn.jiguang.common.connection.IHttpClient;
import cn.jiguang.common.connection.NettyHttpClient;
import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import cn.jiguang.common.resp.ResponseWrapper;
import cn.jiguang.common.utils.Base64;
import cn.jiguang.common.utils.Preconditions;
import cn.jiguang.common.utils.StringUtils;
import cn.jiguang.common.utils.sm2.SM2Util;
import cn.jpush.api.push.CIDResult;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.BatchPushResult;
import cn.jpush.api.push.model.EncryptKeys;
import cn.jpush.api.push.model.EncryptPushPayload;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import com.google.gson.*;
import lombok.extern.log4j.Log4j2;

import java.util.List;

@Log4j2
public class PushClient {

    private IHttpClient _httpClient;
    private String _baseUrl;
    private String _pushPath;
    private String _pushValidatePath;
    private String batchRegidPushPath;
    private String batchAliasPushPath;

    private JsonParser _jsonParser = new JsonParser();

    // If not present, true by default.
    private int _apnsProduction;

    // If not present, the default value is 86400(s) (one day)
    private long _timeToLive;

    // encrypt type, the default value is empty
    private String _encryptType;

    public PushClient(String masterSecret, String appKey, HttpProxy proxy, ClientConfig conf, int maxConnectionCount, int maxConnectionPerRoute, int maxRoute) {
        ServiceHelper.checkBasic(appKey, masterSecret);

        this._baseUrl = (String) conf.get(ClientConfig.PUSH_HOST_NAME);
        this._pushPath = (String) conf.get(ClientConfig.PUSH_PATH);
        this._pushValidatePath = (String) conf.get(ClientConfig.PUSH_VALIDATE_PATH);

        this.batchAliasPushPath = (String) conf.get(ClientConfig.BATCH_ALIAS_PUSH_PATH);
        this.batchRegidPushPath = (String) conf.get(ClientConfig.BATCH_REGID_PUSH_PATH);

        this._apnsProduction = (Integer) conf.get(ClientConfig.APNS_PRODUCTION);
        this._timeToLive = (Long) conf.get(ClientConfig.TIME_TO_LIVE);
        this._encryptType = (String) conf.get(ClientConfig.ENCRYPT_TYPE);

        String authCode = ServiceHelper.getBasicAuthorization(appKey, masterSecret);
        ApacheHttpClient apacheHttpClient = new ApacheHttpClient(authCode, proxy, conf);
        apacheHttpClient.setMaxConnectionCount(maxConnectionCount);
        apacheHttpClient.setMaxConnectionPerRoute(maxConnectionPerRoute);
        apacheHttpClient.setMaxHostConnection(maxRoute);
        this._httpClient = apacheHttpClient;
    }

    public PushResult sendPush(PushPayload pushPayload) throws APIConnectionException, APIRequestException {
        Preconditions.checkArgument(!(null == pushPayload), "pushPayload should not be null");

        if (_apnsProduction > 0) {
            pushPayload.resetOptionsApnsProduction(true);
        } else if (_apnsProduction == 0) {
            pushPayload.resetOptionsApnsProduction(false);
        }

        if (_timeToLive >= 0) {
            pushPayload.resetOptionsTimeToLive(_timeToLive);
        }

        ResponseWrapper response = _httpClient.sendPost(_baseUrl + _pushPath, getEncryptData(pushPayload));

        return NewBaseResult.fromResponse(response, PushResult.class);
    }

    public PushResult sendPushValidate(PushPayload pushPayload) throws APIConnectionException, APIRequestException {
        Preconditions.checkArgument(!(null == pushPayload), "pushPayload should not be null");

        if (_apnsProduction > 0) {
            pushPayload.resetOptionsApnsProduction(true);
        } else if (_apnsProduction == 0) {
            pushPayload.resetOptionsApnsProduction(false);
        }

        if (_timeToLive >= 0) {
            pushPayload.resetOptionsTimeToLive(_timeToLive);
        }

        ResponseWrapper response = _httpClient.sendPost(_baseUrl + _pushValidatePath, getEncryptData(pushPayload));

        return NewBaseResult.fromResponse(response, PushResult.class);
    }

    public PushResult sendPush(String payloadString) throws APIConnectionException, APIRequestException {
        Preconditions.checkArgument(StringUtils.isNotEmpty(payloadString), "pushPayload should not be empty");

        try {
            _jsonParser.parse(payloadString);
        } catch (JsonParseException e) {
            Preconditions.checkArgument(false, "payloadString should be a valid JSON string.");
        }

        ResponseWrapper response = _httpClient.sendPost(_baseUrl + _pushPath, getEncryptData(payloadString));

        return NewBaseResult.fromResponse(response, PushResult.class);
    }

    public PushResult sendPushValidate(String payloadString) throws APIConnectionException, APIRequestException {
        Preconditions.checkArgument(StringUtils.isNotEmpty(payloadString), "pushPayload should not be empty");

        try {
            _jsonParser.parse(payloadString);
        } catch (JsonParseException e) {
            Preconditions.checkArgument(false, "payloadString should be a valid JSON string.");
        }

        ResponseWrapper response = _httpClient.sendPost(_baseUrl + _pushValidatePath, getEncryptData(payloadString));

        return NewBaseResult.fromResponse(response, PushResult.class);
    }

    public BatchPushResult batchSendPushByRegId(List<PushPayload> pushPayloadList) throws APIConnectionException, APIRequestException {
        return batchSendPush(_baseUrl + batchRegidPushPath, pushPayloadList);
    }

    public BatchPushResult batchSendPushByAlias(List<PushPayload> pushPayloadList) throws APIConnectionException, APIRequestException {
        return batchSendPush(_baseUrl + batchAliasPushPath, pushPayloadList);
    }

    public BatchPushResult batchSendPush(String url, List<PushPayload> pushPayloadList) throws APIConnectionException, APIRequestException {

        Preconditions.checkArgument((null != pushPayloadList), "param should not be null");
        Preconditions.checkArgument((!pushPayloadList.isEmpty()), "pushPayloadList should not be empty");

        Gson gson = new Gson();

        JsonObject contentJson = new JsonObject();

        CIDResult cidResult = getCidList(pushPayloadList.size(), "push");
        int i = 0;
        JsonObject pushPayLoadList = new JsonObject();
        // setting cid
        for (PushPayload payload : pushPayloadList) {
            String cid = payload.getCid();
            if (cid != null && !cid.trim().isEmpty()) {
                payload.setCid(null);
            } else {
                cid = cidResult.cidlist.get(i++);
            }
            pushPayLoadList.add(cid, payload.toJSON());
        }
        contentJson.add("pushlist", pushPayLoadList);

        ResponseWrapper response = _httpClient.sendPost(url, getEncryptData(gson.toJson(contentJson)));

        return BatchPushResult.fromResponse(response);

    }

    /**
     * Get cid list, the data form of cid is appKey-uuid.
     *
     * @param count the count of cid list, from 1 to 1000. default is 1.
     * @param type  default is "push", option: "schedule"
     * @return CIDResult, an array of cid
     * @throws APIConnectionException connect exception
     * @throws APIRequestException    request exception
     */
    public CIDResult getCidList(int count, String type) throws APIConnectionException, APIRequestException {
        Preconditions.checkArgument(count >= 1 && count <= 1000, "count should not less than 1 or larger than 1000");
        Preconditions.checkArgument(type == null || type.equals("push") || type.equals("schedule"), "type should be \"push\" or \"schedule\"");
        ResponseWrapper responseWrapper;
        if (type != null) {
            responseWrapper = _httpClient.sendGet(_baseUrl + _pushPath + "/cid?count=" + count + "&type=" + type);
        } else {
            responseWrapper = _httpClient.sendGet(_baseUrl + _pushPath + "/cid?count=" + count);
        }
        return NewBaseResult.fromResponse(responseWrapper, CIDResult.class);
    }

    public void setHttpClient(IHttpClient client) {
        this._httpClient = client;
    }

    // 如果使用 NettyHttpClient，在发送请求后需要手动调用 close 方法
    public void close() {
        if (_httpClient != null && _httpClient instanceof NettyHttpClient) {
            ((NettyHttpClient) _httpClient).close();
        } else if (_httpClient != null && _httpClient instanceof ApacheHttpClient) {
            ((ApacheHttpClient) _httpClient).close();
        }
    }

    /**
     * 获取加密的payload数据
     *
     * @param payloadData
     * @return
     */
    private String getEncryptData(String payloadData) {
        JsonElement payloadElement = _jsonParser.parse(payloadData);
        JsonObject jsonObject = payloadElement.getAsJsonObject();
        Audience audience = Audience.fromJsonElement(jsonObject.get("audience"));
        return getEncryptData(payloadData, audience);
    }

    /**
     * 获取加密的payload数据
     *
     * @param pushPayload
     * @return
     */
    private String getEncryptData(PushPayload pushPayload) {
        if (StringUtils.isEmpty(_encryptType)) {
            return pushPayload.toString();
        }
        if (EncryptKeys.ENCRYPT_SMS2_TYPE.equals(_encryptType)) {
            EncryptPushPayload encryptPushPayload = new EncryptPushPayload();
            try {
                encryptPushPayload.setPayload(String.valueOf(Base64.encode(SM2Util.encrypt(pushPayload.toString(), EncryptKeys.DEFAULT_SM2_ENCRYPT_KEY))));
            } catch (Exception e) {
                throw new RuntimeException("encrypt word exception", e);
            }
            encryptPushPayload.setAudience(pushPayload.getAudience());
            return encryptPushPayload.toString();
        }
        // 不支持的加密默认不加密
        return pushPayload.toString();
    }

    /**
     * 获取加密的payload数据
     *
     * @param pushPayload
     * @return
     */
    private String getEncryptData(String pushPayload, Audience audience) {
        if (StringUtils.isEmpty(_encryptType)) {
            return pushPayload;
        }
        if (EncryptKeys.ENCRYPT_SMS2_TYPE.equals(_encryptType)) {
            EncryptPushPayload encryptPushPayload = new EncryptPushPayload();
            try {
                encryptPushPayload.setPayload(String.valueOf(Base64.encode(SM2Util.encrypt(pushPayload, EncryptKeys.DEFAULT_SM2_ENCRYPT_KEY))));
            } catch (Exception e) {
                throw new RuntimeException("encrypt word exception", e);
            }
            encryptPushPayload.setAudience(audience);
            return encryptPushPayload.toString();
        }
        // 不支持的加密默认不加密
        return pushPayload;
    }

}


