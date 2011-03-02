package mockapi

class ApiController {
	
    def error = {
	    println "timeout url:${request.getRequestURL()}"
	    Thread.sleep(3000)
    }
    def io_error = {
	    println "io url:${request.getRequestURL()}"
	    throw new java.io.IOException("La tenes adantro mal!!!")
    }
    def auth = {
	    println "auth url:${request.getRequestURL()}"
        if(params.access_token!="082610-BUPEUYIKN7I1M4RNIPQWIWR4TCILJ5-63762259" &&
		params.access_token!="ADMIN-BUPEUYIKN7I1M4RNIPQWIWR4TCILJ5-63762259" &&
		params.access_token!="APP_USR-1-091409-b7eb9e906a237eedb84ce9f6f07e105dfc1c130293738be45657110f11ff8cd62c053e8711263d731c1740d80b43f9ead5d3a8d3a3cfde3cc81842d9eeff2cda-94394269" &&
		params.access_token!="082610-BUPEUYIKN7I1M4RNIPQWIWR4TCILJ5-INVALIDUSER") {
            throw new com.ml.exceptions.NotFoundException("El access_token no existe man!!")
        }
        def a
        if(params.access_token.startsWith("APP")){
		a = ["user_id": "41068759",
		"client_id": "1",
		"scopes": ["read", "write"],
		"creation_date": "12-01-11 00:00:00",
		"expires_in": "3600",
		"status": "active"
		]
		
	}else if(params.access_token.startsWith("ADMIN")){
		a = ["user_id": "41068759",
		"client_id": "1",
		"scopes": ["read", "write"],
		"creation_date": "12-01-11 00:00:00",
		"expires_in": "3600",
		"status": "active",
		"admin_id":"123456"
		]
		
	}else if(params.access_token.endsWith("INVALIDUSER")){
		a = ["user_id": "41068759",
		"client_id": "1",
		"scopes": ["read", "write"],
		"creation_date": "12-01-11 00:00:00",
		"expires_in": "3600",
		"status": "inactive"
		]
    
	} else {
		a = ["user_id": "41068759",
		"client_id": "",
		"scopes": ["read", "write"],
		"creation_date": "12-01-11 00:00:00",
		"expires_in": "3600",
		"status": "active"
		]
	}
        [response:a]
    }

    def app_api = {
	    println "app url:${request.getRequestURL()}"
	def a = ["id":1,
		"name":"TestApp",
		"description":"Aplicacion de prueba",
		"thumbnail":"http://animerapid.files.wordpress.com/2008/05/simpsons.png?w=64null=64",
		"owner_id":123456,
		"catalog_product_id":null,
		"need_authorization":true,
		"short_name":"testapp",
		"url":"http://sandbox.mercadolibre.com/testapp",
		"callback_url":"http://www.arielmendez.com.ar/mlApp.php",
		"sandbox_mode":false,
		"active":true,
		"max_requests_per_minute":2]
	[response:a]
    }
    def alive = {
	    def a = ["id":1,"name":"javo"]
	[response:a]
    }
    def retrieve = {
	    println "retrieve url:${request.getRequestURL()}, params ${request.params}"
        def r = Resource.get(params.id)
        if(!r) {
            throw new com.ml.exceptions.NotFoundException("No existe el recurso ${params.id}".toString())
        }
        expiresIn 60
        setLastModified(r.lastUpdated)
        [response:r]
    }

    def retrieve_private = {
        if(!request.getHeader("X-Caller-Id") && !request.getHeader("X-Caller-Scopes")) {
            throw new com.ml.exceptions.ForbiddenException("Se debe especificar un caller.id y un caller.status")
        }
        def r = Resource.get(params.id)
        if(!r) {
            throw new com.ml.exceptions.NotFoundException("No existe el recurso ${params.id}".toString())
        }
        expiresIn 60 
        setLastModified(r.lastUpdated)
        [response:r]
    }

    def create = {
	    request.getHeaderNames().each{ headerName ->
		println "$headerName: ${request.getHeader(headerName)}"
	    }
	    println "create url:${request.getRequestURL()}"
	    println "create body:${request.JSON}"
        if(!request.getHeader("X-Caller-Id") && !request.getHeader("X-Caller-Scopes")) {
            throw new com.ml.exceptions.ForbiddenException("Se debe especificar un caller.id y un caller.status")
        }
        def r = new Resource(request.JSON)
        try{
		r.save(flush:true)
		println "resource created:${r.id}"
        }catch(Exception e){
		e.printStackTrace()
	}
        [response:r, status:201]
    }

    def update = {
	    println "update url:${request.getRequestURL()}"
	    println "update body:${request.JSON}"
        if(!request.getHeader("X-Caller-Id") && !request.getHeader("X-Caller-Scopes")) {
            throw new com.ml.exceptions.ForbiddenException("Se debe especificar un caller.id y un caller.status")
        }
        def r = Resource.get(params.id)
        if(!r) {
            throw new com.ml.exceptions.NotFoundException("No existe el recurso ${params.id}".toString())
        }
        r.properties = request.JSON
	try{
		r.save(flush:true)
		println "resource updated:${r.id}"
	}catch(Exception e){
		e.printStackTrace()
	}
        [response:r, status:200]
    }
    
    def delete = {
	    println "delete url:${request.getRequestURL()}"
        if(!request.getHeader("X-Caller-Id") && !request.getHeader("X-Caller-Scopes")) {
            throw new com.ml.exceptions.ForbiddenException("Se debe especificar un caller.id y un caller.status")
        }
        def r = Resource.get(params.id)
        if(!r) {
            throw new com.ml.exceptions.NotFoundException("No existe el recurso ${params.id}".toString())
        }
	try{
		r.delete(flush:true)
		println "resource deleted:${r.id}"
	}catch(Exception e){
		e.printStackTrace()
	}
        [response:r, status:200]
    }
    
    def echelonCross = {
      if(params.action=="cross"){
	response.setHeader("Age", "0")
	response.setHeader("X-Echelon-Lid", "6666666666")
      } else if(params.action=="cache"){
      	response.setHeader("Age", "1000")
	response.setHeader("X-Echelon-Lid", "5555555555")
      }    
      [response:[], status:200]
      	
    }
    
    def conContentLength = {
	    def lala = "lala"
	    def rta = "{\"rta\":\"$lala\"}"
	    response.setHeader("Content-Length", rta.length().toString());
	    render text:rta
    }
    
    def xRequest = {
	    render "{\"rta\":\"${params.id}\"}"
	    response.setHeader( "X-Echelon-Lid", "brokenLid${params.id}}")
    }
    
    def echo = {
		if (params.status) {
			response.status = Integer.parseInt(params.status)
		}
		def headers = request.getParameterValues("headers")
		headers.each {
			if (it) {
				def h = it.split(":")
				response.setHeader(h[0], h[1])
			}
		}
	    render "${params.body}"
    }
    
    def echoPost = {
	    def cs = request.characterEncoding
	    def texto = request.reader.text
	    render "${texto}"
    }

    def echoHeaders = {
	    def headers = request
	    def hs = request.headerNames
	    def rta = "*"*50
	    while (hs.hasMoreElements()){
		    def name = hs.nextElement()
		    def value = request.getHeader(name)
		    rta += "| ${name}:${value}" 
	    }
	    render "${rta}"
    }
}
