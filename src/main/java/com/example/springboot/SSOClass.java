package com.example.springboot;

import java.io.IOException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SSOClass {
	
	public static JSONObject work(String username) throws IOException, ParseException {
		JSONObject resultObj = new JSONObject();
		OkHttpClient client = new OkHttpClient().newBuilder().build();
		MediaType mediaType = MediaType.parse("application/json");
		RequestBody body = RequestBody.create(mediaType, "{\"username\":\""+username+"\"}");
		Request request = new Request.Builder().url(
				"https://id.atlassian.com/rest/check-username?continue=https://id.atlassian.com/join/user-access?resource=ari%3Acloud%3Ajira%3A%3Asite%2Fb78d349c-bb4a-44b4-902b-12fa6ea8f439&continue=https%3A%2F%2Fjohnsonesign.atlassian.net&application=jira")
				.method("POST", body).addHeader("Content-Type", "application/json")
				.addHeader("Cookie",
						"atlassian.account.ffs.id=d3fb6724-a7b2-4224-92bb-31038179ceb3; atlassian.account.xsrf.token=497403cd-598a-43d1-b8ff-c280261e6292")
				.build();
		Response response = client.newCall(request).execute();
		JSONObject resp = (JSONObject) new JSONParser().parse(response.body().string());
		System.out.println(resp.toString());
		String redirect_URL = null;
		boolean isSSO = false;
		if (resp.get("action").equals("no_action")) {
			resultObj.put("isValidUser", true);
			resultObj.put("isSSOUser", false);
		} else if(resp.get("action").equals("redirect")&&resp.get("redirect_type").equals("sso")){
			redirect_URL = (String) resp.get("redirect_uri");
			resultObj.put("isValidUser", true);
			resultObj.put("isSSOUser", true);
			isSSO=true;
		}else {
			resultObj.put("isValidUser", false);
			resultObj.put("error", "No Account on Jira, Please singup");
		}

		if (isSSO) {
			MediaType mediaType1 = MediaType.parse("text/plain");
			RequestBody body1 = RequestBody.create(mediaType1, "");
			Request request1 = new Request.Builder().url(
							redirect_URL)
					.method("GET", null)
					.addHeader("Cookie",
							"atlassian.account.ffs.id=36ba4e82-6ccb-4ae4-9bde-2f0a4e4c4298; atlassian.account.xsrf.token=57e8f184-b1b7-43ec-b693-79d15a73456c")
					.build();
			Response response1 = client.newCall(request1).execute();
			String HTMLResp = response1.body().string();
			System.out.println(HTMLResp);
			if (HTMLResp.contains("Okta")) {
				
				resultObj.put("SSO", "Okta");
				
			}else if(HTMLResp.contains("microsoft")) {
				
				resultObj.put("SSO", "Azure");
				
			}
		}
		return resultObj;
	}

}