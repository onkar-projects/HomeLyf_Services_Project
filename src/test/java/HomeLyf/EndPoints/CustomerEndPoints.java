package HomeLyf.EndPoints;

import static io.restassured.RestAssured.given;

//import HomeLyf.Payload.UserLogin_Payload;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class CustomerEndPoints {
//	static String bearertoken="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1bmlxdWVfbmFtZSI6IjIwMTY2IiwibmFtZWlkIjoiTW96aWxsYS81LjAgKFdpbmRvd3MgTlQgMTAuMDsgV2luNjQ7IHg2NCkgQXBwbGVXZWJLaXQvNTM3LjM2IChLSFRNTCwgbGlrZSBHZWNrbykgQ2hyb21lLzEyMy4wLjAuMCBTYWZhcmkvNTM3LjM2IiwibmJmIjoxNzE0MzcyNTY4LCJleHAiOjE3MTQzODY5NjgsImlhdCI6MTcxNDM3MjU2OCwiaXNzIjoiaHR0cHM6Ly9xZHRhcy5jb20vIiwiYXVkIjoiaHR0cHM6Ly9xZHRhcy5jb20vQ3VzdG9tZXJBUEkvIn0.qCpNCY9OcAEEsybYUoSLW4oORSZZMy0tTH5f3eQnbVI";
	public static Response CustomerGetBooking(String bearertoken ,String status) {
		System.out.println("----Hi iam token----->"+ bearertoken);
		Response response = given().header("Authorization", "Bearer " + bearertoken).contentType(ContentType.JSON)
				.queryParam("Status", status).log().all().when().get(Routes.customer_getBooking);

		return response;
	}
}
