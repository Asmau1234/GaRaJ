package com.lityum.main;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

public class ServiceHandler {
	 
    static String response = null;
    public final static int GET = 1;
    public final static int POST = 2;
   // public final static int CONNECTION_TIMEOUT = 20000;
    //public final static int WAIT_RESPONSE_TIMEOUT = 20000;
 
    public ServiceHandler() {
 
    }
 
    /**
     * Making service call
     * @url - url to make request
     * @method - http request method
     * */
    public String makeServiceCall(String url, int method) {
        return this.makeServiceCall(url, method, null);
    }
 
    /**
     * Making service call
     * @url - url to make request
     * @method - http request method
     * @params - http request params
     * */
    public String makeServiceCall(String url, int method,
            List<NameValuePair> params) {
        try {
            // http client
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpEntity httpEntity = null;
            HttpResponse httpResponse = null;
             
            // Checking http request method type
            if (method == POST) {
                HttpPost httpPost = new HttpPost(url);
                // adding post params
                if (params != null) {
                	
                    httpPost.setEntity(new UrlEncodedFormEntity(params));
                }
 
                httpResponse = httpClient.execute(httpPost);
 
            } else if (method == GET) {
                // appending params to url
                if (params != null) {
                    String paramString = URLEncodedUtils
                            .format(params, "utf-8");
                    url += "?" + paramString;
                }
                HttpGet httpGet = new HttpGet(url);
 
                httpResponse = httpClient.execute(httpGet);
 
            }

            HttpParams paramz = httpClient.getParams();
           // HttpConnectionParams.setConnectionTimeout(paramz, CONNECTION_TIMEOUT);
           // HttpConnectionParams.setSoTimeout(paramz, WAIT_RESPONSE_TIMEOUT);
            HttpConnectionParams.setTcpNoDelay(paramz, true);
            HttpClientParams.setRedirecting(paramz, false);
            int responseCode = 
            	    httpResponse.getStatusLine().getStatusCode();


            	 if (responseCode != HttpStatus.SC_OK) {
            	  Header[] headers = 
            	   httpResponse.getHeaders("Location");
            	  if (headers != null && headers.length != 0) {
            	     //Need to redirect 
            	    String newUrl = 
            	       headers[headers.length - 1].getValue();
            	  
            	    makeServiceCall(newUrl,method,params);
            	   //call the doHttpRequest 
            	   //recursively with new url
            	  }
            	 } else {
            		 httpEntity = httpResponse.getEntity();
                     response = EntityUtils.toString(httpEntity);
            	 }
           
 
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
         
        return response;
 
    }
}
