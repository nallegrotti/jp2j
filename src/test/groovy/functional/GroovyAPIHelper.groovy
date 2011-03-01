package functional

import groovyx.net.http.*
import static groovyx.net.http.ContentType.JSON

class GroovyAPIHelper {
    public static def DOMAIN = "http://localhost:8080"
    static def PUBLIC_RESOURCE = "/public_resource"
    static def PRIVATE_RESOURCE = "/private_resource"
    static def APP_RESOURCE = "/applications"
    public static def PING_URI = "/ping"
    static def QUOTA_RESOURCE = "/applications/#id/quota"
    static def TIMEOUT_ERROR_RESOURCE = "/timeout"
    static def ECHELON_CROSS = "/echelon?action=cross"
    static def ECHELON_CACHE = "/echelon?action=cache"
    static def IO_ERROR_RESOURCE = "/ioexception"
    static def JALIVE_URL = "/jm/jalive"
    static def IS_ALIVE_URL = "/isAlive"
    public static def AUTH_URI = "/auth/access_token/"
    public static def CONTENT_CHECK = "/content_length"
    public static def ECHO = "/echo"
    public static def ECHO_HEADERS = "/echoHeaders"
    
    static def DEFAULT_HEADERS = [Accept:"application/json", "Content-Type" : "application/json", "X-Forwarded-For": "166.66.66.6"]
    static def NO_FORWARD_HEADERS = ["Accept:application/json", "Content-Type:application/json"]
    static def BROWSER_HEADERS = ["Accept:text/html", "Content-Type:text/html", "X-Forwarded-For: 166.66.66.6"]
    static def http = new RESTClient( DOMAIN )
    
    static def get (int id, String...headers){ 
    	def resp = http.get(path: "$PUBLIC_RESOURCE/$id", headers: headers?headers:DEFAULT_HEADERS)
    }    

    static def get (String uri, String...headers){ 
    	def resp = http.get(path: uri, headers: headers?headers:DEFAULT_HEADERS)
    }    

    static def trace (int id, String...headers){ 
    	def resp = http.trace(path: "$PUBLIC_RESOURCE/$id", headers: headers?headers:DEFAULT_HEADERS)
    }    

    static def post (String name) {
	def resp = http.post(path: PRIVATE_RESOURCE, 
				 headers:[ "X-Caller-Scopes" : "123", "X-Caller-Id" : "123" ],
//			     query:[],
                             body:[name: name],
			     requestContentType : JSON)
    }

    static def delete (int id) {
	def resp = http.delete(path: "${PRIVATE_RESOURCE}/${id}", 
//		query:[],
		headers:["X-Caller-Scopes":"123", "X-Caller-Id":"123"],
		requestContentType : JSON)
    }
    
}