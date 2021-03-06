package geogebra.html5.move.ggtapi.models;

import geogebra.common.move.ggtapi.models.json.JSONObject;
import geogebra.common.move.ggtapi.models.json.JSONString;
import geogebra.html5.util.JSON;

import java.util.Set;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * @author gabor
 *	JSON parser for web and touch, to return common JSONValue-s
 */
public class JSONParser {

	/**
	 * @param json javascript object got from JSON.parse
	 * @return  JSONObject a common json object
	 */
	public static JSONObject parseToJSONObject(JavaScriptObject json) {
	   com.google.gwt.json.client.JSONObject tmp = new com.google.gwt.json.client.JSONObject(json);
	   JSONObject ret = new JSONObject();
	   Set<String> keys = tmp.keySet();
	   for (String key : keys) {
		   ret.put(key, new JSONString(JSON.get(json, key)));
	   }	   
	   return ret;
    }

}
