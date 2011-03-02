package functional

import groovyx.net.http.*
import static groovyx.net.http.ContentType.JSON

class GroovyAPIHelper {
    public static def DOMAIN = "http://localhost:8080"
    public static def ECHO = "/echo"
    public static def ECHO_HEADERS = "/echoHeaders"
    
    static def http = new RESTClient( DOMAIN )
}