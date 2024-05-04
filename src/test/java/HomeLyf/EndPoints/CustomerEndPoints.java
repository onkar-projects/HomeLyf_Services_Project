package HomeLyf.EndPoints;

import static io.restassured.RestAssured.given;

import HomeLyf.Payload.CustomerBooking_Payload;
import HomeLyf.Payload.UpdatePaymentStatus_Payload;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class CustomerEndPoints
{
	
	public static Response Timeslots(String bearertoken, int categoryId, int addressID) {
		Response response = given().header("Authorization", "Bearer " + bearertoken).contentType(ContentType.JSON)
				.queryParam("categoryId", categoryId).queryParam("addressID", addressID).log().all().when()
				.get(Routes.customer_GET_Booking_Timeslot);
		return response;
	}
	
public static Response Booking (String token,CustomerBooking_Payload Payload)

	{
		Response response =  given().header("Authorization","Bearer "+token).contentType(ContentType.JSON).body(Payload).log().all().when()
				.post(Routes.customer_POST_Booking);

		return response;
	}
public static Response paymentstatus(String token)
{
	Response response=given().header("Authorization","Bearer "+token).contentType(ContentType.JSON).log().all()
			.when().get(Routes.CustomerLookup_GET_PaymentStatus);
	return response;
}
public static Response paymentMode(String token)
{
	Response response=given().header("Authorization","Bearer "+token).contentType(ContentType.JSON).log().all()
			.when().get(Routes.CustomerLookup_GET_PaymentMode);
	return response;
}
public static Response BookingStatus(String token)
{
	Response response=given().header("Authorization","Bearer "+token).contentType(ContentType.JSON).log().all()
			.when().get(Routes.CustomerLookup_GET_BookingStatus);
	return response;
}

public static Response updatepaymentstatus(String token, UpdatePaymentStatus_Payload Payload)
{
	Response response=given().header("Authorization","Bearer "+token)
			.contentType(ContentType.JSON)
			.body(Payload)
			.log().all()
			 .when()
			.put(Routes.customer_PUT_Booking_UpdatePaymentStatus);
	return response;
}


}
 