package HomeLyf.Payload;

import java.util.List;

public class VendorDetail {
	
	private int id;
    private List<Integer> serviceCategories;
    private List<Integer> servicePostCodes;
    private long aadharNumber;
    private String experience;
    private Address address;
    
    public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public List<Integer> getServiceCategories() {
		return serviceCategories;
	}
	public void setServiceCategories(List<Integer> serviceCategories) {
		this.serviceCategories = serviceCategories;
	}
	public List<Integer> getServicePostCodes() {
		return servicePostCodes;
	}
	public void setServicePostCodes(List<Integer> servicePostCodes) {
		this.servicePostCodes = servicePostCodes;
	}
	public long getAadharNumber() {
		return aadharNumber;
	}
	public void setAadharNumber(long aadharNumber) {
		this.aadharNumber = aadharNumber;
	}
	public String getExperience() {
		return experience;
	}
	public void setExperience(String experience) {
		this.experience = experience;
	}
	public Address getAddress() {
		return address;
	}
	public void setAddress(Address address) {
		this.address = address;
	}
	
}
