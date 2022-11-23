package com.example.springboot;

import java.io.IOError;
import java.io.IOException;
import java.text.ParseException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;
import com.example.springboot.ROPCClass;
import com.example.springboot.SSOClass;
import org.json.simple.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


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

}
