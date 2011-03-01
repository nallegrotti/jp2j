
package functional
;

import groovyx.net.http.HttpResponseException;
import groovyx.net.http.HTTPBuilder;

import static org.junit.Assert.*;
import static org.junit.Assume.*;
import static org.junit.matchers.JUnitMatchers.*


import org.apache.xerces.impl.xs.identity.Selector.Matcher;
import org.junit.*;

import org.junit.After;
import org.junit.Before;
import static functional.GroovyAPIHelper.*;

class JsonPTest {
	def ACCESS_TOKEN = "082610-BUPEUYIKN7I1M4RNIPQWIWR4TCILJ5-63762259";
	private static final String BAD_ACCESS_TOKEN = "082610-BUPEUYIKN7I1M4RNIPQWIWR4TCILJ5-63762259-JASKJ";
	
	def resourceId
	
	@Before
	public void setUp() throws Exception {
		def resp = post("setUp")
		assert 201 == resp.status
		resourceId = resp.data.id
	}
	
	@After
	public void tearDown() throws Exception {
		assumeNotNull(resourceId)
		delete(resourceId)
	}
	
	@Test
	void jsonPContentHeaders(){
		def uri = "$GroovyAPIHelper.ECHO"
		def http = new HTTPBuilder( GroovyAPIHelper.DOMAIN )
		
		http.get (path:uri, headers:["Accept":"application/json", "X-Forwarded-For": "166.66.66.6", "X-Public":"True"]
			, query:[body:"dummy", headers:"Content-Type:text/html;charset=utf-8", callback:'test']
			) { resp ->
				assertNotNull resp
				assertEquals 200, resp.status
				def text = resp.entity.content.text
				assert  text ==~ "test\\(\\[200,.*\"Content-Type\":\"text/html;charset=utf-8\".*dummy\\]\\);"
				
				def cType = resp.headers["Content-Type"]
				assert cType.value == "text/javascript;charset=utf-8"
			}
	}
	
	@Test
	void jsonPCallContentLength(){
		def uri = "$CONTENT_CHECK"
		println uri
		def http = new HTTPBuilder( DOMAIN )
		http.get (path:uri, headers:["Accept":"application/json", "X-Forwarded-For": "166.66.66.6", "X-Public":"True"]
			, query:[callback:"test"]
			) { resp ->
				assertNotNull resp
				assertEquals 200, resp.status
				assertEquals 'text/javascript', resp.contentType
				def text = resp.entity.content.text
				assertEquals text.length(), Integer.parseInt(resp.headers["Content-Length"].value)
				assert text ==~ "test\\(\\[200, *\\{.*\\}, *\\{.*}]\\);"
			}
	}
	
	@Test
	void jsonPCallError(){
		def uri = "$PUBLIC_RESOURCE/$resourceId"
		println uri
		def http = new HTTPBuilder( DOMAIN )
		http.get (path:uri, headers:["Accept":"application/json", "Content-Type" : "text/javascript", "X-Forwarded-For": "166.66.66.6", "X-Public":"True"]
			, query:[callback:"test", access_token: "invalid_token"]
			) { resp ->
				assertNotNull resp
				assertEquals 200, resp.status
				assertEquals 'text/javascript', resp.contentType
				def text = resp.entity.content.text
				assert text ==~ "test\\(\\[404, *\\{.*\\}, *\\{\"message\".*\\}]\\);"
			}
	}
	
	@Test
	void jsonPCallAppJson(){
		def uri = "$PUBLIC_RESOURCE/$resourceId"
		println uri
		def http = new HTTPBuilder( DOMAIN )
		http.get (path:uri, headers:["Accept":"application/json", "Content-Type" : "text/javascript", "X-Forwarded-For": "166.66.66.6", "X-Public":"True"]
			, query:[callback:"test"]
			) { resp ->
				assertNotNull resp
				assertEquals 200, resp.status
				assertEquals 'text/javascript', resp.contentType
				def text = resp.entity.content.text
				assert text ==~ "test\\(\\[200, *\\{.*\\}, *\\{\"id\":$resourceId.*\\}]\\);"
			}
	}
	
	@Test
	void jsonPCallAppJsonWithAcceptParams(){
		def uri = "$PUBLIC_RESOURCE/$resourceId"
		println uri
		def http = new HTTPBuilder( DOMAIN )
		http.get (path:uri, headers:["Accept":"application/json; q=1", "X-Forwarded-For": "166.66.66.6", "X-Public":"True"]
			, query:[callback:"test"]
			) { resp ->
				assertNotNull resp
				assertEquals 200, resp.status
				assertEquals 'text/javascript', resp.contentType
				def text = resp.entity.content.text
				assert text ==~ "test\\(\\[200, *\\{.*\\}, *\\{\"id\":$resourceId.*\\}]\\);"
			}
	}
	
	@Test
	void jsonPCallAppTodos(){
		def uri = "$PUBLIC_RESOURCE/$resourceId"
		println uri
		def http = new HTTPBuilder( DOMAIN )
		http.get (path:uri, headers:["Accept":"*/*", "Content-Type" : "text/javascript", "X-Forwarded-For": "166.66.66.6", "X-Public":"True"]
			, query:[callback:"test"]
			) { resp ->
				assertNotNull resp
				assertEquals 200, resp.status
				assertEquals 'text/javascript', resp.contentType
				def text = resp.entity.content.text
				assert text ==~ "test\\(\\[200, *\\{.*\\}, *\\{\"id\":$resourceId.*\\}]\\);"
			}
	}
	
	@Test
	void jsonPCallAppOpera(){
		def uri = "$PUBLIC_RESOURCE/$resourceId"
		println uri
		def http = new HTTPBuilder( DOMAIN )
		http.get (path:uri, headers:["Accept":"text/html, application/xml;q=0.9, application/xhtml+xml, image/png, image/jpeg, image/gif, image/x-xbitmap, */*;q=0.1", "Content-Type" : "text/javascript", "X-Forwarded-For": "166.66.66.6", "X-Public":"True"]
			, query:[callback:"test"]
			) { resp ->
				assertNotNull resp
				assertEquals 200, resp.status
				assertEquals 'text/javascript', resp.contentType
				def text = resp.entity.content.text
				assert text ==~ "test\\(\\[200, *\\{.*\\}, *\\{\"id\":$resourceId.*\\}]\\);"
			}
	}
	
	@Test
	void jsonPCallPost(){
		def uri = "$PRIVATE_RESOURCE"
		println uri
		def http = new HTTPBuilder( DOMAIN )
		http.get (path:uri, headers:["Accept":"application/json", "Content-Type" : "text/javascript", "X-Forwarded-For": "166.66.66.6", "X-Public":"True"]
			, query:["callback":"test", "access_token":ACCESS_TOKEN, "_method":"POST", "_body":"{name:\"test\"}"]
			) { resp ->
				assertNotNull resp
				assertEquals 200, resp.status
				assertEquals 'text/javascript', resp.contentType
				def text = resp.entity.content.text
				assert text ==~ "test\\(\\[201, *\\{.*\\}, *\\{\"id\":.*,\"name\":\"test\"\\}]\\);"
			}
	}
	
	@Test
	void jsonPCallPut(){
		def uri = "$PRIVATE_RESOURCE/${resourceId}"
		println uri
		def http = new HTTPBuilder( DOMAIN )
		http.get (path:uri, headers:["Accept":"application/json", "Content-Type" : "text/javascript", "X-Forwarded-For": "166.66.66.6", "X-Public":"True"]
			, query:["callback":"test", "access_token":ACCESS_TOKEN, "_method":"PUT", "_body":"{name:\"test2\"}"]
			) { resp ->
				assertNotNull resp
				assertEquals 200, resp.status
				assertEquals 'text/javascript', resp.contentType
				def text = resp.entity.content.text
				assert text ==~ "test\\(\\[200, *\\{.*\\}, *\\{\"id\":.*,\"name\":\"test2\"\\}]\\);"
			}
	}
	
	@Test
	void jsonPCallDelete(){
		def response = post("delete-jsonp", ACCESS_TOKEN)
		assert 201 == response.status
		def resId = response.data.id
		println resId
		
		//Prueba delete con Access Token inválido
		def uri = "$PRIVATE_RESOURCE/${resId}"
		println uri
		def http = new HTTPBuilder( DOMAIN )
		http.get (path:uri, headers:["Accept":"application/json", "Content-Type" : "text/javascript", "X-Forwarded-For": "166.66.66.6", "X-Public":"True"]
			, query:["callback":"test", "access_token":BAD_ACCESS_TOKEN, "_method":"DELETE"]
			) { resp ->
				assertNotNull resp
				assertEquals 200, resp.status
				assertEquals 'text/javascript', resp.contentType
				def text = resp.entity.content.text
				assert text ==~ "test\\(\\[404, *\\{.*\\}, *\\{.*\\}]\\);"
			}
		
		
		//Prueba con Access Token válido	
		http.get (path:uri, headers:["Accept":"application/json", "Content-Type" : "text/javascript", "X-Forwarded-For": "166.66.66.6", "X-Public":"True"]
			, query:["callback":"test", "access_token":ACCESS_TOKEN, "_method":"DELETE"]
			) { resp ->
				assertNotNull resp
				assertEquals 200, resp.status
				assertEquals 'text/javascript', resp.contentType
				def text = resp.entity.content.text
				assert text ==~ "test\\(\\[200, *\\{.*\\}, *\\{\"id\":$resId.*\"name\":\"delete-jsonp\".*\\}]\\);"
			}
		
		
		try{
			response = get(resId)
			assertFail "No debe existir"
		}catch(HttpResponseException e){
			assert e.message == "Not Found"
		}
	}
	
	@Test
	void jsonPCallAppJavascript(){
		def uri = "$PUBLIC_RESOURCE/${resourceId}"
		println uri
		def http = new HTTPBuilder( DOMAIN )
		http.get (path:uri, headers:["Accept":"application/javascript", "Content-Type" : "text/javascript", "X-Forwarded-For": "166.66.66.6", "X-Public":"True"]
			, query:[callback:"test"]
			) { resp ->
				assertNotNull resp
				assertEquals 200, resp.status
				assertEquals 'text/javascript', resp.contentType
				def text = resp.entity.content.text
				assert text ==~ "test\\(\\[200, *\\{.*\\}, *\\{\"id\":${resourceId}.*\\}]\\);"
			}
	}
	
	@Test
	void jsonPCallPostWithAccent(){
		def uri = "$GroovyAPIHelper.ECHO"
		println uri
		def http = new HTTPBuilder( DOMAIN )
		http.get (path:uri, headers:["Accept":"application/javascript", "Content-Type" : "text/javascript", "X-Forwarded-For": "166.66.66.6", "X-Public":"True"]
			, query:[callback:"test", _method:'POST', _body:'á é í ó ú' ]
			) { resp ->
				assertNotNull resp
				assertEquals 200, resp.status
				assertEquals 'text/javascript', resp.contentType
				def text = resp.entity.content.text
				assert text ==~ "test\\(\\[200, *\\{.*\\}, *á é í ó ú\\]\\);"
			}
	}
	
	@Test
	void jsonPContentType(){
		def uri = "$GroovyAPIHelper.ECHO_HEADERS"
		println uri
		def http = new HTTPBuilder( DOMAIN )
		http.get (path:uri, headers:["Accept":"application/javascript", "Content-Type" : "text/javascript", "X-Forwarded-For": "166.66.66.6", "X-Public":"True"]
			, query: [callback:"test"])
		{ resp ->
			assertNotNull resp
			def text = resp.entity.content.text.toLowerCase()
			assert text ==~ "test\\(\\[200, *\\{.*\\},.*content-type:application/json.*\\);" 
		}

	}
}
