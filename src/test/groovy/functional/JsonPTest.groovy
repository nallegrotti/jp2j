
package functional
;

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

	@Before
	public void setUp() throws Exception {
	}
	
	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	void jsonPContentHeaders(){
		def uri = "$GroovyAPIHelper.ECHO"
		def http = new HTTPBuilder( GroovyAPIHelper.DOMAIN )
		
		http.get (path:uri, headers:["X-Public":"True"]
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
	void jsonPCallError(){
		def uri = "$ECHO"
		println uri
		def http = new HTTPBuilder( DOMAIN )
		http.get (path:uri, headers:["X-Public":"True"]
			, query:[callback:"test", status:"404", body:"{response body}"]
			) { resp ->
				assertNotNull resp
				assertEquals 200, resp.status
				assertEquals 'text/javascript', resp.contentType
				def text = resp.entity.content.text
				assert text ==~ "test\\(\\[404, *\\{.*\\},.*\\{response body\\}]\\);"
			}
	}
	
	@Test
	void jsonPCallAppJson(){
		def uri = "$ECHO"
		println uri
		def http = new HTTPBuilder( DOMAIN )
		http.get (path:uri, headers:["X-Public":"True"]
			, query:[callback:"test", body:"{response body}"]
			) { resp ->
				assertNotNull resp
				assertEquals 200, resp.status
				assertEquals 'text/javascript', resp.contentType
				def text = resp.entity.content.text
				assert text ==~ "test\\(\\[200, *\\{.*\\},.*\\{response body\\}]\\);"
			}
	}
	
	@Test
	void jsonPCallPost(){
		def uri = "$ECHO"
		println uri
		def http = new HTTPBuilder( DOMAIN )
		http.get (path:uri, headers:[ "X-Public":"True"]
			, query:["callback":"test", "_method":"POST", "_body":"{name:\"test\"}"]
			) { resp ->
				assertNotNull resp
				assertEquals 200, resp.status
				assertEquals 'text/javascript', resp.contentType
				def text = resp.entity.content.text
				assert text ==~ "test\\(\\[200, *\\{.*\\},\\s*POSTED:\\s*\\{name:\"test\"\\}]\\);"
			}
	}
	
	@Test
	void jsonPCallPut(){
		def uri = "$ECHO"
		println uri
		def http = new HTTPBuilder( DOMAIN )
		http.get (path:uri, headers:[ "X-Public":"True"]
			, query:["callback":"test", "_method":"PUT", "_body":"{name:\"test\"}"]
			) { resp ->
				assertNotNull resp
				assertEquals 200, resp.status
				assertEquals 'text/javascript', resp.contentType
				def text = resp.entity.content.text
				assert text ==~ "test\\(\\[200, *\\{.*\\},\\s*PUT:\\s*\\{name:\"test\"\\}]\\);"
			}
	}
	
	@Test
	void jsonPCallDelete(){
		def uri = "$ECHO"
		println uri
		def http = new HTTPBuilder( DOMAIN )
		http.get (path:uri, headers:[ "X-Public":"True"]
			, query:["callback":"test", "_method":"DELETE", "_body":"{name:\"test\"}"]
			) { resp ->
				assertNotNull resp
				assertEquals 200, resp.status
				assertEquals 'text/javascript', resp.contentType
				def text = resp.entity.content.text
				assert text ==~ "test\\(\\[200, *\\{.*\\},\\s*DELETED:\\s*\\{name:\"test\"\\}]\\);"
			}
	}
	
	@Test
	void jsonPCallPostWithAccent(){
		def uri = "$GroovyAPIHelper.ECHO"
		println uri
		def http = new HTTPBuilder( DOMAIN )
		http.get (path:uri, headers:["Accept":"application/javascript", "Content-Type" : "text/javascript", "X-Forwarded-For": "166.66.66.6", "X-Public":"True"]
			, query:[callback:"test", _method:'POST', _body:'{á é í ó ú}' ]
			) { resp ->
				assertNotNull resp
				assertEquals 200, resp.status
				assertEquals 'text/javascript', resp.contentType
				def text = resp.entity.content.text
				assert text ==~ "test\\(\\[200, *\\{.*\\},\\s*POSTED:\\s*\\{á é í ó ú\\}\\]\\);"
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

	@Test
	void jsonPCallParameterFilter(){
		def uri = "$GroovyAPIHelper.ECHO_PARAMS"
		println uri
		def http = new HTTPBuilder( DOMAIN )
		http.get (path:uri, headers:["Accept":"application/javascript", "Content-Type" : "text/javascript", "X-Forwarded-For": "166.66.66.6", "X-Public":"True"]
			, query:[callback:"test", _method:'POST', _body:'dummy', moreParameter:'any' ]
			) { resp ->
				assertNotNull resp
				assertEquals 200, resp.status
				assertEquals 'text/javascript', resp.contentType
				def text = resp.entity.content.text
				assert text ==~ "test\\(\\[200, *\\{.*\\},.*moreParameter:any.*\\]\\);"
				assert !(text ==~ "test\\(\\[200, *\\{.*\\},.*callback:.*\\]\\);")
				assert !(text ==~ "test\\(\\[200, *\\{.*\\},.*_method:.*\\]\\);")
				assert !(text ==~ "test\\(\\[200, *\\{.*\\},.*_body:.*\\]\\);")
			}
	}
}
