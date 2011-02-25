package functional;

import groovyx.net.http.HTTPBuilder;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

class FunctionalTests {
	
	def DOMAIN = "http://localhost:8080"
	
	@Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }
    
    @Test
	void filterActive() {
		def uri = "/echo"
		def http = new HTTPBuilder( DOMAIN )
		http.get (path:uri, headers:["Accept":"application/json", "X-Forwarded-For": "166.66.66.6"]
			, query:[body:"dummy", callback:"test"]
			) { resp ->
				assertNotNull resp
				def text = resp.entity.content.text
				assert text ==~ "test.*"
			}
	}    
	
	@Test
	void filterNotActive() {
		def uri = "/echo"
			def http = new HTTPBuilder( DOMAIN )
		http.get (path:uri, headers:["Accept":"application/json", "X-Forwarded-For": "166.66.66.6"]
		                             , query:[body:"dummy", callback:"test"]
		) { resp ->
		assertNotNull resp
		def text = resp.entity.content.text
		assert text ==~ "dummy"
		}
	}
}
