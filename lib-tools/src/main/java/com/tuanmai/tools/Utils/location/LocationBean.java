package com.tuanmai.tools.Utils.location;

/**
 * 
 * 
 * @author liyinchu
 * @Date 2015-7-13下午5:29:27
 * 
 */
public final class LocationBean {

	/**
	 * 省
	 */
	public String province = "";
	/**
	 * 市
	 */
	public String locationCity = "";

	/**
	 * 区
	 */
	public String district = "";

	/**
	 * 街道
	 */
	public String street = "";

	/**
	 * 经度
	 */
	public double latitude = 0;

	/**
	 * 纬度
	 */
	public double longitude = 0;

	@Override
	public String toString() {
		return "LocationBean [province=" + province + ", locationCity="
				+ locationCity + ", district=" + district + ", street="
				+ street + ", latitude=" + latitude + ", longitude="
				+ longitude + "]";
	}

}
