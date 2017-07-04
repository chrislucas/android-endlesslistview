package imagineapps.uptolv.utils.http;

import java.util.Map;

import imagineapps.uptolv.action.DoOnBackground;

/**
 * Created by r028367 on 03/07/2017.
 */

public abstract class ModelHTTPRequest implements DoOnBackground {
    protected String url;
    protected Map<String, String> parameters;

    public ModelHTTPRequest() {}

    public ModelHTTPRequest(String url) {
        this.url = url;
    }

    public ModelHTTPRequest(String url, Map<String, String> parameters) {
        this.url = url;
        this.parameters = parameters;
    }

    public String getUrl() {
        return url;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }
}
