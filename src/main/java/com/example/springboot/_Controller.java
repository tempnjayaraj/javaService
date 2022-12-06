package com.example.springboot;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
//import okhttp3.RequestBody;


@CrossOrigin
@Controller
public class _Controller {

	@GetMapping("/")
	@ResponseBody
	public String index() {
		return "Greetings from ESign!";
	}

	@RequestMapping(
			value = "/authenticate" ,
			method=RequestMethod.POST, 
			consumes={org.springframework.http.MediaType.APPLICATION_JSON_VALUE})
	@ResponseBody
	public Boolean authenticateUser(@RequestBody JSONObject obj) throws ParseException,IOException{
		String username = (String) obj.get("username");
		String password = (String) obj.get("password");
		String clientID = (String) obj.get("clientID");
		String tenantID = (String) obj.get("tenantID");		
		Boolean isAuth = ROPCClass.work(username,password,clientID,tenantID);
		return isAuth;
	}

	@RequestMapping(
			value = "/checkSSO" ,
			method=RequestMethod.POST, 
			consumes={org.springframework.http.MediaType.APPLICATION_JSON_VALUE})
	@ResponseBody
	public JSONObject checkUser(@RequestBody JSONObject obj) throws ParseException,IOException, org.json.simple.parser.ParseException{
		String username = (String) obj.get("username");
		return SSOClass.work(username);
	}

	@GetMapping("/flowid")
	@ResponseBody
	public String checkresponse(@RequestBody JSONObject obj)throws ParseException,IOException, org.json.simple.parser.ParseException{
		OkHttpClient client = new OkHttpClient().newBuilder()
		.build();
		String envID = (String)obj.get("environmentID");
		String loginHint =(String)obj.get("loginHint");
		MediaType mediaType = MediaType.parse("application/json");
		Request request = new Request.Builder()
		.url("https://auth.pingone.asia/"+envID+"/as/authorize?client_id=adminui&redirect_uri=https%3A%2F%2Fconsole.pingone.asia%2Findex.html&response_type=id_token%20token&scope=openid%20profile%20email&login_hint="+loginHint)
		.method("GET", null)
		.addHeader("Content-Type", "application/json")
		.build();
		Response response = client.newCall(request).execute();
		String ID = response.toString().replace("Response{protocol=h2, code=200, message=, url=https://apps.pingone.asia/"+envID+"/signon/?flowId=", "").replace("}", "");
		System.out.println(ID);
		
		return ID;
	}
	
	@PostMapping("/validpingid")
	@ResponseBody
	public boolean checkValidAccount(@RequestBody JSONObject obj)throws ParseException,IOException,org.json.simple.parser.ParseException{
		
		boolean isvalid=false;
		JSONObject abc= new JSONObject();
		
		
		
		OkHttpClient client = new OkHttpClient().newBuilder().build();
		
		String username = (String)obj.get("username");
		String password=(String)obj.get("password");
		String envID = (String)obj.get("environmentID");
		String loginHint =(String)obj.get("loginHint");
		String domain=(String)obj.get("domain");
		
		String url=null;
		
		
		abc.put("username", username);
		abc.put("password", password);
		
		if(domain.toLowerCase().contains("asia")) {
			
			url="pingone.asia";
				
		}else if(domain.toLowerCase().contains("america")) {
			
			url= "pingone.com";
			
		}
		
		
		
		MediaType mediaType = MediaType.parse("application/json");
		Request request = new Request.Builder()
				.url("https://auth."+url+"/"+envID+"/as/authorize?client_id=adminui&redirect_uri=https%3A%2F%2Fconsole."+url+"%2Findex.html&response_type=id_token%20token&scope=openid%20profile%20email&login_hint="+loginHint)
				.method("GET", null)
				.addHeader("Content-Type", "application/json")
				.build();
		Response response = client.newCall(request).execute();
		System.out.println(response);
		String FlowID = response
				.toString()
				.replace("Response{protocol=h2, code=200, message=, url=https://apps."+url+"/"+envID+"/signon/?flowId=", "")
				.replace("}", "");
		
		
		OkHttpClient client2 = new OkHttpClient().newBuilder()
			    .build();
		MediaType mediaType2 = MediaType.parse("application/vnd.pingidentity.usernamePassword.check+json");
		
		
		okhttp3.RequestBody body = okhttp3.RequestBody.create(mediaType2, abc.toString());
		Request req= new Request.Builder()
				.url("https://auth."+url+"/"+envID+"/flows/"+FlowID)
				.method("POST",(okhttp3.RequestBody) body)
				.addHeader("Content-Type", "application/vnd.pingidentity.usernamePassword.check+json")
				.build();
		
		Response finalResponse = client.newCall(req).execute();
		isvalid=finalResponse.body().string().contains("\"status\" : \"COMPLETED\"");
		
		return isvalid;
		
	}


}
