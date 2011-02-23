#!/usr/bin/env groovy

import groovyx.net.http.*
def errThread = Thread.start {
    def process = "${project.properties['base-dir']}/scripts/grails.ksh ${project.properties['grails-home']} -Dserver.port=8080 run-app -Dmockapi=true".execute()
}
def http = new RESTClient( 'http://localhost:8080' )
def x 
x = { i -> 
	try{
		if(i > 10){
			fail("mockapi starting error, aborting")
			return
		}
    	def resp = http.get( path: '/ping' ) 
		if(resp.status != 200){		
			println "content-type:$resp.contentType, status:$resp.status"
            Thread.sleep(3000)
            x(i +1)
        }
		println "mockapi is alive!!"
    }catch(Exception e){
		println "mockapi not ready..."
		Thread.sleep(3000)
        x(i+1)
	}                          
}
x(1)
