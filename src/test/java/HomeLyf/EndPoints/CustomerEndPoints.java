package HomeLyf.EndPoints;

import static io.restassured.RestAssured.given;

import HomeLyf.Payload.UserLogin_Payload;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class CustomerEndPoints {

	public static Response userLogin(UserLogin_Payload Payload) {
		Response response = given().contentType(ContentType.JSON).body(Payload).log().all().when()
				.post(Routes.account_login);

		return response;
	}
	public static Response lookupCategory() {
		Response response = given().contentType(ContentType.JSON).log().all().when()
				.get(Routes.getLookupCategory);

		return response;
	}

	public static Response getsubCategoryId(String token, int categoryId) {

		Response response = given().header("Authorization", "Bearer " + token).contentType(ContentType.JSON).log().all()
				.when().get(Routes.getsubCategoryId,categoryId);
		return response;
	}

	

}
