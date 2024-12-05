package in.Welove.Model;

public class VcModel {

  private  String key,response;

    public VcModel(String key, String response) {
        this.key = key;
        this.response = response;
    }

    public VcModel() {

    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
