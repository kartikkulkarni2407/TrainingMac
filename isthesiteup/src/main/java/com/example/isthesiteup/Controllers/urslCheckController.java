package com.example.isthesiteup.Controllers;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class urslCheckController {

    private final String SITE_IS_UP="Site is Up";
    private final String SITE_IS_DOWN ="Site is down";
    private final String INCORRECT_URL ="INCORRECT URL";


    @GetMapping("/check")
public String isTheSiteUp(@RequestParam  String url)
{
String returnMessage ="";
try {
    URL urlObj = new URL(url);
    HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
    conn.setRequestMethod("GET");
    conn.connect();
    int responseCodeCategory = conn.getResponseCode()/100 ;
    if(responseCodeCategory !=2 && responseCodeCategory!=3)
    {
    returnMessage= "responseCategory"+responseCodeCategory+ " "+conn.getResponseCode()+ " "+SITE_IS_DOWN;
    }
    else
    returnMessage= SITE_IS_UP;
} catch (MalformedURLException e) {
    returnMessage= INCORRECT_URL;
} catch (IOException e) {
    returnMessage= "IO"+SITE_IS_DOWN;
}


return returnMessage;

}    
}
