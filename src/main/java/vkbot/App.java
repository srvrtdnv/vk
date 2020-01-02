package vkbot;

import com.vk.api.sdk.client.TransportClient;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.*;

/**
 * Hello world!
 *
 */
public class App
{
	static Integer x = 111;
    public static void main( String[] args ) throws Exception
    {
    	while (true) {
    		Thread.sleep(500);
    		System.out.println( App.x );
    	}
    }
}
