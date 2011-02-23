class UrlMappings {
	
	static mappings = {
		"/auth/access_token/$access_token"(controller:"api", action:"auth")
		"/applications/$id"(controller:"api", action:"app_api")
		"/public_resource/$id"(controller:"api", action = [
				GET:"retrieve"
			])
		"/alive"(controller:"api", action:"alive")
		"/private_resource/$id?"(controller:"api", action = [
				POST:"create", 
				GET:"retrieve_private", 
				PUT:"update", 
				DELETE:"delete"
			])
		"/timeout"(controller:"api", action:"error")
		
		"/ioexception"(controller:"api", action:"io_error")
		"500"(controller:'error', action:'treatExceptions')
		
		"/echelon/cross/$action"(controller:"api", action:"error")
		
		"/content_length"(controller:"api", action = [
				GET:"conContentLength"
			])
		
		"/echo"(controller:"api", action = [
				GET:"echo",
				POST:"echoPost"
			])
		"/echoHeaders"(controller:"api", action = [
				GET:"echoHeaders"
			])
	}
}
