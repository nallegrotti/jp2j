package mockapi

class ApiController {
	
    def alive = {
	    def a = ["id":1,"name":"javo"]
		[response:a]
    }

	def conContentLength = {
	    def lala = "lala"
	    def rta = "{\"rta\":\"$lala\"}"
	    response.setHeader("Content-Length", rta.length().toString());
	    render text:rta
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
	    render "POSTED: ${texto}"
    }

    def echoPut = {
    		def cs = request.characterEncoding
    		def texto = request.reader.text
    		render "PUT: ${texto}"
    }
    
    def echoDelete = {
    		def cs = request.characterEncoding
    		def texto = request.reader.text
    		render "DELETED: ${texto}"
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
	
	def echoParams = {
		println params
		render "${'*'*50}  ${params}"
	}
	
	def exception = {
		request['flagAtribute'] = 'Flaged'
		throw new Exception("**  Boom!  **")
	}
	def errorHandler = {
		render "Error!: //${request.exception.cause.message}//**${request.flagAtribute}**}"
		response.status = 500
	}
	def queryString = {
		render "${request.queryString}"
	}
}
