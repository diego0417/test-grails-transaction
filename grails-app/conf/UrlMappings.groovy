class UrlMappings {

	static mappings = {


				"/ping"{
					controller = "ping"
					action = "ping"
				}

				"/transaction/$id"(controller : "transaction") {
					action = [GET:"getTransaction", POST:"create"]
				}

				"/transaction/withTransaction"(controller : "transaction"){
					action = [POST:"createWithTransaction"]
				}

				"/$controller/$action?/$id?(.$format)?"{
						constraints {
								// apply constraints here
						}
				}

				"/"(view:"/index")
				"500"(view:'/error')
	}
}
