package test.config

import grails.transaction.Transactional

import utils.DataSourcePoolUtil

class TransactionService {

    static transactional = false

    def sessionFactory
    def sessionUtilService
    def restApiCallService

    def createWithTransaction(Transaction trx) {
        trx.dateCreated = new Date()
        Transaction.withTransaction {
            trx.save()
        }

        return trx.id
    }

    def createWithFlush(Transaction trx) {
        trx.dateCreated = new Date()
        if (!trx.save(flush:true)){
          throw new RuntimeException(trx.errors.toString())
        }

        return trx.id
    }

    def getTransaction(id) {
      return Transaction.get(id)
    }

    def testingClosingSession(Transaction trx) {

      println "################################################################"
      println "testingClosingSession"
      println "################################################################"


      DataSourcePoolUtil.printPoolInfo(log, "START")

      trx.dateCreated = new Date()
      if (!trx.save(flush:true)){
        throw new RuntimeException(trx.errors.toString())
      }

      def id = trx.id

      DataSourcePoolUtil.printPoolInfo(log, "Save Finished")

      closeSession()

      DataSourcePoolUtil.printPoolInfo(log, "Hibernate Close Session")

      def trx1 = Transaction.read(id)

      println "TRX1: $trx1"

      DataSourcePoolUtil.printPoolInfo(log, "Getting ID")

      //trx.withTransaction {
        trx1.status = "completed"
        trx1.save(flush:true)
      //}

      DataSourcePoolUtil.printPoolInfo(log, "END")

      def rest = restApiCallService.getApiCall("localhost:ok!")

      DataSourcePoolUtil.printPoolInfo(log, "Finished calling restApiCallService.getApiCall()")

      println "################################################################"

      return trx1

    }

    def testingPoolSize(Transaction trx) {
      DataSourcePoolUtil.printPoolInfo(log, "START")

      trx.dateCreated = new Date()
      if (!trx.save(flush:true)){
        throw new RuntimeException(trx.errors.toString())
      }
      Thread.sleep(1000 * 10)

      return trx
    }

    def testingPoolSizeWithTransaction(Transaction trx) {
      DataSourcePoolUtil.printPoolInfo(log, "START")

      trx.dateCreated = new Date()

      Transaction.withTransaction {
          trx.save()
      }

      Thread.sleep(1000 * 10)

      return trx
    }


   def getCurrentSession() {
     return sessionFactory.getCurrentSession()
   }

   def closeSession() {
     getCurrentSession().disconnect()
   }

}
