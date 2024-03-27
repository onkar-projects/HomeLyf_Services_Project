package HomeLyf.Utilities;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class CommonMethods {
	
	public static JsonPath jsonToString(Response response) {
		String res = response.asPrettyString();
		JsonPath js = new JsonPath(res);
		return js;
	}
}
