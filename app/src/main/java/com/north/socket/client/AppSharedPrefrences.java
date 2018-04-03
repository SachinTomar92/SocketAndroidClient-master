package com.north.socket.client;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.mswipetech.wisepad.sdk.MSWisepadController.GATEWAY_ENVIRONMENT;
import com.mswipetech.wisepad.sdk.MSWisepadController.NETWORK_SOURCE;

/**
 * AppSharedPrefrences 
 * defines the data that can persisted using hte shared preferences
 * 
 */
public class AppSharedPrefrences
{


	/* the shared preference store name where the persistence data get saved*/
	public static final String stSharePrefereceStore = "prefrences";

	/* the shared preference name references data*/
	public static final String NETWORK_SOURCE_NAME = "networksource";
	public static final String GATEWAY_ENVIRONMENT_NAME = "gatewayenvironment";


	/* singleton reference to access the share persistence data  */
	private static AppSharedPrefrences mAppSharedPreferences = null;
	private static SharedPreferences mSharedPreferences;

	private AppSharedPrefrences()
	{

	}

	/**
	 * Initialize the AppSharedPrefrences through single ton pattern
	 * @param
	 *
	 * @return
	 * the application shared preference
	 */
	public static AppSharedPrefrences getAppSharedPrefrencesInstace()
	{
		if(mAppSharedPreferences == null)
		{
			mAppSharedPreferences = new AppSharedPrefrences();

		}
		return mAppSharedPreferences;

	}

	/**
	 * Initialize the SharedPrefrences the allows to save the persistence data
	 * @param
	 *
	 * @return
	 * the application SharedPreferencese
	 */
	public SharedPreferences getSharePreferencesInstance(){

		if(mSharedPreferences == null)
		{

			mSharedPreferences = ApplicationData.getApplicationContex().getSharedPreferences(stSharePrefereceStore,
					Context.MODE_PRIVATE);
		}
		return mSharedPreferences;
	}

	/**
	 * setGatewayEnvironment
	 * sets the the details about the mswipe gateway live or labs environment to the shared preferences
	 * @param
	 * gatewayEnvironment that defined the parameter to be set to
	 * @return
	 */
	public  void setGatewayEnvironment(GATEWAY_ENVIRONMENT gatewayEnvironment)
	{
		Editor edit = getSharePreferencesInstance().edit();
		edit.putInt(GATEWAY_ENVIRONMENT_NAME, gatewayEnvironment.ordinal());
		edit.commit();
	}

	/**
	 * getGatewayEnvironment
	 * return the the details about the mswipe gateway live or labs environment saved to the shared preferences
	 * @param
	 * @return
	 * the saved mswipe gateway live or labls environment
	 */
	public GATEWAY_ENVIRONMENT getGatewayEnvironment()
	{
		GATEWAY_ENVIRONMENT gateWayDefault = GATEWAY_ENVIRONMENT.PRODUCTION;
		int gateWay = getSharePreferencesInstance().getInt(GATEWAY_ENVIRONMENT_NAME, gateWayDefault.ordinal());
		return	GATEWAY_ENVIRONMENT.values()[gateWay];
	}

	/**
	 * setNetworkSource
     * sets the network source that the msiwpe must check for for indetify the device type 
     * @param  
     * networkSource that defined the parameter to be set to
     * @return 
     */
	public  void setNetworkSource(NETWORK_SOURCE networkSource)
	{
		Editor edit = getSharePreferencesInstance().edit();
		edit.putInt(NETWORK_SOURCE_NAME, networkSource.ordinal());
		edit.commit();
	}
	
	/**
	 * getNetworkSource
     * return the the details about the network source choosed my the user for the sdk to identify the mobile device
     * @param  
     * @return 
     * the saved mswipe NETWORK_SOURCE it could be either wifi, ethernet, imei
     */
	public NETWORK_SOURCE getNetworkSource()
	{
		NETWORK_SOURCE gateWayDefault = NETWORK_SOURCE.WIFI;
		int gateWay = getSharePreferencesInstance().getInt(NETWORK_SOURCE_NAME, gateWayDefault.ordinal());
		return	NETWORK_SOURCE.values()[gateWay];
	}
	
	/**
	 * setting session token
	 */
	public  void setSessionToken(String sessionToken)
	{
		Editor edit = getSharePreferencesInstance().edit();
		edit.putString("sessiontoken", sessionToken);
		edit.commit();
	}
	
	/**
	 * getting session token
	 * @return
	 */
	public String getSessionToken()
	{	
		String sessionToken = getSharePreferencesInstance().getString("sessiontoken","");
		return sessionToken;
	}
	
	/**
	 * setting reference id token
	 */
	public  void setReferenceId(String refernceId)
	{
		Editor edit = getSharePreferencesInstance().edit();
		edit.putString("referenceid", refernceId);
		edit.commit();
	}
	
	/**
	 * getting reference id
	 * @return
	 */
	public String getReferenceId()
	{	
		String referenceId = getSharePreferencesInstance().getString("referenceid","");
		return referenceId;
	}

	/**
	 * setting tip enabled
	 */
	public void setTipEnabled(boolean tipEnabled)
	{
		Editor edit = getSharePreferencesInstance().edit();
		edit.putBoolean("tipenabled", tipEnabled);
		edit.commit();
	}
	
	/**
	 * getting tip enabled 
	 * @return
	 */
	public boolean getTipEnabled()
	{	
		boolean tipEnabled = getSharePreferencesInstance().getBoolean("tipenabled", false);
		return tipEnabled;
	}
	
	
	/**
	 * setting currency denominator
	 */
	public  void setCurrencyCode(String currencyCode)
	{
		Editor edit = getSharePreferencesInstance().edit();
		edit.putString("currenyCode", currencyCode);
		edit.commit();
	}
	
	/**
	 * getting currency denominator
	 * @return
	 */
	public String getCurrencyCode()
	{	
		String currencyCode = getSharePreferencesInstance().getString("currenyCode","");
		return currencyCode;
	}
	
	
	/**
	 * setting convenience percentage
	 */
	public  void setConveniencePercentage(float conveniencePercentage)
	{
		Editor edit = getSharePreferencesInstance().edit();
		edit.putFloat("conveniencePercentage", conveniencePercentage);
		edit.commit();
	}
	
	/**
	 * getting convenience percentage
	 * @return
	 */
	public float getConveniencePercentage()
	{	
		float conveniencePercentage = getSharePreferencesInstance().getFloat("conveniencePercentage",0.0f);
		return conveniencePercentage;
	}
	
	/**
	 * setting servicePercentageOnConvenience
	 */
	public  void setServicePercentageOnConvenience(float servicePercentageOnConvenience)
	{
		Editor edit = getSharePreferencesInstance().edit();
		edit.putFloat("servicePercentageOnConvenience", servicePercentageOnConvenience);
		edit.commit();
	}
	
	/**
	 * getting servicePercentageOnConvenience percentage
	 * @return
	 */
	public  float getServicePercentageOnConvenience()
	{	
		float servicePercentageOnConvenience = getSharePreferencesInstance().getFloat("servicePercentageOnConvenience",0.0f);
		return servicePercentageOnConvenience;
	}
	
	
	/**
	 * setting amount
	 */
	public  void setLastTrxAmount(String amount)
	{
		Editor edit = getSharePreferencesInstance().edit();
		edit.putString("amount", amount);
		edit.commit();
	}
	
	/**
	 * getting amount
	 * @return
	 */
	public String getLastTrxAmount()
	{	
		String amount = getSharePreferencesInstance().getString("amount","");
		return amount;
	}
	
	/**
	 * setting card number
	 */
	public  void setLastTrxCardLastFourDigits(String lastfourdigits){
		Editor edit = getSharePreferencesInstance().edit();
		edit.putString("lastfourdigits", lastfourdigits);
		edit.commit();
	}
	
	/**
	 * getting card number
	 * @return
	 */
	public String getLastTrxCardLastFourDigits(){
		String lastfourdigits = getSharePreferencesInstance().getString("lastfourdigits","");
		return lastfourdigits;
	}
	
	
	/**
	 * setting ksn
	 */
	public  void setLastTrxKSN(String ksn){
		Editor edit = getSharePreferencesInstance().edit();
		edit.putString("ksn", ksn);
		edit.commit();
	}
	
	/**
	 * getting ksn
	 * @return
	 */
	public String getLastTrxKSN(){
		String ksn = getSharePreferencesInstance().getString("ksn","");
		return ksn;
	}

	/**
	 * getting reference id
	 * @return
	 */
	public  boolean isPinBypassEnabled()
	{
		return getSharePreferencesInstance().getBoolean("pinbypass",false);
	}

	/**
	 * setting reference id token
	 */
	public  void setPinBypass(boolean pinBypass)
	{
		Editor edit = getSharePreferencesInstance().edit();
		edit.putBoolean("pinbypass", pinBypass);
		edit.commit();
	}


	/**
	 * setting tip enabled
	 */
	public void setReceiptEnabled(boolean receiptEnabled)
	{
		Editor edit = getSharePreferencesInstance().edit();
		edit.putBoolean("receiptEnabled", receiptEnabled);
		edit.commit();
	}

	/**
	 * getting tip enabled
	 * @return
	 */
	public boolean isReceiptEnabled()
	{
		return getSharePreferencesInstance().getBoolean("receiptEnabled", false);
	}

	public void setPrinterSupportRequired(boolean printerSupport) {

		Editor edit = getSharePreferencesInstance().edit();
		edit.putBoolean("printerSupport", printerSupport);
		edit.commit();
	}

	public boolean isPrinterSupportRequired(){
		return getSharePreferencesInstance().getBoolean("printerSupport", false);
	}



	public void setPrintSignatureRequired(boolean printsignature) {

		Editor edit = getSharePreferencesInstance().edit();
		edit.putBoolean("printsignature", printsignature);
		edit.commit();
	}

	public boolean isPrintSignatureRequired(){
		return getSharePreferencesInstance().getBoolean("printsignature", false);
	}
	
}
