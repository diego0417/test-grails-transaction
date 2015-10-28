package utils

public final class DataSourcePoolUtil {

	public static final Map getPoolStatus() {
        try {
            def dataSource = SpringUtil.getBean("dataSourceUnproxied");
            Map pool = new HashMap()
            pool.put("size", dataSource.poolSize);
            pool.put("active", dataSource.getActive());
            pool.put("numActive", dataSource.getNumActive());
            pool.put("idle", dataSource.getIdle());
            pool.put("numIdle", dataSource.getNumIdle());
            pool.put("waitCount", dataSource.getWaitCount());
            pool.put("class_name", dataSource.getClass().getName())
            return pool
        } catch (Exception e) {
            println "Error getting dataSourceUnproxied: ${e.message}"
            return [:]
        }
	}

  static Map getSessionInfo() {
    try {
      def sessionFactory = SpringUtil.getBean("sessionFactory");
      def session = sessionFactory.getCurrentSession()
      return ["is_open": session.isOpen(), "is_connected":session.isConnected()]
    } catch (Exception e) {
      return ["message":"Getting Session"]
    }
  }

	static void printPoolInfo(def log, String msg) {
		Map pool = getPoolStatus()
    String mm = "Message: $msg\n[DB Status Pool: $pool]\n[Session: ${getSessionInfo()}]"
		log.info "$mm"
    println "$mm\n------------------------------------------"
	}

	static def printPoolInfo(def log, String msg, Closure closure) {
		printPoolInfo(log, "START-".concat(msg))
		def result = closure.call()
		printPoolInfo(log, "END-".concat(msg))
		return result
	}
}
