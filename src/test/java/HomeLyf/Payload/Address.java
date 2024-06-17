package HomeLyf.Payload;

public class Address {
	String name;
	String type;
	String line1;
	String line2;
	String line3;
	String location;
	int postcodeId;
	int cityID;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getLine1() {
		return line1;
	}

	public void setLine1(String line1) {
		this.line1 = line1;
	}

	public String getLine2() {
		return line2;
	}

	public void setLine2(String line2) {
		this.line2 = line2;
	}

	public String getLine3() {
		return line3;
	}

	public void setLine3(String line3) {
		this.line3 = line3;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public int getPostcodeId() {
		return postcodeId;
	}

	public void setPostcodeId(int postid) {
		this.postcodeId = postid;
	}

	public int getCityID() {
		return cityID;
	}

	public void setCityID(int cityid2) {
		this.cityID = cityid2;
	}
}
