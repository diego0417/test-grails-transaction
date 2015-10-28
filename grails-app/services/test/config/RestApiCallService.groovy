package test.config

class RestApiCallService {

  static transactional  = false

  boolean releaseSession = true

  def getApiCall(String uri) {
    sleep(1000)
    return [status: 200, data:[status:"completed", message:"ok!"]]
  }

}
